package ovh.asetniew.ba;

import javafx.scene.chart.ScatterChart;

public class BASimulationNonLinearUsingLists extends BASimulationUsingLists {
    private double gamma;
    public BASimulationNonLinearUsingLists(int m_0, int m, int maxSteps, double gamma, ScatterChart<Number, Number> scatterPlot) throws Exception {
        super(m_0, m, maxSteps, scatterPlot);
        this.gamma = gamma;
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
