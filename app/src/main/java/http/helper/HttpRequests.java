package http.helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;

import static android.os.StrictMode.ThreadPolicy;
import static android.os.StrictMode.setThreadPolicy;

/**
 * Created by The wizard of OZ on 3/28/2015.
 */
public class HttpRequests {
    private static final String USER_AGENT="Chrome 41.0.2228.0";

    public static LinkedList<String> getBarSpecials(int barid) throws IOException, JSONException{
        URL url = new URL("http://drinklynx.me/api/v1/specials/");
        ThreadPolicy policy = new ThreadPolicy.Builder().permitAll().build();
        setThreadPolicy(policy);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setDoInput(true);
        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("User_Agent", USER_AGENT);
        JSONObject json = new JSONObject();
        json.put("barid", barid);
        OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
        out.write(json.toString());
        out.close();
        int rCode = con.getResponseCode();
        if(rCode == 200){
            BufferedReader br =  new BufferedReader(new InputStreamReader(con.getInputStream()));
            String jsonString = br.readLine();
            JSONArray jArray = new JSONArray(jsonString);
            LinkedList<String> list = new LinkedList<String>();
            for(int i = 0; i < jArray.length(); i++){
                JSONObject j = (JSONObject) jArray.get(i);
                list.add(j.getString("description"));
            }
            return list;
        }

        return null;
    }
    public static HashMap<String, LinkedList<Double>> getLocalBars(double lat0, double lat1, double lon0, double lon1) throws IOException, JSONException{
        URL url = new URL("http://drinklynx.me/api/v1/bars/");

        ThreadPolicy policy = new ThreadPolicy.Builder().permitAll().build();
        setThreadPolicy(policy);

        HttpURLConnection con = (HttpURLConnection) url.openConnection();


        con.setRequestMethod("GET");
        con.setDoOutput(true);
        con.setDoInput(true);
        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("User_Agent", USER_AGENT);
        //create the json to be sent
        JSONObject json = new JSONObject();
        json.put("lat0", lat0);
        json.put("lat1", lat1);
        json.put("lon0", lon0);
        json.put("lon1", lon1);
        //send it

        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
        out.write(json.toString());
        out.close();

        //did it work?
        int rCode = con.getResponseCode();
        if(rCode == 200){
            BufferedReader br =  new BufferedReader(new InputStreamReader(con.getInputStream()));
            String jsonString = br.readLine();
            JSONArray jArray = new JSONArray(jsonString);
            HashMap<String, LinkedList<Double>> map = new HashMap<String, LinkedList<Double>>();
            for(int i = 0; i < jArray.length(); i++){
                JSONObject j = (JSONObject) jArray.get(i);
                LinkedList<Double> vals = new LinkedList<Double>();
                vals.add(j.getDouble("Lon"));
                vals.add(j.getDouble("Lat"));
                vals.add(j.getDouble("id"));
                map.put(j.getString("Name"),vals);
            }
            return map;

        }
        return null;
    }
    public static void main(String[] args) throws IOException, JSONException {
        System.out.println(getLocalBars(2.35376, 48.87171, 2.35376, 48.87171));
    }
}



