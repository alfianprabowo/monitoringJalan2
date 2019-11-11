package com.example.alfia.monitoringjalan.Ruas;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.alfia.monitoringjalan.R;

import java.util.List;

public class Ruas_Coordinates_ListAdapter extends BaseAdapter {

    private Context context;
    private List<Ruas_Coordinates> ruasList;

    public Ruas_Coordinates_ListAdapter(Context context, List<Ruas_Coordinates> ruasList){
        this.context = context;
        this.ruasList = ruasList;
    }

    @Override
    public int getCount() {
        return ruasList.size();
    }

    @Override
    public Object getItem(int i) {
        return ruasList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = View.inflate(context, R.layout.row_coordinates, null);

        TextView tvLat = v.findViewById(R.id.tvLat);
        TextView tvKm = v.findViewById(R.id.tvKm);
        TextView tvDate = v.findViewById(R.id.tvDateSurveyed);

        tvLat.setText("Lat " +ruasList.get(i).getLat()+" , Lng "+ruasList.get(i).getLng());
        tvKm.setText("KM "+ruasList.get(i).getPoint_km()+" + "+ruasList.get(i).getPoint_m());
        tvDate.setText("Survey : " + ruasList.get(i).getDate_surveyed());

        v.setTag(ruasList.get(i).getRuas_id());
        if (i % 2 == 1) {
            v.setBackgroundColor(ContextCompat.getColor(context, R.color.lightgray));
        } else {
            v.setBackgroundColor(Color.WHITE);
        }
        return v;
    }
}
