package ovh.asetniew.ba;

import javafx.concurrent.Task;

/**
 * Start should be moved outside
 */
public interface BASimulation {
    void begin() throws Exception;
    void linkTwoNodes(int i, int j);
    float getProbability(int nodeNumber);
    int getNodeLevel(int number);
    void getDegreeDistribution();
}
