/**
 * Created by Derongan on 2/23/2016.
 */
public class BitBoard{
    public long board[] = {0L,0L};
    public int height[] = {0,7,14,21,28,35,42};
    public int moves[] = new int[7*6];
    public int togo = 0;

    int played = 0;

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

        played = g.getCoins();
        togo = g.getActivePlayer()+1;
    }

    public void unMakeMove(){
        int move = moves[--togo];   //Get column to undo
        board[togo&1] ^= 1L<<--height[move]; //Undo it
        played--;
        winner = -1;
    }

    public BitBoard copy() {
        BitBoard b = new BitBoard();
        b.board[0] = board[0];
        b.board[1] = board[1];
        b.togo = togo;
        System.arraycopy(height,0, b.height, 0, 7);
        System.arraycopy(moves, 0, b.moves, 0, 7*6);
        b.played = played;
        b.winner = winner;
        return b;
    }

    public boolean canMakeMove(int move){
        return height[move] < move*7 + 6;
    }

    public void makeMove(int move){
        board[togo&1] ^= 1L<<height[move]++;
        togo++;
        played++;
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

        winner = 0;
    }

    public long getAt(int x, int y){
        return (board[0] &  (1L << (x*7+y))) - (board[1] &  (1L << (x*7+y)));
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
        return played;
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
                long piece = getAt(j,i);
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

    public static void main(String[] args){
        GameState_General g = new GameState_General(7,6);
        g.makeMove(0);
        g.makeMove(1);
        g.makeMove(0);
        g.makeMove(1);
        g.makeMove(0);
        g.makeMove(1);
        BitBoard b = new BitBoard(g);
        b.makeMove(2);
        b.makeMove(2);
        b.makeMove(4);
        b.makeMove(3);
        b.makeMove(3);
        b.makeMove(0);
        System.out.println(b.getWinner());
        b.display();
    }
}
