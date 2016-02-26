/**
 * Created by Derongan on 2/23/2016.
 */
public class NegaMaxTesterBit2 extends NegaMaxAIBit {
    @Override
    protected int evaluate(BitBoard state){
        return all4sEval(state);
    }
}
