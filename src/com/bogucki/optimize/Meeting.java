package com.bogucki.optimize;

public class Meeting {
    private Client client;
    private long timeEarliestPossible;
    private long timeLatesttPossible;

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public long getTimeEarliestPossible() {
        return timeEarliestPossible;
    }

    public void setTimeEarliestPossible(long timeEarliestPossible) {
        this.timeEarliestPossible = timeEarliestPossible;
    }

    public long getTimeLatesttPossible() {
        return timeLatesttPossible;
    }

    public void setTimeLatesttPossible(long timeLatesttPossible) {
        this.timeLatesttPossible = timeLatesttPossible;
    }

    public long getOptimalTimeOfStart() {
        return optimalTimeOfStart;
    }

    public void setOptimalTimeOfStart(long optimalTimeOfStart) {
        this.optimalTimeOfStart = optimalTimeOfStart;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    private long optimalTimeOfStart;
    private long duration;
}
