import java.util.ArrayList;

public class AI {

    //Return first move pair
    //Not Minning
    // Value not incrementing

    boolean M = false;
    int colour = -1;
    int i = 0;
    int j = 0;
    int selectedPieceX = 99;
    int selectedPieceY = 99;
    CheckersGame glassClone = CheckersGame.glass;
    CheckersGame.Board boardClone = glassClone.controller;
    CheckersGame.Board.BoardState stateClone = boardClone.state;
    //CheckersGame.BoardState stateClone = state;
    int rn = stateClone.redNumber;
    int bn = stateClone.blackNumber;
    int[][] copyBoard = new int[8][8];
    CheckersGame.Pair minMove = null;
    CheckersGame.Pair maxMove = null;
    int minResult =  Integer.MAX_VALUE;
    int maxResult = Integer.MIN_VALUE;
    ArrayList<CheckersGame.Pair> start = new ArrayList<>();
    Node send = new Node();

    public Node2 bestMove(int[][] s) {
        for (int k = 0; k < s.length; k ++) {
            copyBoard[k] = s[k].clone(); }
        CheckersGame glassClone = new CheckersGame();
        CheckersGame.Board boardClone = glassClone.controller;
        CheckersGame.Board.BoardState stateClone = boardClone.state;
        Node move = minimax(copyBoard, 1);
        move.row = send.row;
        move.col = send.col;
        boardClone.pieceSelected = true;
        boardClone.selectedPieceCol = move.getCol();
        boardClone.selectedPieceRow = move.getRow();
        System.out.println("Move paras: ");
        System.out.println(move.getRow());
        System.out.println(move.getCol());
        boardClone.selectedPieceColour = -1;
        Node2 m2 = new Node2(selectedPieceX, selectedPieceY, move.getRow(), move.getCol());
        return m2;
        //stateClone.changePosition(selectedPieceX, selectedPieceY, move.getRow(), move.getCol(), -1);
    }

    public Node minimax(int[][] s, int depth){

        System.out.println("Rerun");
        ArrayList<CheckersGame.Pair> bp = boardClone.getBlackPiecePositions();
        ArrayList<CheckersGame.Pair> rp = boardClone.getRedPiecePositions();

        if (depth == 0 /*|| stateClone.checkWinner() != stateClone.TIE*/) {
            System.out.println("Done");
            System.out.println(start.size());

            if (i == 78){
                System.out.println("Here");
                System.out.println(start.get(0).getRow());
                System.out.println(start.get(0).getCol());
                send.row = (start.get(0).getRow());
                send.col = (start.get(0).getCol());
                return new Node(start.get(0), 0, selectedPieceX, selectedPieceY);}

            else if (M == false) {
                return new Node(minMove, minResult, selectedPieceX, selectedPieceY);
            }
            else if (M == true){
                return new Node(maxMove, maxResult, selectedPieceX, selectedPieceY);
            }
        }

            if (M == true) {
                M = false;
                System.out.println("Minimizing");
                for (int k = 0; k < rp.size(); k++) {
                    int row = rp.get(k).getRow();
                    int col = rp.get(k).getCol();
                    boardClone.getValidMoves(row, col);
                    if (boardClone.ValidMoves.size() != 0) {
                        for (CheckersGame.Pair p: boardClone.ValidMoves) {
                            start.add(p);
                            stateClone.changePosition(row, col, p.getRow(), p.getCol(), colour);
                            int score = rn - bn;
                            selectedPieceX = row;
                            selectedPieceY = col;
                            s = stateClone.getState();
                        
                            if (score < minResult) {
                                i = 5;
                                minResult = score;
                                minMove = p;
                                selectedPieceX = row;
                                selectedPieceY = col;
                            }
                            Node best = minimax(s, 0);      
                        }
                    }
                }
                System.out.println("Finally returned something");
                
            }

            else if (M != true) {
                M = true;
                System.out.println("Maximizing");
                System.out.println("Size of bp: " + bp.size());
                for (int k = 0; k < bp.size(); k++) {
                    int row = bp.get(k).getRow();
                    int col = bp.get(k).getCol();
                    boardClone.getValidMoves(row, col);
                    System.out.println(boardClone.ValidMoves.size());
                    if (boardClone.ValidMoves.size() != 0) {
                        System.out.println("got here");
                        for (CheckersGame.Pair p: boardClone.ValidMoves) {
                            start.add(p);
                            int score = bn - rn;
                            selectedPieceX = row;
                            selectedPieceY = col;
                            i = 78;
                            stateClone.changePosition(row, col, p.getRow(), p.getCol(), colour);
                            s = stateClone.getState();    
                                                    
                            if (score > maxResult) {
                                i = 5;
                                maxResult = score;
                                maxMove = p;
                                selectedPieceX = row;
                                selectedPieceY = col;
                            }
                            Node best = minimax(s, 0);
                        }
                    }
                }
                System.out.println("Returned another thing");
                
            }
        return new Node(null, stateClone.checkWinner(), 0, 0);
    }

    public void setM(boolean val){
        M = val;
    }

    class Node {
        private CheckersGame.Pair Rmove;
        private int value;
        private int row;
        private int col;

        public Node(){}

        public Node(CheckersGame.Pair p, int val, int row, int col) {
            this.Rmove = p;
            this.value = val;
            this.row = row;
            this.col = col;
        }

        public int getValue() {
            return value;
        }
        public CheckersGame.Pair getMove() {
            return Rmove;
        }
        public int getRow() {
            return row;
        }
        public int getCol() {
            return col;
        }
    }

    class Node2 {
        int row1;
        int col1;
        int row2;
        int col2;

        public Node2(){}

        public Node2(int r, int c, int r2, int c2) {
            this.row1 = r;
            this.col1 = c;
            this.row2 = r2;
            this.col2 = c2;
        }
        /*public int getRow() {
            return row;
        }
        public int getCol() {
            return col;
        }*/
    }
}