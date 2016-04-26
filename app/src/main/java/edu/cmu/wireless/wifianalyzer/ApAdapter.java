package edu.cmu.wireless.wifianalyzer;

import android.net.wifi.ScanResult;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.support.v7.widget.RecyclerView.Adapter;

/**
 * Created by s4keng on 4/8/16.
 */
public class ApAdapter extends Adapter<ApAdapter.ViewHolder> {

    private List<ScanResult> accessPoints;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView apInfo;

        public ViewHolder(View view) {
            super(view);
            apInfo = (TextView) view.findViewById(R.id.ap_info);
        }
    }

    public ApAdapter(List<ScanResult> results) {
        accessPoints = new ArrayList<>(results);
    }

    public void setAccessPoints(List<ScanResult> results) {
//        accessPoints = new ArrayList<>(results);
        accessPoints.clear();
        accessPoints.addAll(results);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ap_list_row, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ScanResult ap = accessPoints.get(position);
        //FIXME: getString
        holder.apInfo.setText(ap.BSSID+": "+ap.level+" dbm");
    }


    @Override
    public int getItemCount() {
        return accessPoints.size();
    }

}
