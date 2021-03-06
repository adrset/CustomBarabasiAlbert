package ovh.asetniew.ba;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.chart.*;
import org.apache.commons.math3.fitting.WeightedObservedPoint;
import ovh.asetniew.ba.fitting.PowerDistributionFitter;
import ovh.asetniew.ba.nodes.Node;
import ovh.asetniew.misc.Timer;

import java.io.BufferedWriter;
import java.io.FileWriter;

import java.io.IOException;
import java.util.*;

public class BASimulationUsingLists extends Task<Integer> implements BASimulation {

    protected Random generator;

    protected final int MAX_FPS = 30000;

    protected int currentStep;

    protected int maxSteps;

    protected int m_0;

    protected int m;

    protected int dimension;

    protected Map<Integer, Integer> distribution;
    protected List<Node> nodes;
    protected double[] probability;

    protected Timer timer;
    protected ScatterChart<Number, Number> loglogPlot;
    protected ScatterChart<Number, Number> semilogPlot;
    protected XYChart.Series<Number, Number> semilogData = new XYChart.Series<>();
    protected XYChart.Series<Number, Number> loglogData = new XYChart.Series<>();
    protected XYChart.Series<Number, Number> semilogDataFitted = new XYChart.Series<>();
    protected XYChart.Series<Number, Number> loglogDataFitted = new XYChart.Series<>();

    public BASimulationUsingLists(int m_0, int m, int maxSteps, ScatterChart<Number, Number> loglogPlot, ScatterChart<Number, Number> semilogPlot) throws Exception {


        this.loglogPlot = loglogPlot;
        this.semilogPlot = semilogPlot;
        this.nodes = new ArrayList<>();
        this.timer = new Timer();
        this.m = m;
        this.m_0 = m_0;
        this.maxSteps = maxSteps;

        this.generator = new Random();

        for (int ii = 0; ii < m_0; ii++) {
            nodes.add(new Node());
        }


        if (this.m > this.m_0) {
            throw new Exception("m must not be larger than m!");
        }
        // The maximum number of nodes
        // set it to dimension = maxSteps * m + m_0;
        this.dimension = this.maxSteps * this.m + this.m_0;

        this.probability = new double[this.dimension];


        this.connectInitial();

    }

    private void connectInitial() {
        for (int ii = 0; ii < m_0; ii++) {
            for (int jj = 0; jj < m_0; jj++) {
                if (ii == jj) {
                    continue;
                }
                nodes.get(ii).nodes.add(nodes.get(jj));
            }
        }
    }

    protected void getTheoreticalLine(Map<Integer, Integer> map) {
        PowerDistributionFitter fitter = new PowerDistributionFitter();
        ArrayList<WeightedObservedPoint> points = new ArrayList<>();

        int maxX=0;
        for (int key : map.keySet()) {
            WeightedObservedPoint point = new WeightedObservedPoint(1.0,
                    key,
                    map.get(key));

            maxX = Math.max(key, maxX);
            points.add(point);
        }

        final double coeffs[] = fitter.fit(points);
        System.out.println(Arrays.toString(coeffs));

        loglogDataFitted = new XYChart.Series();
        semilogDataFitted = new XYChart.Series();

        //sc.lookup(".default-color0.chart-series-line");
        loglogDataFitted.setName("Fit");
        semilogDataFitted.setName("Fit");

        for (double x = 1.0; x < maxX; x+=0.1) {
            loglogDataFitted.getData().add(new XYChart.Data<>(x, coeffs[0]*Math.pow(x, coeffs[1])));
            semilogDataFitted.getData().add(new XYChart.Data<>(x, coeffs[0]*Math.pow(x, coeffs[1])));
        }

        Platform.runLater(()->{
            // Add series to charts
            loglogPlot.getData().add(loglogDataFitted);
            semilogPlot.getData().add(semilogDataFitted);
        });
    }


    public Map<Integer, Integer> getDegreeDistribution() {
        Map<Integer, Integer> map = new HashMap<>();


        for (int ii = 0; ii < nodes.size(); ii++) {
            int size = nodes.get(ii).nodes.size();
            if (map.get(size) == null) {
                map.put(size, 1);
            } else {
                map.put(size, map.get(size) + 1);
            }


        }

        //
        getTheoreticalLine(map);
        //


        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("p(k).txt"));

