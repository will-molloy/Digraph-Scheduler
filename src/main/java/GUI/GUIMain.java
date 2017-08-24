package GUI;

import CommonInterface.ISearchState;
import CommonInterface.ISolver;
import Exporter.GraphExporter;
import GUI.Events.SolversThread;
import GUI.Events.SysInfoMonitoringThread;
import GUI.Frame.DataVisualization;
import GUI.Frame.GUIEntry;
import GUI.Frame.view.Controller;
import GUI.Interfaces.GUIMainInterface;
import GUI.Util.ColorManager;
import GUI.Models.GMouseManager;
import GUI.Models.SysInfoModel;
import Graph.EdgeWithCost;
import Graph.Graph;
import Graph.Vertex;
import Solver.AbstractSolver;
import Util.Helper;
import com.alee.laf.WebLookAndFeel;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.embed.swing.JFXPanel;
import javafx.embed.swing.SwingNode;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import lombok.Synchronized;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Created by e on 7/08/17.
 */
public class GUIMain implements GUIMainInterface {

//    public static final String STYLE_RESORUCE = "url('style.css')";



//    private static ViewerPipe viewerPipe = null;
    private static JFrame rootFrame;
    private static boolean inited = false;

    private JPanel panel1;
    private JButton startButton;
    private JFXPanel jfxPanel1;
    private JButton stopButton;

//    private SolverWorker solverWorker;

    //TODO - PASS THE FOLLOWING TO CONTROLLER (DECLARE THEM AS PUBLIC STATIC FIELDS)
    private static org.graphstream.graph.Graph visualGraph = null;
    private static GraphViewer viewer = null;
    private static ViewPanel viewPanel = null;
    private ViewPanel viewPanel1;

    private JProgressBar progressBar1;

    private ScheduleChart<Number, String> scheduleChart;
    private static final String procStr = "Processor";

    private static ISolver solver;

    private static Graph _graph;

    private static SolversThread solversThread = null;

    private static SysInfolPieChart sysInfolPieChart = SysInfolPieChart.getInstance();

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        panel1 = new JPanel();
        panel1.setLayout(new FormLayout("fill:min(d;500px):grow,left:4dlu:noGrow,fill:min(d;500px):grow", "center:d:grow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,top:4dlu:noGrow"));
        CellConstraints cc = new CellConstraints();
        panel1.add(viewPanel1, cc.xy(1, 1));
        stopButton = new JButton();
        stopButton.setText("Stop");
        panel1.add(stopButton, cc.xy(3, 7));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel1.add(scrollPane1, cc.xy(3, 1, CellConstraints.FILL, CellConstraints.FILL));
        scrollPane1.setViewportView(jfxPanel1);
        startButton = new JButton();
        startButton.setText("Start");
        panel1.add(startButton, cc.xy(1, 7));
        progressBar1 = new JProgressBar();
        panel1.add(progressBar1, cc.xyw(1, 5, 3, CellConstraints.FILL, CellConstraints.DEFAULT));
        final JLabel label1 = new JLabel();
        label1.setText("Progress:");
        panel1.add(label1, cc.xy(1, 3));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }

//    public static final String STYLE_RESORUCE = "url('style.css')";


