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

public class Ruas_ListAdapter extends BaseAdapter {

    private Context context;
    private List<Ruas> ruasList;

    public Ruas_ListAdapter(Context context, List<Ruas> ruasList){
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
        View v  = View.inflate(context, R.layout.row_ruas, null);

        TextView tvNomorRuas = v.findViewById(R.id.tvNomorRuas);
        TextView tvNamaRuas = v.findViewById(R.id.tvNamaRuas);
        TextView tvPanjangRuas = v.findViewById(R.id.tvPanjangRuas);
        TextView tvStart = v.findViewById(R.id.tvTitikMulai);
        TextView tvEnd = v.findViewById(R.id.tvTitikAkhir);

        String nomor2;
        if(ruasList.get(i).getNomor_2() == 0){
            nomor2 = "";
        } else {
            nomor2 = "" + ruasList.get(i).getNomor_2();
        }
        tvNomorRuas.setText("No : " +ruasList.get(i).getNomor_1() + "-" + nomor2  + "-" + ruasList.get(i).getNomor_3());
        tvNamaRuas.setText( ruasList.get(i).getNama());
        tvPanjangRuas.setText( "" +ruasList.get(i).getPanjang());
        tvStart.setText( "" + ruasList.get(i).getStart());
        tvEnd.setText( "" + ruasList.get(i).getEnd());

        v.setTag(ruasList.get(i).getId());
        if (i % 2 == 1) {
            v.setBackgroundColor(ContextCompat.getColor(context, R.color.lightgray));
        } else {
            v.setBackgroundColor(Color.WHITE);
        }
        return v;
    }
}
