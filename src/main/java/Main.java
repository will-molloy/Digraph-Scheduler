import Exporter.GraphExporter;
import Graph.EdgeWithCost;
import Graph.Exceptions.GraphException;
import Graph.Graph;
import Graph.Vertex;
import Parser.EdgeCtor;
import Parser.Exceptions.ParserException;
import Parser.InputParser;
import Parser.VertexCtor;
import Solver.AStarSolver;
import Solver.AStarSolverPar;
import Solver.Interfaces.ISolver;

import java.io.*;

public class Main {

    private Main(){
        //Ensure this class is not instantiated
    }

    public static void main(String[] args) {
        if(args.length != 1) {
            System.err.println("$0 takes 1 argument");
            return;
        }


        File inputFile = new File(args[0]);
        if(!inputFile.exists() || !inputFile.canRead()) {
            System.err.println("Can't open file");
        }

        Graph<Vertex, EdgeWithCost<Vertex>> graph = new Graph<Vertex, EdgeWithCost<Vertex>>();

        InputParser<Vertex, EdgeWithCost<Vertex>> parser = new InputParser<Vertex, EdgeWithCost<Vertex>>(new VertexCtor(), new EdgeCtor());
        try {
            parser.doParse(graph, new BufferedReader(new FileReader(inputFile)));
        } catch (ParserException | FileNotFoundException e) {
            e.printStackTrace();
        }
        graph.finalise();

        ISolver solver = new AStarSolverPar(graph, 4);
        solver.doSolve();

        System.out.print(graph.toString());

        final GraphExporter<Vertex,EdgeWithCost<Vertex>> vertexEdgeWithCostGraphExporter;
        vertexEdgeWithCostGraphExporter = new GraphExporter<Vertex, EdgeWithCost<Vertex>>();
        vertexEdgeWithCostGraphExporter.doExport(graph, new BufferedWriter(new OutputStreamWriter(System.out)));

    }



}

