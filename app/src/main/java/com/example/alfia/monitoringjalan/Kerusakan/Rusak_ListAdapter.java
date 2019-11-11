package com.example.alfia.monitoringjalan.Kerusakan;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.alfia.monitoringjalan.R;

import java.util.List;

public class Rusak_ListAdapter extends BaseAdapter {

    private Context context;
    private List<Rusak> rusakList;

    public Rusak_ListAdapter(Context context, List<Rusak> rusakList){
        this.context = context;
        this.rusakList = rusakList;
    }

    @Override
    public int getCount() {
        return rusakList.size();
    }

    @Override
    public Object getItem(int i) {
        return rusakList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = View.inflate(context, R.layout.row_rusak, null);

        TextView tvDate = v.findViewById(R.id.tvDateSurveyed_rusak);
        TextView tvLat = v.findViewById(R.id.tvLat_rusak);
        TextView tvLng = v.findViewById(R.id.tvLng_rusak);
        TextView tvPointKM = v.findViewById(R.id.tvPointKM_rusak);
        TextView tvPointM = v.findViewById(R.id.tvPointM_rusak);
        TextView tvVolume = v.findViewById(R.id.tvVolume_rusak);
        //TextView tvFixed = v.findViewById(R.id.tvFixed_rusak);
        TextView tvBagianJalan = v.findViewById(R.id.tvBagianJalan_rusak);
        TextView tvKerusakan = v.findViewById(R.id.tvKerusakan_rusak);
        ImageView statusView = v.findViewById(R.id.statusView);


        tvDate.setText("Survey : " + rusakList.get(i).getDate_surveyed());
        tvLat.setText("Lat " + rusakList.get(i).getLat());
        tvLng.setText("Lng " + rusakList.get(i).getLng());
        tvPointKM.setText("KM : " + rusakList.get(i).getPoint_km());
        tvPointM.setText("+ " + rusakList.get(i).getPoint_m());
        tvVolume.setText("Volume : " + rusakList.get(i).getVolume());

        String status = "";
        if(rusakList.get(i).getFixed() == 0){
            status = "Belum diperbaiki";
            statusView.setImageResource(R.drawable.status_no);
        } else if (rusakList.get(i).getFixed() == 1){
            status = "Telah diperbaiki";
            statusView.setImageResource(R.drawable.status_ok);
        } else{
            status = "Tidak jelas";
        }

        //tvFixed.setText("Status : " + status);
        tvBagianJalan.setText("Bagian Jalan: " + rusakList.get(i).getBagian_jalan());
        tvKerusakan.setText("Kerusakan : " + rusakList.get(i).getKerusakan());

        v.setTag(rusakList.get(i).getId());
        if (i % 2 == 1) {
            v.setBackgroundColor(ContextCompat.getColor(context, R.color.lightgray));
        } else {
            v.setBackgroundColor(Color.WHITE);
        }
        return v;
    }
}
