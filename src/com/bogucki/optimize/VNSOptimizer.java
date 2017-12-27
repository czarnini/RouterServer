package com.bogucki.optimize;


import com.bogucki.databse.DistanceHelper;

import java.util.ArrayList;

public class VNSOptimizer {
    private final ArrayList<String> meetings;
    private DistanceHelper distanceHelper;

    private Route currentBest = null;
    private Route opt2Result = null;

    public VNSOptimizer(ArrayList<String> meetings) {
        this.meetings = meetings;
        distanceHelper = new DistanceHelper(meetings);
    }

    public int[] optimize() {
        System.out.println("Start optimizing");
        initialize();
        int distance = 2;
        while (distance < meetings.size()) {

            opt2Result = currentBest.generateNeightbourRoute(distance);
            opt2();
            if(opt2Result.getCost() < currentBest.getCost()){
                System.out.println("new best found! " + opt2Result.getCost());
                currentBest = new Route(opt2Result);
                distance = 2;
            }else {
                System.out.println("No better route found =< ");
                distance += 2;
            }

        }
        System.out.println("Stop optimizing");
        return opt2Result.getCitiesOrder();
    }

    private void initialize() {
        opt2Result = Route.newRandomRoute(meetings.size(), distanceHelper);
        opt2();
        currentBest = new Route(opt2Result);
    }

    public ArrayList<String> getMeetings() {
        return meetings;
    }


    private void opt2() {
        for (int a = 0; a < 2000; a++) {
            for (int i = 0; i < meetings.size() - 2; i++) {
                for (int j = i + 2; j < meetings.size() - 1; j++) {
                    int distA = distanceHelper.getTime(i, i + 1, 0)
                            + distanceHelper.getTime(j, j + 1, 0);
                    int distB = distanceHelper.getTime(i, j, 0)
                            + distanceHelper.getTime(i + 1, j + 1, 0);

                    if (distA > distB) {
                        opt2Result.swap(i + 1, j);
                    }
                }
            }
            opt2Result.countCost();
        }
    }


}
