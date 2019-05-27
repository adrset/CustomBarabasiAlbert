package ovh.asetniew.ba;

import javafx.application.Platform;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart;

public class BASimulationNonLinearUsingLists extends BASimulationUsingLists {
    private double gamma;
    public BASimulationNonLinearUsingLists(int m_0, int m, int maxSteps, double gamma, ScatterChart<Number, Number> scatterPlot,ScatterChart<Number, Number> scatterPlot2) throws Exception {
        super(m_0, m, maxSteps, scatterPlot,scatterPlot2);
        this.gamma = gamma;
    }

    @Override
    protected Integer call() throws Exception {
       Integer i = super.call();
        Platform.runLater(()->{
            scatterPlot.getData().get(scatterPlot.getData().size() - 1).setName("NonLinear N = " + maxSteps + " m = " + m + " m_0 =" + m_0 );
            scatterPlot2.getData().get(scatterPlot2.getData().size() - 1).setName("NonLinear N = " + maxSteps + " m = " + m + " m_0 =" + m_0 );
        });




       return i;
    }

    @Override
    public float getProbability(int nodeNumber) {

        float level = (float) getNodeLevel(nodeNumber);
        float denominator = getAllNodeLevelRaisedToGamma();
        level = (float)Math.pow(level, gamma);
        level = level / denominator;

        return level;


    }

    public float getAllNodeLevelRaisedToGamma(){
        float sum = 0.0f;
        for (int ii = 0; ii < nodes.size() - 1; ii++) {
            float level = (float) getNodeLevel(ii);
            level = (float)Math.pow(level, gamma);
            sum += level;
        }

        return sum;

    }


}
