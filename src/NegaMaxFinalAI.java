import java.util.Arrays;
import java.util.HashMap;

public class NegaMaxFinalAI extends AIModule{
    protected final int WORST = -10000;
    protected final int BEST = 10000;

    private int us;


    protected int startDepth = 1;
    private int lastDepth = startDepth +2;

    public int tuning = 9;

    private final int NOHOPE = -2000;

    private final int[] defaultOrder = {3,2,4,5,1,0,6};

    public int[] bestAtLevel = new int[50];

    public int maxDepth;

    @Override
    public void getNextMove(GameStateModule game) {
        //states.clear();
        us = game.getActivePlayer();

        //Arrays.fill(bestAtLevel, -1);

        chosenMove = 4;

        int depth = startDepth;//lastDepth-2;

        int move;

        BitBoard board = new BitBoard(game);

        int who = board.getActivePlayer() == 2 ? -1 : 1;


        while (!terminate) {
            //System.out.println("------");
            move = negaMaxAB(depth, board, who);
            //System.out.println("AT depth "+depth+" move " + move + " was chosen" +" who" + who);
            //board.display();
            if (!terminate && move != NOHOPE)
                chosenMove = move;
            depth++;
            //states.clear();
        }

        System.out.println("Max reached depth " + String.valueOf(depth - 1) + " by player " + String.valueOf(us));
        //System.out.println("This means we looked ahead " + String.valueOf((depth - 1) / 2) + " moves");
        lastDepth = depth - 1;
    }

    //General evaluation function for earlier game
    protected int bitValuate(BitBoard state){
        long[] wins = BitBoard.winGroups;

        long p1b = state.board[0];
        long p2b = state.board[1];

        int ret = 0;

        for(int i = 0; i < BitBoard.winGroups.length; i++){
            long board = BitBoard.winGroups[i];
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
            else{
                continue;
            }
        }

        return ret;
    }

    public int smartEvaluate(BitBoard state){
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

        int odd2last = -1;

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

    protected int evaluate(BitBoard state){
        return smartEvaluate(state)+bitValuate(state);
    }

    public int negaMaxAB(int depth, BitBoard state, int who){
        maxDepth = depth;

        int max = WORST;
        int score;

        int move = 4;

        int h = 0;

        int alpha = WORST;
        int beta = BEST;


        //REplaced call to getwidth with 7
        for(int i = 0; i < 7; i++){
            int x = defaultOrder[i];
            if(state.canMakeMove(x)) {
                if(x == 1){
                    int t = 1;
                }
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
            //System.out.println("Loss");
            return NOHOPE;
        }

        //System.out.println("Finished "+depth);
        return move;
    }

    private int negaMaxABHelper(int depth, BitBoard state, int who){
        int max = WORST;

//        if(depth + tuning > maxDepth && states.get(state) != null){
//            return states.get(state);
//        }

        //The column that was best
        int bestMove = 0;

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
                        bestMove = x;
                        max = score;
                        //max = Math.max(score, max);
                    }
                }
            }
        }

        return max;
    }
}
