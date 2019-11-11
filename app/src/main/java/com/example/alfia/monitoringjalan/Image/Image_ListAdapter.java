package com.example.alfia.monitoringjalan.Image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.alfia.monitoringjalan.R;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class Image_ListAdapter extends BaseAdapter {

    private Context context;
    private List<Image> imageList;

    public Image_ListAdapter(Context context, List<Image> imageList) {
        this.context = context;
        this.imageList = imageList;
    }


    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public Object getItem(int i) {
        return imageList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = View.inflate(context, R.layout.row_image, null);
        Image img = imageList.get(i);

        byte[] laporan_gambar = img.getPhoto();
        Bitmap bitmap = BitmapFactory.decodeByteArray(laporan_gambar, 0, laporan_gambar.length);

        ImageView imageView = v.findViewById(R.id.gambarView);
        TextView tvDeskripsi= v.findViewById(R.id.tvDescImage);

        tvDeskripsi.setText("Nama : " + imageList.get(i).getName());
        imageView.setImageBitmap(bitmap);

        v.setTag(imageList.get(i).getId());
        if (i % 2 == 1) {
            v.setBackgroundColor(ContextCompat.getColor(context, R.color.lightgray));
        } else {
            v.setBackgroundColor(Color.WHITE);
        }
        return v;
    }


}
