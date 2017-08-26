package Solver;

import Datastructure.FastPriorityQueue;
import Graph.EdgeWithCost;
import Graph.Graph;
import Graph.Vertex;
import javafx.application.Platform;
import Util.Helper;
import lombok.extern.log4j.Log4j;

import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.IntStream;

@Log4j
public class AStarSolverReuseable extends AbstractSolver {

    private final Queue<SearchState> queue;

    public AStarSolverReuseable(Graph<Vertex, EdgeWithCost<Vertex>> graph, int processorCount, int parallelSize) {
        super(graph, processorCount,parallelSize);
        queue = new FastPriorityQueue<>();
    }

    @Override
    void doSolve() {
    	setUp();
        queue.add(currBestState);
        do {
        	
        	algorithmSolve();

            if (currBestState.getNumVertices() == graph.getVertices().size()) {
                // We have found THE optimal solution
                if (updater != null && timer != null) {
                    Platform.runLater(() -> updater.update(currBestState, this)); // required by FX framework
                    timer.cancel();
                }
                log.debug("Final queue size: " + queue.size());
                return;
            }

        } while (Helper.getRemainingMemory() > 600_000_000L); // GB, MB, kB
        continueSolveWithBnB();
    }

    protected void setUp() {
        if (updater != null) {
            /* We have an updater and a UI to update */
            isUpdatableProgressBar = true;
            AbstractSolver solver = this; //provide a reference to GUI classes
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                                          @Override
                                          public void run() {
                                              Platform.runLater(()->updater.update(queue.peek(), solver)); // required by FX framework
                                          }
                                      },
                    100, 100);
        }
    }
    
    protected void algorithmSolve(){
        SearchState currentState = queue.remove();
        currentState.getLegalVertices().forEach(vertex -> IntStream.range(0, processorCount).forEach(processor -> {
            SearchState nextSearchState = new SearchState(currentState, vertex, processor);
            if (!queue.contains(nextSearchState)) {
                queue.add(nextSearchState);
                //increase the state counter for GUI, process only when there is a GUI to update
                if (isUpdatableProgressBar){ //true if there is a GUI progress bar needs to be updated
                    stateCounter++;
                }
            }
        }));
        
    }
    
    
    
    protected void continueSolveWithBnB() {
        if (timer != null) timer.cancel();
        log.info("Calling DFSSolver");

        // transfer the current optimal state and clear the rest.
        DFSSolver dfsSolver = new DFSSolver(graph, processorCount, currBestState);
        queue.clear();
        dfsSolver.setUpdater(getUpdater());
        System.gc();

        currBestState = dfsSolver.completeSolve();
    }
}