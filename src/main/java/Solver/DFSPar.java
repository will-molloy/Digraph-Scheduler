package Solver;//####[1]####
//####[1]####
import java.util.Iterator;//####[3]####
import java.util.Set;//####[4]####
import java.util.stream.IntStream;//####[5]####
import CommonInterface.ISearchState;//####[7]####
import org.graphstream.graph.Graph;//####[9]####
import org.graphstream.graph.Node;//####[10]####
import lombok.Data;//####[12]####
import java.util.concurrent.ConcurrentLinkedQueue;//####[13]####
import java.util.concurrent.CopyOnWriteArrayList;//####[14]####
//####[14]####
//-- ParaTask related imports//####[14]####
import pt.runtime.*;//####[14]####
import java.util.concurrent.ExecutionException;//####[14]####
import java.util.concurrent.locks.*;//####[14]####
import java.lang.reflect.*;//####[14]####
import pt.runtime.GuiThread;//####[14]####
import java.util.concurrent.BlockingQueue;//####[14]####
import java.util.ArrayList;//####[14]####
import java.util.List;//####[14]####
//####[14]####
public class DFSPar extends DFSolver {//####[16]####
    static{ParaTask.init();}//####[16]####
    /*  ParaTask helper method to access private/protected slots *///####[16]####
    public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[16]####
        if (m.getParameterTypes().length == 0)//####[16]####
            m.invoke(instance);//####[16]####
        else if ((m.getParameterTypes().length == 1))//####[16]####
            m.invoke(instance, arg);//####[16]####
        else //####[16]####
            m.invoke(instance, arg, interResult);//####[16]####
    }//####[16]####
//####[18]####
    private static int bound = Integer.MAX_VALUE;//####[18]####
//####[19]####
    private static SearchState result;//####[19]####
//####[21]####
    private CopyOnWriteArrayList<Set<Node>> searchingTask = null;//####[21]####
//####[23]####
    public DFSPar(Graph graph, int processorCount) {//####[23]####
        super(graph, processorCount);//####[24]####
    }//####[25]####
//####[27]####
    private void updateLog(SearchState s, int cost) {//####[27]####
        if (cost < bound) //####[28]####
        {//####[28]####
            bound = cost;//####[29]####
            result = s;//####[30]####
        }//####[31]####
    }//####[32]####
//####[35]####
    @Override//####[35]####
    public void doSolve() {//####[35]####
        SearchState.init(graph);//####[36]####
        SearchState s = new SearchState();//####[37]####
        solving(s);//####[38]####
        scheduleVertices(result);//####[39]####
    }//####[40]####
//####[42]####
    private void solving(SearchState s) {//####[42]####
        Set<Node> legalVerticesSet = s.getLegalVertices();//####[43]####
        for (int i = 0; i < legalVerticesSet.size(); i++) //####[44]####
        {//####[44]####
            searchingTask.add(legalVerticesSet);//####[46]####
            for (Iterator<Node> it = legalVerticesSet.iterator(); it.hasNext(); ) //####[48]####
            {//####[48]####
                Node v = it.next();//####[49]####
                for (int processorC = 0; processorC < processorCount - 1; i++) //####[50]####
                {//####[50]####
                    SearchState next = new SearchState(s, v, processorC);//####[51]####
                    if (next.getDfCost() > bound) //####[52]####
                    {//####[52]####
                        return;//####[53]####
                    }//####[54]####
                    if (next.getSize() == graph.getNodeCount()) //####[55]####
                    {//####[55]####
                        updateLog(next, next.getDfCost());//####[56]####
                        return;//####[57]####
                    }//####[58]####
                    recursiveSolve(next);//####[59]####
                }//####[60]####
            }//####[61]####
        }//####[64]####
    }//####[66]####
