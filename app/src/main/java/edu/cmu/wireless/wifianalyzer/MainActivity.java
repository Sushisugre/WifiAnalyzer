package edu.cmu.wireless.wifianalyzer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    WiFiStatusReceiver wifiReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        this.wifiReceiver = new WiFiStatusReceiver();

//        this.registerReceiver(this.wifiReceiver,
//                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
//        this.registerReceiver(this.wifiReceiver,
//                new IntentFilter(WifiManager.RSSI_CHANGED_ACTION));
        this.registerReceiver(this.wifiReceiver,
                new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        this.scheduleWiFiScan();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        // unregister receiver
        this.unregisterReceiver(this.wifiReceiver);
    }

    /**
     * Schedule a task to scan wifi periodically
     */
    public void scheduleWiFiScan(){
        ScheduledExecutorService scheduler =
                Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate
                (new Runnable() {
                    public void run() {
                        Log.d("PeriodicScan", "start scanning");
                        WifiManager wifiManager = (WifiManager) WifiAnalyzer.getAppContext()
                                .getSystemService(Context.WIFI_SERVICE);
                        wifiManager.startScan();
                    }
                }, 0, 2, TimeUnit.SECONDS);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {


        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public String makeFragmentName(int containerViewId, long id) {
            return "android:switcher:" + containerViewId + ":" + id;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
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
            signalStrength.setText("Loading...");
            Log.d("PlaceHolderFragment", "signalStrength View set");

            return rootView;
        }

        public void updateSignalStrength(int value){
            Log.d("PlaceHolderFragment", "update signal strength: " + value);
            if(signalStrength!=null)
                signalStrength.setText("Signal Strength: "+ Integer.toString(value) + " dBm");
        }
    }


    public class WiFiStatusReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, Intent intent) {

            Log.d("WifiReceiver", intent.getAction());

            ConnectivityManager cm =
                    (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

            final NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

            if (activeNetwork != null
                    && activeNetwork.isConnectedOrConnecting()
                    && activeNetwork.getType() == ConnectivityManager.TYPE_WIFI){

                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int item = mViewPager.getCurrentItem();
                        String tag = mSectionsPagerAdapter.makeFragmentName(R.id.pager,item);
                        FragmentManager fragmentManager = MainActivity.this.getSupportFragmentManager();
                        PlaceholderFragment fragment = (PlaceholderFragment)fragmentManager.
                                findFragmentByTag(tag);

                        WifiManager wifiManager = (WifiManager) WifiAnalyzer.getAppContext()
                                .getSystemService(Context.WIFI_SERVICE);
                        fragment.updateSignalStrength(wifiManager.getConnectionInfo().getRssi());
                    }
                });

            }
        }
    }

}
