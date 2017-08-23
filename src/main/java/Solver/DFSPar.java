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
import pt.runtime.TaskID;//####[13]####
import java.util.concurrent.ConcurrentLinkedQueue;//####[15]####
import java.util.concurrent.CopyOnWriteArrayList;//####[16]####
//####[16]####
//-- ParaTask related imports//####[16]####
import pt.runtime.*;//####[16]####
import java.util.concurrent.ExecutionException;//####[16]####
import java.util.concurrent.locks.*;//####[16]####
import java.lang.reflect.*;//####[16]####
import pt.runtime.GuiThread;//####[16]####
import java.util.concurrent.BlockingQueue;//####[16]####
import java.util.ArrayList;//####[16]####
import java.util.List;//####[16]####
//####[16]####
public class DFSPar extends DFSolver {//####[18]####
    static{ParaTask.init();}//####[18]####
    /*  ParaTask helper method to access private/protected slots *///####[18]####
    public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[18]####
        if (m.getParameterTypes().length == 0)//####[18]####
            m.invoke(instance);//####[18]####
        else if ((m.getParameterTypes().length == 1))//####[18]####
            m.invoke(instance, arg);//####[18]####
        else //####[18]####
            m.invoke(instance, arg, interResult);//####[18]####
    }//####[18]####
//####[20]####
    private static int bound = Integer.MAX_VALUE;//####[20]####
//####[21]####
    private static SearchState currBestState;//####[21]####
//####[22]####
    private static int currUpperBound = Integer.MAX_VALUE;//####[22]####
//####[24]####
    public DFSPar(Graph<Vertex, EdgeWithCost<Vertex>> graph, int processorCount, int parNumber) {//####[24]####
        super(graph, processorCount, parNumber);//####[25]####
    }//####[26]####
//####[30]####
    @Override//####[30]####
    public void doSolve() {//####[30]####
        SearchState.initialise(graph);//####[31]####
        SearchState nullstate = new SearchState();//####[32]####
        calculatingFirstLayerSearchingState(nullstate);//####[33]####
        solving(nullstate);//####[41]####
        scheduleVertices(currBestState);//####[43]####
    }//####[44]####
//####[46]####
    private static volatile Method __pt__buildTask_SearchState_method = null;//####[46]####
    private synchronized static void __pt__buildTask_SearchState_ensureMethodVarSet() {//####[46]####
        if (__pt__buildTask_SearchState_method == null) {//####[46]####
            try {//####[46]####
                __pt__buildTask_SearchState_method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__buildTask", new Class[] {//####[46]####
                    SearchState.class//####[46]####
                });//####[46]####
            } catch (Exception e) {//####[46]####
                e.printStackTrace();//####[46]####
            }//####[46]####
        }//####[46]####
    }//####[46]####
    public TaskID<Void> buildTask(SearchState state) {//####[46]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[46]####
        return buildTask(state, new TaskInfo());//####[46]####
    }//####[46]####
    public TaskID<Void> buildTask(SearchState state, TaskInfo taskinfo) {//####[46]####
        // ensure Method variable is set//####[46]####
        if (__pt__buildTask_SearchState_method == null) {//####[46]####
            __pt__buildTask_SearchState_ensureMethodVarSet();//####[46]####
        }//####[46]####
        taskinfo.setParameters(state);//####[46]####
        taskinfo.setMethod(__pt__buildTask_SearchState_method);//####[46]####
        taskinfo.setInstance(this);//####[46]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[46]####
    }//####[46]####
    public TaskID<Void> buildTask(TaskID<SearchState> state) {//####[46]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[46]####
        return buildTask(state, new TaskInfo());//####[46]####
    }//####[46]####
    public TaskID<Void> buildTask(TaskID<SearchState> state, TaskInfo taskinfo) {//####[46]####
        // ensure Method variable is set//####[46]####
        if (__pt__buildTask_SearchState_method == null) {//####[46]####
            __pt__buildTask_SearchState_ensureMethodVarSet();//####[46]####
        }//####[46]####
        taskinfo.setTaskIdArgIndexes(0);//####[46]####
        taskinfo.addDependsOn(state);//####[46]####
        taskinfo.setParameters(state);//####[46]####
        taskinfo.setMethod(__pt__buildTask_SearchState_method);//####[46]####
        taskinfo.setInstance(this);//####[46]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[46]####
    }//####[46]####
    public TaskID<Void> buildTask(BlockingQueue<SearchState> state) {//####[46]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[46]####
        return buildTask(state, new TaskInfo());//####[46]####
    }//####[46]####
    public TaskID<Void> buildTask(BlockingQueue<SearchState> state, TaskInfo taskinfo) {//####[46]####
        // ensure Method variable is set//####[46]####
        if (__pt__buildTask_SearchState_method == null) {//####[46]####
            __pt__buildTask_SearchState_ensureMethodVarSet();//####[46]####
        }//####[46]####
        taskinfo.setQueueArgIndexes(0);//####[46]####
        taskinfo.setIsPipeline(true);//####[46]####
        taskinfo.setParameters(state);//####[46]####
        taskinfo.setMethod(__pt__buildTask_SearchState_method);//####[46]####
        taskinfo.setInstance(this);//####[46]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[46]####
    }//####[46]####
    public void __pt__buildTask(SearchState state) {//####[46]####
        solving(state);//####[47]####
    }//####[48]####
//####[48]####
//####[50]####
    private void solving(SearchState s) {//####[50]####
        Set<Vertex> legalVerticesSet = s.getLegalVertices();//####[51]####
        for (Iterator<Vertex> it = legalVerticesSet.iterator(); it.hasNext(); ) //####[52]####
        {//####[52]####
            Vertex v = it.next();//####[53]####
            for (int processorC = 0; processorC < processorCount; processorC++) //####[54]####
            {//####[54]####
                SearchState next = new SearchState(s, v, processorC);//####[55]####
                if (next.getUnderestimate() > currUpperBound) //####[56]####
                {//####[56]####
                    return;//####[57]####
                }//####[58]####
                if (next.getNumVertices() == graph.getVertices().size()) //####[59]####
                {//####[59]####
                    updateLog(next);//####[60]####
                    return;//####[61]####
                }//####[62]####
                solving(next);//####[63]####
            }//####[64]####
        }//####[65]####
    }//####[66]####
}//####[66]####
