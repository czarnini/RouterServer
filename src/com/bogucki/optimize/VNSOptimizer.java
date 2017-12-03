package com.bogucki.optimize;


public class VNSOptimizer {
    private  Meeting[] meetings;

    private Route currentBest = null;
    public VNSOptimizer(Meeting[] meetings) {
        this.meetings = meetings;
    }

    public void optimize(){

    }

    public void initialize(){


    }

    public Meeting[] getMeetings() {
        return meetings;
    }
}