//####[68]####
    private static volatile Method __pt__buildTask__method = null;//####[68]####
    private synchronized static void __pt__buildTask__ensureMethodVarSet() {//####[68]####
        if (__pt__buildTask__method == null) {//####[68]####
            try {//####[68]####
                __pt__buildTask__method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__buildTask", new Class[] {//####[68]####
                    //####[68]####
                });//####[68]####
            } catch (Exception e) {//####[68]####
                e.printStackTrace();//####[68]####
            }//####[68]####
        }//####[68]####
    }//####[68]####
    public TaskID<Void> buildTask() {//####[68]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[68]####
        return buildTask(new TaskInfo());//####[68]####
    }//####[68]####
    public TaskID<Void> buildTask(TaskInfo taskinfo) {//####[68]####
        // ensure Method variable is set//####[68]####
        if (__pt__buildTask__method == null) {//####[68]####
            __pt__buildTask__ensureMethodVarSet();//####[68]####
        }//####[68]####
        taskinfo.setParameters();//####[68]####
        taskinfo.setMethod(__pt__buildTask__method);//####[68]####
        taskinfo.setInstance(this);//####[68]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[68]####
    }//####[68]####
    public void __pt__buildTask() {//####[68]####
    }//####[70]####
//####[70]####
//####[72]####
    private static volatile Method __pt__parallelTask_SetNode_SearchState_method = null;//####[72]####
    private synchronized static void __pt__parallelTask_SetNode_SearchState_ensureMethodVarSet() {//####[72]####
        if (__pt__parallelTask_SetNode_SearchState_method == null) {//####[72]####
            try {//####[72]####
                __pt__parallelTask_SetNode_SearchState_method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__parallelTask", new Class[] {//####[72]####
                    Set.class, SearchState.class//####[72]####
                });//####[72]####
            } catch (Exception e) {//####[72]####
                e.printStackTrace();//####[72]####
            }//####[72]####
        }//####[72]####
    }//####[72]####
    public TaskIDGroup<Void> parallelTask(Set<Node> set, SearchState s) {//####[72]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[72]####
        return parallelTask(set, s, new TaskInfo());//####[72]####
    }//####[72]####
    public TaskIDGroup<Void> parallelTask(Set<Node> set, SearchState s, TaskInfo taskinfo) {//####[72]####
        // ensure Method variable is set//####[72]####
        if (__pt__parallelTask_SetNode_SearchState_method == null) {//####[72]####
            __pt__parallelTask_SetNode_SearchState_ensureMethodVarSet();//####[72]####
        }//####[72]####
        taskinfo.setParameters(set, s);//####[72]####
        taskinfo.setMethod(__pt__parallelTask_SetNode_SearchState_method);//####[72]####
        taskinfo.setInstance(this);//####[72]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[72]####
    }//####[72]####
    public TaskIDGroup<Void> parallelTask(TaskID<Set<Node>> set, SearchState s) {//####[72]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[72]####
        return parallelTask(set, s, new TaskInfo());//####[72]####
    }//####[72]####
    public TaskIDGroup<Void> parallelTask(TaskID<Set<Node>> set, SearchState s, TaskInfo taskinfo) {//####[72]####
        // ensure Method variable is set//####[72]####
        if (__pt__parallelTask_SetNode_SearchState_method == null) {//####[72]####
            __pt__parallelTask_SetNode_SearchState_ensureMethodVarSet();//####[72]####
        }//####[72]####
        taskinfo.setTaskIdArgIndexes(0);//####[72]####
        taskinfo.addDependsOn(set);//####[72]####
        taskinfo.setParameters(set, s);//####[72]####
        taskinfo.setMethod(__pt__parallelTask_SetNode_SearchState_method);//####[72]####
        taskinfo.setInstance(this);//####[72]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[72]####
    }//####[72]####
    public TaskIDGroup<Void> parallelTask(BlockingQueue<Set<Node>> set, SearchState s) {//####[72]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[72]####
        return parallelTask(set, s, new TaskInfo());//####[72]####
    }//####[72]####
    public TaskIDGroup<Void> parallelTask(BlockingQueue<Set<Node>> set, SearchState s, TaskInfo taskinfo) {//####[72]####
        // ensure Method variable is set//####[72]####
        if (__pt__parallelTask_SetNode_SearchState_method == null) {//####[72]####
            __pt__parallelTask_SetNode_SearchState_ensureMethodVarSet();//####[72]####
        }//####[72]####
        taskinfo.setQueueArgIndexes(0);//####[72]####
        taskinfo.setIsPipeline(true);//####[72]####
        taskinfo.setParameters(set, s);//####[72]####
        taskinfo.setMethod(__pt__parallelTask_SetNode_SearchState_method);//####[72]####
        taskinfo.setInstance(this);//####[72]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[72]####
    }//####[72]####
    public TaskIDGroup<Void> parallelTask(Set<Node> set, TaskID<SearchState> s) {//####[72]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[72]####
        return parallelTask(set, s, new TaskInfo());//####[72]####
    }//####[72]####
    public TaskIDGroup<Void> parallelTask(Set<Node> set, TaskID<SearchState> s, TaskInfo taskinfo) {//####[72]####
        // ensure Method variable is set//####[72]####
        if (__pt__parallelTask_SetNode_SearchState_method == null) {//####[72]####
            __pt__parallelTask_SetNode_SearchState_ensureMethodVarSet();//####[72]####
        }//####[72]####
        taskinfo.setTaskIdArgIndexes(1);//####[72]####
        taskinfo.addDependsOn(s);//####[72]####
        taskinfo.setParameters(set, s);//####[72]####
        taskinfo.setMethod(__pt__parallelTask_SetNode_SearchState_method);//####[72]####
        taskinfo.setInstance(this);//####[72]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[72]####
    }//####[72]####
    public TaskIDGroup<Void> parallelTask(TaskID<Set<Node>> set, TaskID<SearchState> s) {//####[72]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[72]####
        return parallelTask(set, s, new TaskInfo());//####[72]####
    }//####[72]####
    public TaskIDGroup<Void> parallelTask(TaskID<Set<Node>> set, TaskID<SearchState> s, TaskInfo taskinfo) {//####[72]####
        // ensure Method variable is set//####[72]####
        if (__pt__parallelTask_SetNode_SearchState_method == null) {//####[72]####
            __pt__parallelTask_SetNode_SearchState_ensureMethodVarSet();//####[72]####
        }//####[72]####
        taskinfo.setTaskIdArgIndexes(0, 1);//####[72]####
        taskinfo.addDependsOn(set);//####[72]####
        taskinfo.addDependsOn(s);//####[72]####
        taskinfo.setParameters(set, s);//####[72]####
        taskinfo.setMethod(__pt__parallelTask_SetNode_SearchState_method);//####[72]####
        taskinfo.setInstance(this);//####[72]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[72]####
    }//####[72]####
    public TaskIDGroup<Void> parallelTask(BlockingQueue<Set<Node>> set, TaskID<SearchState> s) {//####[72]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[72]####
        return parallelTask(set, s, new TaskInfo());//####[72]####
    }//####[72]####
    public TaskIDGroup<Void> parallelTask(BlockingQueue<Set<Node>> set, TaskID<SearchState> s, TaskInfo taskinfo) {//####[72]####
        // ensure Method variable is set//####[72]####
        if (__pt__parallelTask_SetNode_SearchState_method == null) {//####[72]####
            __pt__parallelTask_SetNode_SearchState_ensureMethodVarSet();//####[72]####
        }//####[72]####
        taskinfo.setQueueArgIndexes(0);//####[72]####
        taskinfo.setIsPipeline(true);//####[72]####
        taskinfo.setTaskIdArgIndexes(1);//####[72]####
        taskinfo.addDependsOn(s);//####[72]####
        taskinfo.setParameters(set, s);//####[72]####
        taskinfo.setMethod(__pt__parallelTask_SetNode_SearchState_method);//####[72]####
        taskinfo.setInstance(this);//####[72]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[72]####
    }//####[72]####
    public TaskIDGroup<Void> parallelTask(Set<Node> set, BlockingQueue<SearchState> s) {//####[72]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[72]####
        return parallelTask(set, s, new TaskInfo());//####[72]####
    }//####[72]####
    public TaskIDGroup<Void> parallelTask(Set<Node> set, BlockingQueue<SearchState> s, TaskInfo taskinfo) {//####[72]####
        // ensure Method variable is set//####[72]####
        if (__pt__parallelTask_SetNode_SearchState_method == null) {//####[72]####
            __pt__parallelTask_SetNode_SearchState_ensureMethodVarSet();//####[72]####
        }//####[72]####
        taskinfo.setQueueArgIndexes(1);//####[72]####
        taskinfo.setIsPipeline(true);//####[72]####
        taskinfo.setParameters(set, s);//####[72]####
        taskinfo.setMethod(__pt__parallelTask_SetNode_SearchState_method);//####[72]####
        taskinfo.setInstance(this);//####[72]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[72]####
    }//####[72]####
    public TaskIDGroup<Void> parallelTask(TaskID<Set<Node>> set, BlockingQueue<SearchState> s) {//####[72]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[72]####
        return parallelTask(set, s, new TaskInfo());//####[72]####
    }//####[72]####
    public TaskIDGroup<Void> parallelTask(TaskID<Set<Node>> set, BlockingQueue<SearchState> s, TaskInfo taskinfo) {//####[72]####
        // ensure Method variable is set//####[72]####
        if (__pt__parallelTask_SetNode_SearchState_method == null) {//####[72]####
            __pt__parallelTask_SetNode_SearchState_ensureMethodVarSet();//####[72]####
        }//####[72]####
        taskinfo.setQueueArgIndexes(1);//####[72]####
        taskinfo.setIsPipeline(true);//####[72]####
        taskinfo.setTaskIdArgIndexes(0);//####[72]####
        taskinfo.addDependsOn(set);//####[72]####
        taskinfo.setParameters(set, s);//####[72]####
        taskinfo.setMethod(__pt__parallelTask_SetNode_SearchState_method);//####[72]####
        taskinfo.setInstance(this);//####[72]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[72]####
    }//####[72]####
    public TaskIDGroup<Void> parallelTask(BlockingQueue<Set<Node>> set, BlockingQueue<SearchState> s) {//####[72]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[72]####
        return parallelTask(set, s, new TaskInfo());//####[72]####
    }//####[72]####
    public TaskIDGroup<Void> parallelTask(BlockingQueue<Set<Node>> set, BlockingQueue<SearchState> s, TaskInfo taskinfo) {//####[72]####
        // ensure Method variable is set//####[72]####
        if (__pt__parallelTask_SetNode_SearchState_method == null) {//####[72]####
            __pt__parallelTask_SetNode_SearchState_ensureMethodVarSet();//####[72]####
        }//####[72]####
        taskinfo.setQueueArgIndexes(0, 1);//####[72]####
        taskinfo.setIsPipeline(true);//####[72]####
        taskinfo.setParameters(set, s);//####[72]####
        taskinfo.setMethod(__pt__parallelTask_SetNode_SearchState_method);//####[72]####
        taskinfo.setInstance(this);//####[72]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[72]####
    }//####[72]####
    public void __pt__parallelTask(Set<Node> set, SearchState s) {//####[72]####
        for (Iterator<Node> it = set.iterator(); it.hasNext(); ) //####[73]####
        {//####[73]####
            Node v = it.next();//####[74]####
            for (int processorC = 0; processorC < processorCount - 1; processorC++) //####[75]####
            {//####[75]####
                SearchState next = new SearchState(s, v, processorC);//####[76]####
                if (next.getDfCost() > bound) //####[77]####
                {//####[77]####
                    return;//####[78]####
                }//####[79]####
                if (next.getSize() == graph.getNodeCount()) //####[80]####
                {//####[80]####
                    updateLog(next, next.getDfCost());//####[81]####
                    return;//####[82]####
                }//####[83]####
                recursiveSolve(next);//####[84]####
            }//####[85]####
        }//####[86]####
    }//####[87]####
//####[87]####
}//####[87]####