            for (int key : map.keySet()) {
                writer.write(key + "\t" + map.get(key) + "\n");
            }

            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return map;
    }

    @Override
    protected Integer call() throws Exception {
        try {
            begin();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Platform.runLater(() -> {
            loglogData = new XYChart.Series();

            //sc.lookup(".default-color0.chart-series-line");
            loglogData.setName("BA N = " + maxSteps + " m = " + m + " m_0 =" + m_0);
            semilogData.setName("BA N = " + maxSteps + " m = " + m + " m_0 =" + m_0);


            int i = 1;
            double maxX = -1;
            double minX = 10000000;
            double maxY = -1;
            double minY = 10000000;
            for (int key : distribution.keySet()) {
                loglogData.getData().add(new XYChart.Data<>(key, distribution.get(key)));
                semilogData.getData().add(new XYChart.Data<>(key, distribution.get(key)));
                maxX = Double.max(key, maxX);
                minX = Double.min(key, minX);
                maxY = Double.max(distribution.get(key), maxY);
                minY = Double.min(distribution.get(key), minY);
            }
            // Add series to charts
            loglogPlot.getData().add(loglogData);
            semilogPlot.getData().add(semilogData);

            // since their nodes are not null anymore, let's set the style to scatter

            ValueAxis axisX = (ValueAxis) loglogPlot.getXAxis();
            ValueAxis axisY = (ValueAxis) loglogPlot.getYAxis();

            axisX.setUpperBound(Math.pow(10, getPower(maxX) - 1));
            //axisY.setLowerBound(0.0001);
            axisY.setUpperBound(Math.pow(10, getPower(maxY) - 1));
            axisX = (ValueAxis) semilogPlot.getXAxis();
            axisY = (ValueAxis) semilogPlot.getYAxis();

            axisX.setUpperBound(Math.pow(10, getPower(maxX) - 1));
            //axisY.setLowerBound(0.0001);
            axisY.setUpperBound(Math.pow(10, getPower(maxY) - 1));

        });


        return 1;
    }

    public static int getPower(double x) {
        double ret = x;
        int power = 1;
        while (ret > 1) {
            ret /= 10;
            power++;
        }

        return power;
    }

    @Override
    protected void succeeded() {
        super.succeeded();
        System.out.println("Success!");
        updateProgress(maxSteps, maxSteps);
    }

    @Override
    protected void failed() {
        super.failed();
        System.out.println("Failed!");
    }

    public void begin() throws Exception {

        double timeToWait;
        double time;
        for (int ii = this.currentStep; ii < this.maxSteps; ii++) {
            timer.start();
            update();

            updateProgress(currentStep, maxSteps);
            time = timer.end();

            if (time > 1f / MAX_FPS) {
                continue;
            }

            timeToWait = Math.abs(1f / MAX_FPS - time) * 1000f;


            while (timeToWait > 0) {
                Thread.sleep(1);
                timeToWait -= 1f;
            }
        }
        System.out.println("Total time: " + timer.getTotalTime());
        printNeightbourMatrix();
        distribution = getDegreeDistribution();


    }


    public void printNeightbourMatrix() {
        printNeightbourMatrix(this.m_0 + this.currentStep * this.m);
    }

    public void printNeightbourMatrix(int dim) {
        try {
            String str = "Hello";
            BufferedWriter writer = new BufferedWriter(new FileWriter("out.txt"));

            StringBuilder ab = new StringBuilder();

            for (int ii = 0; ii < nodes.size(); ii++) {

                for (int jj = 0; jj < nodes.size(); jj++) {

                    if (nodes.get(ii).nodes.contains(nodes.get(jj))) {
                        ab.append("1");

                    } else {
                        ab.append("0");
                    }

                    if (jj != nodes.size() - 1) {
                        ab.append(" ");
                    }

                }

                if (ii != nodes.size() - 1) {
                    ab.append("\n");
                }


            }

            writer.write(ab.toString());

            writer.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void printAdjacencyList() {
        try {
            String str = "Hello";
            BufferedWriter writer = new BufferedWriter(new FileWriter("out.txt"));

            StringBuilder ab = new StringBuilder();
            ab.append("{");
            for (int ii = 0; ii < nodes.size(); ii++) {
                ab.append("{");

                Set<Node> childNodes = nodes.get(ii).nodes;

                Iterator<Node> iter = childNodes.iterator();

                while (iter.hasNext()) {
                    Node child = iter.next();
                    int index = nodes.indexOf(child);
                    if (index != -1) {
                        if (iter.hasNext()) {
                            ab.append(index + ", ");
                        } else {
                            ab.append(index);
                        }
                    }
                }


                if (ii < nodes.size() - 1) {
                    ab.append("},");
                } else {
                    ab.append("}");
                }


            }
            ab.append("}");
            writer.write(ab.toString());

            writer.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected void addNode() {
        int index = this.m_0 + this.currentStep * this.m;
        Node node = new Node();
        nodes.add(node);
        // make m new connections for new node
        for (int ii = 0; ii < this.m; ii++) {
            linkToExistingNode(node);

        }


    }

    private void linkToExistingNode(Node node) {
        double sum = 0.0;

        for (int ii = 0; ii < nodes.size() - 1; ii++) {
            probability[ii] = getProbability(ii);
            sum += probability[ii];
        }


        double randomNumber = generator.nextDouble() * sum;

        sum = 0.0;
        int foundIndex = -1;

        for (int ii = 0; ii < nodes.size() - 1; ii++) {

            sum += probability[ii];


            if (sum > randomNumber) {


                foundIndex = ii;

                break;
            }
        }

        try {
            if (foundIndex == -1) {
                throw new Exception("WTH");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        linkTwoNodes(nodes.size() - 1, foundIndex);


    }

    public void linkTwoNodes(int i, int j) {
        nodes.get(i).nodes.add(nodes.get(j));
        nodes.get(j).nodes.add(nodes.get(i));
    }


    /**
     * @param number index of the node
     * @return number of node connections
     */
    @Override
    public int getNodeLevel(int number) {
        int level = 0;
        Node node = nodes.get(number);
        for (int jj = 0; jj < node.nodes.size(); jj++) {
            level += 1;
        }

        return level;
    }


    public void update() {
        addNode();
        this.currentStep += 1;

    }

    /**
     * @param nodeNumber a number of node
     * @return the probability of linking to specified node
     */
    @Override
    public float getProbability(int nodeNumber) {
        float level = (float) getNodeLevel(nodeNumber);

        return level / (2.0f * this.m * (this.currentStep + 1));
    }


    public int getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(int currentStep) {
        this.currentStep = currentStep;
    }

    public int getMaxSteps() {
        return maxSteps;
    }

    public void setMaxSteps(int maxSteps) {
        this.maxSteps = maxSteps;
    }

    public int getM_0() {
        return m_0;
    }

    public void setM_0(int m_0) {
        this.m_0 = m_0;
    }

    public int getM() {
        return m;
    }

    public void setM(int m) {
        this.m = m;
    }

    public int getDimension() {
        return dimension;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }
}
