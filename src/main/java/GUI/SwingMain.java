package GUI;

import CommonInterface.ISearchState;
import CommonInterface.ISolver;
import Graph.EdgeWithCost;
import Graph.Graph;
import Graph.Vertex;
import Util.Helper;
import com.alee.laf.WebLookAndFeel;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.paint.Color;
import lombok.Synchronized;
import org.graphstream.graph.implementations.Graphs;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerPipe;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 * Created by e on 7/08/17.
 */
public class SwingMain implements Runnable, IUpdatableState {

    public static final String STYLE_RESORUCE = "url('style.css')";
    private static final String procStr = "Processor";
    private static final ColorManager colorManager = new ColorManager();
    private static ISolver solver;
    private static org.graphstream.graph.Graph visualGraph = null;
    private static Viewer viewer = null;
    private static ViewPanel viewPanel = null;
    private static ViewerPipe viewerPipe = null;
    private static JFrame rootFrame;
    private static boolean inited = false;
    private ViewPanel viewPanel1;
    private JPanel panel1;
    private JButton startButton;
    private JFXPanel jfxPanel1;
    private JButton stopButton;
    private JProgressBar progressBar1;
    private SolverWorker solverWorker;
    private ScheduleChart<Number, String> scheduleChart;

    public SwingMain() {
        $$$setupUI$$$();
        startButton.addActionListener(actionEvent -> {
            solverWorker = new SolverWorker();
            solverWorker.execute();
        });
        Platform.runLater(() -> initFX(jfxPanel1));
        stopButton.addActionListener(actionEvent -> {
            if (solverWorker != null) {
                solverWorker.cancel(true);
                solverWorker = null;
            }
        });
    }

    public static void init(org.graphstream.graph.Graph graph, ISolver solveri) {
        visualGraph = Graphs.clone(graph);
        solver = solveri;
        initRest();
    }

    public static void init(Graph<? extends Vertex, ? extends EdgeWithCost> graph, ISolver solveri) {
        visualGraph = Helper.convertToGsGraph(graph);
        solver = solveri;
        initRest();
    }

    private static void initRest() {
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        WebLookAndFeel.install();
        viewer = new Viewer(visualGraph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        viewer.enableAutoLayout();
        viewerPipe = viewer.newViewerPipe();
        viewPanel = viewer.addDefaultView(false);
        visualGraph.addAttribute("ui.stylesheet", STYLE_RESORUCE);
        rootFrame = new JFrame();
        inited = true;
    }

    public void createUIComponents() {
        viewPanel1 = viewPanel;
        viewPanel1.setPreferredSize(new Dimension(500, 500));
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
            seriesArr[i].getData().add(new XYChart.Data(0, procStrNames[i], new ScheduleChart.ExtraData(0, "", "")));
        });
        String[] nodeNameArr = new String[visualGraph.getNodeSet().size()];
        for (int i = 0; i < seriesArr.length; i++) {
            nodeNameArr[i] = visualGraph.getNode(i).getId();
        }

        xAxis.setLabel("");
        xAxis.setTickLabelFill(Color.CHOCOLATE);
        xAxis.setMinorTickCount(4);

        yAxis.setLabel("");
        yAxis.setTickLabelFill(Color.CHOCOLATE);
        yAxis.setTickLabelGap(10);
        yAxis.setCategories(FXCollections.<String>observableArrayList(procStrNames));

        scheduleChart.setLegendVisible(false);
        scheduleChart.setBlockHeight(50);
        scheduleChart.getData().addAll(seriesArr);

        scheduleChart.getStylesheets().add("chart.css");
    }

    @Override
    public void run() {
        if (!inited) throw new RuntimeException(getClass() + " has to be initialise'd before running");
        progressBar1.setMaximum(visualGraph.getNodeSet().size());
        rootFrame.setContentPane(panel1);
        rootFrame.pack();
        rootFrame.setPreferredSize(new Dimension(1000, 1000));
        rootFrame.setMinimumSize(new Dimension(1000, 1000));
        rootFrame.setVisible(true);
    }

    @Override
    @Synchronized
    public void updateWithState(ISearchState searchState) {
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
                        , new ScheduleChart.ExtraData(cost, colorManager.next(), n.getId())));
                seriesList.add(series);
            }
        });
        int curSize = searchState.getNumVertices();
        if (progressBar1.getValue() < curSize) {
            progressBar1.setValue(curSize);
        }

        Platform.runLater(() -> {
            scheduleChart.getData().clear();
            XYChart.Series[] seriesArr = new XYChart.Series[seriesList.size()];
            scheduleChart.getData().addAll(seriesList.toArray(seriesArr));
        });
    }

    private void initFX(JFXPanel fxPanel) {
        Scene scene = new Scene(scheduleChart);
        fxPanel.setScene(scene);
        fxPanel.setPreferredSize(new Dimension(500, 500));
    }

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

    private static class ColorManager implements Iterator<String> {
        static final String[] stylesStr = {"status-red", "status-blue", "status-green", "status-magenta", "status-yellow", "status-cyan"};
        private AtomicInteger atomicInteger = new AtomicInteger();

        @Override
        public boolean hasNext() {
            return true;
        }

        @Override
        @Synchronized
        public String next() {
            int i = atomicInteger.incrementAndGet();
            if (i >= stylesStr.length) {
                atomicInteger.set(0);
                return stylesStr[0];
            } else return stylesStr[i];
        }
    }

    private class SolverWorker extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() throws Exception {
            solver.associateUI(SwingMain.this);
            solver.doSolveAndCompleteSchedule();
            return null;
        }

        @Override
        protected void done() {
            // print output, graph exporter
        }
    }

}
