package ovh.asetniew.ba;

import javafx.application.Platform;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart;

import java.util.Map;

public class BASimulationNonLinearUsingLists extends BASimulationUsingLists {
    private double gamma;
    public BASimulationNonLinearUsingLists(int m_0, int m, int maxSteps, double gamma, ScatterChart<Number, Number> scatterPlot, ScatterChart<Number, Number> scatterPlot2) throws Exception {
        super(m_0, m, maxSteps, scatterPlot,scatterPlot2);
        this.gamma = gamma;
    }

    @Override
    protected Integer call() throws Exception {
       Integer i = super.call();
        Platform.runLater(()->{
            loglogPlot.getData().get(loglogPlot.getData().size() - 1).setName("BB N = " + maxSteps + " m = " + m + " m_0 =" + m_0 );
            semilogPlot.getData().get(semilogPlot.getData().size() - 1).setName("BB N = " + maxSteps + " m = " + m + " m_0 =" + m_0 );
        });




       return i;
    }

    @Override
    protected void getTheoreticalLine(Map<Integer, Integer> map) {
        if(Double.compare(gamma, 1.0) == 0){
            super.getTheoreticalLine(map);
        }
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
