package edu.cmu.wireless.wifianalyzer;

import android.net.wifi.ScanResult;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import static android.support.v7.widget.RecyclerView.Adapter;
import static android.support.v7.widget.RecyclerView.ViewHolder;

/**
 * Created by s4keng on 4/8/16.
 */
public class ApAdapter extends Adapter {

    private List<ScanResult> accessPoints;

    public static class ApViewHolder extends RecyclerView.ViewHolder {
        public TextView apInfo;

        public ApViewHolder(View view) {
            super(view);
            apInfo = (TextView) view.findViewById(R.id.ap_info);
        }
    }

    public void setAccessPoints(List<ScanResult> results) {
        accessPoints = results;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
