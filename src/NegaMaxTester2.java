/**
 * Created by Derongan on 2/23/2016.
 */
public class NegaMaxTester2 extends NegaMaxAI {
    @Override
    protected int evaluate(GameStateModule state){
        return all4sEval(state);
    }
}
