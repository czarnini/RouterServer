package com.bogucki.networking;

import com.bogucki.databse.DistanceHelper;
import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {

    public static void main(String[] args) throws IOException {

        if (!new File("Distances.db").exists()) {
            DistanceHelper helper = new DistanceHelper();
            helper.createAddressDictionary();
            helper.cleanUp();
        }

        HttpServer server = HttpServer.create(new InetSocketAddress(9000), 0);
        server.createContext("/", new RootHandler());
        server.createContext("/optimize", new EchoPostOptimizeHandler());
        server.createContext("/newAddress", new EchoPostNewAddressHandler());
        server.setExecutor(null);
        server.start();
    }
}
