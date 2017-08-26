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
import pt.runtime.CurrentTask;//####[13]####
import pt.runtime.ParaTask;//####[14]####
import pt.runtime.TaskID;//####[15]####
import java.util.concurrent.ConcurrentLinkedQueue;//####[17]####
import java.util.concurrent.CopyOnWriteArrayList;//####[18]####
//####[18]####
//-- ParaTask related imports//####[18]####
import pt.runtime.*;//####[18]####
import java.util.concurrent.ExecutionException;//####[18]####
import java.util.concurrent.locks.*;//####[18]####
import java.lang.reflect.*;//####[18]####
import pt.runtime.GuiThread;//####[18]####
import java.util.concurrent.BlockingQueue;//####[18]####
import java.util.ArrayList;//####[18]####
import java.util.List;//####[18]####
//####[18]####
public class DFSPar extends DFSolver {//####[20]####
    static{ParaTask.init();}//####[20]####
    /*  ParaTask helper method to access private/protected slots *///####[20]####
    public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[20]####
        if (m.getParameterTypes().length == 0)//####[20]####
            m.invoke(instance);//####[20]####
        else if ((m.getParameterTypes().length == 1))//####[20]####
            m.invoke(instance, arg);//####[20]####
        else //####[20]####
            m.invoke(instance, arg, interResult);//####[20]####
    }//####[20]####
//####[22]####
    public DFSPar(Graph<Vertex, EdgeWithCost<Vertex>> graph, int processorCount, int parNumber) {//####[22]####
        super(graph, processorCount, parNumber);//####[23]####
    }//####[25]####
//####[29]####
    @Override//####[29]####
    public void doSolve() {//####[29]####
        SearchState.initialise(graph);//####[30]####
        SearchState nullstate = new SearchState();//####[31]####
        calculatingnNextLayerSearchingState(nullstate);//####[32]####
        ParaTask.setScheduling(ParaTask.ScheduleType.WorkSharing);//####[34]####
        System.out.println("Thread size: " + ParaTask.getThreadPoolSize(ParaTask.ThreadPoolType.MULTI));//####[35]####
        TaskIDGroup g = this.buildTask(parallelTask);//####[36]####
        try {//####[38]####
            g.waitTillFinished();//####[39]####
        } catch (Exception e) {//####[40]####
            e.printStackTrace();//####[41]####
        }//####[42]####
        scheduleVertices(currBestState);//####[44]####
    }//####[45]####
//####[47]####
    private static volatile Method __pt__buildTask_ConcurrentLinkedQueueSearchState_method = null;//####[47]####
    private synchronized static void __pt__buildTask_ConcurrentLinkedQueueSearchState_ensureMethodVarSet() {//####[47]####
        if (__pt__buildTask_ConcurrentLinkedQueueSearchState_method == null) {//####[47]####
            try {//####[47]####
                __pt__buildTask_ConcurrentLinkedQueueSearchState_method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__buildTask", new Class[] {//####[47]####
                    ConcurrentLinkedQueue.class//####[47]####
                });//####[47]####
            } catch (Exception e) {//####[47]####
                e.printStackTrace();//####[47]####
            }//####[47]####
        }//####[47]####
    }//####[47]####
    public TaskIDGroup<Void> buildTask(ConcurrentLinkedQueue<SearchState> states) {//####[47]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[47]####
        return buildTask(states, new TaskInfo());//####[47]####
    }//####[47]####
    public TaskIDGroup<Void> buildTask(ConcurrentLinkedQueue<SearchState> states, TaskInfo taskinfo) {//####[47]####
        // ensure Method variable is set//####[47]####
        if (__pt__buildTask_ConcurrentLinkedQueueSearchState_method == null) {//####[47]####
            __pt__buildTask_ConcurrentLinkedQueueSearchState_ensureMethodVarSet();//####[47]####
        }//####[47]####
        taskinfo.setParameters(states);//####[47]####
        taskinfo.setMethod(__pt__buildTask_ConcurrentLinkedQueueSearchState_method);//####[47]####
        taskinfo.setInstance(this);//####[47]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[47]####
    }//####[47]####
    public TaskIDGroup<Void> buildTask(TaskID<ConcurrentLinkedQueue<SearchState>> states) {//####[47]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[47]####
        return buildTask(states, new TaskInfo());//####[47]####
    }//####[47]####
    public TaskIDGroup<Void> buildTask(TaskID<ConcurrentLinkedQueue<SearchState>> states, TaskInfo taskinfo) {//####[47]####
        // ensure Method variable is set//####[47]####
        if (__pt__buildTask_ConcurrentLinkedQueueSearchState_method == null) {//####[47]####
            __pt__buildTask_ConcurrentLinkedQueueSearchState_ensureMethodVarSet();//####[47]####
        }//####[47]####
        taskinfo.setTaskIdArgIndexes(0);//####[47]####
        taskinfo.addDependsOn(states);//####[47]####
        taskinfo.setParameters(states);//####[47]####
        taskinfo.setMethod(__pt__buildTask_ConcurrentLinkedQueueSearchState_method);//####[47]####
        taskinfo.setInstance(this);//####[47]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[47]####
    }//####[47]####
    public TaskIDGroup<Void> buildTask(BlockingQueue<ConcurrentLinkedQueue<SearchState>> states) {//####[47]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[47]####
        return buildTask(states, new TaskInfo());//####[47]####
    }//####[47]####
    public TaskIDGroup<Void> buildTask(BlockingQueue<ConcurrentLinkedQueue<SearchState>> states, TaskInfo taskinfo) {//####[47]####
        // ensure Method variable is set//####[47]####
        if (__pt__buildTask_ConcurrentLinkedQueueSearchState_method == null) {//####[47]####
            __pt__buildTask_ConcurrentLinkedQueueSearchState_ensureMethodVarSet();//####[47]####
        }//####[47]####
        taskinfo.setQueueArgIndexes(0);//####[47]####
        taskinfo.setIsPipeline(true);//####[47]####
        taskinfo.setParameters(states);//####[47]####
        taskinfo.setMethod(__pt__buildTask_ConcurrentLinkedQueueSearchState_method);//####[47]####
        taskinfo.setInstance(this);//####[47]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[47]####
    }//####[47]####
    public void __pt__buildTask(ConcurrentLinkedQueue<SearchState> states) {//####[47]####
        SearchState currentState = null;//####[48]####
        while ((currentState = states.poll()) != null) //####[49]####
        {//####[49]####
            long id = Thread.currentThread().getId();//####[50]####
            System.out.println("[thread " + id + "]");//####[51]####
            solving(currentState);//####[52]####
        }//####[53]####
    }//####[54]####
//####[54]####
}//####[54]####
