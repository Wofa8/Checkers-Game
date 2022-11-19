import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.*;
import java.util.ArrayList;
import java.awt.event.*;

public class CheckersGame extends JPanel{
    public static void main(String[] args){
        JFrame window = new JFrame("Checkers");
        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        window.setLocation( (screensize.width + 60), (screensize.height + 80) );
        window.setBounds(450, 50, 800, 550);
        CheckersGame glass = new CheckersGame();
        window.setContentPane(glass);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);
        window.setResizable(false);
    }
    
    static JLabel note;
    private JButton startAfreshButton;
    private JButton exitButton;
    static CheckersGame glass = new CheckersGame();
    Board controller = new Board();
    //static randomAI ai;
    static AI ai;

    public CheckersGame() {
        setLayout(null);
        setBackground(new Color(0,100,100));

        Board controller = new Board();
        add(startAfreshButton);
        add(exitButton);
        add(controller);
        add(note);

        controller.setBounds(0,0,520, 512);
        startAfreshButton.setBounds(550, 220, 200, 40);
        exitButton.setBounds(550, 300, 200, 40);
        note.setBounds(535, 130, 230, 60);
        note.setText("Start Playing!");
    }

    public static void changeText(String mes){
        note.setText(mes);
    }

    public class Board extends JPanel implements MouseListener, ActionListener{

        ArrayList<Pair> ValidMoves = new ArrayList<>();
        ArrayList<Pair> BlackPositions = new ArrayList<>();
        ArrayList<Pair> RedPositions = new ArrayList<>();
        ArrayList<Pair> blackMoves = new ArrayList<>();
        ArrayList<Pair> RedMoves = new ArrayList<>();
        boolean pieceSelected = false;
        int selectedPieceRow;
        int selectedPieceCol;
        int selectedPieceColour;
        Pair currentPiece = new Pair(100,100);
        BoardState state;

        Board(){
            note = new JLabel("",JLabel.CENTER);
            note.setFont(new  Font("Serif", Font.BOLD, 25));
            note.setForeground(Color.white);
            state = new BoardState();
            addMouseListener(this);
            startAfreshButton = new JButton("Reset Game");
            startAfreshButton.addActionListener(this);
            exitButton = new JButton("Exit");
            exitButton.addActionListener(this);
            repaint();
        }

        //This method paints the board, all the pieces, and the possible moves of any selected piece 
        public void paintComponent (Graphics g) {
            Color brown = new Color(139,69,19);
            Color cream = new Color(255,228,181);
            boolean primary = true;
            //Painting board
            for(int row = 0; row < 8; row++){
                for(int col = 0; col < 8; col++){
                    if(!primary){
                        g.setColor(brown);
                    }
                    else{
                        g.setColor(cream);
                    }
                    g.fillRect(col*64, row*64, 64, 64);
                    primary = !primary;

                    //Painting red and black pieces
                    int val = state.checkPiece(row, col);
                    if (val == state.RED){
                        g.setColor(Color.RED);
                        g.fillOval((col*64) + 7, (64*row) + 5, 50, 50);}
                    else if (val == state.BLACK){
                        g.setColor(Color.BLACK);
                        g.fillOval((col*64) + 7, (64*row) + 5, 50, 50);
                    }
                }
                primary = !primary;
            }
            //Drawing the right black border
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(Color.black);
            g2.setStroke(new BasicStroke(5));
            g2.drawLine(515, 0, 515,512);

            //Painting the possible moves of the highlighted piece
            if( ValidMoves.size() != 0 ){
                g2.setColor(Color.blue);
                g2.setStroke(new BasicStroke(2));
                for( Pair p: ValidMoves ) {
                    g2.drawRect( p.getCol() * 64, p.getRow() * 64 , 64, 64);
                }
                g2.setColor(Color.green);
                g2.drawRect( currentPiece.getCol() * 64, currentPiece.getRow() * 64 , 64, 64);
            }
            g.dispose();
            g2.dispose();
        }

        public void mouseClicked(MouseEvent e) {
            int col = e.getX() / 64;
            int row = e.getY() / 64;

            if (pieceSelected == true){
                checkPosition(row, col);
            }
            else if (pieceSelected == false){
                if (state.checkPiece(row, col) != state.getTurn()){
                    System.out.println("Click a piece of your colour");
                }
                else{
                    ValidMoves.clear();
                    this.showMoves(row, col);
                    currentPiece.setRow(row);
                    currentPiece.setCol(col);
                }
            }
            
        }
        public void mouseEntered(MouseEvent e) {} //note.setText("Mouse Entered"); }  
        public void mouseExited(MouseEvent e) {} //note.setText("Mouse Exited"); }  
        public void mousePressed(MouseEvent e) {} //note.setText("Mouse Pressed"); }  
        public void mouseReleased(MouseEvent e) {} //note.setText("Mouse Released"); }

        public void checkPosition(int row, int col){
            // if piece is a different piece and same colour show moves
            if (state.checkPiece(row, col) == selectedPieceColour) {
                ValidMoves.clear();
                this.showMoves(row, col);
                pieceSelected = false;
            }
            // else if location selected is a valid move, move piece to that location
            else if (isValidMove(row, col)) {
                state.changePosition(selectedPieceRow, selectedPieceCol, row, col, selectedPieceColour);
                repaint();
                pieceSelected = false;
                getBlackPiecePositions();
                getRedPiecePositions();
                state.checkWinner();
                if (state.turn == 1){
                    state.turn = -1;
                    System.out.println("Turn changed to " + state.turn);
                    note.setText("Black Player's Turn!");
                    //ai = new randomAI();
                    ai = new AI();
                    //randomAI.Node2 m6 = ai.getRandomMove();
                    AI.Node2 m4 = ai.bestMove(state.getState());
                    state.changePosition(m4.row1, m4.col1, m4.row2, m4.col2, -1);
                    //state.changePosition(m6.row1, m6.col1, m6.row2, m6.col2, -1);
                    
                }
                else {
                    state.turn = 1;
                    System.out.println("Turn changed to " + state.turn);
                    note.setText("Red Player's Turn!");
                }
                repaint();
            }
            // else print click valid position
            else {
                System.out.println("Click valid move");  }
        }

        public void moveAI(int row, int col) {
            state.changePosition(selectedPieceRow, selectedPieceCol, row, col, state.BLACK);
            repaint();
        }
        
        public void showMoves(int row, int col) {
            int colour = state.checkPiece(row, col);
            if (colour == state.EMPTY) {
                System.out.println("Select a piece");
            }
            else {
                pieceSelected = true;
                selectedPieceRow = row;
                selectedPieceCol = col;
                if (colour == state.BLACK) {
                    selectedPieceColour = state.BLACK;
                }
                else {
                    selectedPieceColour = state.RED;
                }
                getValidMoves(row, col);
                repaint();
            }
            
        }

        public ArrayList<Pair> getBlackPiecePositions(){
            BlackPositions = new ArrayList<>();
            for (int i = 0; i < 8; i ++){
                for (int j = 0; j < 8; j ++) {
                    if (state.checkPiece(i, j) == state.BLACK) 
                    BlackPositions.add( new Pair(i, j) );
                }
            }
            return BlackPositions;
        }

        public ArrayList<Pair> getRedPiecePositions(){
            RedPositions = new ArrayList<>();
            for (int i = 0; i < 8; i ++){
                for (int j = 0; j < 8; j ++) {
                    if (state.checkPiece(i, j) == state.RED) 
                    RedPositions.add( new Pair(i, j) );
                }
            }
            return RedPositions;
        }

        public boolean isValidMove(int row, int col) {
            for (Pair p: ValidMoves) {
                if (p.getRow() == row && p.getCol() == col) {
                    return true;
                }
            }
            return false;
        }
        public void getValidMoves(int row, int col) {
            int Piece = state.checkPiece(row, col);
            if (col == 0) {
                if (Piece == state.BLACK){
                    if (state.checkPiece(row + 1, col + 1) == state.EMPTY) {
                        ValidMoves.add(new Pair(row + 1, col + 1));
                        System.out.printf("(%d, %d) is a valid move\n", row + 1, col + 1);
                    }
                    else {
                        if (state.checkPiece(row + 2, col + 2) == state.EMPTY && state.checkPiece(row + 1, col + 1) == state.RED) {
                            ValidMoves.add(new Pair(row + 2, col + 2));
                            System.out.printf("(%d, %d) is a valid move\n", row + 2, col + 2);
                        }
                    }
                }
                
                else if (Piece == state.RED) {
                    if (state.checkPiece(row - 1, col + 1) == state.EMPTY) {
                        ValidMoves.add(new Pair(row - 1, col + 1));
                        System.out.printf("(%d, %d) is a valid move\n", row - 1, col + 1);
                    }
                    else {
                        if (state.checkPiece(row - 2, col + 2) == state.EMPTY && state.checkPiece(row - 1, col + 1) == state.BLACK) {
                            ValidMoves.add(new Pair(row - 2, col + 2));
                            System.out.printf("(%d, %d) is a valid move\n", row - 2, col + 2);
                        }
                    }
                }
            }
            else if (col == 7) {
                if (Piece == state.BLACK){
                    if (state.checkPiece(row + 1, col - 1) == state.EMPTY) {
                        ValidMoves.add(new Pair(row + 1, col - 1));
                        System.out.printf("(%d, %d) is a valid move\n", row + 1, col - 1);
                    }
                    else {
                        if (state.checkPiece(row + 2, col - 2) == state.EMPTY && state.checkPiece(row + 1, col - 1) == state.RED) {
                            ValidMoves.add(new Pair(row + 2, col - 2));
                            System.out.printf("(%d, %d) is a valid move\n", row + 2, col - 2);
                        }
                    }
                }

                else if (Piece == state.RED) {
                    if (state.checkPiece(row - 1, col - 1) == state.EMPTY) {
                        ValidMoves.add(new Pair(row - 1, col - 1));
                        System.out.printf("(%d, %d) is a valid move\n", row - 1, col - 1);
                    }
                    else {
                        if (state.checkPiece(row - 2, col - 2) == state.EMPTY && state.checkPiece(row - 1, col - 1) == state.BLACK) {
                            ValidMoves.add(new Pair(row - 2, col - 2));
                            System.out.printf("(%d, %d) is a valid move\n", row - 2, col - 2);
                        }
                    }
                }
            }

            else {
                if (Piece == state.BLACK){
                    if (state.checkPiece(row + 1, col + 1) == state.EMPTY) {
                        ValidMoves.add(new Pair(row + 1, col + 1));
                        System.out.printf("(%d, %d) is a valid move\n", row + 1, col + 1);
                    }
                    else {
                        canJump(Piece, row, col);
                        
                    }

                    if (state.checkPiece(row + 1, col - 1) == state.EMPTY) {
                        ValidMoves.add(new Pair(row + 1, col - 1));
                        System.out.printf("(%d, %d) is a valid move\n", row + 1, col - 1);
                    }
                    else {
                        canJump(Piece, row, col);
                    }
                }
                
                else if (Piece == state.RED) {
                    if (state.checkPiece(row - 1, col + 1) == state.EMPTY) {
                        ValidMoves.add(new Pair(row - 1, col + 1));
                        System.out.printf("(%d, %d) is a valid move\n", row - 1, col + 1);
                    }
                    else {
                        canJump(Piece, row, col);
                    }

                    if (state.checkPiece(row - 1, col - 1) == state.EMPTY) {
                        ValidMoves.add(new Pair(row - 1, col - 1));
                        System.out.printf("(%d, %d) is a valid move\n", row - 1, col - 1);
                    }
                    else {
                        canJump(Piece, row, col);
                    }
                }
            }
            if( ValidMoves.size() == 0) System.out.println("No valid moves for this piece");
        }

        void canJump(int Piece, int row, int col){
            if (col + 2 > 7){
                if (Piece == state.BLACK) {
                    if (state.checkPiece(row + 2, col - 2) == state.EMPTY && state.checkPiece(row + 1, col - 1) == state.RED) {
                        ValidMoves.add(new Pair(row + 2, col - 2));
                        System.out.printf("(%d, %d) is a valid move\n", row + 2, col - 2);
                    }
                }
                if (Piece == state.RED) {
                    if (state.checkPiece(row - 2, col - 2) == state.EMPTY && state.checkPiece(row - 1, col - 1) == state.BLACK) {
                        ValidMoves.add(new Pair(row - 2, col - 2));
                        System.out.printf("(%d, %d) is a valid move\n", row - 2, col - 2);
                    }
                }
            }
    
            else if(col - 2 < 0){
                if (Piece == state.BLACK) {
                    if (state.checkPiece(row + 2, col + 2) == state.EMPTY && state.checkPiece(row + 1, col + 1) == state.RED) {
                        ValidMoves.add(new Pair(row + 2, col + 2));
                        System.out.printf("(%d, %d) is a valid move\n", row + 2, col + 2);
                    }
                }
                if (Piece == state.RED) {
                    if (state.checkPiece(row - 2, col + 2) == state.EMPTY && state.checkPiece(row - 1, col + 1) == state.BLACK) {
                        ValidMoves.add(new Pair(row - 2, col + 2));
                        System.out.printf("(%d, %d) is a valid move\n", row - 2, col + 2);
                    }
                }
            }
            else {
                if (Piece == state.BLACK) {
                    if (state.checkPiece(row + 2, col + 2) == state.EMPTY && state.checkPiece(row + 1, col + 1) == state.RED) {
                        ValidMoves.add(new Pair(row + 2, col + 2));
                        System.out.printf("(%d, %d) is a valid move\n", row + 2, col + 2);
                    }
                    if (state.checkPiece(row + 2, col - 2) == state.EMPTY && state.checkPiece(row + 1, col - 1) == state.RED) {
                        ValidMoves.add(new Pair(row + 2, col - 2));
                        System.out.printf("(%d, %d) is a valid move\n", row + 2, col - 2);
                    }
                }
                else if (Piece == state.RED) {
                    if (state.checkPiece(row - 2, col + 2) == state.EMPTY && state.checkPiece(row - 1, col + 1) == state.BLACK) {
                        ValidMoves.add(new Pair(row - 2, col + 2));
                        System.out.printf("(%d, %d) is a valid move\n", row - 2, col + 2);
                    }
                    if (state.checkPiece(row - 2, col - 2) == state.EMPTY && state.checkPiece(row - 1, col - 1) == state.BLACK) {
                        ValidMoves.add(new Pair(row - 2, col - 2));
                        System.out.printf("(%d, %d) is a valid move\n", row - 2, col - 2);
                    }
                }
            }
        }

        public void actionPerformed(ActionEvent e) {
            Object event = e.getSource();
            if (event == startAfreshButton){
                state.newGame();
                repaint();
            }
            else if (event == exitButton)
                System.exit(0);
        }

        public class BoardState {
            public final int RED = 1;
            public final int BLACK = -1;
            public final int EMPTY = 0;
            public final int TIE = 0;
            public int redNumber = 12;
            public int blackNumber = 12;
            int turn = 1;
            boolean moveSelected;
    
            public int[][] Board;
    
            public BoardState() {
                Board = new int[8][8];
                initialize();
            }
    
            public int checkPiece(int row, int col) {
                return Board[row][col];
            }
    
            public void changePosition(int old_row, int old_col, int new_row, int new_col, int colour) {
                System.out.println("reassigning");
                Board[old_row][old_col] = EMPTY;
                Board[new_row][new_col] = colour;
    
                System.out.println("reassigned");
                if (new_col - old_col == 2) {
                    if (colour == BLACK) {
                        Board[old_row + 1][old_col + 1] = EMPTY;
                        redNumber--;
                    }
                    else if(colour == RED) {
                        Board[old_row - 1][old_col + 1] = EMPTY;
                        blackNumber--;
                    }
                }
                else if (new_col - old_col == -2) {
                    if (colour == BLACK) {
                        Board[old_row + 1][old_col - 1] = EMPTY;
                        redNumber--;
                    }
                    else if (colour == RED) {
                        Board[old_row - 1][old_col - 1] = EMPTY;
                        blackNumber--;
                    }
                }
                repaint();
            }
    
            public void initialize() {
                for (int row = 0; row < 3; row++) {
                    for (int col = 0; col < 8; col ++) {
                        if ((row % 2 != 0) && (col % 2 == 0)) {
                            this.Board[row][col] = BLACK;
                        }
                        else if((row % 2 == 0) && (col % 2 != 0)) {
                        this.Board[row][col] = BLACK;
                        }
                        else {this.Board[row][col] = EMPTY;}
                    }
                }
    
                for (int row = 5; row < 8; row++) {
                    for (int col = 0; col < 8; col ++) {
                        if ((row % 2 != 0) && (col % 2 == 0)) {
                            this.Board[row][col] = RED;
                        }
                        else if((row % 2 == 0) && (col % 2 != 0)) {
                            this.Board[row][col] = RED;
                        }
                        else {this.Board[row][col] = EMPTY;}
                    }
                }
            }
    
            public void changeTurn(){
                if (turn == 1){
                    turn = -1;
                    note.setText("Black Player's Turn!");
                }
                else if (turn == -1) {
                    turn = 1;
                    note.setText("Red Player's Turn!");
                }
                System.out.println("Turn changed to " + turn);
            }
    
            int getTurn(){ return turn; }
            
            void resetTurn(){ turn = 1; }
    
            void newGame(){
                this.Board = new int[8][8];
                initialize();
                resetTurn();
                repaint();
            }
    
            public int checkWinner() {
                if (redNumber == 0) {
                    note.setText("Black Wins!");
                }
                else if (blackNumber == 0) {
                    note.setText("Red Wins!");
                }
                else if (blackNumber == 0 && redNumber == 0){
                    note.setText("It's a tie!");
                }
                return 0;
            }
    
            public int[][] getState() {
                return Board;
            }
    
            public int getState(int i, int j) {
                return Board[i][j];
            }
            
        }
    }

  
    








    class Pair{
        private int row;
        private int col;

        public Pair(int r, int c){
            this.row = r;
            this.col = c;
        }

        public Pair(){
            this.row = 99;
            this.col = 99;
        }

        public int getRow() {
            return row;
        }

        public int getCol() {
            return col;
        }

        public void setRow(int r) {
            row = r;
        }

        public void setCol(int c) {
            col = c;
        }
    }  
}