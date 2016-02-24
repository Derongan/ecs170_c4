/**
 * Created by Derongan on 2/23/2016.
 */
public class NegaMaxTester1 extends NegaMaxAI {
    public NegaMaxTester1(){
    }
    @Override
    protected int evaluate(GameStateModule state){
        return all4sEval(state);
    }
}
