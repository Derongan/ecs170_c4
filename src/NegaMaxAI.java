import java.util.HashMap;

public class NegaMaxAI extends AIModule{
    protected final int WORST = -10000;
    protected final int BEST = 10000;

    private int us;

    private int lastDepth = 4;
    protected int startDepth = 1;

    private final int NOHOPE = -2000;

    private int[] defaultOrder = {3,5,1,0,6,2,4};

    private HashMap<GameStateModule, Integer> states = new HashMap<>();

    @Override
    public void getNextMove(GameStateModule game) {
        states.clear();
        us = game.getActivePlayer();

        chosenMove = 4;

        int depth = startDepth;

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

    protected int all4sEval(GameStateModule state) {
        /*if(states.get(state) != null){
            System.out.println("Hash hit");
            return states.get(state);
        }*/
        int val = 0;
        //There are three possible wins in each row
        for (int row = 0; row < state.getHeight(); row++){
            for (int i = 0; i < state.getWidth()-3; i++) {
                int p0 = formatPlayer5(state.getAt(i, row));
                int p1 = formatPlayer5(state.getAt(i, row));
                int p2 = formatPlayer5(state.getAt(i, row));
                int p3 = formatPlayer5(state.getAt(i, row));
                int sum = p0 + p1 + p2 + p3;
                if (sum <= 4)
                    switch(sum){
                        case 1:
                            val += 1;
                            break;
                        case 2:
                            val += 10;
                            break;
                        case 3:
                            val += 50;
                            break;
                    }
                if (sum % 5 == 0)
                    switch(sum){
                        case 5:
                            val += -1;
                            break;
                        case 10:
                            val += -10;
                            break;
                        case 15:
                            val += -50;
                            break;
                    }
            }
        }

        for (int col = 0; col < state.getWidth(); col++){
            for (int i = 0; i < state.getHeight()-3; i++) {
                int p0 = formatPlayer5(state.getAt(col, i));
                int p1 = formatPlayer5(state.getAt(col, i));
                int p2 = formatPlayer5(state.getAt(col, i));
                int p3 = formatPlayer5(state.getAt(col, i));
                int sum = p0 + p1 + p2 + p3;
                if (sum <= 4)
                    switch(sum){
                        case 1:
                            val += 1;
                            break;
                        case 2:
                            val += 10;
                            break;
                        case 3:
                            val += 50;
                            break;
                    }
                if (sum % 5 == 0)
                    switch(sum){
                        case 5:
                            val += -1;
                            break;
                        case 10:
                            val += -10;
                            break;
                        case 15:
                            val += -50;
                            break;
                    }
            }
        }
        int rv = val+ 16*formatPlayer(state.getActivePlayer());
        //states.put(state, rv);
        return rv;
    }

    //Takes a player code and changes it so we can do addition on
    //player codes to see who has what in a row
    private int formatPlayer5(int id){
        if(id == 0){
            return 0;
        }
        else if(id == 1){
            return 1;
        }
        else
            return 5;
    }

    //Takes a player code and changes it to +/-1
    protected int formatPlayer(int id){
        if(id == 0){
            return 0;
        }
        else if(id == 1){
            return 1;
        }
        else
            return -1;
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

    public int negaMax(int depth, GameStateModule state, int who){
        int max = Integer.MIN_VALUE;
        int score;

        int move = 4;

        int h = 0;

        for(int i = 0; i < state.getWidth(); i++){
            int x = defaultOrder[i];
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
            if(score <= WORST)
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
            int x = defaultOrder[i];
            if(state.canMakeMove(x)) {
                state.makeMove(x);
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

    public static void main(String[] args){

        //NegaMaxTester1 nb = new NegaMaxTester1();
        NegaMaxTesterBit1 b = new NegaMaxTesterBit1();

        //nb.terminate = false;
        b.terminate = false;

        GameState_General g = new GameState_General(7,6);

        GameState_General g1 = g.copy();
        GameState_General g2 = g.copy();
        /*
        long startTime = System.currentTimeMillis();
        nb.negaMax(8, g1, 1);
        long endTime = System.currentTimeMillis();

        System.out.println("That took " + (endTime - startTime) + " milliseconds");*/

        long diff = 0;

        for(int i = 0; i < 20; i++) {
            long startTime = System.currentTimeMillis();
            BitBoard bb = new BitBoard(g1);
            b.states.clear();
            int r = b.negaMax(9, bb, 1);
            long endTime = System.currentTimeMillis();

            diff += endTime - startTime;
            System.out.println("DONE");
        }

        System.out.println("That took " + diff/20.0 + " milliseconds on average");
    }
}
