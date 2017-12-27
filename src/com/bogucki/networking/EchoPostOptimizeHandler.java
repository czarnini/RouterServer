package com.bogucki.networking;

import com.bogucki.optimize.Route;
import com.bogucki.optimize.VNSOptimizer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class EchoPostOptimizeHandler implements HttpHandler {
    private List<String> cities = new ArrayList<>();
    int cost = 0;
    Route result;

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

        VNSOptimizer vnsOptimizer = new VNSOptimizer(parseQuery(query.toString()));
        try {
            result = new Route(vnsOptimizer.optimize());
        } catch (Exception e) {
            e.printStackTrace();
        }
        sendResponse(he);
    }


    private void sendResponse(HttpExchange he) throws IOException {
        StringBuilder response = new StringBuilder();
        response.append(cost);
        for (int i = 0; i < result.getCitiesOrder().length; i++) {
            response.append(";"+result.getCity(i));
        }
        he.sendResponseHeaders(200, response.length());
        OutputStream os = he.getResponseBody();
        os.write(response.toString().getBytes());
        os.close();
    }

    private ArrayList<String> parseQuery(String query) {
        ArrayList<String> result = new ArrayList<>();
        try {
            System.out.println("Parsing" + query);
            JSONObject jsonQUERY = new JSONObject(query);
            JSONArray sentCities = jsonQUERY.getJSONArray("addresses");
            if (null != sentCities) {
                int length = sentCities.length();
                for (int i = 0; i < length; ++i) {
                    result.add(sentCities.getString(i).toLowerCase().trim().replaceAll(" *, *", ","));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


}



