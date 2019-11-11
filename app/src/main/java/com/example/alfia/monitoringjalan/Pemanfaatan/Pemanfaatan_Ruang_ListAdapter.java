package com.example.alfia.monitoringjalan.Pemanfaatan;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.alfia.monitoringjalan.R;

import java.util.List;

public class Pemanfaatan_Ruang_ListAdapter extends BaseAdapter {

    private Context context;
    private List<Pemanfaatan_Ruang> ruangList;

    public Pemanfaatan_Ruang_ListAdapter(Context context, List<Pemanfaatan_Ruang> ruang){
        this.context = context;
        this.ruangList = ruang;
    }

    @Override
    public int getCount() {
        return ruangList.size();
    }

    @Override
    public Object getItem(int i) {
        return ruangList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = View.inflate(context, R.layout.row_pemanfaatan, null);

        TextView tvRuang = v.findViewById(R.id.rowRuangJalan_pemanfaatan);
        TextView tvPemanfaatan = v.findViewById(R.id.rowPemanfaatanJalan);
        TextView tvDate = v.findViewById(R.id.rowDateSurveyed_pemanfaaan);
        TextView tvLat = v.findViewById(R.id.rowLat_pemanfaatan);
        TextView tvLng = v.findViewById(R.id.rowLng_pemanfaatan);
        TextView tvKM = v.findViewById(R.id.rowPointKM_pemanfaatan);
        TextView tvM = v.findViewById(R.id.rowPointM_pemanfaatan);

        tvRuang.setText("Ruang Jalan : " + ruangList.get(i).getRuang_jalan());
        tvPemanfaatan.setText("Pemanfataan Ruang : " + ruangList.get(i).getPemanfaatan());
        tvDate.setText("Survey : " + ruangList.get(i).getDate_surveyed());
        tvLat.setText("Lat " + ruangList.get(i).getLat());
        tvLng.setText(" Lng " + ruangList.get(i).getLng());
        tvKM.setText("KM : " + ruangList.get(i).getPoint_km());
        tvM.setText("+ " + ruangList.get(i).getPoint_m());

        v.setTag(ruangList.get(i).getId());
        if (i % 2 == 1) {
            v.setBackgroundColor(ContextCompat.getColor(context, R.color.lightgray));
        } else {
            v.setBackgroundColor(Color.WHITE);
        }
        return  v;
    }
}
