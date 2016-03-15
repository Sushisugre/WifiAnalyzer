package edu.cmu.wireless.wifianalyzer;

import android.app.Application;
import android.content.Context;

public class WifiAnalyzer extends Application{

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        WifiAnalyzer.context = getApplicationContext();
    }

    /**
     * Static way of getting application context
     *
     * Caveat: If called before application's onCreate, it will return null
     * @return application context
     */
    public static Context getAppContext() {
        return WifiAnalyzer.context;
    }
}
