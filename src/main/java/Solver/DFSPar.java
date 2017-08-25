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
    }//####[24]####
//####[28]####
    @Override//####[28]####
    public void doSolve() {//####[28]####
        SearchState.initialise(graph);//####[29]####
        SearchState nullstate = new SearchState();//####[30]####
        calculatingnNextLayerSearchingState(nullstate);//####[31]####
        System.out.println("Thread size: " + praNumber);//####[33]####
        ParaTask.setScheduling(ParaTask.ScheduleType.WorkSharing);//####[35]####
        ParaTask.setThreadPoolSize(ParaTask.ThreadPoolType.MULTI, 4);//####[37]####
        System.out.println("Thread size: " + ParaTask.getThreadPoolSize(ParaTask.ThreadPoolType.MULTI));//####[38]####
        TaskIDGroup g = this.buildTask(parallelTask);//####[39]####
        try {//####[41]####
            g.waitTillFinished();//####[42]####
        } catch (Exception e) {//####[43]####
            e.printStackTrace();//####[44]####
        }//####[45]####
        scheduleVertices(currBestState);//####[47]####
    }//####[48]####
//####[50]####
    private static volatile Method __pt__buildTask_ConcurrentLinkedQueueSearchState_method = null;//####[50]####
    private synchronized static void __pt__buildTask_ConcurrentLinkedQueueSearchState_ensureMethodVarSet() {//####[50]####
        if (__pt__buildTask_ConcurrentLinkedQueueSearchState_method == null) {//####[50]####
            try {//####[50]####
                __pt__buildTask_ConcurrentLinkedQueueSearchState_method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__buildTask", new Class[] {//####[50]####
                    ConcurrentLinkedQueue.class//####[50]####
                });//####[50]####
            } catch (Exception e) {//####[50]####
                e.printStackTrace();//####[50]####
            }//####[50]####
        }//####[50]####
    }//####[50]####
    public TaskIDGroup<Void> buildTask(ConcurrentLinkedQueue<SearchState> states) {//####[50]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[50]####
        return buildTask(states, new TaskInfo());//####[50]####
    }//####[50]####
    public TaskIDGroup<Void> buildTask(ConcurrentLinkedQueue<SearchState> states, TaskInfo taskinfo) {//####[50]####
        // ensure Method variable is set//####[50]####
        if (__pt__buildTask_ConcurrentLinkedQueueSearchState_method == null) {//####[50]####
            __pt__buildTask_ConcurrentLinkedQueueSearchState_ensureMethodVarSet();//####[50]####
        }//####[50]####
        taskinfo.setParameters(states);//####[50]####
        taskinfo.setMethod(__pt__buildTask_ConcurrentLinkedQueueSearchState_method);//####[50]####
        taskinfo.setInstance(this);//####[50]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[50]####
    }//####[50]####
    public TaskIDGroup<Void> buildTask(TaskID<ConcurrentLinkedQueue<SearchState>> states) {//####[50]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[50]####
        return buildTask(states, new TaskInfo());//####[50]####
    }//####[50]####
    public TaskIDGroup<Void> buildTask(TaskID<ConcurrentLinkedQueue<SearchState>> states, TaskInfo taskinfo) {//####[50]####
        // ensure Method variable is set//####[50]####
        if (__pt__buildTask_ConcurrentLinkedQueueSearchState_method == null) {//####[50]####
            __pt__buildTask_ConcurrentLinkedQueueSearchState_ensureMethodVarSet();//####[50]####
        }//####[50]####
        taskinfo.setTaskIdArgIndexes(0);//####[50]####
        taskinfo.addDependsOn(states);//####[50]####
        taskinfo.setParameters(states);//####[50]####
        taskinfo.setMethod(__pt__buildTask_ConcurrentLinkedQueueSearchState_method);//####[50]####
        taskinfo.setInstance(this);//####[50]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[50]####
    }//####[50]####
    public TaskIDGroup<Void> buildTask(BlockingQueue<ConcurrentLinkedQueue<SearchState>> states) {//####[50]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[50]####
        return buildTask(states, new TaskInfo());//####[50]####
    }//####[50]####
    public TaskIDGroup<Void> buildTask(BlockingQueue<ConcurrentLinkedQueue<SearchState>> states, TaskInfo taskinfo) {//####[50]####
        // ensure Method variable is set//####[50]####
        if (__pt__buildTask_ConcurrentLinkedQueueSearchState_method == null) {//####[50]####
            __pt__buildTask_ConcurrentLinkedQueueSearchState_ensureMethodVarSet();//####[50]####
        }//####[50]####
        taskinfo.setQueueArgIndexes(0);//####[50]####
        taskinfo.setIsPipeline(true);//####[50]####
        taskinfo.setParameters(states);//####[50]####
        taskinfo.setMethod(__pt__buildTask_ConcurrentLinkedQueueSearchState_method);//####[50]####
        taskinfo.setInstance(this);//####[50]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[50]####
    }//####[50]####
    public void __pt__buildTask(ConcurrentLinkedQueue<SearchState> states) {//####[50]####
        SearchState currentState = null;//####[51]####
        while ((currentState = states.poll()) != null) //####[52]####
        {//####[52]####
            long id = Thread.currentThread().getId();//####[53]####
            System.out.println("[thread " + id + "]");//####[54]####
            solving(currentState);//####[55]####
        }//####[56]####
    }//####[57]####
//####[57]####
}//####[57]####
