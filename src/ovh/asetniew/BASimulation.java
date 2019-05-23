package ovh.asetniew;

public class BASimulation implements BAModification{

    private int currentStep;

    private int maxSteps;

    private int m_0;

    private int m;

    private int dimension;

    private boolean[][] neighbours;

    BASimulation(int m_0, int m, int maxSteps) throws Exception {
        this.m = m;
        this.m_0 = m_0;
        this.maxSteps = maxSteps;

        if (this.m > this.m_0) {
            throw new Exception("m must not be larger than m!");
        }
        // The maximum number of nodes
        // set it to dimension = maxSteps * m + m_0;
        this.dimension = this.maxSteps * this.m + this.m_0;
        // Neightbourship matrix
        this.neighbours = new boolean[this.dimension][];

        for (int ii = 0; ii < this.dimension; ii++) {
            this.neighbours[ii] = new boolean[this.dimension];
        }

        this.connectInitial();

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

    void printNeightbourMatrix(){
        printNeightbourMatrix(this.m_0 + this.currentStep * this.m);
    }

    void printNeightbourMatrix(int dim){
        StringBuilder ab = new StringBuilder();

        for (int ii = 0; ii < this.m_0; ii++) {
            ab.append("| ");
            for (int jj = 0; jj < this.m_0; jj++) {
                ab.append(neighbours[ii][jj] ? 1 : 0).append(", ");
            }
            ab.delete(ab.length() - 2, ab.length()-1);
            ab.append("|");
            ab.append("\n");
        }

        System.out.println(ab);
    }

    private void addNode(){
        int index = this.m_0 + this.currentStep * this.m;

        for(int ii=0 ; ii < this.m; ii++){


        }

        System.out.println(index);
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
}
