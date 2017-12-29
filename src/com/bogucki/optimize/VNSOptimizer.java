package com.bogucki.optimize;


import com.bogucki.databse.DistanceHelper;

import java.util.ArrayList;

public class VNSOptimizer {
    private final ArrayList<String> meetings;
    private DistanceHelper distanceHelper;

    private Route currentBest = null;
    private Route opt2Result = null;


    private static  int INITIAL_DISTANCE =4;
    private static  int DISTANCE_STEP = 2;

    public VNSOptimizer(ArrayList<String> meetings) {
        this.meetings = meetings;
        distanceHelper = new DistanceHelper(meetings);
    }

    public int[] optimize() {
        System.out.println("Start optimizing");
        initialize();

        for (int i = 0; i < 2000000; i++) {
            int distance = INITIAL_DISTANCE;
            while (distance < meetings.size()) {
                opt2Result = currentBest.generateNeightbourRoute(distance);
                opt2();
                if (opt2Result.getCost() < currentBest.getCost()) {
                    System.out.println("new best found! " + opt2Result.getCost());
                    currentBest = new Route(opt2Result);
                    distance = INITIAL_DISTANCE;
                } else {
                    distance += DISTANCE_STEP;
                }
            }
        }
        System.out.println("Stop optimizing");
        return opt2Result.getCitiesOrder();
    }

    private void initialize() {
        opt2Result = Route.newRandomRoute(meetings.size(), distanceHelper);
        for (int i = 0; i < 500; i++) {
            opt2();
        }
        currentBest = new Route(opt2Result);
    }


    private void opt2() {
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
