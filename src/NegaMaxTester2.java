/**
 * Created by Derongan on 2/23/2016.
 */
public class NegaMaxTester2 extends NegaMaxAI {
    @Override
    protected int evaluate(GameStateModule state){
        if(state.getCoins() == 0){
            System.out.println("FIRST MOVE");
            if(state.getAt(4,7) != 0)
                return BEST;
        }
        else{
            return connectedEval(state);
        }
        return 0;
    }
}
