package ovh.asetniew.ba;

import javafx.concurrent.Task;

import java.util.Map;

/**
 * Start should be moved outside
 */
public interface BASimulation {
    void begin() throws Exception;
    void linkTwoNodes(int i, int j);
    float getProbability(int nodeNumber);
    int getNodeLevel(int number);
    Map<Integer, Integer> getDegreeDistribution();
}
