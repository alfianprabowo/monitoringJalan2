package com.example.alfia.monitoringjalan.Data;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

public class Data_ListAdapter extends BaseAdapter {

    private Context context;
    private List<Data> dataList;

    public Data_ListAdapter(Context context, List<Data> dataList){
        this.context = context;
        this.dataList = dataList;
    }


    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int i) {
        return dataList.get(i);
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
