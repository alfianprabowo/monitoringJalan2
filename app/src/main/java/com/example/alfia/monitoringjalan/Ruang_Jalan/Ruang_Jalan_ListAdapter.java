package com.example.alfia.monitoringjalan.Ruang_Jalan;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

public class Ruang_Jalan_ListAdapter extends BaseAdapter {

    private Context context;
    private List<Ruang_Jalan> ruang_jalanList;

    public Ruang_Jalan_ListAdapter(Context context, List<Ruang_Jalan> ruang_jalanList){
        this.context = context;
        this.ruang_jalanList = ruang_jalanList;
    }

    @Override
    public int getCount() {
        return ruang_jalanList.size();
    }

    @Override
    public Object getItem(int i) {
        return ruang_jalanList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return null;
    }
}
