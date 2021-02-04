package com.example.knowyourgovernment;

import android.net.Uri;
import android.os.AsyncTask;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class OfficialDownloader extends AsyncTask<String, Void, String> {
    private MainActivity mainActivity;
    private ArrayList<Official> list = new ArrayList<>();
    private static final String key = "AIzaSyDzsjo0RDlOtLQ0QJt2gmfrVMPVJw_A7tM";
    static String city;
    static String state;
    static String zip;


    public OfficialDownloader(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }


    @Override
    protected String doInBackground(String... strings) {
        String info = strings[0];
        String DATA_URL = "https://www.googleapis.com/civicinfo/v2/representatives";
        Uri.Builder buildURL = Uri.parse(DATA_URL).buildUpon();
        buildURL.appendQueryParameter("key", key);
        buildURL.appendQueryParameter("address", info);
        String urlToUse = buildURL.build().toString();
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            } else {
                conn.setRequestMethod("GET");
                InputStream is = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String line;
                while ((line = reader.readLine()) != null)
                    sb.append(line).append('\n');
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return sb.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        try {
            JSONObject data = new JSONObject(s);
            JSONObject addressObject = data.getJSONObject("normalizedInput");
            city = addressObject.getString("city");
            state = addressObject.getString("state");
            zip = addressObject.getString("zip");
            JSONArray officeArray = data.getJSONArray("offices");
            JSONArray officialsArray = data.getJSONArray("officials");
            for (int i = 0; i < officialsArray.length(); i++) {
                String title;
                String address = "";
                String party = "";
                String phone = "";
                String photoURL = "";
                String email = "";
                String url = "";
                String name;
                Map<String, String> channels = new HashMap<>();
                JSONObject official = (JSONObject) officialsArray.get(i);
                title = "";
                name = official.getString("name");
                if (official.has("address")) {
                    JSONArray addressArray = official.getJSONArray("address");
                    JSONObject address1 = (JSONObject) addressArray.get(0);
                    StringBuilder sb = new StringBuilder();
                    sb.append(address1.getString("line1")).append('\n');
                    sb.append(address1.getString("line2")).append('\n');
                    sb.append(address1.getString("city")).append('\n');
                    sb.append(address1.getString("state")).append('\n');
                    sb.append(address1.getString("zip")).append('\n');
                    address = sb.toString().trim();
                }
                if (official.has("party")) {
                    party = official.getString("party");
                }
                if (official.has("emails")) {
                    JSONArray emails = official.getJSONArray("emails");
                    email = emails.get(0).toString();
                }
                if (official.has("phones")) {
                    JSONArray phones = official.getJSONArray("phones");
                    phone = phones.get(0).toString();
                }
                if (official.has("urls")) {
                    JSONArray urls = official.getJSONArray("urls");
                    url = urls.get(0).toString();
                }
                if (official.has("photoUrl")) {
                    photoURL = official.getString("photoUrl");
                }
                if (official.has("channels")) {
                    JSONArray channelArray = official.getJSONArray("channels");
                    for (int j = 0; j < channelArray.length(); j++) {
                        JSONObject channel = (JSONObject) channelArray.get(j);
                        channels.put(channel.getString("type"), channel.getString("id"));
                    }
                }
                Official newOfficial = new Official(title, name, address, party, phone, photoURL, url, channels, email);
                list.add(newOfficial);
            }
            for (int h = 0; h < officeArray.length(); h++) {
                JSONObject office = (JSONObject) officeArray.get(h);
                String titleName = office.getString("name");
                JSONArray officialIndices = office.getJSONArray("officialIndices");
                {
                    for (int j = 0; j < officialIndices.length(); j++) {
                        int index = Integer.parseInt(officialIndices.get(j).toString());
                        list.get(index).setTitle(titleName);
                    }
                }
            }
            mainActivity.receiveOfficials(list);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
