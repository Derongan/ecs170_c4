import java.util.Arrays;
import java.util.HashMap;

public class AlphaBetaAI extends AIModule{
    protected final int WORST = -10000;
    protected final int BEST = 10000;

    private int us;


    protected int startDepth = 8;
    private int lastDepth = startDepth +2;

    public int tuning = 9;

    private final int NOHOPE = -2000;

    private final int[] defaultOrder = {3,2,4,5,1,0,6};

    private int[] bestAtLevel = new int[50];

    public final HashMap<BitBoard, Integer> states = new HashMap<>();

    public int maxDepth;

    public final HashMap<BitBoard, Integer> knownMoves = new HashMap<>();

    public AlphaBetaAI(){
        BitBoard b = new BitBoard();
        knownMoves.put(b.copy(), 3);
        b.makeMove(3);
        knownMoves.put(b.copy(), 3);
        b.makeMove(3);
        knownMoves.put(b.copy(), 3);
        b.makeMove(3);
        knownMoves.put(b.copy(), 3);
        b.makeMove(3);
        knownMoves.put(b.copy(), 3);
        b.makeMove(3);
        b.makeMove(2);
        knownMoves.put(b.copy(), 2);
    }

    @Override
    public void getNextMove(GameStateModule game) {
        states.clear();
        us = game.getActivePlayer();

        //Arrays.fill(bestAtLevel, -1);

        chosenMove = 4;

        int depth = lastDepth-2;

        int move;

        //System.out.println("Moving");

        BitBoard board = new BitBoard(game);

        //board.display();

        if(knownMoves.get(board) != null){
            chosenMove = knownMoves.get(board);
            return;
        }

        int who = board.getActivePlayer() == 2 ? -1 : 1;


        while(!terminate){
            //System.out.println("------");
            move = negaMaxAB(depth, board, who);
            //System.out.println("AT depth "+depth+" move " + move + " was chosen" +" who" + who);
            //board.display();
            if(!terminate && move != NOHOPE)
                chosenMove = move;
            depth++;
            states.clear();
        }

        System.out.println("Max reached depth " + String.valueOf(depth - 1) + " by player " + String.valueOf(us));
        //System.out.println("This means we looked ahead " + String.valueOf((depth - 1) / 2) + " moves");
        lastDepth = depth-1;
    }

    public int simpleEval(BitBoard state){
        long b = state.board[state.togo&1];
        long b2 = state.board[(state.togo+1)&1];
        //Lets simply check vertical 3 in a rows for both players
        return Long.bitCount(b & (b >> 2)) - Long.bitCount(b2 & (b2 >> 2));
    }

    public int[] order(int depth){
        depth = maxDepth-depth;
        if(bestAtLevel[depth] == -1)
            return defaultOrder;

        int x = bestAtLevel[depth];

        int[] ordering = new int[7];

        //Set first to one that prunes best
        ordering[0] = x;

        //Place to take from our default array
        int pos = 0;

        //For position 1 to 6
        for(int i = 1; i < 7; i++){
            //If our default array at i is the best, we already inserted it so skip!
            if(x == defaultOrder[pos])
                pos++;
            ordering[i] = defaultOrder[pos];
            pos++;
        }
        return ordering;
        //return defaultOrder;
    }

    protected int bitValuate(BitBoard state){
        long[] wins = BitBoard.winGroups;

        long p1b = state.board[0];
        long p2b = state.board[1];

        int ret = 0;

        int sign;

        int count;

        for(int i = 0; i < BitBoard.winGroups.length; i++){
            long board = BitBoard.winGroups[i];
            //If they are the sole owners of this winslot
            if((p1b & board) == 0){
                count = Long.bitCount(p2b & board);
                sign = -1;
            }
            //If we are the sole owners of this winslot
            else if((p2b & board) == 0){
                count = Long.bitCount(p1b & board);
                sign = 1;
            }
            else{
                continue;
            }

            ret += sign*(2 << count);
        }

        return ret;
    }

