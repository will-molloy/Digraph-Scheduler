package Solver;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.IntStream;

import CommonInterface.ISearchState;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import lombok.Data;

/**
 * Created by mason on 31/07/17.
 */
@Data
public class DFSolver extends AbstractSolver {

	private static int bound = Integer.MAX_VALUE;
	private static SearchState result;

	ArrayList<SearchState> parallelTask = new ArrayList<SearchState>();
	
	public DFSolver(Graph graph, int processorCount) {
		super(graph, processorCount);
	}

	@Override
	public void doSolve() {
		SearchState.init(graph);
		SearchState nullstate = new SearchState();
		
		calculatingFirstLayerSearchingState(nullstate);
		
		solving(nullstate);

		scheduleVertices(result);
	}

	/*
	 *Will be used for parallel task.
	 *Put in null state and return arraylist of search state which can be solved in parallel. 
	 *@Param SearchState
	 *@return the fist layer of searching state 
	 *
	 */
	private void calculatingFirstLayerSearchingState(SearchState nullState) {
		Set<Node> legalVerticesSet = nullState.getLegalVertices();
		for(Iterator<Node> it = legalVerticesSet.iterator();it.hasNext();) {
			Node v = it.next();
			for(int processorC = 0; processorC<processorCount; processorC++) {
				SearchState next = new SearchState(nullState, v, processorC);
			}
		}
	}


	private void solving(SearchState s){
		Set<Node> legalVerticesSet = s.getLegalVertices();
		for(Iterator<Node> it = legalVerticesSet.iterator();it.hasNext();) {
			Node v = it.next();
			for(int processorC = 0; processorC<processorCount; processorC++) {
				SearchState next = new SearchState(s, v, processorC);
				if (next.getDfCost() > bound){
					return;
				}
				if (next.getSize() == graph.getNodeCount()){
					updateLog(next, next.getDfCost());
					return;
				}
				solving(next);
			}
		}

	}

	protected void recursiveSolve(SearchState s) {
		Set<Node> legalVerticesSet = s.getLegalVertices();

		legalVerticesSet.stream().forEach( v -> {
			IntStream.of(0, processorCount-1).forEach( i -> {
				SearchState next = new SearchState(s, v, i);
				if (next.getDfCost() > bound){
					return;
				}
				if (next.getSize() == graph.getNodeCount()){
					updateLog(next, next.getDfCost());
					return;
				}
				recursiveSolve(next);
			}
					);
		});
	}

	private void updateLog(SearchState s, int cost){
		if (cost<bound){
			bound = cost;
			result = s;
		}
	}

	//	private void solving(SearchState s) {
	//	    s.getLegalVertices().forEach(v -> IntStream.range(0, processorCount).forEach(i -> {
	//	                SearchState next = new SearchState(s, v, i);
	//	                if (next.getPriority() >= bound) {
	//	                    return;
	//	                }
	//	                if (next.getSize() == graph.getNodeCount()) {
	//	                    updateLog(next);
	//	                    return;
	//	                }
	//	                solving(next);
	//	            }
	//	    ));
	//	}

	private void updateLog(SearchState s) {
		int underestimate = s.getPriority();
		if (underestimate < bound) {
			bound = underestimate;
			result = s;
		}
	}


}

