package com.bogucki.optimize;



import com.bogucki.databse.DistanceHelper;

import java.util.Arrays;
import java.util.Random;

/**
 * Klasa opisująca trasę przejazdu. Reprezentacją trasy będzie tablica indeksów.
 */
public class Route {
    private int[] citiesOrder;
    private DistanceHelper distanceHelper;

    public Route(int routeLength, DistanceHelper helper) {
        citiesOrder = new int[routeLength];
        distanceHelper = helper;
        for (int i = 0; i < routeLength; i++) {
            citiesOrder[i] = -1;
        }
    }

    public Route(int[] baseRoute) {
        citiesOrder = baseRoute;
    }

    public Route(Route baseRoute) {
        this.citiesOrder = baseRoute.getCitiesOrder();
    }

    public int getCity(int index) {
        return citiesOrder[index];
    }

    public void setCity (int city, int index) {
        this.citiesOrder[index] = city;
    }

    public int[] getCitiesOrder() {
        return citiesOrder;
    }


    public void swap(int i,int j){
        int [] prefix = Arrays.copyOfRange(citiesOrder, 0, i);
        int [] toSwap = Arrays.copyOfRange(citiesOrder, i, j+1);
        int [] sufix = Arrays.copyOfRange(citiesOrder, j+1, citiesOrder.length);


        int lastElementIndex = toSwap.length-1;
        for (int k = 1; k < toSwap.length; k++) {
            int tmp = toSwap[k];
            toSwap[k] = toSwap[lastElementIndex - k];
            toSwap[lastElementIndex - k] = tmp;
        }
        citiesOrder =   org.apache.commons.lang3.ArrayUtils.addAll(
                        org.apache.commons.lang3.ArrayUtils.addAll(prefix, toSwap), sufix
        );
    }


    public static Route newRandomRoute(int size, DistanceHelper helper){
        Random generator = new Random();
        Route route = new Route(size, helper);
        for (int i = 0; i < size; i++) {
            int insertIndex;
            do {
                insertIndex = generator.nextInt(size);
            } while (route.getCity(insertIndex) != -1);
            route.setCity(i, insertIndex);
        }
        return route;
    }

    public int countCost(){
        int result =0;
        for (int i = 0; i < citiesOrder.length-1; i++) {
            result += distanceHelper.getTime(meetings.get(currentBest.getCity(i)), meetings.get(currentBest.getCity(i+1)), 0);
        }
        return result;
    }




}
