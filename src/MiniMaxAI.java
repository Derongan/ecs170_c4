import java.util.Random;

public class MiniMaxAI extends AIModule {

    private int me;
    private final Random r = new Random();

    private int bestAtLevel;

    private int mDepth;

    private int highest = 6;

    @Override
    public void getNextMove(GameStateModule game) {
        chosenMove = 4;

        me = game.getActivePlayer();

        int depth = 4;//highest-1;

        //do{
            mDepth = depth;
            miniMax(depth, game, true);

            if(!terminate)
                chosenMove = bestAtLevel;
            //depth++;
        //}while(!terminate);

        highest = depth-1;

        //System.out.println(depth);

    }

    private int evaluate(GameStateModule state){
        /*int who;
        for(int i = 0; i < state.getWidth();i++){
            for(int j = 0; j < state.getHeight(); j++){
                who = state.getAt(i,j);
            }
        }*/
        return 0;
    }

    private int negaMax(int depth, GameStateModule state, int maxPlayer){
        if(terminate) {
            return 0;
        }
        if(state.isGameOver()){
            if(state.getWinner() == 0){
                return 0;
            }
            else if(state.getWinner() == me){
                return maxPlayer * 10000;
            }
            return maxPlayer * -10000;
        }
        else if(depth == 0){
            return maxPlayer * evaluate(state);
        }
        double best = Double.NEGATIVE_INFINITY;
        int h = 0;
        for(int i = 0; i < state.getWidth(); i++){
            if(state.canMakeMove(i)){
                state.makeMove(i);
                int v = -negaMax(depth - 1, state.copy(), -maxPlayer);

                if(best < v){
                    if(mDepth == depth)
                        bestAtLevel = i;
                    best = v;
                }
                state.unMakeMove();
            }
        }

        if(h == 6){
            System.out.println("HOPELESS");
        }

        return (int)best;
    }

    private int miniMax(int depth, GameStateModule state, boolean maxPlayer){
        if(terminate) {
            return -1;
        }
        if(state.isGameOver()){
            if(state.getWinner() == 0){
                return 0;
            }
            else if(state.getWinner() == me){
                return 10000;
            }
            return -10000;
        }
        else if(depth == 0){
            return evaluate(state);
        }
        double best;

        int h = 0;
        if(maxPlayer){
            best = Double.NEGATIVE_INFINITY;
            for(int i = 0; i < state.getWidth(); i++){
                if(state.canMakeMove(i)){
                    state.makeMove(i);
                    int v = miniMax(depth - 1, state.copy(), !maxPlayer);
                    if(v == -10000)
                        h++;
                    if(best < v){
                        if(mDepth == depth)
                            bestAtLevel = i;
                        best = v;
                    }
                    state.unMakeMove();
                }
            }
        }
        else{
            best = Double.POSITIVE_INFINITY;
            for(int i = 0; i < state.getWidth(); i++){
                if(state.canMakeMove(i)){
                    state.makeMove(i);
                    int v = miniMax(depth - 1, state.copy(), !maxPlayer);
                    if(best > v){
                        best = v;
                    }
                    state.unMakeMove();
                }
            }
        }
        if(mDepth == depth)
            System.out.println(h);
        return (int)best;
    }
}
