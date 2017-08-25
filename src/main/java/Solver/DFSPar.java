package Solver;//####[1]####
//####[1]####
import java.util.Iterator;//####[3]####
import java.util.Set;//####[4]####
import java.util.stream.IntStream;//####[5]####
import CommonInterface.ISearchState;//####[7]####
import Graph.EdgeWithCost;//####[8]####
import Graph.Vertex;//####[9]####
import Graph.Graph;//####[10]####
import lombok.Data;//####[12]####
import pt.runtime.ParaTask;//####[13]####
import pt.runtime.TaskID;//####[14]####
import java.util.concurrent.ConcurrentLinkedQueue;//####[16]####
import java.util.concurrent.CopyOnWriteArrayList;//####[17]####
//####[17]####
//-- ParaTask related imports//####[17]####
import pt.runtime.*;//####[17]####
import java.util.concurrent.ExecutionException;//####[17]####
import java.util.concurrent.locks.*;//####[17]####
import java.lang.reflect.*;//####[17]####
import pt.runtime.GuiThread;//####[17]####
import java.util.concurrent.BlockingQueue;//####[17]####
import java.util.ArrayList;//####[17]####
import java.util.List;//####[17]####
//####[17]####
public class DFSPar extends DFSolver {//####[19]####
    static{ParaTask.init();}//####[19]####
    /*  ParaTask helper method to access private/protected slots *///####[19]####
    public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[19]####
        if (m.getParameterTypes().length == 0)//####[19]####
            m.invoke(instance);//####[19]####
        else if ((m.getParameterTypes().length == 1))//####[19]####
            m.invoke(instance, arg);//####[19]####
        else //####[19]####
            m.invoke(instance, arg, interResult);//####[19]####
    }//####[19]####
//####[21]####
    public DFSPar(Graph<Vertex, EdgeWithCost<Vertex>> graph, int processorCount, int parNumber) {//####[21]####
        super(graph, processorCount, parNumber);//####[22]####
    }//####[23]####
//####[27]####
    @Override//####[27]####
    public void doSolve() {//####[27]####
        SearchState.initialise(graph);//####[28]####
        SearchState nullstate = new SearchState();//####[29]####
        calculatingnNextLayerSearchingState(nullstate);//####[30]####
        ConcurrentLinkedQueue<SearchState> A = parallelTask;//####[31]####
        ParaTask.setThreadPoolSize(ParaTask.ThreadPoolType.ONEOFF, praNumber);//####[33]####
        TaskIDGroup g = this.buildTask(parallelTask);//####[35]####
        try {//####[37]####
            g.waitTillFinished();//####[38]####
        } catch (Exception e) {//####[39]####
            e.printStackTrace();//####[40]####
        }//####[41]####
        scheduleVertices(currBestState);//####[43]####
    }//####[44]####
//####[46]####
    private static volatile Method __pt__buildTask_ConcurrentLinkedQueueSearchState_method = null;//####[46]####
    private synchronized static void __pt__buildTask_ConcurrentLinkedQueueSearchState_ensureMethodVarSet() {//####[46]####
        if (__pt__buildTask_ConcurrentLinkedQueueSearchState_method == null) {//####[46]####
            try {//####[46]####
                __pt__buildTask_ConcurrentLinkedQueueSearchState_method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__buildTask", new Class[] {//####[46]####
                    ConcurrentLinkedQueue.class//####[46]####
                });//####[46]####
            } catch (Exception e) {//####[46]####
                e.printStackTrace();//####[46]####
            }//####[46]####
        }//####[46]####
    }//####[46]####
    public TaskIDGroup<Void> buildTask(ConcurrentLinkedQueue<SearchState> states) {//####[46]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[46]####
        return buildTask(states, new TaskInfo());//####[46]####
    }//####[46]####
    public TaskIDGroup<Void> buildTask(ConcurrentLinkedQueue<SearchState> states, TaskInfo taskinfo) {//####[46]####
        // ensure Method variable is set//####[46]####
        if (__pt__buildTask_ConcurrentLinkedQueueSearchState_method == null) {//####[46]####
            __pt__buildTask_ConcurrentLinkedQueueSearchState_ensureMethodVarSet();//####[46]####
        }//####[46]####
        taskinfo.setParameters(states);//####[46]####
        taskinfo.setMethod(__pt__buildTask_ConcurrentLinkedQueueSearchState_method);//####[46]####
        taskinfo.setInstance(this);//####[46]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[46]####
    }//####[46]####
    public TaskIDGroup<Void> buildTask(TaskID<ConcurrentLinkedQueue<SearchState>> states) {//####[46]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[46]####
        return buildTask(states, new TaskInfo());//####[46]####
    }//####[46]####
    public TaskIDGroup<Void> buildTask(TaskID<ConcurrentLinkedQueue<SearchState>> states, TaskInfo taskinfo) {//####[46]####
        // ensure Method variable is set//####[46]####
        if (__pt__buildTask_ConcurrentLinkedQueueSearchState_method == null) {//####[46]####
            __pt__buildTask_ConcurrentLinkedQueueSearchState_ensureMethodVarSet();//####[46]####
        }//####[46]####
        taskinfo.setTaskIdArgIndexes(0);//####[46]####
        taskinfo.addDependsOn(states);//####[46]####
        taskinfo.setParameters(states);//####[46]####
        taskinfo.setMethod(__pt__buildTask_ConcurrentLinkedQueueSearchState_method);//####[46]####
        taskinfo.setInstance(this);//####[46]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[46]####
    }//####[46]####
    public TaskIDGroup<Void> buildTask(BlockingQueue<ConcurrentLinkedQueue<SearchState>> states) {//####[46]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[46]####
        return buildTask(states, new TaskInfo());//####[46]####
    }//####[46]####
    public TaskIDGroup<Void> buildTask(BlockingQueue<ConcurrentLinkedQueue<SearchState>> states, TaskInfo taskinfo) {//####[46]####
        // ensure Method variable is set//####[46]####
        if (__pt__buildTask_ConcurrentLinkedQueueSearchState_method == null) {//####[46]####
            __pt__buildTask_ConcurrentLinkedQueueSearchState_ensureMethodVarSet();//####[46]####
        }//####[46]####
        taskinfo.setQueueArgIndexes(0);//####[46]####
        taskinfo.setIsPipeline(true);//####[46]####
        taskinfo.setParameters(states);//####[46]####
        taskinfo.setMethod(__pt__buildTask_ConcurrentLinkedQueueSearchState_method);//####[46]####
        taskinfo.setInstance(this);//####[46]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[46]####
    }//####[46]####
    public void __pt__buildTask(ConcurrentLinkedQueue<SearchState> states) {//####[46]####
        SearchState currentState = null;//####[47]####
        while ((currentState = states.poll()) != null) //####[48]####
        {//####[48]####
            long id = Thread.currentThread().getId();//####[49]####
            System.out.println("[thread " + id + "]");//####[50]####
            solving(currentState);//####[51]####
        }//####[52]####
    }//####[53]####
//####[53]####
}//####[53]####
