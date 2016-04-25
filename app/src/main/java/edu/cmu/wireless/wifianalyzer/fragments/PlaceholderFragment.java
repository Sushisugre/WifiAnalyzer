package edu.cmu.wireless.wifianalyzer.fragments;

import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import edu.cmu.wireless.wifianalyzer.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    TextView signalStrength;

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
        Log.d("PlaceHolderFragment", "signalStrength View set");
        return rootView;
    }

    public void updateSignalStrength(WifiInfo connInfo){
        int value = connInfo.getRssi();
        Log.d("PlaceHolderFragment", "update signal strength: " + value);

        if(signalStrength!=null) {
            String sigStrength = getString(R.string.connection_info,
                    connInfo.getSSID(),connInfo.getBSSID(), connInfo.getRssi());
            signalStrength.setText(sigStrength);
        }
    }
}