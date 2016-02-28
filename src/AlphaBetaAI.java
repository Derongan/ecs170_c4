import java.util.HashMap;

public class AlphaBetaAI extends AIModule{
    protected final int WORST = -10000;
    protected final int BEST = 10000;

    private int us;

    private int lastDepth = 4;
    protected int startDepth = 1;

    private final int NOHOPE = -2000;

    private int[] defaultOrder = {3,2,4,5,1,0,6};

    public HashMap<BitBoard, Integer> states = new HashMap<>();

    @Override
    public void getNextMove(GameStateModule game) {
        states.clear();
        us = game.getActivePlayer();

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
        System.out.println(String.valueOf(depth)+" by player "+String.valueOf(us));
        lastDepth = depth-1;
    }

    public int simpleEval(BitBoard state){
        int ret;
        long b = state.board[state.togo&1];
        long b2 = state.board[(state.togo+1)&1];
        //Lets simply check vertical 3 in a rows for both players
        return Long.bitCount(b & (b >> 2)) - Long.bitCount(b2 & (b2 >> 2));
    }

    protected int evaluate(BitBoard state){
        return 0;
        //return simpleEval(state);
    }

    public int negaMaxAB(int depth, BitBoard state, int who){
        int max = Integer.MIN_VALUE;
        int score;

        int move = 4;

        int h = 0;

        int alpha = WORST-1;
        int beta = BEST+1;

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
        int max = Integer.MIN_VALUE;

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

                    max = Math.max(score, max);
                    alpha = Math.max(alpha, score);
                    if (alpha >= beta)
                        break;
                }
            }
        }

        /*if(depth > 4)
            states.put(state.copy(), max);*/

        return max;
    }
}
