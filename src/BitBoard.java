import com.sun.deploy.util.ArrayUtil;

import java.util.Arrays;
import java.util.Collections;

/**
 * Created by Derongan on 2/23/2016.
 */
public class BitBoard{
    public long board[] = {0L,0L};
    public int height[] = {0,7,14,21,28,35,42};
    public int moves[] = new int[7*6];
    public int togo = 0;

    int winner = -1;

    public BitBoard(){

    }

    public BitBoard(GameStateModule g){
        for(int i = 0; i < 7; i ++){
            for(int j = 0; j < 6; j++){
                int who = g.getAt(i,j);
                setAt(i,j,who);
            }
        }

        for(int i = 0; i < 7; i++){
            height[i] = g.getHeightAt(i) + i*7;
        }

        togo = g.getCoins();
    }

    public void unMakeMove(){
        int move = moves[--togo];   //Get column to undo
        board[togo&1] ^= 1L<<--height[move]; //Undo it
        winner = -1;
    }

    public BitBoard copy() {
        BitBoard b = new BitBoard();
        b.board[0] = board[0];
        b.board[1] = board[1];
        b.togo = togo;
        System.arraycopy(height,0, b.height, 0, 7);
        System.arraycopy(moves, 0, b.moves, 0, 7*6);
        b.winner = winner;
        return b;
    }

    public boolean canMakeMove(int move){
        return height[move] < move*7 + 6;
    }

    public void makeMove(int move){
        board[togo&1] ^= 1L<<height[move]++;
        moves[togo++] = move;
        computeVictory();
    }

    public int getWinner(){
        return winner;
    }

    public int getActivePlayer() {
        return (togo&1) == 1L ? 2 : 1;
    }

    public boolean isGameOver(){
        return winner != -1;
    }

    //Taken from https://tromp.github.io/c4/Connect4.java
    public void computeVictory(){
        int curp = (togo+1) & 1;
        long b = board[curp];
        curp = (curp == 1L) ? 2 : 1;
        long y = b & (b>>6);
        if ((y & (y >> 2*6)) != 0) { // check diagonal \
            winner = curp;
            return;
        }
        y = b & (b>>7);
        if ((y & (y >> 2*7)) != 0) { // check horizontal -
            winner = curp;
            return;
        }
        y = b & (b>>8); // check diagonal /
        if ((y & (y >> 2*8)) != 0){
            winner = curp;
            return;
        }
        y = b & (b>>1); // check vertical |
        if( (y & (y >> 2)) != 0) {
            winner = curp;
            return;
        }

        if(togo == 42)
            winner = 0;
    }

    public int getAt(int x, int y){
        return ((int)(((board[0] >>> (7*x + y))&1) - ((board[1] >>> (7*x + y))&1)));
    }

    public int getHeightAt(int x) {
        return height[x] - (7*x);
    }

    public int getWidth() {
        return 7;
    }

    public int getHeight() {
        return 6;
    }

    public int getCoins() {
        return togo;
    }

    public void setAt(int x, int y, int who){
        if(who == 0)
            return;
        board[who-1] ^=  1L << (x*7+y);
    }

    //i=4
    //j=4
    //0,0 should be bottom left
    //If we store column by column height becomes more useful
    public void display(){
        for(int i = 5; i >= 0; i--){
            for(int j = 0; j < 7; j++){
                int piece = getAt(j,i);
                if(piece > 0)
                    System.out.print("X ");
                else if (piece < 0)
                    System.out.print("O ");
                else
                    System.out.print("_ ");
            }
            System.out.println();
        }
        System.out.println();
    }

    public String repr(){
        StringBuilder builder = new StringBuilder();
        for(int i = 5; i >= 0; i--){
            for(int j = 0; j < 7; j++){
                int piece = getAt(j,i);
                if(piece > 0)
                    builder.append("X ");
                else if (piece < 0)
                    builder.append("O ");
                else
                    builder.append("_ ");
            }
            builder.append('\n');
        }
        builder.append('\n');

        return builder.toString();
    }

    @Override
    public boolean equals(Object o){
        boolean ret = (board[0] == ((BitBoard)o).board[0]) || (flip(board[0]) == flip(((BitBoard)o).board[0]));
        ret &= (board[1] == ((BitBoard)o).board[1]) || (flip(board[1]) == flip(((BitBoard)o).board[1]));
        return ret & (board[1] == ((BitBoard)o).board[1]) && ((togo&1L) == (((BitBoard)o).togo&1L));
    }

    public long flip(long l){
        long sum = 0;
        long r = Long.reverse(l) >>> 15;
        sum |= (r & 0b1000000100000010000001000000100000010000001000000L) >>> 6;
        sum |= (r & 0b0100000010000001000000100000010000001000000100000L) >>> 4;
        sum |= (r & 0b0010000001000000100000010000001000000100000010000L) >>> 2;
        sum |= (r & 0b0001000000100000010000001000000100000010000001000L);
        sum |= (r & 0b0000100000010000001000000100000010000001000000100L) << 2;
        sum |= (r & 0b0000010000001000000100000010000001000000100000010L) << 4;
        return sum;
    }

    @Override
    public int hashCode(){
        return Math.min((int) (board[0] + board[1] + (togo & 1)), (int)(flip(board[0]) + flip(board[1]) + (togo&1)));
    }

    public static void main(String[] args){
        BitBoard b = new BitBoard();

        AlphaBetaAI ab = new AlphaBetaAI();
        NegaMaxAIBit nm = new NegaMaxAIBit();

        ab.terminate = false;
        nm.terminate = false;

        b.makeMove(3);
        b.makeMove(3);
        b.makeMove(2);
        b.makeMove(4);
        b.makeMove(4);
        b.makeMove(4);
        b.makeMove(3);
        b.makeMove(3);
        b.makeMove(3);


        b.display();


        System.out.println(ab.negaMaxAB(2,b,-1));
        System.out.println(nm.negaMax(2,b,-1));
    }
}
