package com.bogucki.optimize;


import com.bogucki.databse.DistanceHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Klasa opisująca trasę przejazdu. Reprezentacją trasy będzie tablica indeksów.
 */
public class Route {
    private int[] citiesOrder;
    private DistanceHelper distanceHelper;
    private int cost;

    private Route(int routeLength, DistanceHelper helper) {
        citiesOrder = new int[routeLength];
        distanceHelper = helper;
        Arrays.fill(citiesOrder, -1);
    }

    public Route(int[] baseRoute) {
        citiesOrder = baseRoute;
    }

    Route(Route baseRoute) {
        this.citiesOrder = baseRoute.getCitiesOrder().clone();
        this.distanceHelper = baseRoute.distanceHelper;
        this.cost = baseRoute.cost;
    }

    public int getCity(int index) {
        return citiesOrder[index];
    }

    private void setCity(int city, int index) {
        this.citiesOrder[index] = city;
    }

    public int[] getCitiesOrder() {
        return citiesOrder;
    }


    void swap(int i, int j) {
        int[] prefix = Arrays.copyOfRange(citiesOrder, 0, i);
        int[] toSwap = Arrays.copyOfRange(citiesOrder, i, j + 1);
        int[] suffix = Arrays.copyOfRange(citiesOrder, j + 1, citiesOrder.length);


        int lastElementIndex = toSwap.length - 1;
        for (int k = 1; k < toSwap.length; k++) {
            int tmp = toSwap[k];
            toSwap[k] = toSwap[lastElementIndex - k];
            toSwap[lastElementIndex - k] = tmp;
        }
        citiesOrder = org.apache.commons.lang3.ArrayUtils.addAll(
                org.apache.commons.lang3.ArrayUtils.addAll(prefix, toSwap), suffix
        );

    }


    static Route newRandomRoute(int size, DistanceHelper helper) {
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

    void countCost() {
        int result = 0;
        for (int i = 0; i < citiesOrder.length - 1; i++) {
            result += distanceHelper.getTime(citiesOrder[i], citiesOrder[i + 1], 0);
        }
        cost = result;
    }

    public int getCost() {
        return cost;
    }


    /**
     * @param distance musi być parzysty - jedna krawędź to dwa wierzchołki
     * @return
     */
    Route generateNeightbourRoute(int distance) {
        ArrayList<Integer> indexesToBeMoved = new ArrayList<>();
        Random generator = new Random();
        int protectedIndex;
        for (int i = 0; i < distance; i++) {
            do {
                protectedIndex = generator.nextInt(citiesOrder.length);
            } while (indexesToBeMoved.indexOf(protectedIndex) != -1);
            indexesToBeMoved.add(protectedIndex);
        }

        Route result = new Route(this);
        for (int i = 0; i < indexesToBeMoved.size()-1; i += 2) {
            result.setCity(citiesOrder[indexesToBeMoved.get(i)], indexesToBeMoved.get(i+1));
            result.setCity(citiesOrder[indexesToBeMoved.get(i+1)], indexesToBeMoved.get(i));
        }

        return result;
    }

}
