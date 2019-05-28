package ovh.asetniew.ba;


import javafx.application.Platform;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart;
import ovh.asetniew.ba.BASimulationUsingLists;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BASimulationPreferentialVarietyUsingLists extends BASimulationUsingLists {
    private List<Float> adaptiveCoefficients;
    public BASimulationPreferentialVarietyUsingLists(int m_0, int m, int maxSteps, ScatterChart<Number, Number> scatterPlot, ScatterChart<Number, Number> scatterPlot2) throws Exception {
        super(m_0, m, maxSteps, scatterPlot,scatterPlot2);

        adaptiveCoefficients = new ArrayList<>();
        for(int ii=0; ii < m_0; ii++){
            this.adaptiveCoefficients.add((float)generator.nextGaussian());
        }
    }

    @Override
    protected Integer call() throws Exception {
        Integer i = super.call();
        Platform.runLater(()->{
            loglogPlot.getData().get(loglogPlot.getData().size() - 1).setName("Adaptive N = " + maxSteps + " m = " + m + " m_0 =" + m_0 );
            semilogPlot.getData().get(semilogPlot.getData().size() - 1).setName("Adaptive N = " + maxSteps + " m = " + m + " m_0 =" + m_0 );
        });




        return i;
    }

    @Override
    protected void addNode() {
        this.adaptiveCoefficients.add(generator.nextFloat());
        super.addNode();

    }

    @Override
    public float getProbability(int nodeNumber) {

        float level = (float) getNodeLevel(nodeNumber);
        float denominator = getAllNodeLevel();
        level = level*adaptiveCoefficients.get(nodeNumber);
        level = level / denominator;

        return level;


    }

    public float getAllNodeLevel(){
        float sum = 0.0f;
        for (int ii = 0; ii < nodes.size() - 1; ii++) {
            float level = (float) getNodeLevel(ii);
            level = adaptiveCoefficients.get(ii)*level;
            sum += level;
        }

        return sum;

    }


}
