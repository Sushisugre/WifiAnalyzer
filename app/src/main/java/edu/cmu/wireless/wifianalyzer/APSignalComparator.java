package edu.cmu.wireless.wifianalyzer;

import android.net.wifi.ScanResult;

import java.util.Comparator;

/**
 * Comparator to sort received AP in signal strength descending order
 */
public class APSignalComparator implements Comparator<ScanResult>{
    @Override
    public int compare(ScanResult a, ScanResult b) {

        return b.level - a.level;
    }
}
