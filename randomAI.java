import java.util.ArrayList;
import java.util.Random;

public class randomAI {
    Random rand = new Random();
    int colour = -1;

    CheckersGame glassClone = new CheckersGame();
    CheckersGame gl = CheckersGame.glass;
    CheckersGame.Board boardClone = glassClone.controller;
    BoardState stateClone = boardClone.state;
    ArrayList<CheckersGame.Pair> bp = gl.controller.getBlackPiecePositions();
    ArrayList<CheckersGame.Pair> rp = gl.controller.getRedPiecePositions();
    Node2 m4;
    int[][] s = stateClone.Board;
    int[][] o = CheckersGame.glass.controller.state.getState();;
    //stateClone.blackNumber = bp.size();
    //stateClone.RedNumber = bp.size();

    public Node2 getRandomMove(){
        for (int k = 0; k < o.length; k ++) {
            s[k] = o[k].clone(); }
        //int row = rand.nextInt(8);
        //int col = rand.nextInt(8);
        
        for (int k = 0; k < bp.size(); k++) {
            int row = bp.get(k).getRow();
            int col = bp.get(k).getCol();
            boardClone.getValidMoves(row, col);
            if (boardClone.ValidMoves.size() != 0) {
                System.out.println("Size: " + boardClone.ValidMoves.size());
                int num = rand.nextInt(boardClone.ValidMoves.size());
                CheckersGame.Pair move = boardClone.ValidMoves.get(num);
                m4 = new Node2(row, col, move.getRow(), move.getCol());
                return m4;
            }
            //getRandomMove();

        /*if (stateClone.checkPiece(row, col) != colour) {
            getRandomMove();
            System.out.println("rerunning");
        }*/
        /*else{
            boardClone.getValidMoves(row, col);
            if (boardClone.ValidMoves.size() != 0) {
                System.out.printf("row: %d, col: %d %n", row, col);
                System.out.println("Size: " + boardClone.ValidMoves.size());
                int num = rand.nextInt(boardClone.ValidMoves.size());
                System.out.println("Number: " + num);
                CheckersGame.Pair move = boardClone.ValidMoves.get(num);
                boardClone.selectedPieceRow = row;
                boardClone.selectedPieceCol = col;

                boardClone.moveAI(move.getRow(), move.getCol());
                stateClone.changePosition(row, col, move.getRow(), move.getCol(), -1);
                //stateClone.changeTurn();
            }
            else {
                getRandomMove();
            }*/
        }
        return m4;
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
    }
}
