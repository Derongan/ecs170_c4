/**
 * Created by Derongan on 2/23/2016.
 */
public class NegaMaxTesterBit1 extends NegaMaxAIBit {
    public NegaMaxTesterBit1(){
    }
    @Override
    protected int evaluate(BitBoard state){
        return all4sEval(state);
    }
}