    protected int threatEval(BitBoard state){
        BitBoard threatBoard = state.findThreats();

        long b0 = threatBoard.board[0];
        long b1 = threatBoard.board[1];


        long even0 = threatBoard.board[0] &186172425540906L;
        long even1 = threatBoard.board[1] &186172425540906L;
        long odd0 = threatBoard.board[0] & 93086212770453L; //Odd
        long odd1 = threatBoard.board[1] & 93086212770453L; //Odd
        //threatBoard.display();

        int ret = 20*(Long.bitCount(even0) - Long.bitCount(even1)) + 20*(Long.bitCount(odd0) - Long.bitCount(odd1));

        //threatBoard.display();
        //System.out.println("Moves left: "+(42-state.togo));

        //Check threat polarity
        /*threatBoard.board[0] &
        */
        int max;

        //If there are an even number of moves to go it must be Player 1s turn
        int r = (42-state.togo)%2;
            //Check if this player can win

        max = Long.bitCount(threatBoard.board[r]);  //Number of threats we have
        int ts = 0;
        for(int i = 0; i < max; i++) {    //For every thread
            int shift = Long.numberOfTrailingZeros(threatBoard.board[r]);
            threatBoard.board[r] >>= shift;
            ts += shift;
            int x = ts/7;
            int y = ts%7;
            if(state.getHeightAt(x) == y){
                    return BEST * (r == 0 ? 1 : -1);
            }
            ts++;
            threatBoard.board[r] >>= 1;
        }
        return ret;
    }

    protected int oddEvenEval(BitBoard state){
        BitBoard threatBoard = state.findThreats();

        //threatBoard.display();

        long even0 = threatBoard.board[0] &186172425540906L;
        long even1 = threatBoard.board[1] &186172425540906L;
        long odd0 = threatBoard.board[0] & 93086212770453L; //Odd
        long odd1 = threatBoard.board[1] & 93086212770453L; //Odd

        int good0 = 0;
        int good1 = 0;

        for(int i = 0; i < 7; i++){ //For each column
            long odd = odd0 & (0b111111 << (7*i));
            long even = even1 & (0b111111 << (7*i));

            //If we have an odd threat and there is no even threat from the other player below us
            if(odd > 0 && (even == 0 || Long.numberOfTrailingZeros(odd) < Long.numberOfTrailingZeros(even))){
                good0++;
            }

            //If they have an even threat and there is no odd threat from the other player below them
            if(even > 0 && (odd == 0 || Long.numberOfTrailingZeros(even) < Long.numberOfTrailingZeros(odd))){
                good1++;
            }
        }

        return (good0 - good1)*100;
    }

    protected int evaluate(BitBoard state){
        return oddEvenEval(state) + bitValuate(state) + threatEval(state);
    }

    public int negaMaxAB(int depth, BitBoard state, int who){
        maxDepth = depth;

        int max = WORST;
        int score;

        int move = 4;

        int h = 0;

        int alpha = WORST;
        int beta = BEST;


        for(int i = 0; i < state.getWidth(); i++){
            int x = defaultOrder[i];
            if(state.canMakeMove(x)) {
                if(x == 1){
                    int t = 1;
                }
                state.makeMove(x);
                score = -negaMaxABHelper(depth - 1, state, -who, -beta, -alpha);
                //System.out.println(score+" "+x);
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


        if(h == state.getWidth()){
            return NOHOPE;
        }

        //System.out.println("Finished "+depth);
        return move;
    }

    private int negaMaxABHelper(int depth, BitBoard state, int who, int alpha, int beta){
        int max = WORST;

        if(depth + tuning > maxDepth && states.get(state) != null){
            return states.get(state);
        }

        //The column that was best
        int bestMove = 0;

        //Ordering to use
        int[] ordering = order(depth);

        if(terminate){
            return 0;
        }
        else if(state.isGameOver()){
            if(state.getWinner() == 0)
                max =  0;
            else
                //max = state.getWinner() == us ? BEST : WORST;
                max = who * (state.getWinner() == 1 ? BEST : WORST);

        }
        else if(depth == 0){
            max = who*evaluate(state);
        }
        else {
            int score;
            for (int i = 0; i < state.getWidth(); i++) {
                int x = ordering[i];
                if (state.canMakeMove(x)) {
                    state.makeMove(x);
                    score = -negaMaxABHelper(depth - 1, state, -who, -beta, -alpha);
                    state.unMakeMove();

                    if(max < score) {
                        bestMove = x;
                        max = score;
                        //max = Math.max(score, max);
                    }
                    alpha = Math.max(alpha, score);
                    if (alpha >= beta)
                        break;
                }
            }
        }

        if (depth + tuning > maxDepth)
            states.put(state.copy(), max);

        bestAtLevel[maxDepth-depth] = bestMove;

        return max;
    }
}
