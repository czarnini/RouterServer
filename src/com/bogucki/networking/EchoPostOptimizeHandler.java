package com.bogucki.networking;

import com.bogucki.optimize.Meeting;
import com.bogucki.optimize.Route;
import com.bogucki.optimize.VNSOptimizer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;

public class EchoPostOptimizeHandler implements HttpHandler {
    private Route result;

    @Override
    public void handle(HttpExchange he) throws IOException {

        // parse request
        System.out.println("POST queried");
        System.out.println(he.getRequestBody());
        InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);

        StringBuilder query = new StringBuilder();
        String tmp;
        while ((tmp = br.readLine()) != null) {
            query.append(tmp);
        }

        ArrayList<Meeting> meetings = parseQuery(query.toString());
        VNSOptimizer[] optimizers = new VNSOptimizer[4];
        Thread[] threads = new Thread[4];
        for (int i = 0; i < optimizers.length; i++) {
            try {
                optimizers[i] = new VNSOptimizer(meetings);
                VNSOptimizer finalOptimizer = optimizers[i];
                threads[i] = new Thread(finalOptimizer::optimize);
                threads[i].start();
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        try {
            threads[3].join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        result = findBestSub(optimizers);
        sendResponse(he);
    }

    private Route findBestSub(VNSOptimizer[] subResults) {
        int minCost = subResults[0].getCurrentBest().getCost();
        int minCostIndex = 0;
        for (int i = 1; i < subResults.length; i++) {
            if (subResults[i].getCurrentBest().getCost() < minCost) {
                minCost = subResults[i].getCurrentBest().getCost();
                minCostIndex = i;
            }
        }
        System.out.println("Best of best is: " + minCost);
        return subResults[minCostIndex].getCurrentBest();
    }


    private void sendResponse(HttpExchange he) throws IOException {
        StringBuilder response = new StringBuilder();
        int cost = result.getCost();
        response.append(cost);
        for (int i = 0; i < result.getCitiesOrder().length; i++) {
            response.append(";" + result.getCity(i));
        }
        he.sendResponseHeaders(200, response.length());
        OutputStream os = he.getResponseBody();
        os.write(response.toString().getBytes());
        os.close();
    }

    private ArrayList<Meeting> parseQuery(String query) {
        ArrayList<Meeting> result = new ArrayList<>();
        try {
            System.out.println("Parsing" + query);
            JSONObject jsonQUERY = new JSONObject(query);
            JSONArray sentMeetings = jsonQUERY.getJSONArray("meetings");
            if (null != sentMeetings) {
                int length = sentMeetings.length();
                for (int i = 0; i < length; ++i) {
                    JSONObject currentMeeting = sentMeetings.getJSONObject(i);
                    result.add(new Meeting(
                            currentMeeting.getString("address").replaceAll(" *, *", ",").trim().toLowerCase(),
                            currentMeeting.getInt("ETP"),
                            currentMeeting.getInt("LTP")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


}



