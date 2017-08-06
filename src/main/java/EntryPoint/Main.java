package EntryPoint;

import Exporter.GraphExporter;
import FileUtilities.FileSinkSpecialDot;
import Parser.EdgeCtor;
import Parser.InputParser;
import Parser.VertexCtor;
import Solver.*;
import CommonInterface.ISolver;
import Util.Helper;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.stream.file.FileSink;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceDOT;
import Graph.Vertex;
import Graph.EdgeWithCost;

import java.io.*;

public class Main {

    private Main(){
        //Ensure this class is not instantiated
    }
        /*
         * Now this is the place where namespaces come in handy!
         * Sadly, we are in Java
         */

    private static void callSolverOld(File file, int procN, int parN) {
        Graph.Graph<Vertex, EdgeWithCost<Vertex>> graph;

        InputParser<Vertex, EdgeWithCost<Vertex>> parser = new InputParser<Vertex, EdgeWithCost<Vertex>>(new VertexCtor(), new EdgeCtor());
        graph = parser.doParseAndFinaliseGraph(file);
        System.out.print(graph.toString());

        ISolver solver;
        if(parN != 1) {
            solver = new SolverOld.AStarSolverPar(graph, procN);
        }
        else {
            solver = new SolverOld.AStarSolver(graph, procN);
        }
        solver.doSolve();


        final GraphExporter<Vertex,EdgeWithCost<Vertex>> vertexEdgeWithCostGraphExporter;
        vertexEdgeWithCostGraphExporter = new GraphExporter<Vertex, EdgeWithCost<Vertex>>();
        vertexEdgeWithCostGraphExporter.doExport(graph, new BufferedWriter(new OutputStreamWriter(System.out)));
    }

    private static void callGSSolver(File file, int procN, int parN) {
        org.graphstream.graph.Graph g = new DefaultGraph("g");

        FileSource fs = new FileSourceDOT();
        fs.addSink(g);
        try {
            fs.readAll(new BufferedInputStream(new FileInputStream(file)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Helper.finalise(g);

        ISolver solver;
        if(parN != 1) {
            solver = new AStarSolverPar(g, procN);
        }
        else {
            solver = new AStarSolver(g, procN);
        }
        solver.doSolve();

        Helper.stripUneeded(g);

        FileSink sink = new FileSinkSpecialDot("88");
        try {
            sink.writeAll(g, new BufferedOutputStream(System.out));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        Namespace ns = null;
        ArgumentParser argumentParser = ArgumentParsers.newArgumentParser("HDNW")
                .defaultHelp(true)
                .description("A GPU Scheduling program");
        argumentParser.addArgument("-l", "--library")
                .choices("gs", "old")
                .setDefault("gs")
                .required(false)
                .help("Choose library to use. gs -> Use graphstream library, old -> use old parser and datastructure");
        argumentParser.addArgument("-a", "--algorithm")
                .choices("as", "bnb")
                .setDefault("as")
                .required(false)
                .help("Choose the algorithm to use");
        argumentParser.addArgument("-p", "--processors")
                .metavar("N")
                .required(true)
                .type(Integer.class)
                .nargs(1)
                .help("Processor count");
        argumentParser.addArgument("-r", "--parallel")
                .metavar("M")
                .type(Integer.class)
                .nargs(1)
                .setDefault("1")
                .required(false)
                .help("Use parallel processing");
        argumentParser.addArgument("file")
                .metavar("FILENAME")
                .nargs(1)
                .required(true)
                .help("Filename to process");

        try {
            ns = argumentParser.parseArgs(args);
        } catch (ArgumentParserException e) {
            argumentParser.handleError(e);
            System.exit(1);
        }

        int procN, parN;
        String fileName, libraryStr;

        procN = (int) ns.getList("processors").get(0);
        parN = (int) ns.getList("parallel").get(0);
        fileName = (String) ns.getList("file").get(0);
        libraryStr = ns.getString("library");

        File inputFile = new File(fileName);
        if(!inputFile.exists() || !inputFile.canRead()) {
            System.err.println("Can't open file");
        }

        if(libraryStr.matches("gs"))
            callGSSolver(inputFile, procN, parN);
        else if(libraryStr.matches("old"))
            callSolverOld(inputFile, procN, parN);

    }


}
