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
//####[51]####
    SearchState completeSolve() {//####[51]####
        currUpperBound = Integer.MAX_VALUE;//####[52]####
        setTimer();//####[53]####
        calculatingnNextLayerSearchingState(currBestState);//####[54]####
        TaskIDGroup solvingGroup = this.buildTask(parallelTask);//####[55]####
        try {//####[56]####
            solvingGroup.waitTillFinished();//####[57]####
        } catch (Exception e) {//####[58]####
            e.printStackTrace();//####[59]####
        }//####[60]####
        updateTime();//####[61]####
        return currBestState;//####[62]####
    }//####[63]####
//####[70]####
    private static volatile Method __pt__buildTask_ConcurrentLinkedQueueSearchState_method = null;//####[70]####
    private synchronized static void __pt__buildTask_ConcurrentLinkedQueueSearchState_ensureMethodVarSet() {//####[70]####
        if (__pt__buildTask_ConcurrentLinkedQueueSearchState_method == null) {//####[70]####
            try {//####[70]####
                __pt__buildTask_ConcurrentLinkedQueueSearchState_method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__buildTask", new Class[] {//####[70]####
                    ConcurrentLinkedQueue.class//####[70]####
                });//####[70]####
            } catch (Exception e) {//####[70]####
                e.printStackTrace();//####[70]####
            }//####[70]####
        }//####[70]####
    }//####[70]####
    /**
	 * @author kzy0618
	 * Run solving state on different searchstate in separate thread 
	 * @Param ConcurrentLinkedQueue<SearchState>
	 *///####[70]####
    public TaskIDGroup<Void> buildTask(ConcurrentLinkedQueue<SearchState> states) {//####[70]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[70]####
        return buildTask(states, new TaskInfo());//####[70]####
    }//####[70]####
    /**
	 * @author kzy0618
	 * Run solving state on different searchstate in separate thread 
	 * @Param ConcurrentLinkedQueue<SearchState>
	 *///####[70]####
    public TaskIDGroup<Void> buildTask(ConcurrentLinkedQueue<SearchState> states, TaskInfo taskinfo) {//####[70]####
        // ensure Method variable is set//####[70]####
        if (__pt__buildTask_ConcurrentLinkedQueueSearchState_method == null) {//####[70]####
            __pt__buildTask_ConcurrentLinkedQueueSearchState_ensureMethodVarSet();//####[70]####
        }//####[70]####
        taskinfo.setParameters(states);//####[70]####
        taskinfo.setMethod(__pt__buildTask_ConcurrentLinkedQueueSearchState_method);//####[70]####
        taskinfo.setInstance(this);//####[70]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[70]####
    }//####[70]####
    /**
	 * @author kzy0618
	 * Run solving state on different searchstate in separate thread 
	 * @Param ConcurrentLinkedQueue<SearchState>
	 *///####[70]####
    public TaskIDGroup<Void> buildTask(TaskID<ConcurrentLinkedQueue<SearchState>> states) {//####[70]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[70]####
        return buildTask(states, new TaskInfo());//####[70]####
    }//####[70]####
    /**
	 * @author kzy0618
	 * Run solving state on different searchstate in separate thread 
	 * @Param ConcurrentLinkedQueue<SearchState>
	 *///####[70]####
    public TaskIDGroup<Void> buildTask(TaskID<ConcurrentLinkedQueue<SearchState>> states, TaskInfo taskinfo) {//####[70]####
        // ensure Method variable is set//####[70]####
        if (__pt__buildTask_ConcurrentLinkedQueueSearchState_method == null) {//####[70]####
            __pt__buildTask_ConcurrentLinkedQueueSearchState_ensureMethodVarSet();//####[70]####
        }//####[70]####
        taskinfo.setTaskIdArgIndexes(0);//####[70]####
        taskinfo.addDependsOn(states);//####[70]####
        taskinfo.setParameters(states);//####[70]####
        taskinfo.setMethod(__pt__buildTask_ConcurrentLinkedQueueSearchState_method);//####[70]####
        taskinfo.setInstance(this);//####[70]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[70]####
    }//####[70]####
    /**
	 * @author kzy0618
	 * Run solving state on different searchstate in separate thread 
	 * @Param ConcurrentLinkedQueue<SearchState>
	 *///####[70]####
    public TaskIDGroup<Void> buildTask(BlockingQueue<ConcurrentLinkedQueue<SearchState>> states) {//####[70]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[70]####
        return buildTask(states, new TaskInfo());//####[70]####
    }//####[70]####
    /**
	 * @author kzy0618
	 * Run solving state on different searchstate in separate thread 
	 * @Param ConcurrentLinkedQueue<SearchState>
	 *///####[70]####
    public TaskIDGroup<Void> buildTask(BlockingQueue<ConcurrentLinkedQueue<SearchState>> states, TaskInfo taskinfo) {//####[70]####
        // ensure Method variable is set//####[70]####
        if (__pt__buildTask_ConcurrentLinkedQueueSearchState_method == null) {//####[70]####
            __pt__buildTask_ConcurrentLinkedQueueSearchState_ensureMethodVarSet();//####[70]####
        }//####[70]####
        taskinfo.setQueueArgIndexes(0);//####[70]####
        taskinfo.setIsPipeline(true);//####[70]####
        taskinfo.setParameters(states);//####[70]####
        taskinfo.setMethod(__pt__buildTask_ConcurrentLinkedQueueSearchState_method);//####[70]####
        taskinfo.setInstance(this);//####[70]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[70]####
    }//####[70]####
    /**
	 * @author kzy0618
	 * Run solving state on different searchstate in separate thread 
	 * @Param ConcurrentLinkedQueue<SearchState>
	 *///####[70]####
    public void __pt__buildTask(ConcurrentLinkedQueue<SearchState> states) {//####[70]####
        SearchState currentState = null;//####[71]####
        while ((currentState = states.poll()) != null) //####[72]####
        {//####[72]####
            long id = Thread.currentThread().getId();//####[73]####
            solving(currentState);//####[74]####
        }//####[75]####
    }//####[76]####
//####[76]####
}//####[76]####
