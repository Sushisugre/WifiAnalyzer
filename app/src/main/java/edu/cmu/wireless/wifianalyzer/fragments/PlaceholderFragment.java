package edu.cmu.wireless.wifianalyzer.fragments;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.cmu.wireless.wifianalyzer.ApAdapter;
import edu.cmu.wireless.wifianalyzer.R;
import edu.cmu.wireless.wifianalyzer.WifiAnalyzer;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    private TextView signalStrength;
    private RecyclerView mAPList;
    private RecyclerView.LayoutManager mLayoutManager;
    private ApAdapter mApAdapter;
    private List<ScanResult> mScanResults;

    public PlaceholderFragment() {
        mScanResults = new ArrayList<>();
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PlaceholderFragment newInstance(int sectionNumber) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        signalStrength = (TextView)rootView.findViewById(R.id.signalStrength);
        signalStrength.setText(getString(R.string.loading));

        // init ap list for current SSID
        mAPList = (RecyclerView) rootView.findViewById(R.id.ap_list);
        mAPList.setHasFixedSize(false);

        mLayoutManager = new LinearLayoutManager(rootView.getContext());
        mAPList.setLayoutManager(mLayoutManager);

        mApAdapter = new ApAdapter(mScanResults);
        mAPList.setAdapter(mApAdapter);

        return rootView;
    }

    public void updateSignalStrength(WifiInfo connInfo){

        WifiManager wifiManager = (WifiManager) WifiAnalyzer.getAppContext()
                .getSystemService(Context.WIFI_SERVICE);

        String connSSID = connInfo.getSSID().substring(1,connInfo.getSSID().length()-1);

        // update scan result for ap list view
        mScanResults.clear();
        List<ScanResult> allResults = wifiManager.getScanResults();

        // filter out the APs with the same SSID as our current connection
        for (ScanResult result: allResults) {
            if (result.SSID.equals(connSSID)) {
                mScanResults.add(result);
            }
        }

        // TODO: sort it by signal strength


        mApAdapter.setAccessPoints(mScanResults);
        mApAdapter.notifyDataSetChanged();

        // update current connection info
        if(signalStrength!=null) {
            String sigStrength = getString(R.string.connection_info,
                    connSSID, connInfo.getBSSID(), connInfo.getRssi());
            signalStrength.setText(sigStrength);
        }
    }
}