public class BitBoard{
    public long board[] = {0L,0L};
    public int height[] = {0,7,14,21,28,35,42};
    public int moves[] = new int[7*6];
    public int togo = 0;


    static final int WIDTH = 7;
    static final int HEIGHT = 6;

    static final int H1 = HEIGHT+1;
    static final int H2 = HEIGHT+2;
    static final int SIZE = HEIGHT*WIDTH;
    static final int SIZE1 = H1*WIDTH;
    static final long ALL1 = (1L<<SIZE1)-1L; // assumes SIZE1 < 63
    static final int COL1 = (1<<H1)-1;
    static final long BOTTOM = ALL1 / COL1; // has bits i*H1 set
    static final long TOP = BOTTOM << HEIGHT;

    //All 69 possible winning groups
    public static long[] winGroups = {
            //4 vertical from 0 row
            15L,
            1920L,
            245760L,
            31457280L,
            4026531840L,
            515396075520L,
            65970697666560L,
            //4 vertical from 1
            30L,
            3840L,
            491520L,
            62914560L,
            8053063680L,
            1030792151040L,
            131941395333120L,
            //4 vertical from 2
            60L,
            7680L,
            983040L,
            125829120L,
            16106127360L,
            2061584302080L,
            263882790666240L,
            //4 horizontal from 0 row
            2113665L,                   //21
            270549120L,
            34630287360L,
            4432676782080L,
            //4 horizontal from 1
            4227330L,
            541098240L,
            69260574720L,
            8865353564160L,
            //4 horizontal from 2
            8454660L,
            1082196480L,
            138521149440L,
            17730707128320L,
            //4 horizontal from 3
            16909320L,
            2164392960L,
            277042298880L,
            35461414256640L,
            //4 horizontal from 4
            33818640L,
            4328785920L,
            554084597760L,
            70922828513280L,
            //4 horizontal from 5(highest)
            67637280L,
            8657571840L,
            1108169195520L,
            141845657026560L,
            //Right up diags
            16843009L,
            2155905152L,
            275955859456L,
            35322350010368L,
            33686018L,
            4311810304L,
            551911718912L,
            70644700020736L,
            67372036L,
            8623620608L,
            1103823437824L,
            141289400041472L,
            //Right down diags
            2130440L,
            272696320L,
            34905128960L,
            4467856506880L,
            4260880L,
            545392640L,
            69810257920L,
            8935713013760L,
            8521760L,
            1090785280L,
            139620515840L,
            17871426027520L
    };

    int winner = -1;

    public BitBoard(){



    }

    public long positioncode()
    {
        return 2*board[0] + board[1] + BOTTOM;
// color[0] + color[1] + BOTTOM forms bitmap of heights
// so that positioncode() is a complete board encoding
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

    public int getWinner2(){
        if(winner == 0)
            return 0;
        return winner == 2 ? -1 : 1;
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

        /*boolean ret = (board[0] == ((BitBoard)o).board[0]) || (board[0] == flip(((BitBoard)o).board[0]));
        ret &= (board[1] == ((BitBoard)o).board[1]) || (board[1] == flip(((BitBoard)o).board[1]));
        return ret && ((togo&1L) == (((BitBoard)o).togo&1L));*/

        //Improved equals
        //return board[0] == ((BitBoard)o).board[0] && Arrays.equals(height, ((BitBoard)o).height);


        return board[0] == ((BitBoard)o).board[0] && board[1] == ((BitBoard)o).board[1];
    }

    public static long flip(long l){
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

    public BitBoard findThreats(){
        BitBoard threats = new BitBoard();
        for(int i = 0; i < BitBoard.winGroups.length; i++){
            long b = BitBoard.winGroups[i];
            //If they are the sole owners of this winslot
            if((board[0] & b) == 0){
                if(Long.bitCount(board[1] & b) == 3){
                    threats.board[1] |= ~board[1] & b;
                }
            }
            //If we are the sole owners of this winslot
            else if((board[1] & b) == 0){
                if(Long.bitCount(board[0] & b) == 3){
                    threats.board[0] |= ~board[0] & b;
                }
            }
            else{
                continue;
            }
        }

        return threats;
    }

    //NOT WORKING!! IGNORES CASE WHERE THREATS ARE IN SAME SLOT
    public void removeUselessThreats(){
        long b0 = board[0];
        long b1 = board[1];

        //Remove some empty threats
        board[1] = (((b0 << 1) ^ b1) & 279258638311359L)&b1; //Long representing full board
        board[0] = (((b1 << 1) ^ b0) & 279258638311359L)&b0;
    }

    @Override
    public int hashCode(){
        return (int)(board[0] + board[1] + (togo & 1));
        //return Math.min((int) (board[0] + board[1] + (togo & 1)), (int)(flip(board[0]) + flip(board[1]) + (togo&1)));
    }

    public static void main(String[] args) {
        BitBoard b = new BitBoard();
        GameState_General g = new GameState_General(7,6);
        alphabeta_zugzwang zz = new alphabeta_zugzwang();
        zz.terminate = false;

        AlphaBetaAI ab = new AlphaBetaAI();
        ab.terminate = false;

        String moves = "444443333311115555676655121234266";

        for(int i = 0; i < moves.length(); i++){
            b.makeMove(Character.getNumericValue(moves.charAt(i))-1);
            g.makeMove(Character.getNumericValue(moves.charAt(i)) - 1);
        }

        b.display();
        b.findThreats().display();

        System.out.println(ab.smartEvaluate(b));
//
//        for(int i = 0; i < 7; i++){
//            b.makeMove(i);
//            System.out.println(ab.bitValuate(b)+ab.smartEvaluate(b));
//            b.unMakeMove();
//        }

        //ab.states.clear();
        //System.out.println(ab.negaMaxAB(12, b, 1) + 1);

        //b.makeMove(4);

        //System.out.println(ab.evaluate(b));
    }
}
