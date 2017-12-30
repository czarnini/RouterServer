package com.bogucki.databse;

import com.bogucki.MapsAPI.GoogleMaps;
import com.bogucki.optimize.Meeting;
import org.apache.commons.lang3.StringUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DistanceHelper {

    private static final String ADDRESSES_DICT = "ADDRESSES_DICT";
    private Connection c;


    private volatile List<Integer> citiesIDs;
    private volatile HashMap<Integer, HashMap<Integer, Integer>> costs;

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
    private String generateHoursColumns() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i <= 0; i++) {  //TODO 23
            builder.append("C").append(i).append(" INT NOT NULL");
            if (i != 0) {//TODO 23
                builder.append(", ");
            }
        }
        return builder.toString();
    }

    public DistanceHelper(ArrayList<Meeting> meetings) {
        try {
            String databaseUrl = "jdbc:sqlite:Distances.db";
            c = DriverManager.getConnection(databaseUrl);
            loadDistancesToRAM(meetings);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void loadDistancesToRAM(List<Meeting> meetings) {
        if (null == meetings) {
            return;
        }

        System.out.println("Loading requested cities from HDD to RAM");
        citiesIDs = new ArrayList<>();
        costs = new HashMap<>();

        for (Meeting meeting : meetings) {
            try {
                int tmpCityID = mapAddressToID(meeting.getAddress());
                citiesIDs.add(tmpCityID);
                costs.put(tmpCityID, new HashMap<>(getResult(0, tmpCityID)));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }


    public void cleanUp() {
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

    public int getTime(int origin, int destination, int timeOfStart) {
        int originID = citiesIDs.get(origin);
        int destinationID = citiesIDs.get(destination);
        return costs.get(originID).get(destinationID);
    }


    private HashMap<Integer, Integer> getResult(int timeOfStart, int originID) throws SQLException {
        HashMap<Integer, Integer> tmp = new HashMap<>();
        String query = "SELECT dest_id, C" +
                timeOfStart +
                " FROM A" +
                originID;
        Statement statement = c.createStatement();
        ResultSet rs = statement.executeQuery(query);
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
    private int addAddress(String address) throws SQLException {

        String currentAddresses = StringUtils.join(getAllAddresses(),"|");
        ArrayList<Integer> timesToNewAddress = GoogleMaps.getDistances(currentAddresses , address);
        ArrayList<Integer> timesFromNewAddress = GoogleMaps.getDistances(address, currentAddresses);

        int id;
        id = addAddressToDict(address);

        createAddressTable(id, timesFromNewAddress);
        insertTimes(1, id, timesToNewAddress);

        System.out.println("Adding " + address + "to database with ID: " + id);
        return id;
    }

    private int addAddressToDict(String address) throws SQLException {
        StringBuilder query = new StringBuilder("INSERT INTO " + ADDRESSES_DICT)
                .append("(ADDRESS) VALUES ('")
                .append(address)
                .append("');");


        Statement statement = c.createStatement();
        statement.executeUpdate(query.toString());
        int id = statement.getGeneratedKeys().getInt(1);
        statement.close();
        return id;
    }

    private void createAddressTable(int originId, ArrayList<Integer> timesFromNewAddress) throws SQLException {
        String query = "CREATE TABLE A" +
                originId +
                " (" +
                "DEST_ID INT NOT NULL, " +
                generateHoursColumns() +
                ");";

        c.createStatement().executeUpdate(query);

        for (int destinationId = 1; destinationId < originId; destinationId++) {
            timesFromNewAddress.set(0, timesFromNewAddress.get(destinationId-1));
            insertTimes(originId, destinationId, timesFromNewAddress);
        }
    }

    //TODO 24 godziny
    private void insertTimes(int originId, int destinationId, ArrayList<Integer> times) throws SQLException {
        if (originId == destinationId && destinationId == 1) {
            return;
        }
        int helpfulIndex = 0;
        do {
            String query = "INSERT INTO A" +
                    originId +
                    "(dest_id, C0) VALUES (" +
                    destinationId +
                    ", " +
                    times.get(helpfulIndex) +
                    ");";


            c.createStatement().executeUpdate(query);
            originId++;
            helpfulIndex++;
        } while (originId < destinationId);
    }

    private String getAddress(int id) throws SQLException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT ADDRESS")
                .append(" FROM " + ADDRESSES_DICT + " ")
                .append("WHERE ID  = ")
                .append(id);
        Statement statement = c.createStatement();
        ResultSet rs = statement.executeQuery(query.toString());
        String address = rs.getString(1);
        rs.close();
        statement.close();
        return address;
    }

    private ArrayList<String> getAllAddresses() throws SQLException {
        String query = "SELECT ADDRESS" +
                " FROM " + ADDRESSES_DICT + ";";
        Statement statement = c.createStatement();
        ResultSet rs = statement.executeQuery(query);
        ArrayList<String> result = new ArrayList<>();
        while (rs.next()) {
            result.add(rs.getString(1));
        }
        rs.close();
        statement.close();
        return result;
    }


    public int mapAddressToID(String addressToCheck) throws SQLException {
        int id = getAddressID(addressToCheck);
        if (id != -1) {
            System.out.println("ID == " + id);
        }
        return -1 == id ? addAddress(addressToCheck) : id;
    }


    private int getAddressID(String addressToCheck) throws SQLException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT ID")
                .append(" FROM " + ADDRESSES_DICT + " ")
                .append("WHERE ADDRESS  = '")
                .append(addressToCheck)
                .append("';");

        ResultSet rs = c.createStatement().executeQuery(query.toString());
        if (rs.isClosed()) {
            return -1;
        } else {
            int id = rs.getInt(1);
            rs.close();
            return id;
        }
    }


    public void addAddresses(List<String> addressesToAdd) throws SQLException {
        for (String address : addressesToAdd) {
            addAddress(address);
        }

    }
}
