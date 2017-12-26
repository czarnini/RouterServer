package com.bogucki.databse;

import com.bogucki.MapsAPI.GoogleMaps;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DistanceHelper {

    private static final String ADDRESSES_DICT = "ADDRESSES_DICT";
    private  Connection c;



    private  volatile List<Integer> citiesIDs;
    private  volatile List<String> citiesNames;
    private  volatile HashMap<Integer, HashMap<Integer, Integer>> costs;

    private int originID;
    private int destinationID;

    public void createAddressDictionary() {
        try {
            System.out.println("Creating database");
            StringBuilder query = new StringBuilder("CREATE TABLE " + ADDRESSES_DICT + " ")
                    .append(" (")
                    .append("ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, ")
                    .append("ADDRESS CHAR(100) NOT NULL);");

            //System.outprintln(query.toString());
            Statement statement = c.createStatement();
            statement.executeUpdate(query.toString());
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    //TODO dodać 24 godziny
    private  String generateHoursColumns() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i <= 0; i++) {  //TODO 23
            builder.append("C").append(i).append(" INT NOT NULL");
            if (i != 0) {//TODO 23
                builder.append(", ");
            }
        }
        return builder.toString();
    }

    public DistanceHelper() {
        try {
            String databaseUrl = "jdbc:sqlite:Distances.db";
            c = DriverManager.getConnection(databaseUrl);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void cleanUp(){
        try {
            c.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Calculate Time between A and B when the trip starts at T
     * SELECT T FROM Ta WHERE DEST_ID = B;
     *
     * @param origin      - origin
     * @param destination - destination
     * @param timeOfStart - starting time
     */
    public int getTime(String origin, String destination, int timeOfStart) {
        {

            originID = citiesIDs.get(citiesNames.indexOf(origin));
            destinationID = citiesIDs.get(citiesNames.indexOf(destination));

            return costs.get(originID).get(destinationID);

        }

    }

    private int getResult(int timeOfStart) throws SQLException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT C")
                .append(timeOfStart)
                .append(" FROM A")
                .append(originID)
                .append(" WHERE DEST_ID = ")
                .append(destinationID);
        //System.outprintln(query.toString());
        Statement statement = c.createStatement();
        ResultSet rs = statement.executeQuery(query.toString());
        int result = rs.getInt(1);
        rs.close();
        statement.close();
        return result;
    }

    private  HashMap<Integer, Integer> getResult(int timeOfStart, int originID) throws SQLException {
        HashMap<Integer, Integer> tmp = new HashMap<>();
        StringBuilder query = new StringBuilder();
        query.append("SELECT dest_id, C")
                .append(timeOfStart)
                .append(" FROM A")
                .append(originID);
        //System.outprintln(query.toString());
        Statement statement = c.createStatement();
        ResultSet rs = statement.executeQuery(query.toString());
        while (rs.next()) {
            tmp.put(rs.getInt(1), rs.getInt(2));
        }
        rs.close();
        statement.close();
        return tmp;
    }

    /**
     * a. Insert address into address_dict
     * b. Create table Tn (n -> id from dict)
     * c. Add row with time of trip to tables T1-Tn-1
     *
     * @param address - address to be added
     */
    private  int addAddress(String address) throws SQLException {
        int id;
        id = addAddressToDict(address);
        createAddressTable(id);
        insertTimes(1, id);
        System.out.println("Adding " + address +"to database with ID: " + id);
        return id;
    }

    private  int addAddressToDict(String address) throws SQLException {
        StringBuilder query = new StringBuilder("INSERT INTO " + ADDRESSES_DICT)
                .append("(ADDRESS) VALUES ('")
                .append(address)
                .append("');");

        //System.outprintln(query.toString());

        Statement statement = c.createStatement();
        statement.executeUpdate(query.toString());
        int id = statement.getGeneratedKeys().getInt(1);
        statement.close();
        return id;
    }

    private  void createAddressTable(int originId) throws SQLException {
        StringBuilder query = new StringBuilder("CREATE TABLE A")
                .append(originId)
                .append(" (")
                .append("DEST_ID INT NOT NULL, ")
                .append(generateHoursColumns())
                .append(");");

        //System.outprintln(query.toString());
        c.createStatement().executeUpdate(query.toString());

        for (int destinationId = 1; destinationId < originId; destinationId++) {
            insertTimes(originId, destinationId);
        }
    }

    //TODO 24 godziny
    private  void insertTimes(int originId, int destinationId) throws SQLException {
        if (originId == destinationId && destinationId == 1) {
            return;
        }
        do {
            String origin = getAddress(originId),
                    destination = getAddress(destinationId);
            int timeToInsert = GoogleMaps.getDistance(origin, destination);
            StringBuilder query = new StringBuilder("INSERT INTO A")
                    .append(originId)
                    .append("(dest_id, C0) VALUES (")
                    .append(destinationId)
                    .append(", ")
                    .append(timeToInsert)
                    .append(");");

            //System.outprintln(query.toString());
            c.createStatement().executeUpdate(query.toString());
            originId++;
        } while (originId < destinationId);
    }

    private  String getAddress(int id) throws SQLException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT ADDRESS")
                .append(" FROM " + ADDRESSES_DICT + " ")
                .append("WHERE ID  = ")
                .append(id);
        //System.outprintln(query.toString());
        Statement statement = c.createStatement();
        ResultSet rs = statement.executeQuery(query.toString());
        String address = rs.getString(1);
        rs.close();
        statement.close();
        return address;
    }

    public  int mapAddressToID(String addressToCheck) throws SQLException {
//        System.out.println("Mapping " + addressToCheck + "to ID");
        int id = getAddressID(addressToCheck);
        if(id != -1){
            System.out.println("ID == "+ id);
        }
        return -1 == id ? addAddress(addressToCheck) : id;
    }


    private  int getAddressID(String addressToCheck) throws SQLException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT ID")
                .append(" FROM " + ADDRESSES_DICT + " ")
                .append("WHERE ADDRESS  = '")
                .append(addressToCheck)
                .append("';");
        //System.outprintln(query.toString());
        ResultSet rs = c.createStatement().executeQuery(query.toString());
        if (rs.isClosed()) {
            return -1;
        } else {
            int id = rs.getInt(1);
            rs.close();
            return id;
        }
    }

    public  void loadDistancesToRAM(List<String> list) {
        System.out.println("Loading requested cities from HDD to RAM");
        citiesNames = new ArrayList<>(list);
        citiesIDs = new ArrayList<>();
        costs = new HashMap<>();

        for (String city : list) {
            try {
                int tmpCityID = mapAddressToID(city);
                List<Integer> tmp = new ArrayList<>();
                citiesIDs.add(tmpCityID);
                costs.put(tmpCityID, new HashMap<>(getResult(0, tmpCityID)));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }
}
