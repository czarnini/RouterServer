package com.bogucki.networking;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class EchoPostOptimizeHandler implements HttpHandler {
    private List<String> cities = new ArrayList<>();

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
        parseQuery(query.toString());

        // send response
        StringBuilder response = new StringBuilder();
        he.sendResponseHeaders(200, response.length());
        OutputStream os = he.getResponseBody();
        os.write(response.toString().getBytes());
        os.close();
    }

    private void parseQuery(String query) throws UnsupportedEncodingException {

        System.out.println("Parsing" + query);

        JSONObject jsonQUERY = new JSONObject(query);
        JSONArray sentCities = jsonQUERY.getJSONArray("destination_addresses");


    }
}



