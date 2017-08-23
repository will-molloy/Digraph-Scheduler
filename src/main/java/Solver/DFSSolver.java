package Solver;

import Graph.EdgeWithCost;
import Graph.Graph;
import Graph.Vertex;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


/**
 * DFS Solver, uses branch and bound technique to prune search states in the stack.
 * Doesn't use much memory since the current best state is cached while the stack is cleared on each iteration.
 *
 * Created by mason on 31/07/17.
 * @author Mason Shi, Edward Huang, Will Molloy
 */
@Data
public final class DFSSolver extends AbstractSolver {

    private int currUpperBound;
    private SearchState currBestState;

    public DFSSolver(Graph<Vertex, EdgeWithCost<Vertex>> graph, int processorCount) {
        super(graph, processorCount);
    }

    @Override
    public void doSolve() {
        SearchState.initialise(graph);
        // Upper bound is initially topological sort i.e. all the nodes scheduled to one processor (when edge cost can be ignored)
        doTopologicalSortSolveAndSetInitialUpperBound();
        if (processorCount != 1) { // when processorCount = 1, topologicalSort is the solution.
            SearchState s = new SearchState();
            solving(s);
        }
        scheduleVertices(currBestState);
    }

    private void solving(SearchState currState) {
        currState.getLegalVertices().forEach(vertex -> IntStream.range(0, processorCount).forEach(processor -> {
                    SearchState nextState = new SearchState(currState, vertex, processor);
                    if (nextState.getUnderestimate() >= currUpperBound) {
                        return;
                    }
                    if (nextState.getNumVertices() == graph.getVertices().size()) {
                        updateLog(nextState);
                        return;
                    }
                    solving(nextState);
                }
        ));
    }

    private void updateLog(SearchState s) {
        int underestimate = s.getUnderestimate();
        if (underestimate < currUpperBound) {
            currUpperBound = underestimate;
            currBestState = s;
        }
    }

    /**
     * Solves the scheduling problem with one processing core
     */
    private void doTopologicalSortSolveAndSetInitialUpperBound() {
        // copy edges, this method will alter the graph (a copy of it)
        Map<Vertex, java.util.List<EdgeWithCost<Vertex>>> outwardEdges = new HashMap<>();
        graph.getOutwardEdgeMap().keySet().forEach(vertex -> outwardEdges.put(vertex, graph.getOutwardEdgeMap().get(vertex).toJavaList()));
        Map<Vertex, java.util.List<EdgeWithCost<Vertex>>> inwardEdges = new HashMap<>();
        graph.getInwardEdgeMap().keySet().forEach(vertex -> inwardEdges.put(vertex, graph.getInwardEdgeMap().get(vertex).toJavaList()));

        // list that will contain sorted vertices
        java.util.List<Vertex> sortedVertices = new ArrayList<>();
        // set of nodes with no incoming edge (dependency satisfied)
        Queue<Vertex> legalVertices = new LinkedList<>(graph.getVertices().parallelStream().filter(vertex -> inwardEdges.get(vertex).isEmpty()).collect(Collectors.toSet()));

        // exhaust vertices until all have been added to sorted list
        while(!legalVertices.isEmpty()){
            Vertex currVertex = legalVertices.remove();

            // add vertex to tail of sorted list
            sortedVertices.add(currVertex);

            // Iterate and exhaust all edges, from: currentVertex to: vertexTo
            Queue<EdgeWithCost<Vertex>> edgesFromCurrVertex = new LinkedList<>(outwardEdges.get(currVertex));
            while(!edgesFromCurrVertex.isEmpty()){
                // Remove the edge from the graph (both outward and inward edge maps)
                EdgeWithCost edge = edgesFromCurrVertex.remove();
                outwardEdges.put(currVertex, edgesFromCurrVertex.parallelStream().collect(Collectors.toList()));
                Vertex vertexTo = edge.getTo();
                inwardEdges.put(vertexTo, inwardEdges.get(vertexTo).parallelStream().filter(e -> !e.getFrom().equals(currVertex)).collect(Collectors.toList()));

                // check vertexTo has no other inwardEdges (i.e. none excluding this one)
                if (inwardEdges.get(vertexTo).isEmpty()){
                    legalVertices.add(vertexTo);
                }
            }
        }
        if (!sortedVertices.containsAll(graph.getVertices())){
            throw new IllegalStateException("TopologicalSort doesn't contain all vertices.");
        }

        // Schedule the vertices all on the first core.
        final SearchState[] searchState = {new SearchState()};
        sortedVertices.forEach(vertex -> searchState[0] = new SearchState(searchState[0], vertex, 0));

        // Set initial upper bound and currBestState state
        currUpperBound = searchState[0].getUnderestimate();
        currBestState = searchState[0];
    }

}
