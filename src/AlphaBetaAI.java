import java.util.Arrays;
import java.util.HashMap;
import java.util.InputMismatchException;

public class AlphaBetaAI extends AIModule{
    protected final int WORST = -10000;
    protected final int BEST = 10000;

    private int us;

    private int lastDepth = 4;
    protected int startDepth = 1;

    private final int NOHOPE = -2000;

    private final int[] defaultOrder = {3,2,4,5,1,0,6};

    private int[] bestAtLevel = new int[50];

    public final HashMap<BitBoard, Integer> states = new HashMap<>();

    private int maxDepth;

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

        Arrays.fill(bestAtLevel, -1);

        chosenMove = 4;

        int depth = startDepth;

        int move;

        System.out.println("Moving");

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
            if(!terminate && move != NOHOPE)
                chosenMove = move;
            depth++;
            states.clear();
        }

        System.out.println("Max reached depth "+String.valueOf(depth-1)+" by player "+String.valueOf(us));
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

        for(long board : wins){
            //If they are the sole owners of this winslot
            if((p1b & board) == 0){
                count = Long.bitCount(p2b & board);
                sign = 1;
            }
            //If we are the sole owners of this winslot
            else if((p2b & board) == 0){
                count = Long.bitCount(p1b & board);
                sign = -1;
            }
            else{
                continue;
            }
            switch(count){
                case 1:
                    ret += sign*2;
                    break;
                case 2:
                    ret += sign*10;
                    break;
                case 3:
                    ret += sign*50;
                    break;
                case 4:
                    return sign * BEST;
            }
        }
        return -ret;
    }

    protected int evaluate(BitBoard state){
        return bitValuate(state);
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
                state.makeMove(x);
                score = -negaMaxABHelper(depth - 1, state, -who, -beta, -alpha);
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
        return move;
    }

    private int negaMaxABHelper(int depth, BitBoard state, int who, int alpha, int beta){
        int max = WORST;

        /*if(depth > 4 && states.get(state) != null){
            return states.get(state);
        }*/

        //The column that was best
        int bestMove = 0;

        //Ordering to use
        int[] ordering = order(depth);

        if(terminate){
            return 0;
        }
        else if(state.isGameOver()){
            if(state.getWinner2() == 0)
                max =  0;
            else if(state.getWinner2() == who)
                max = BEST;
            else
                max = WORST;
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

        /*if(depth > 4)
            states.put(state.copy(), max);*/

        bestAtLevel[maxDepth-depth] = bestMove;

        return max;
    }
}
