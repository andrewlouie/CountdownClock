package com.andrewaarondev.countdownclock;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomSimpleAdapter extends SimpleAdapter {
    private Context mContext;
    public LayoutInflater inflater = null;

    public CustomSimpleAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        mContext = context;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (convertView == null)
            vi = inflater.inflate(R.layout.cell, null);
        HashMap<String, Integer> data = (HashMap<String, Integer>) getItem(position);
        ImageView image = (ImageView) vi.findViewById(R.id.thumbnail);
        Integer image_url = data.get("presetimgs");
        Picasso.with(mContext).load(image_url).error(R.raw.sunset).noFade().resize(150, 150).centerCrop().into(image);
        return vi;
    }
}
