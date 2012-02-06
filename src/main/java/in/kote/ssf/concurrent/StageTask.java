/*
 * Please refer to http://code.thejo.in/license/
 * for details about source code license.
 */

package in.kote.ssf.concurrent;

/**
 * Every stage will accept Runnable objects to be executed in that stage. <br />
 * <code>StageTask</code> should be extended by every such class.
 * 
 * @see IStage
 * @author Thejo
 */
public abstract class StageTask implements Runnable  {
    protected IStage nextStage;

    public IStage getNextStage() {
        return nextStage;
    }

    public void setNextStage(IStage nextStage) {
        this.nextStage = nextStage;
    }
}
