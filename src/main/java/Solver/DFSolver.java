package Solver;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.IntStream;

import CommonInterface.ISearchState;
import Graph.EdgeWithCost;
import Graph.Vertex;
import Graph.Graph;


import lombok.Data;
import pt.runtime.TaskID;

/**
 * Created by mason on 31/07/17.
 */
@Data
public class DFSolver extends AbstractSolver {

	private static int currUpperBound = Integer.MAX_VALUE;
	private static SearchState currBestState;
	private int praNumber;

	ArrayList<SearchState> parallelTask = new ArrayList<SearchState>();
	
	public DFSolver(Graph<Vertex, EdgeWithCost<Vertex>> graph, int processorCount, int praNumber) {
		super(graph, processorCount);
		this.praNumber=praNumber;
	}

	@Override
	public void doSolve() {
		SearchState.initialise(graph);
		SearchState nullstate = new SearchState();
		
		solving(nullstate);

		scheduleVertices(currBestState);
	}

	/**
	 *Will be used for parallel task.
	 *Put in null state and return arraylist of search state which can be solved in parallel. 
	 *@Param SearchState
	 *@return the fist layer of searching state 
	 */
	protected void calculatingFirstLayerSearchingState(SearchState nullState) {
		Set<Vertex> legalVerticesSet = nullState.getLegalVertices();
		for(Iterator<Vertex> it = legalVerticesSet.iterator();it.hasNext();) {
			Vertex v = it.next();
			for(int processorC = 0; processorC<processorCount; processorC++) {
				SearchState newSearchState = new SearchState(nullState, v, processorC);
				parallelTask.add(newSearchState);
			}
		}
	}

	private void solving(SearchState s){
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

	protected void recursiveSolve(SearchState s) {
		Set<Vertex> legalVerticesSet = s.getLegalVertices();

		legalVerticesSet.stream().forEach( v -> {
			IntStream.of(0, processorCount-1).forEach( i -> {
				SearchState next = new SearchState(s, v, i);
				if (next.getUnderestimate() > currUpperBound){
					return;
				}
				if (next.getNumVertices() == graph.getVertices().size()){
					updateLog(next);
					return;
				}
				recursiveSolve(next);
			}
					);
		});
	}

	 protected void updateLog(SearchState s) {
	        int underestimate = s.getUnderestimate();
	        if (underestimate < currUpperBound) {
	            currUpperBound = underestimate;
	            currBestState = s;
	            System.out.println("AA\n");
	        }
	    }

}

