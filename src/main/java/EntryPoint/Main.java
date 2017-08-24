package EntryPoint;

import CommonInterface.ISolver;
import Exporter.GraphExporter;
import GUI.SwingMain;
import Graph.EdgeWithCost;
import Graph.Graph;
import Graph.Vertex;
import Solver.AStarSolverPar;
import Solver.SmartSolver;
import Util.Helper;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

import javax.swing.*;
import java.io.*;
import java.util.Collections;

public final class Main {

    private Main() {
        //Ensure this class is not instantiated
    }

    private static void callSolver(File file, int procN, int parN, OutputStream os) {
        Graph<Vertex, EdgeWithCost<Vertex>> graph = Helper.fileToGraph(file);
        ISolver solver;
        if (parN != 1) {
            solver = new AStarSolverPar(graph, procN); // TODO Parallel DFS/A* -> SmartSolver
        } else {
            solver = new SmartSolver(graph, procN);
        }
        solver.doSolve();

        final GraphExporter<Vertex, EdgeWithCost<Vertex>> vertexEdgeWithCostGraphExporter;
        vertexEdgeWithCostGraphExporter = new GraphExporter<Vertex, EdgeWithCost<Vertex>>();
        vertexEdgeWithCostGraphExporter.doExport(graph, new BufferedWriter(new OutputStreamWriter(os)));
    }

    public static void main(String[] args) {
        org.apache.log4j.BasicConfigurator.configure();
        Namespace ns = null;
        ArgumentParser argumentParser = ArgumentParsers.newArgumentParser("Scheduler")
                .defaultHelp(true)
                .description("A GPU Scheduling program");
        argumentParser.addArgument("-g", "--gui")
                .action(Arguments.storeTrue())
                .help("Choose whether to use GUI(Not implemented at the moment)");
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
                .setDefault(Collections.singletonList(1))
                .required(false)
                .help("Use parallel processing");
        argumentParser.addArgument("infile")
                .metavar("INFILENAME")
                .nargs(1)
                .required(true)
                .help("Filename to process");
        argumentParser.addArgument("outfile")
                .metavar("OUTFILENAME")
                .nargs("?")
                .required(false)
                .help("Output file name, write to STDOUT if non-specified");

        try {
            ns = argumentParser.parseArgs(args);
        } catch (ArgumentParserException e) {
            argumentParser.handleError(e);
            System.exit(1);
        }

        int procN, parN;
        String fileName;
        OutputStream os = null;
        boolean gui;

        gui = ns.getBoolean("gui");
        procN = (int) ns.getList("processors").get(0);
        parN = (int) ns.getList("parallel").get(0);
        fileName = (String) ns.getList("infile").get(0);
        String outfile = ns.getString("outfile");

        if (outfile == null) {
            os = new BufferedOutputStream(System.out);
        } else {
            try {
                os = new FileOutputStream(new File(outfile));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        File inputFile = new File(fileName);
        if (!inputFile.exists() || !inputFile.canRead()) {
            System.err.println("Can't open file");
        }

        if (gui) {
            Graph<Vertex, EdgeWithCost<Vertex>> graph = Helper.fileToGraph(inputFile);
            ISolver solver = new Solver.AStarSolver(graph, procN); // TODO DFS with GUI update, then change to SmartSolver
            SwingMain.init(graph, solver);
            SwingUtilities.invokeLater(new SwingMain());
        } else {
            callSolver(inputFile, procN, parN, os);
        }
    }
}

