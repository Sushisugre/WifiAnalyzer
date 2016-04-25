package edu.cmu.wireless.wifianalyzer;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.geometry.Point;
import com.google.maps.android.heatmaps.WeightedLatLng;
import com.google.maps.android.projection.SphericalMercatorProjection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by s4keng on 4/18/16.
 */
public class FileUtil {

    private static final SphericalMercatorProjection sProjection = new SphericalMercatorProjection(1.0D);


    /**
     * Save sampled signal strength to json file
     *
     * @throws JSONException
     * @throws IOException
     */
    public static void saveItems(FileOutputStream outputStream, HashMap<String, WeightedLatLng> samples)
            throws JSONException, IOException {

        JSONArray sampleArray = new JSONArray();

        for (Map.Entry<String, WeightedLatLng> entry: samples.entrySet()){
            WeightedLatLng sample = entry.getValue();
            JSONObject object = new JSONObject();
            object.put("time", entry.getKey());
            object.put("lat", sample.getPoint().x);
            object.put("lng", sample.getPoint().y);
            object.put("weight", (-1)/(sample.getIntensity()/10000));
            sampleArray.put(object);

        }

        outputStream.write(sampleArray.toString().getBytes());
        outputStream.flush();
    }


    public static void readItems(FileInputStream inputStream, HashMap<String, WeightedLatLng> samples)
            throws JSONException {
        String json = new Scanner(inputStream).useDelimiter("\\A").next();
        JSONArray array = new JSONArray(json);
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            double lat = object.getDouble("lat");
            double lng = object.getDouble("lng");
            Point point = new Point(lat, lng);
            LatLng latLng =sProjection.toLatLng(point);

            double weight = ((-1)/(object.getDouble("weight")))*10000;
            String time = object.getString("time");
            samples.put(time,new WeightedLatLng(latLng, weight));
            Log.d("test", "" + lat+" "+" "+lng+" " + weight);
        }
    }
}
