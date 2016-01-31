package edu.depaul.drinkspecials;

import android.app.Activity;
import android.util.Log;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Client extends Activity implements Runnable {
    private static final String TAG = "";
    private static final String SERVER_IP = "192.168.1.11";
    private static final int SERVERPORT = 9999;
    public static String currentLocation;
    public static double clientLatitude,clientLongitude;
    public static ArrayList<String> inputList, iList;

    public Client(double la, double ln){
        clientLatitude = la;
        clientLongitude = ln;
    }

    @Override
    public void run() {
        try {
            Socket client = new Socket(SERVER_IP, SERVERPORT);

            DataOutputStream output= new DataOutputStream(client.getOutputStream());
            DataInputStream input= new DataInputStream(client.getInputStream());
            BufferedReader in = new BufferedReader(new InputStreamReader(input));

            currentLocation = "" + clientLatitude + " " + clientLongitude;

            output.writeUTF(currentLocation);
            Log.e(TAG, "SENT Current Position to server: " + currentLocation);

            readServer(in);
            formatInput(inputList);

            in.close();
            output.flush();
            output.close();
            client.close();

        } catch (UnknownHostException e) {
            Log.e(TAG, e + "-Unknown Host: " + SERVER_IP);
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(TAG, e + "-Couldn't get I/O for the connection to " + SERVER_IP);
            e.printStackTrace();
        } catch (Exception e) {
            Log.e(TAG, e + "-EXCEPTION " + SERVER_IP + SERVERPORT);
            e.printStackTrace();
        }
    }

    public void readServer(BufferedReader in) throws IOException{
        String inputLine = "";
        inputList = new ArrayList<>();
        iList = new ArrayList<>();
        while((inputLine = in.readLine()) != null) {
            Log.e(TAG, "RECEIVED from server: " + inputLine);
            inputList.add(inputLine);
        }
    }

    public void formatInput(ArrayList<String> inputList){
        for(String s: inputList){
            if(s!=null) {
                String[] t = s.split(":");
                for(String remove: t){
                    remove = remove.substring(2);
                    iList.add(remove);
                }
                Log.e(TAG, "WITHIN LIST: " + iList.get(0));
                Log.e(TAG, "WITHIN LIST: " + iList.get(1));
            }
            else
                Log.e(TAG, "LIST IS NULL");
        }
    }

    public static ArrayList<String>getIList(){
        return iList;
    }
}