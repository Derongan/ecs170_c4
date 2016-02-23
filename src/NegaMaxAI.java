/**
 * Created by Derongan on 2/22/2016.
 */
public class NegaMaxAI extends AIModule{
    protected final int WORST = -1000;
    protected final int BEST = 1000;

    private int us;

    private int lastDepth = 4;

    private final int NOHOPE = -2000;

    @Override
    public void getNextMove(GameStateModule game) {
        us = game.getActivePlayer();

        chosenMove = 4;

        int depth = 1;

        int move;

        System.out.println("Moving");

        while(!terminate){
            //System.out.println("------");
            move = negaMax(depth, game, 1);
            if(!terminate && move != NOHOPE)
                chosenMove = move;
            depth++;
        }
        System.out.println(depth);
        lastDepth = depth-1;
    }

    protected int threesEval(GameStateModule state){
        int threes = 0;
        for(int row = 0; row < state.getHeight(); row++){
            int cur = 0;
            int cuther = 0;
            for(int col = 0; col < state.getWidth(); col++){
                if(state.getAt(col, row) == us) {
                    cur++;
                    cuther = 0;
                }
                else if(state.getAt(col,row) == 0) {
                    cur++;
                    cuther++;
                }
                else{
                    cur = 0;
                    cuther++;
                }
                if(cur >= 3){
                    threes++;
                    continue;
                }
                if(cuther >= 3){
                    threes--;
                    continue;
                }
            }
        }

        for(int col = 0; col < state.getWidth(); col++){
            int cur = 0;
            int cuther = 0;
            for(int row = 0; row < state.getHeight(); row++){
                if(state.getAt(col, row) == us) {
                    cur++;
                    cuther = 0;
                }
                else if(state.getAt(col,row) == 0) {
                    cur++;
                    cuther++;
                }
                else{
                    cur = 0;
                    cuther++;
                }
                if(cur >= 3){
                    threes++;
                    continue;
                }
                if(cuther >= 3){
                    threes--;
                    continue;
                }
            }
        }

        return threes;
    }

    protected int connectedEval(GameStateModule state){
        int con = 0;
        int val;
        int add;
        for(int i = 0; i < state.getWidth(); i++){
            for(int j = 0; j < state.getHeight(); j++){
                val = state.getAt(i,j);
                if(val == us)
                    add = 1;
                else
                    add = -1;
                if(i+1 < state.getWidth() && state.getAt(i+1,j) == us){
                    con+=add;
                }
                if(j+1 < state.getHeight() && state.getAt(i,j+1) == us){
                    con+=add;
                }

                if(j+1 < state.getHeight() && i+1 < state.getWidth() && state.getAt(i+1,j+1) == us) {
                    con+=add;
                }

                if(j-1 > 0 && i+1 < state.getWidth() && state.getAt(i+1,j-1) == us) {
                    con+=add;
                }
            }
        }
        return con;
    }

    protected int evaluate(GameStateModule state){
        if(state.getCoins() == 0){
            System.out.println("FIRST MOVE");
            if(state.getAt(4,7) != 0)
                return BEST;
        }
        else{
            //connectedEval(state);
            threesEval(state);
        }
        return 0;
    }

    private int negaMax(int depth, GameStateModule state, int who){
        int max = Integer.MIN_VALUE;
        int score;

        int move = 4;

        int h = 0;

        for(int i = 0; i < state.getWidth(); i++){
            int x = (i+3) % 7;
            if(state.canMakeMove(x)) {
                state.makeMove(x);
                score = -negaMaxHelper(depth - 1, state.copy(), -who);
                state.unMakeMove();
            }
            else{
                score = WORST-1;
            }
            if(score > max) {
                max = score;
                move = x;
            }
            if(score <= -1000)
                h++;
            //System.out.print(String.valueOf(score)+" ");
        }

        //System.out.println();

        //System.out.println(h);
        if(h == state.getWidth()){
            System.out.println("Loss");
            return NOHOPE;
        }
        return move;
    }

    private int negaMaxHelper(int depth, GameStateModule state, int who){
        //This position may be better than a loss
        if(terminate){
            return 42;
        }
        if(state.isGameOver()){
            if(state.getWinner() == 0)
                return 0;
            else
                return WORST;
        }
        if(depth == 0){
            return evaluate(state);
        }
        int max = Integer.MIN_VALUE;
        int score;
        for(int i = 0; i < state.getWidth(); i++){
            if(state.canMakeMove(i)) {
                state.makeMove(i);
                score = -negaMaxHelper(depth - 1, state.copy(), -who);
                state.unMakeMove();
            }
            else{
                score = WORST-1;
            }
            if(score > max)
                max = score;
        }
        return max;
    }
}
