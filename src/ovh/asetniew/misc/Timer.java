package ovh.asetniew.misc;

public class Timer {

    double lastStartTime;
    double lastEndTime;
    double totalTime;
    double firstStartTime = 0;

    public Timer(){
        totalTime = 0;
    }

    public double getTotalTime(){
        return (System.nanoTime() - this.firstStartTime)/ 1000000000;
    }


    public void start(){
        if(firstStartTime == 0){
            firstStartTime =System.nanoTime();
        }

        lastStartTime = System.nanoTime();
    }

    /**
     * Finishes measuring time and responds with the amount of time that passed in s.
     * @return
     */
    public double end(){
        lastEndTime = System.nanoTime();

        double time = lastEndTime - lastStartTime;
        totalTime += time / 1000000000;
        return time / 1000000000;
    }

}
