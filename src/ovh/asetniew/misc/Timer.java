package ovh.asetniew.misc;

public class Timer {

    double lastStartTime;
    double lastEndTime;

    public Timer(){

    }

    public void start(){
        lastStartTime = System.nanoTime();
    }

    /**
     * Finishes measuring time and responds with the amount of time that passed in s.
     * @return
     */
    public double end(){
        lastEndTime = System.nanoTime();

        double time = lastEndTime - lastStartTime;

        return time / 1000000000;
    }

}
