package edu.cmu.wireless.wifianalyzer.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;
import com.google.maps.android.projection.SphericalMercatorProjection;

import org.json.JSONException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import edu.cmu.wireless.wifianalyzer.FileUtil;
import edu.cmu.wireless.wifianalyzer.R;
import edu.cmu.wireless.wifianalyzer.WifiAnalyzer;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapViewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapViewFragment extends Fragment
            implements
            OnMapReadyCallback,
            LocationListener,
            GoogleMap.OnMyLocationButtonClickListener,
            GoogleApiClient.ConnectionCallbacks,
            GoogleApiClient.OnConnectionFailedListener{

    private static final SphericalMercatorProjection sProjection = new SphericalMercatorProjection(1.0D);

    protected static final String TAG = MapViewFragment.class.getSimpleName();
    // MapView
    private MapView mMapView;
    // Google mapp object
    private GoogleMap mMap;
    // Google API client
    private GoogleApiClient mGoogleClient;
    // Samples of signal strength associated with location
    private HashMap< String,WeightedLatLng> samples = null;
    // Signal strength of current location
    private WeightedLatLng current = null;
    // HeatMap overlap
    private TileOverlay mOverlay;
    private HeatmapTileProvider mProvider;

    private static final int[] ALT_HEATMAP_GRADIENT_COLORS = {
            Color.argb(0, 0, 255, 255),// transparent
            Color.rgb(102, 225, 0),
            Color.rgb(255, 208, 0),
            Color.rgb(255, 77, 0),
            Color.rgb(255, 0, 0)
    };

    public static final float[] ALT_HEATMAP_GRADIENT_START_POINTS = {
            0.0f, 0.10f, 0.20f, 0.60f, 1.0f
    };

    public static final Gradient ALT_HEATMAP_GRADIENT = new Gradient(ALT_HEATMAP_GRADIENT_COLORS,
            ALT_HEATMAP_GRADIENT_START_POINTS);

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public MapViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapViewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapViewFragment newInstance(String param1, String param2) {
        MapViewFragment fragment = new MapViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        samples = new HashMap<>();
        // make sure there's sample here before creating heatmap

        if (samples.isEmpty())
            samples.put("",new WeightedLatLng(new LatLng(0,0),-70));

        mGoogleClient = new GoogleApiClient.Builder(WifiAnalyzer.getAppContext(), this, this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_map_view, container, false);
        mMapView = (MapView) view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();// needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(this);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        try {
            FileOutputStream outputStream = getContext().openFileOutput("sample.json", Context.MODE_PRIVATE);
            FileUtil.saveItems(outputStream, samples);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onStart() {
        super.onStart();

        mGoogleClient.connect();
    }

    @Override
    public void onStop() {
        mGoogleClient.disconnect();

        super.onStop();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("OnLocationChanged", location.toString());
        mMap.clear();

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));

        // get signal strength as weight
        WifiManager wifiManager = (WifiManager) WifiAnalyzer.getAppContext()
                .getSystemService(Context.WIFI_SERVICE);

        double signal = wifiManager.getConnectionInfo().getRssi();
        double weight = ((-1)/(signal))*10000;
        Log.d("OnLocationChanged", "weight:" + weight);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-hh:mm:ss.SSS");
        Date time = Calendar.getInstance().getTime();

        current = new WeightedLatLng(latLng, weight);
        samples.put(formatter.format(time),current);

        mOverlay.clearTileCache();
//        removeHeatMap();
        addHeatMap();

    }

    @Override
    public boolean onMyLocationButtonClick() {
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).

        return false;
    }

    @Override
    public void onConnected(Bundle bundle) {
        //start listening to location updates
        // this is suitable for foreground listening,
        // with the onLocationChanged() invoked for location updates
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setFastestInterval(150)
                .setInterval(300)
                .setSmallestDisplacement(5.0F);
        // smallest displacement that's considered a location change

        if (ContextCompat.checkSelfPermission(
                WifiAnalyzer.getAppContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleClient, locationRequest, this);
        } else {
            // Show rationale and request permission.
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMyLocationButtonClickListener(this);

        if (ContextCompat.checkSelfPermission(
                WifiAnalyzer.getAppContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            try {
                FileInputStream inputStream = getContext().openFileInput("sample.json");
                FileUtil.readItems(inputStream, samples);
            } catch (JSONException e){
            } catch (FileNotFoundException e){
               e.printStackTrace();
            }

            addHeatMap();

        } else {
            // Show rationale and request permission.
        }
    }

    private void removeHeatMap() {
        mOverlay.remove();
    }

    private void addHeatMap() {


        // Create a heat map tile provider, passing it the latlngs of the police stations.
        mProvider = new HeatmapTileProvider.Builder()
                    .weightedData(samples.values())
                    .gradient(ALT_HEATMAP_GRADIENT)
//                    .gradient(HeatmapTileProvider.DEFAULT_GRADIENT)
                    .radius(30)
                    .build();
        // Add a tile overlay to the map, using the heat map tile provider.
        mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
    }
}
