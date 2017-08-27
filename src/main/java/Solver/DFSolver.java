package Solver;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.IntStream;

import CommonInterface.ISearchState;
import Graph.EdgeWithCost;
import Graph.Vertex;
import javafx.application.Platform;
import Graph.Graph;


import lombok.Data;
import pt.runtime.TaskID;

/**
 * Created by mason on 31/07/17.
 */
@Data
public class DFSolver extends AbstractSolver {

	protected static int currUpperBound = Integer.MAX_VALUE;

	ConcurrentLinkedQueue<SearchState> parallelTask = new ConcurrentLinkedQueue<SearchState>();

	public DFSolver(Graph<Vertex, EdgeWithCost<Vertex>> graph, int processorCount, int praNumber) {
		super(graph, processorCount);
		this.parallelProcessorCount=praNumber;
	}

	@Override
	public void doSolve() {
		setTimer();
		SearchState.initialise(graph,processorCount);
		SearchState nullstate = new SearchState();

		solving(nullstate);
		updateTime();
		scheduleVertices();
	}
	
	protected void setTimer() {
		 if (updater != null) {
	            /* We have an updater and a UI to update */
	            isUpdatableProgressBar = true;
	            AbstractSolver solver = this; //provide a reference to GUI classes
	            timer = new Timer();
	            timer.scheduleAtFixedRate(new TimerTask() {
	                                          @Override
	                                          public void run() {
	                                              Platform.runLater(()->updater.update(currBestState, solver)); // required by FX framework
	                                          }
	                                      },
	                    100, 100);
	        }
	}

	/**
	 *Will be used for parallel task.
	 *Put in null state and return ConcurrentLinkedQueue of search state which can be solved in parallel. 
	 *@Param SearchState
	 *@return the ConcurrentLinkedQueue of searching state 
	 */
	protected void calculatingnNextLayerSearchingState(SearchState nullState) {
		parallelTask.add(nullState);
		while(parallelTask.size()<parallelProcessorCount) {
			int size = parallelTask.size();
			for(int i = 0; i < size; i++) {

				//currentState for calculating all next states
				SearchState currentState = parallelTask.poll(); 
				Set<Vertex> legalVerticesSet = currentState.getLegalVertices();

				//calculating cartesian product of every processor and next states
				for(Iterator<Vertex> it = legalVerticesSet.iterator();it.hasNext();) {
					Vertex nextState = it.next();
					IntStream.of(0,processorCount-1).forEach(processor->{
						SearchState newSearchState = new SearchState(currentState, nextState, processor);
						parallelTask.add(newSearchState);
					});
				}
			}
		}
	}

	protected void solving(SearchState s){
		Set<Vertex> legalVerticesSet = s.getLegalVertices();
		for(Iterator<Vertex> it = legalVerticesSet.iterator();it.hasNext();) {
			Vertex v = it.next();
			for(int processorC = 0; processorC<processorCount; processorC++) {
				SearchState next = new SearchState(s, v, processorC);
				if (next.getUnderestimate() > currUpperBound){
					return;
				}
				if (next.getNumVertices() == graph.getVertices().size()){
					updateLog(next);
					return;
				}
				solving(next);
			}
		}

	}

	protected void updateLog(SearchState s) {
		int underestimate = s.getUnderestimate();
		if (underestimate < currUpperBound) {
			currUpperBound = underestimate;
			currBestState = s;
		}
	}
	
	protected void updateTime() {
        if (updater != null && timer != null) {
            Platform.runLater(() -> updater.update(currBestState, this)); // required by FX framework
            timer.cancel();
        }
	}

}

