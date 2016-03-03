

public class minimax_kearney1 extends AIModule{
    private final int WORST = -10000;
    private final int BEST = 10000;
    private final int startDepth = 1;
    private final int NOHOPE = -2000;

    //All 69 possible winning groups
    public final static long[] winGroups = {
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

    private final int[] defaultOrder = {3,2,4,5,1,0,6};

    @Override
    public void getNextMove(GameStateModule game) {
        chosenMove = 4;

        int depth = startDepth;

        int move;

        BitBoard board = new BitBoard(game);

        int who = board.getActivePlayer() == 2 ? -1 : 1;


        while (!terminate) {
            move = negaMaxAB(depth, board, who);

            if (!terminate && move != NOHOPE)
                chosenMove = move;
            depth++;
        }

        //System.out.println("Max reached depth " + String.valueOf(depth - 1) + " by player " + String.valueOf(us));
    }

    //General evaluation function for earlier game
    private int bitValuate(BitBoard state){
        long p1b = state.board[0];
        long p2b = state.board[1];

        int ret = 0;

        for(int i = 0; i < winGroups.length; i++){
            long board = winGroups[i];
            //If they are the sole owners of this winslot
            if((p1b & board) == 0){
                switch(Long.bitCount(p2b & board)){
                    case 1:
                        ret -= 1;
                        break;
                    case 2:
                        ret -= 2;
                        break;
                    case 3:
                        ret -= 3;
                        break;
                }
            }
            //If we are the sole owners of this winslot
            else if((p2b & board) == 0){
                switch(Long.bitCount(p1b & board)){
                    case 1:
                        ret += 1;
                        break;
                    case 2:
                        ret += 2;
                        break;
                    case 3:
                        ret += 3;
                        break;
                }
            }
        }

        return ret;
    }

    private int smartEvaluate(BitBoard state){
        BitBoard threatBoard = state.findThreats();

        BitBoard threat1 = new BitBoard();
        BitBoard threat2 = new BitBoard();

        threat1.board[0] = threatBoard.board[0];
        threat1.board[1] = threatBoard.board[1];

        long allThreats = threatBoard.board[0] | threatBoard.board[1];

        int max = Long.bitCount(allThreats);  //Number of threats we have
        int ts = 0;

        int lastCol = -1;

        int odd1 = 0;
        int even1 = 0;
        int odd2 = 0;
        int even2 = 0;

        boolean ignoreEven1 = false;
        boolean ignoreOdd1 = false;
        boolean ignoreEven2 = false;
        boolean ignoreOdd2 = false;

        for(int i = 0; i < max; i++) {    //For every thread
            int shift = Long.numberOfTrailingZeros(allThreats);
            allThreats >>= shift;
            ts += shift;
            int x = ts/7;
            int y = ts%7;
            if(state.getHeightAt(x) == y){
                if(state.getActivePlayer() == 1) {
                    return BEST;
                }
                else{
                    return WORST;
                }
            }


            //If two threats are in the same column, ignore higher threats from other player with opposite parity
            if(lastCol == x){
                if(threat1.getAt(x,y) == 1){
                    if(!ignoreOdd1 && y % 2 == 0) {//Odd
                        odd1++;
                        ignoreEven2 = true;
                    }
                    else if (!ignoreEven1) {//Even
                        even1++;
                        ignoreOdd2 = true;
                    }
                }
                if(threat2.getAt(x,y) == -1){
                    if(!ignoreOdd2 && y % 2 == 0) {//Odd
                        odd2++;
                        ignoreEven1 = true;
                    }
                    else if(!ignoreEven2) {//Even
                        even2++;
                        ignoreOdd1 = true;
                    }
                }
            }
            else{
                ignoreOdd1 = ignoreOdd2 = ignoreEven2 = ignoreEven1 = false;
                if(threat1.getAt(x,y) == 1){
                    if(y % 2 == 0) {//Odd
                        odd1++;
                        ignoreEven2 = true;
                    }
                    else {//Even
                        even1++;
                        ignoreOdd2 = true;
                    }
                }
                else{
                    if(y % 2 == 0) {//Odd
                        odd2++;
                        ignoreEven1 = true;
                    }
                    else {//Even
                        even2++;
                        ignoreOdd1 = true;
                    }
                }
            }

            ts++;
            allThreats >>= 1;

            lastCol = x;
        }

        if(odd1 > odd2)
            return 100;
        else if(odd2 > odd1+1 || (odd2 == odd1 && even2 > 0))
            return -100;
        return 0;
    }

    private int evaluate(BitBoard state){
        return smartEvaluate(state)+bitValuate(state);
    }

    private int negaMaxAB(int depth, BitBoard state, int who){
        int max = WORST;
        int score;

        int move = 4;

        int h = 0;

        int alpha = WORST;
        int beta = BEST;


        //Replaced call to getwidth with 7
        for(int i = 0; i < 7; i++){
            int x = defaultOrder[i];
            if(state.canMakeMove(x)) {
                state.makeMove(x);
                score = -negaMaxABHelper(depth - 1, state, -who);
                state.unMakeMove();
            }
            else{
                score = WORST-1;
            }
            if(score > max) {
                max = score;
                move = x;
            }
            if(score <= WORST)
                h++;

            alpha = Math.max(alpha, score);
            if (alpha >= beta)
                break;
        }


        //state.getWidth() -> 7
        if(h == 7){
            return NOHOPE;
        }
        return move;
    }

    private int negaMaxABHelper(int depth, BitBoard state, int who){
        int max = WORST;

        if(terminate){
            return 0;
        }
        else if(state.isGameOver()){
            if(state.getWinner() == 0)
                max =  0;
            else
                max = who * (state.getWinner() == 1 ? BEST : WORST);

        }
        else if(depth == 0){
            max = who*evaluate(state);
        }
        else {
            int score;
            //state.getWidth() -> 7
            for (int i = 0; i < 7; i++) {
                int x = defaultOrder[i];
                if (state.canMakeMove(x)) {
                    state.makeMove(x);
                    score = -negaMaxABHelper(depth - 1, state, -who);
                    state.unMakeMove();

                    if(max < score) {
                        max = score;
                        //max = Math.max(score, max);
                    }
                }
            }
        }

        return max;
    }


    //BEGIN NESTED BITBOARD

    public class BitBoard {
        public long board[] = {0L, 0L};
        public int height[] = {0, 7, 14, 21, 28, 35, 42};
        public int moves[] = new int[7 * 6];
        public int togo = 0;


        static final int WIDTH = 7;
        static final int HEIGHT = 6;

        static final int H1 = HEIGHT + 1;
        static final int H2 = HEIGHT + 2;
        static final int SIZE = HEIGHT * WIDTH;
        static final int SIZE1 = H1 * WIDTH;
        static final long ALL1 = (1L << SIZE1) - 1L; // assumes SIZE1 < 63
        static final int COL1 = (1 << H1) - 1;
        static final long BOTTOM = ALL1 / COL1; // has bits i*H1 set
        static final long TOP = BOTTOM << HEIGHT;

        int winner = -1;

        public BitBoard() {

        }

        public BitBoard(GameStateModule g) {
            for (int i = 0; i < 7; i++) {
                for (int j = 0; j < 6; j++) {
                    int who = g.getAt(i, j);
                    setAt(i, j, who);
                }
            }

            for (int i = 0; i < 7; i++) {
                height[i] = g.getHeightAt(i) + i * 7;
            }

            togo = g.getCoins();
        }

        public void unMakeMove() {
            int move = moves[--togo];   //Get column to undo
            board[togo & 1] ^= 1L << --height[move]; //Undo it
            winner = -1;
        }

        public BitBoard copy() {
            BitBoard b = new BitBoard();
            b.board[0] = board[0];
            b.board[1] = board[1];
            b.togo = togo;
            System.arraycopy(height, 0, b.height, 0, 7);
            System.arraycopy(moves, 0, b.moves, 0, 7 * 6);
            b.winner = winner;
            return b;
        }

        public boolean canMakeMove(int move) {
            return height[move] < move * 7 + 6;
        }

        public void makeMove(int move) {
            board[togo & 1] ^= 1L << height[move]++;
            moves[togo++] = move;
            computeVictory();
        }

        public int getWinner() {
            return winner;
        }

        public int getActivePlayer() {
            return (togo & 1) == 1L ? 2 : 1;
        }

        public boolean isGameOver() {
            return winner != -1;
        }

        //Taken from https://tromp.github.io/c4/Connect4.java
        public void computeVictory() {
            int curp = (togo + 1) & 1;
            long b = board[curp];
            curp = (curp == 1L) ? 2 : 1;
            long y = b & (b >> 6);
            if ((y & (y >> 2 * 6)) != 0) { // check diagonal \
                winner = curp;
                return;
            }
            y = b & (b >> 7);
            if ((y & (y >> 2 * 7)) != 0) { // check horizontal -
                winner = curp;
                return;
            }
            y = b & (b >> 8); // check diagonal /
            if ((y & (y >> 2 * 8)) != 0) {
                winner = curp;
                return;
            }
            y = b & (b >> 1); // check vertical |
            if ((y & (y >> 2)) != 0) {
                winner = curp;
                return;
            }

            if (togo == 42)
                winner = 0;
        }

        public int getAt(int x, int y) {
            return ((int) (((board[0] >>> (7 * x + y)) & 1) - ((board[1] >>> (7 * x + y)) & 1)));
        }

        public int getHeightAt(int x) {
            return height[x] - (7 * x);
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

        public void setAt(int x, int y, int who) {
            if (who == 0)
                return;
            board[who - 1] ^= 1L << (x * 7 + y);
        }

        @Override
        public boolean equals(Object o) {
            return board[0] == ((BitBoard) o).board[0] && board[1] == ((BitBoard) o).board[1];
        }

        public BitBoard findThreats() {
            BitBoard threats = new BitBoard();
            for (int i = 0; i < winGroups.length; i++) {
                long b = winGroups[i];
                //If they are the sole owners of this winslot
                if ((board[0] & b) == 0) {
                    if (Long.bitCount(board[1] & b) == 3) {
                        threats.board[1] |= ~board[1] & b;
                    }
                }
                //If we are the sole owners of this winslot
                else if ((board[1] & b) == 0) {
                    if (Long.bitCount(board[0] & b) == 3) {
                        threats.board[0] |= ~board[0] & b;
                    }
                } else {
                    continue;
                }
            }

            return threats;
        }

        @Override
        public int hashCode() {
            return (int) (board[0] + board[1] + (togo & 1));
        }
    }
}
