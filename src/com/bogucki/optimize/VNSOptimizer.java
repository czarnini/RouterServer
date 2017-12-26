package com.bogucki.optimize;


import com.bogucki.databse.DistanceHelper;

import java.util.ArrayList;

public class VNSOptimizer {
    public final ArrayList<String> meetings;
    private DistanceHelper distanceHelper;

    private Route currentBest = null;
    private Route opt2Result = null;

    public VNSOptimizer(ArrayList<String> meetings) {
        this.meetings = meetings;
        distanceHelper = new DistanceHelper();
        distanceHelper.loadDistancesToRAM(meetings);
    }

    public int[] optimize() {
        System.out.println("Start optimizing");
        initialize();

        System.out.println("Stop optimizing");
        return opt2Result.getCitiesOrder();
    }

    private void initialize() {
        opt2Result = Route.newRandomRoute(meetings.size(), distanceHelper);
        opt2();
        currentBest = opt2Result;
    }

    public ArrayList<String> getMeetings() {
        return meetings;
    }



    private void opt2() {
        for (int a = 0; a < 20; a++) {
            for (int i = 0; i < meetings.size() - 2; i++) {
                String iThCity = meetings.get(opt2Result.getCity(i));
                String iThCityNext = meetings.get(opt2Result.getCity(i+1));
                for (int j = i+2; j <meetings.size()-1; j++) {
                    String jThCity = meetings.get(opt2Result.getCity(j));
                    String jThCityNext = meetings.get(opt2Result.getCity(j+1));
                    int distA = distanceHelper.getTime(iThCity, iThCityNext, 0)
                            + distanceHelper.getTime(jThCity, jThCityNext, 0);
                    int distB = distanceHelper.getTime(iThCity, jThCity, 0)
                            + distanceHelper.getTime(iThCityNext, jThCityNext, 0);

                    if (distA>distB){
                        opt2Result.swap(i+1,j);
                    }
                }
            }
            System.out.println(costCounter());
        }
    }



    private void generateNeightbourRoute(){

    }

}
