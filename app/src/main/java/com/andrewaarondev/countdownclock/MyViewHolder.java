package com.andrewaarondev.countdownclock;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Calendar;


public class MyViewHolder {
    private TextView name=null;
    private ImageView thumb=null;
    private TextView timeleft=null;
    private String path = null;
    private Context ctxt;

    MyViewHolder(View row,String path, Context ctxt) {
        name=(TextView)row.findViewById(R.id.name);
        thumb=(ImageView)row.findViewById(R.id.thumb);
        timeleft=(TextView)row.findViewById(R.id.timeleft);
        this.ctxt = ctxt;
        this.path = path;
    }

    TextView getName() {
        return(name);
    }

    ImageView getThumb() {
        return(thumb);
    }

    TextView getTimeLeft() { return(timeleft); }

    void populateFrom(Countdown countdown, Resources r) {
        getName().setText(countdown.getTitle());
        String test = Helpers.getDateDifference(Calendar.getInstance(), countdown.getDate(), countdown.isNoSpecificTime(), r);
        getTimeLeft().setText(test);
        Picasso.with(ctxt).load(Helpers.validUri(path,countdown.getFilename())).error(R.raw.sunset).noFade().centerCrop().resize(150,150).into(getThumb());
    }

}
