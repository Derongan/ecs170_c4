import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.util.Arrays;
import java.util.HashMap;

public class AlphaBetaAI extends AIModule{
    protected final int WORST = -10000;
    protected final int BEST = 10000;

    private int us;

    private int lastDepth = 4;
    protected int startDepth = 1;

    private final int NOHOPE = -2000;

    private int[] defaultOrder = {3,2,4,5,1,0,6};

    private int[] bestAtLevel = new int[50];

    public HashMap<BitBoard, Integer> states = new HashMap<>();

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

        if(board.getCoins() == 0){
            chosenMove = 3;
            return;
        }
        if(board.getCoins() == 2 && board.getHeightAt(3)==2){
            chosenMove = 3;
            return;
        }

        while(!terminate){
            //System.out.println("------");
            move = negaMaxAB(depth, board, 1);
            if(!terminate && move != NOHOPE)
                chosenMove = move;
            depth++;
            states.clear();
        }

        System.out.println("Max reached depth "+String.valueOf(depth-1)+" by player "+String.valueOf(us));
        lastDepth = depth-1;
    }

    public int simpleEval(BitBoard state){
        int ret;
        long b = state.board[state.togo&1];
        long b2 = state.board[(state.togo+1)&1];
        //Lets simply check vertical 3 in a rows for both players
        return Long.bitCount(b & (b >> 2)) - Long.bitCount(b2 & (b2 >> 2));
    }

    public int[] order(int depth){
        /*if(bestAtLevel[depth] == -1)
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
        }*/
        //return orering
        return defaultOrder;
    }

    protected int bitValuate(BitBoard state){
        long[] wins = BitBoard.winGroups;

        long ourboard = state.board[state.togo&1];
        long otherboard = state.board[~state.togo&1];

        int ret = 0;

        int sign = 0;

        int count;

        for(long board : wins){
            //If they are the sole owners of this winslot
            if((ourboard & board) == 0){
                count = Long.bitCount(otherboard & board);
                sign = -1;
            }
            //If we are the sole owners of this winslot
            else if((otherboard & board) == 0){
                count = Long.bitCount(ourboard & board);
                sign = 1;
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
        return ret;
    }

    protected int evaluate(BitBoard state){
        return bitValuate(state);
    }

    public int negaMaxAB(int depth, BitBoard state, int who){
        int max = WORST;
        int score;

        int move = 4;

        int h = 0;

        int alpha = WORST;
        int beta = BEST;

        int[] ordering = order(depth);

        for(int i = 0; i < state.getWidth(); i++){
            int x = ordering[i];
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

        if(depth == 3){
            int t = 0;
        }

        if(terminate){
            max =  42;
        }
        else if(state.isGameOver()){
            if(state.getWinner() == 0)
                max =  0;
            else
                max =  WORST;
        }
        else if(depth == 0){
            max = evaluate(state);
        }
        else {
            int score;
            for (int i = 0; i < state.getWidth(); i++) {
                int x = defaultOrder[i];
                if (state.canMakeMove(x)) {
                    state.makeMove(x);
                    score = -negaMaxABHelper(depth - 1, state, -who, -beta, -alpha);
                    state.unMakeMove();

                    if(score > max){
                        max = score;
                        bestMove = x;
                    }

                    alpha = Math.max(alpha, score);
                    if (alpha >= beta)
                        break;
                }
            }
        }

        /*if(depth > 4)
            states.put(state.copy(), max);*/

        bestAtLevel[depth] = bestMove;

        return max;
    }
}
