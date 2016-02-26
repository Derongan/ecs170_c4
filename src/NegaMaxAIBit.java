import java.util.HashMap;

public class NegaMaxAIBit extends AIModule{
    protected final int WORST = -10000;
    protected final int BEST = 10000;

    private int us;

    private int lastDepth = 4;
    protected int startDepth = 1;

    private final int NOHOPE = -2000;

    private int[] defaultOrder = {3,5,1,0,6,2,4};

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
            move = negaMax(depth, board, 1);
            if(!terminate && move != NOHOPE)
                   chosenMove = move;
            depth++;
            states.clear();
        }
        System.out.println(depth);
        lastDepth = depth-1;
    }

    public int simpleEval(BitBoard state){
        int ret;
        long b = state.board[state.togo&1];
        long b2 = state.board[(state.togo+1)&1];
        //Lets simply check vertical 3 in a rows
        return Long.bitCount(b & (b >> 2)) - Long.bitCount(b2 & (b2 >> 2));
    }

    protected int all4sEval(BitBoard state) {
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


    protected int evaluate(BitBoard state){
        return 0;
    }

    public int negaMax(int depth, BitBoard state, int who){
        int max = Integer.MIN_VALUE;
        int score;

        int move = 4;

        int h = 0;

        for(int i = 0; i < state.getWidth(); i++){
            int x = defaultOrder[i];
            if(state.canMakeMove(x)) {
                state.makeMove(x);
                score = -negaMaxHelper(depth - 1, state, -who);
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
            //System.out.println("Loss");
            return NOHOPE;
        }
        return move;
    }

    private int negaMaxHelper(int depth, BitBoard state, int who){
        int max = Integer.MIN_VALUE;

        if(depth > 4 && states.get(state) != null){
            return states.get(state);
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
                    score = -negaMaxHelper(depth - 1, state, -who);
                    state.unMakeMove();
                } else {
                    score = WORST - 1;
                }
                if (score > max)
                    max = score;
            }
        }

        if(depth > 4)
            states.put(state.copy(), max);

        return max;
    }
}
