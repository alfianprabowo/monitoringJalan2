package com.example.alfia.monitoringjalan.Pemanfaatan;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

public class Pemanfaatan_ListAdapter extends BaseAdapter {

    private Context context;
    private List<Pemanfaatan> pemanfaatanList;

    public Pemanfaatan_ListAdapter(Context context, List<Pemanfaatan> pemanfaatanList){
        this.context =context;
        this.pemanfaatanList = pemanfaatanList;
    }

    @Override
    public int getCount() {
        return pemanfaatanList.size();
    }

    @Override
    public Object getItem(int i) {
        return pemanfaatanList.get(i);
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
