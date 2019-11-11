package com.example.alfia.monitoringjalan.Kerusakan;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.alfia.monitoringjalan.R;

import java.util.List;

public class Kerusakan_ListAdapter extends BaseAdapter {

    private Context context;
    private List<Kerusakan> kerusakanList;

    public Kerusakan_ListAdapter(Context context, List<Kerusakan> kerusakanList) {
        this.context = context;
        this.kerusakanList = kerusakanList;
    }


    @Override
    public int getCount() {
        return kerusakanList.size();
    }

    @Override
    public Object getItem(int i) {
        return kerusakanList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = View.inflate(context, R.layout.row_image, null);
        Kerusakan img = kerusakanList.get(i);

        TextView tvDeskripsi= v.findViewById(R.id.tvDescImage);

        //tvNamaFoto.setText("Nama kerusakan : " + rusakList.get(i).getNama());
        tvDeskripsi.setText("Bagian Jalan : " + kerusakanList.get(i).getBagian_jalan_id());

        //v.setTag(imageList.get(i).getId());
        return v;
    }
}
