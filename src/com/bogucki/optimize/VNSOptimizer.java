package com.bogucki.optimize;


import com.bogucki.databse.DistanceHelper;

import java.util.ArrayList;

public class VNSOptimizer {
    private volatile ArrayList<Meeting> meetings;
    private volatile DistanceHelper distanceHelper;

    public static volatile Route currentBest = null;

    private Route myCurrentBest = null;


    private static int INITIAL_DISTANCE = 4;

    private static int DISTANCE_STEP = 8;

    public VNSOptimizer(ArrayList<Meeting> meetings) {
        this.meetings = meetings;
        distanceHelper = new DistanceHelper(meetings);
    }

    public void optimize() {
        try {
            initialize();
            for (int i = 0; i < 100000; i++) {
                int distance = INITIAL_DISTANCE;
                while (distance < meetings.size()) {
                    Route opt2Result = opt2(myCurrentBest.generateNeightbourRoute(distance));

                    if (opt2Result.isFeasible()) {
                        if (!(opt2Result.getCost() < currentBest.getCost())){
                        distance += DISTANCE_STEP;
                    }else{
                    System.out.println("new best found! " + opt2Result.getCost() +
                            " previous best was: " + currentBest.getCost() +
                            " Thread: " + Thread.currentThread().getName() +
                            " Iteration: " + i);
                    currentBest = new Route(opt2Result);
                    distance = INITIAL_DISTANCE;}
                }
            }

            myCurrentBest = new Route(Route.newRandomRoute(meetings.size(), distanceHelper));
        }
    } catch(
    Exception e)

    {
        e.printStackTrace();
    }

}

    private synchronized void initialize() {
        myCurrentBest = new Route(opt2(Route.newRandomRoute(meetings.size(), distanceHelper)));
        if (null == currentBest) {
            currentBest = new Route(myCurrentBest);
        }
    }


    private Route opt2(Route opt2ResultLocal) {

        for (int i = 0; i < meetings.size() - 2; i++) {
            for (int j = i + 2; j < meetings.size() - 1; j++) {
                int distA = distanceHelper.getTime(i, i + 1, 9)
                        + distanceHelper.getTime(j, j + 1, 9);
                int distB = distanceHelper.getTime(i, j, 9)
                        + distanceHelper.getTime(i + 1, j + 1, 9);

                if (distA > distB) {
                    opt2ResultLocal.swap(i + 1, j);
                }
            }
        }

        opt2ResultLocal.countCost();
        return opt2ResultLocal;
    }


    public Route getCurrentBest() {
        return currentBest;
    }

}
