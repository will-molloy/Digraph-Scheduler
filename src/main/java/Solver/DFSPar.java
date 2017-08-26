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
//####[27]####
    /**
	 * @author kzy0618
	 * DFS B&B parallel solving class.
	 * DFSPar can set size of the thread pool and it is thread safe
	 *///####[27]####
    public DFSPar(Graph<Vertex, EdgeWithCost<Vertex>> graph, int processorCount, int parNumber) {//####[27]####
        super(graph, processorCount, parNumber);//####[28]####
    }//####[29]####
//####[33]####
    @Override//####[33]####
    public void doSolve() {//####[33]####
        SearchState.initialise(graph, processorCount);//####[34]####
        SearchState nullstate = new SearchState();//####[35]####
        calculatingnNextLayerSearchingState(nullstate);//####[36]####
        ParaTask.setScheduling(ParaTask.ScheduleType.WorkSharing);//####[38]####
        TaskIDGroup g = this.buildTask(parallelTask);//####[39]####
        try {//####[41]####
            g.waitTillFinished();//####[42]####
        } catch (Exception e) {//####[43]####
            e.printStackTrace();//####[44]####
        }//####[45]####
        scheduleVertices();//####[47]####
    }//####[48]####
//####[55]####
    private static volatile Method __pt__buildTask_ConcurrentLinkedQueueSearchState_method = null;//####[55]####
    private synchronized static void __pt__buildTask_ConcurrentLinkedQueueSearchState_ensureMethodVarSet() {//####[55]####
        if (__pt__buildTask_ConcurrentLinkedQueueSearchState_method == null) {//####[55]####
            try {//####[55]####
                __pt__buildTask_ConcurrentLinkedQueueSearchState_method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__buildTask", new Class[] {//####[55]####
                    ConcurrentLinkedQueue.class//####[55]####
                });//####[55]####
            } catch (Exception e) {//####[55]####
                e.printStackTrace();//####[55]####
            }//####[55]####
        }//####[55]####
    }//####[55]####
    /**
	 * @author kzy0618
	 * Run solving state on different searchstate in separate thread 
	 * @Param ConcurrentLinkedQueue<SearchState>
	 *///####[55]####
    public TaskIDGroup<Void> buildTask(ConcurrentLinkedQueue<SearchState> states) {//####[55]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[55]####
        return buildTask(states, new TaskInfo());//####[55]####
    }//####[55]####
    /**
	 * @author kzy0618
	 * Run solving state on different searchstate in separate thread 
	 * @Param ConcurrentLinkedQueue<SearchState>
	 *///####[55]####
    public TaskIDGroup<Void> buildTask(ConcurrentLinkedQueue<SearchState> states, TaskInfo taskinfo) {//####[55]####
        // ensure Method variable is set//####[55]####
        if (__pt__buildTask_ConcurrentLinkedQueueSearchState_method == null) {//####[55]####
            __pt__buildTask_ConcurrentLinkedQueueSearchState_ensureMethodVarSet();//####[55]####
        }//####[55]####
        taskinfo.setParameters(states);//####[55]####
        taskinfo.setMethod(__pt__buildTask_ConcurrentLinkedQueueSearchState_method);//####[55]####
        taskinfo.setInstance(this);//####[55]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[55]####
    }//####[55]####
    /**
	 * @author kzy0618
	 * Run solving state on different searchstate in separate thread 
	 * @Param ConcurrentLinkedQueue<SearchState>
	 *///####[55]####
    public TaskIDGroup<Void> buildTask(TaskID<ConcurrentLinkedQueue<SearchState>> states) {//####[55]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[55]####
        return buildTask(states, new TaskInfo());//####[55]####
    }//####[55]####
    /**
	 * @author kzy0618
	 * Run solving state on different searchstate in separate thread 
	 * @Param ConcurrentLinkedQueue<SearchState>
	 *///####[55]####
    public TaskIDGroup<Void> buildTask(TaskID<ConcurrentLinkedQueue<SearchState>> states, TaskInfo taskinfo) {//####[55]####
        // ensure Method variable is set//####[55]####
        if (__pt__buildTask_ConcurrentLinkedQueueSearchState_method == null) {//####[55]####
            __pt__buildTask_ConcurrentLinkedQueueSearchState_ensureMethodVarSet();//####[55]####
        }//####[55]####
        taskinfo.setTaskIdArgIndexes(0);//####[55]####
        taskinfo.addDependsOn(states);//####[55]####
        taskinfo.setParameters(states);//####[55]####
        taskinfo.setMethod(__pt__buildTask_ConcurrentLinkedQueueSearchState_method);//####[55]####
        taskinfo.setInstance(this);//####[55]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[55]####
    }//####[55]####
    /**
	 * @author kzy0618
	 * Run solving state on different searchstate in separate thread 
	 * @Param ConcurrentLinkedQueue<SearchState>
	 *///####[55]####
    public TaskIDGroup<Void> buildTask(BlockingQueue<ConcurrentLinkedQueue<SearchState>> states) {//####[55]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[55]####
        return buildTask(states, new TaskInfo());//####[55]####
    }//####[55]####
    /**
	 * @author kzy0618
	 * Run solving state on different searchstate in separate thread 
	 * @Param ConcurrentLinkedQueue<SearchState>
	 *///####[55]####
    public TaskIDGroup<Void> buildTask(BlockingQueue<ConcurrentLinkedQueue<SearchState>> states, TaskInfo taskinfo) {//####[55]####
        // ensure Method variable is set//####[55]####
        if (__pt__buildTask_ConcurrentLinkedQueueSearchState_method == null) {//####[55]####
            __pt__buildTask_ConcurrentLinkedQueueSearchState_ensureMethodVarSet();//####[55]####
        }//####[55]####
        taskinfo.setQueueArgIndexes(0);//####[55]####
        taskinfo.setIsPipeline(true);//####[55]####
        taskinfo.setParameters(states);//####[55]####
        taskinfo.setMethod(__pt__buildTask_ConcurrentLinkedQueueSearchState_method);//####[55]####
        taskinfo.setInstance(this);//####[55]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[55]####
    }//####[55]####
    /**
	 * @author kzy0618
	 * Run solving state on different searchstate in separate thread 
	 * @Param ConcurrentLinkedQueue<SearchState>
	 *///####[55]####
    public void __pt__buildTask(ConcurrentLinkedQueue<SearchState> states) {//####[55]####
        SearchState currentState = null;//####[56]####
        while ((currentState = states.poll()) != null) //####[57]####
        {//####[57]####
            long id = Thread.currentThread().getId();//####[58]####
            solving(currentState);//####[59]####
        }//####[60]####
    }//####[61]####
//####[61]####
}//####[61]####