//    private class SolverWorker extends SwingWorker<Void, Void> {
//
//        @Override
//        protected Void doInBackground() throws Exception {
//            solver.associateUI(GUIMain.this);
//            solver.doSolve();
//            return null;
//        }
//
//        /**
//         * Executed on the <i>Event Dispatch Thread</i> after the {@code doInBackground}
//         * method is finished. The default
//         * implementation does nothing. Subclasses may override this method to
//         * perform completion actions on the <i>Event Dispatch Thread</i>. Note
//         * that you can query status inside the implementation of this method to
//         * determine the result of this task or whether this task has been cancelled.
//         *
//         * @see #doInBackground
//         * @see #isCancelled()
//         * @see #get
//         */
//        @Override
//        protected void done() {
//            viewer.colorNodes(visualGraph);
//            progressBar1.setValue(progressBar1.getMaximum());
//            // print output, graph exporter
//            System.out.println(GraphExporter.exportGraphToString(_graph));
////            System.out.println(Helper.gsGraphToDOTString(visualGraph));
//        }
//
//
//    }

    public GUIMain() {
        $$$setupUI$$$();
        //TODO - delete the following codes after implementing buttons function in Controller (CustomButton)
        startButton.addActionListener(actionEvent -> {
//            solverWorker = new SolverWorker();
//            solverWorker.execute();
            solversThread = new SolversThread(GUIMain.this, solver);
            solversThread.addListener(GUIMain.this);
            solversThread.start();
            stopButton.setEnabled(true);
        });
        stopButton.addActionListener(actionEvent -> {
            if ((solversThread != null) && (solversThread.isAlive())) {
                solversThread.suspend();
                startButton.removeActionListener(startButton.getActionListeners()[0]);
                startButton.addActionListener(actionEventResume -> {
                    solversThread.resume();
                });
            }
        });
        stopButton.setEnabled(false);
        SysInfoMonitoringThread sysInfoMonitoringThread = new SysInfoMonitoringThread(SysInfoModel.getInstance());
        sysInfoMonitoringThread.addListener(GUIMain.this);
        sysInfoMonitoringThread.start();
    }

    //TODO - MIGRATE TO CONTROLLER
    public void createUIComponents() {
        viewPanel1 = viewPanel;
        viewPanel1.setPreferredSize(new Dimension(500, 1000));
        jfxPanel1 = new JFXPanel();

        final NumberAxis xAxis = new NumberAxis();
        final CategoryAxis yAxis = new CategoryAxis();
        final int procN = solver.getProcessorCount();
        String[] procStrNames = new String[procN];
        scheduleChart = new ScheduleChart<>(xAxis, yAxis);
        XYChart.Series[] seriesArr = new XYChart.Series[solver.getProcessorCount()];

        IntStream.range(0, procN).forEach(i -> {
            XYChart.Series series = new XYChart.Series();
            seriesArr[i] = series;
            procStrNames[i] = procStr.concat(String.valueOf(i + 1));
            seriesArr[i].getData().add(new XYChart.Data(0, procStrNames[i], new ScheduleChart.ExtraData(0, "rgba(0,0,0,0), rgba(0,0,0,0)", "rgba(0,0,0,0);","")));
        });
        String[] nodeNameArr = new String[visualGraph.getNodeSet().size()];
        for (int i = 0; i < seriesArr.length; i++) {
            nodeNameArr[i] = visualGraph.getNode(i).getId();
        }

        xAxis.setLabel("");
        xAxis.setMinorTickCount(4);

        yAxis.setLabel("");
        yAxis.setTickLabelGap(10);
        yAxis.setCategories(FXCollections.observableArrayList(procStrNames));

        scheduleChart.setLegendVisible(false);
        scheduleChart.setBlockHeight(50);
        scheduleChart.getData().addAll(seriesArr);

        scheduleChart.getStylesheets().add(DataVisualization.class.getResource("view/GanttChart.css").toExternalForm());
        Platform.runLater(() -> initFX(jfxPanel1)); //TODO - WHEN MOVED TO CONTROLLER, USE THE PANE IT PROVIDES INSTEAD
    }

    private void initFX(JFXPanel fxPanel) {
//        scheduleChart.setPrefHeight(900);
//        scheduleChart.setPrefWidth(1200);
//        scheduleChart.setMinWidth(500);
//        ScrollPane scrollPane = new ScrollPane();
//        scrollPane.setContent(scheduleChart);
//        Scene scene = new Scene(scrollPane);
        Scene scene = new Scene(scheduleChart);
        fxPanel.setScene(scene);
        fxPanel.setPreferredSize(new Dimension(500, 500));
    }

    //TODO - MIGRATE TO CONTROLLER
    public static void init(Graph<? extends Vertex, ? extends EdgeWithCost> graph, ISolver solveri) {
        _graph = graph;
        visualGraph = Helper.convertToGsGraph(graph);
        visualGraph.addAttribute("ui.stylesheet", "url('./src/main/java/GUI/Frame/view/Graph.css')");
        solver = solveri;


        Controller.graph = graph;
        Controller.visualGraph = Helper.convertToGsGraph(graph);
        Controller.visualGraph.addAttribute("ui.stylesheet", "url('target/classes/GUI/Frame/view/Graph.css')");
        Controller.solver = solveri;
        ColorManager.generateColor(solveri.getProcessorCount());
//        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
//        Controller.viewer = new GraphViewer(Controller.visualGraph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
//        Controller.viewer.initializeLabels(Controller.visualGraph);
//        Controller.viewer.enableAutoLayout();
//        Controller.viewPanel = Controller.viewer.addDefaultView(false);
//        Controller.viewPanel.addMouseListener(new GMouseManager(Controller.viewPanel, Controller.visualGraph, Controller.viewer));
//        Controller.viewPanel.addMouseWheelListener(new GMouseManager(Controller.viewPanel, Controller.visualGraph, Controller.viewer));

//        SwingUtilities.invokeLater(() -> {
//            System.out.println("test run");
//            Controller.swingNode = new SwingNode();
//            Controller.swingNode.setContent(Controller.viewPanel);
//        });


        initRest();
    }

    //TODO - MIGRATE TO CONTROLLER
    private static void initRest() {
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        WebLookAndFeel.install();
        viewer = new GraphViewer(visualGraph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        viewer.initializeLabels(visualGraph); //make sure this method get called immediately after the call to constructor
        viewer.enableAutoLayout();
        viewPanel = viewer.addDefaultView(false);
        viewPanel.addMouseListener(new GMouseManager(viewPanel, visualGraph, viewer));
        viewPanel.addMouseWheelListener(new GMouseManager(viewPanel, visualGraph, viewer));

        rootFrame = new JFrame();
        inited = true;
    }

    @Override
    public void run() {

        //TODO - SET NEW ENTRY POINT WHEN ALL FINISH - This call is blocking, move features to Controller, keep this as a connection point.
        //TODO - pass essential params to Controller.
        Application.launch(GUIEntry.class);

//        if (!inited) throw new RuntimeException(getClass() + " has to be initialise'd before running");
//        //estimate of the maximum number of states
//        progressBar1.setMaximum((int) (Math.pow((double) visualGraph.getNodeCount(),
//                (double) solver.getProcessorCount()) / Math.pow(2d, (double) (solver.getProcessorCount() + 1))
//                / (double) visualGraph.getNodeCount()));
//        rootFrame.setContentPane(panel1);
//        rootFrame.pack();
//        rootFrame.setPreferredSize(new Dimension(1000, 1000));
//        rootFrame.setMinimumSize(new Dimension(1000, 1000));
//        rootFrame.setVisible(true);
//
//        Platform.runLater(sysInfolPieChart);
    }

    @Override
    @Synchronized
    public void updateWithState(ISearchState searchState, AbstractSolver solver) {
        if (searchState == null) return;
        // Remove the past data
        visualGraph.getNodeSet().forEach(e -> {
            e.removeAttribute("ui.class");
            e.removeAttribute("processor");
            e.removeAttribute("startTime");
        });
        int[] processors = searchState.getProcessors();
        int[] startTimes = searchState.getStartTimes();
        List<XYChart.Series> seriesList = new ArrayList<>();
        visualGraph.getNodeSet().forEach(n -> {
            int index = n.getIndex();
            int procOn = processors[index];
            if (procOn != -1) {
                XYChart.Series series = new XYChart.Series();
                n.addAttribute("ui.class", "sched");
                n.addAttribute("processor", procOn + 1);
                n.addAttribute("startTime", startTimes[index]);

                Integer cost = null;
                try {
                    Double d = n.getAttribute("Weight");
                    cost = d.intValue();
                } catch (ClassCastException e) {
                    cost = n.getAttribute("Weight");
                    // Weight attr has to be double or int
                }
                series.getData().add(new XYChart.Data(startTimes[index]
                        , procStr + String.valueOf(procOn + 1)
                        , new ScheduleChart.ExtraData(cost, ColorManager.getColorSetForGanttTasks().get(String.valueOf(procOn + 1)),
                        ColorManager.getColorSetForGanttTasksBorder().get(String.valueOf(procOn + 1)), n.getId())));

                seriesList.add(series);
            }
        });

        //update the GS viewer
        viewer.updateNodes();

        //update progress bar
        int curSize = solver.getStateCounter();
        if (progressBar1.getValue() < curSize) {
            progressBar1.setValue(curSize);
            progressBar1.setMaximum(curSize + 10000000);
        }

        Platform.runLater(() -> {
            scheduleChart.getData().clear();
            XYChart.Series[] seriesArr = new XYChart.Series[seriesList.size()];
            scheduleChart.getData().addAll(seriesList.toArray(seriesArr));
            scheduleChart.setBlockHeight(1000d / solver.getProcessorCount());
        });
    }

    @Override
    public void notifyOfSolversThreadComplete() {
        //Update colorNode AND set progress bar to maximum here;
        viewer.colorNodes(visualGraph);
        progressBar1.setValue(progressBar1.getMaximum());
        startButton.setEnabled(false);
        stopButton.setEnabled(false);
        // print output, graph exporter
        System.out.println(GraphExporter.exportGraphToString(_graph));
    }

    @Override
    public void notifyOfSysInfoThreadUpdate() {
        updateSysInfo(SysInfoModel.getInstance());
    }

    @Override
    public void updateSysInfo(SysInfoModel sysInfoModel) {
        SysInfolPieChart.getInstance().updateCPUChart(sysInfoModel.getCpuPercentage(), sysInfoModel.getMem().getUsedPercent());
        //TODO - Update sys info in GUI.
        //TODO - delete following statements once GUI implementation finishes
//        System.out.print("Memory Usage: " + sysInfoModel.getMem().getUsedPercent()+"\t");
//        System.out.println("CPU Usage:    " + (sysInfoModel.getCpuPerc().getCombined()*100)+"\t");
    }

}
