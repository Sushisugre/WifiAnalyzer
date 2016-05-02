package edu.cmu.wireless.wifianalyzer;


import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.List;

public class WifiSwitcher {

    public void switchTo(Context context, ScanResult newAP) {

        if (newAP == null) return;

        WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        WifiConfiguration newConfig = null;

        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        List<WifiConfiguration> item = wifi.getConfiguredNetworks();

        for (WifiConfiguration config: item) {
            if (!config.SSID.equals("\""+newAP.SSID+"\"")) continue;

            Log.d("WifiPreference", "SSID" + config.SSID);
            Log.d("WifiPreference", "BSSID" + config.BSSID);
            newConfig = config;
            break;
        }

        // no need to switch, already connected to target AP
        if (newConfig == null ||newConfig.BSSID.equals(newAP.BSSID)) {
            return;
        }

        newConfig.BSSID = newAP.BSSID;
        wifiManager.updateNetwork(newConfig);
        wifiManager.reassociate();

        // change back to default setting to allow automatically AP discover
        newConfig.BSSID = "any";
        wifiManager.updateNetwork(newConfig);
    }
}
