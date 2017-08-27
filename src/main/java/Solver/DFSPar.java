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
        currBestState = parallelTask.peek();//####[37]####
        setTimer();//####[38]####
        ParaTask.setScheduling(ParaTask.ScheduleType.WorkSharing);//####[40]####
        TaskIDGroup solvingGroup = this.buildTask(parallelTask);//####[41]####
        try {//####[42]####
            solvingGroup.waitTillFinished();//####[43]####
        } catch (Exception e) {//####[44]####
            e.printStackTrace();//####[45]####
        }//####[46]####
        updateTime();//####[47]####
        scheduleVertices();//####[48]####
    }//####[49]####
//####[56]####
    private static volatile Method __pt__buildTask_ConcurrentLinkedQueueSearchState_method = null;//####[56]####
    private synchronized static void __pt__buildTask_ConcurrentLinkedQueueSearchState_ensureMethodVarSet() {//####[56]####
        if (__pt__buildTask_ConcurrentLinkedQueueSearchState_method == null) {//####[56]####
            try {//####[56]####
                __pt__buildTask_ConcurrentLinkedQueueSearchState_method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__buildTask", new Class[] {//####[56]####
                    ConcurrentLinkedQueue.class//####[56]####
                });//####[56]####
            } catch (Exception e) {//####[56]####
                e.printStackTrace();//####[56]####
            }//####[56]####
        }//####[56]####
    }//####[56]####
    /**
	 * @author kzy0618
	 * Run solving state on different searchstate in separate thread 
	 * @Param ConcurrentLinkedQueue<SearchState>
	 *///####[56]####
    public TaskIDGroup<Void> buildTask(ConcurrentLinkedQueue<SearchState> states) {//####[56]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[56]####
        return buildTask(states, new TaskInfo());//####[56]####
    }//####[56]####
    /**
	 * @author kzy0618
	 * Run solving state on different searchstate in separate thread 
	 * @Param ConcurrentLinkedQueue<SearchState>
	 *///####[56]####
    public TaskIDGroup<Void> buildTask(ConcurrentLinkedQueue<SearchState> states, TaskInfo taskinfo) {//####[56]####
        // ensure Method variable is set//####[56]####
        if (__pt__buildTask_ConcurrentLinkedQueueSearchState_method == null) {//####[56]####
            __pt__buildTask_ConcurrentLinkedQueueSearchState_ensureMethodVarSet();//####[56]####
        }//####[56]####
        taskinfo.setParameters(states);//####[56]####
        taskinfo.setMethod(__pt__buildTask_ConcurrentLinkedQueueSearchState_method);//####[56]####
        taskinfo.setInstance(this);//####[56]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[56]####
    }//####[56]####
    /**
	 * @author kzy0618
	 * Run solving state on different searchstate in separate thread 
	 * @Param ConcurrentLinkedQueue<SearchState>
	 *///####[56]####
    public TaskIDGroup<Void> buildTask(TaskID<ConcurrentLinkedQueue<SearchState>> states) {//####[56]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[56]####
        return buildTask(states, new TaskInfo());//####[56]####
    }//####[56]####
    /**
	 * @author kzy0618
	 * Run solving state on different searchstate in separate thread 
	 * @Param ConcurrentLinkedQueue<SearchState>
	 *///####[56]####
    public TaskIDGroup<Void> buildTask(TaskID<ConcurrentLinkedQueue<SearchState>> states, TaskInfo taskinfo) {//####[56]####
        // ensure Method variable is set//####[56]####
        if (__pt__buildTask_ConcurrentLinkedQueueSearchState_method == null) {//####[56]####
            __pt__buildTask_ConcurrentLinkedQueueSearchState_ensureMethodVarSet();//####[56]####
        }//####[56]####
        taskinfo.setTaskIdArgIndexes(0);//####[56]####
        taskinfo.addDependsOn(states);//####[56]####
        taskinfo.setParameters(states);//####[56]####
        taskinfo.setMethod(__pt__buildTask_ConcurrentLinkedQueueSearchState_method);//####[56]####
        taskinfo.setInstance(this);//####[56]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[56]####
    }//####[56]####
    /**
	 * @author kzy0618
	 * Run solving state on different searchstate in separate thread 
	 * @Param ConcurrentLinkedQueue<SearchState>
	 *///####[56]####
    public TaskIDGroup<Void> buildTask(BlockingQueue<ConcurrentLinkedQueue<SearchState>> states) {//####[56]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[56]####
        return buildTask(states, new TaskInfo());//####[56]####
    }//####[56]####
    /**
	 * @author kzy0618
	 * Run solving state on different searchstate in separate thread 
	 * @Param ConcurrentLinkedQueue<SearchState>
	 *///####[56]####
    public TaskIDGroup<Void> buildTask(BlockingQueue<ConcurrentLinkedQueue<SearchState>> states, TaskInfo taskinfo) {//####[56]####
        // ensure Method variable is set//####[56]####
        if (__pt__buildTask_ConcurrentLinkedQueueSearchState_method == null) {//####[56]####
            __pt__buildTask_ConcurrentLinkedQueueSearchState_ensureMethodVarSet();//####[56]####
        }//####[56]####
        taskinfo.setQueueArgIndexes(0);//####[56]####
        taskinfo.setIsPipeline(true);//####[56]####
        taskinfo.setParameters(states);//####[56]####
        taskinfo.setMethod(__pt__buildTask_ConcurrentLinkedQueueSearchState_method);//####[56]####
        taskinfo.setInstance(this);//####[56]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[56]####
    }//####[56]####
    /**
	 * @author kzy0618
	 * Run solving state on different searchstate in separate thread 
	 * @Param ConcurrentLinkedQueue<SearchState>
	 *///####[56]####
    public void __pt__buildTask(ConcurrentLinkedQueue<SearchState> states) {//####[56]####
        SearchState currentState = null;//####[57]####
        while ((currentState = states.poll()) != null) //####[58]####
        {//####[58]####
            long id = Thread.currentThread().getId();//####[59]####
            solving(currentState);//####[60]####
        }//####[61]####
    }//####[62]####
//####[62]####
}//####[62]####
