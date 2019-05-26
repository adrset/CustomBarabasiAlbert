package ovh.asetniew.ba;
import ovh.asetniew.misc.Timer;

import java.io.BufferedWriter;
import java.io.FileWriter;

import java.util.Map;
import java.util.Random;

public class BASimulationUsingMatrix implements BASimulation, Runnable {

    private Random generator;

    private final int MAX_FPS = 30000;

    private int currentStep;

    private int maxSteps;

    private int m_0;

    private int m;

    private int dimension;

    private boolean[][] neighbours;
    private double[] probability;

    private Timer timer;

    public BASimulationUsingMatrix(int m_0, int m, int maxSteps) throws Exception {
        this.timer = new Timer();
        this.m = m;
        this.m_0 = m_0;
        this.maxSteps = maxSteps;

        this.generator = new Random();

        if (this.m > this.m_0) {
            throw new Exception("m must not be larger than m!");
        }
        // The maximum number of nodes
        // set it to dimension = maxSteps * m + m_0;
        this.dimension = this.maxSteps * this.m + this.m_0;
        // Neightbourship matrix
        this.neighbours = new boolean[this.dimension][];

        this.probability = new double[this.dimension];

        for (int ii = 0; ii < this.dimension; ii++) {
            this.neighbours[ii] = new boolean[this.dimension];
        }

        this.connectInitial();

    }

    public Map<Integer, Integer> getDegreeDistribution(){

        return null;
    }

    private void connectInitial() {
        for (int ii = 0; ii < this.m_0; ii++) {
            for (int jj = 0; jj < this.m_0; jj++) {
                if (ii == jj) {
                    this.neighbours[ii][jj] = false;
                } else {
                    this.neighbours[ii][jj] = true;
                }
            }
        }
    }

    @Override
    public void run() {
        try {
            begin();
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public void begin() throws Exception{

        double timeToWait;
        double time;
        for (int ii = this.currentStep; ii < this.maxSteps; ii++) {
            timer.start();
            update();
            float val = ( (float) currentStep / (float)maxSteps) * 100f;
            System.out.println(String.format("\r%d%%",(int)val));

            time = timer.end();

            if(time > 1f  / MAX_FPS){
                continue;
            }

            timeToWait = Math.abs(1f/ MAX_FPS - time) * 1000f;


            while(timeToWait > 0) {
                Thread.sleep(1);
                timeToWait -= 1f;
            }
        }

        System.out.println("Total time: " + timer.getTotalTime());

        printNeightbourMatrix();

    }



    public void printNeightbourMatrix(){
        printNeightbourMatrix(this.m_0 + this.currentStep * this.m);
    }

    public void printNeightbourMatrix(int dim){
        try {
            String str = "Hello";
            BufferedWriter writer = new BufferedWriter(new FileWriter("out.txt"));

        StringBuilder ab = new StringBuilder();

        for (int ii = 0; ii < dim; ii++) {

            for (int jj = 0; jj < dim; jj++) {
                ab.append(neighbours[ii][jj] ? 1 : 0);

                if(jj != dim - 1){
                    ab.append(" ");
                }
            }
            if(ii != dim-1)
                ab.append("\n");

        }

        writer.write(ab.toString());

        writer.close();

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void addNode(){
        int index = this.m_0 + this.currentStep * this.m;

        // make m new connections for new node
        for(int ii=0 ; ii < this.m; ii++){
            linkToExistingNode(index);

        }
    }

    private void linkToExistingNode(int index){
        double sum = 0.0;

        for(int ii=0 ; ii < index; ii++) {
            probability[ii] = getProbability(ii);
            sum += probability[ii];
        }


        double randomNumber = generator.nextDouble() * sum;

        sum = 0.0;
        int foundIndex = -1;

        for(int ii=0 ; ii < index; ii++){

            sum += probability[ii];



            if (sum > randomNumber){


                foundIndex = ii;

                break;
            }
        }

        try {
            if (foundIndex == -1) {
                throw new Exception("WTH");
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

        linkTwoNodes(index, foundIndex);



    }

    public void linkTwoNodes(int i, int j){
        neighbours[i][j] = true;
        neighbours[j][i] = true;
    }


    /**
     *
     * @param number index of the node
     * @return number of node connections
     */
    @Override
    public int getNodeLevel(int number){
        int level = 0;

        for(int jj = 0 ; jj < this.dimension; jj++){
            level += this.neighbours[number][jj] ? 1  : 0;
        }

        return level;
    }


    public void update(){
        addNode();
        this.currentStep += 1;

    }

    /**
     *
     * @param nodeNumber a number of node
     * @return the probability of linking to specified node
     */
    @Override
    public float getProbability(int nodeNumber) {
        float level = (float) getNodeLevel(nodeNumber);

        return level / (2.0f * this.m * (this.currentStep+1));
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
