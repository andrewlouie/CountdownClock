package com.andrewaarondev.countdownclock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class PresetsActivity extends Activity implements AdapterView.OnItemClickListener {

    ArrayList<Integer> presetimgs = new ArrayList<>();

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setContentView(R.layout.presets);
        List<HashMap<String, Integer>> aList = new ArrayList<>();
        Field[] fields = R.raw.class.getFields();
        for (Field field : fields) {
            try {
                if (field.getName().substring(0, 10).equals("aa_preset_")) {
                    presetimgs.add(field.getInt(null));
                }
            } catch (Exception e) {
                Log.w("tag", e.getMessage());
            }
        }
        for (int i = 0; i < presetimgs.size(); i++) {
            HashMap<String, Integer> hm = new HashMap<>();
            hm.put("presetimgs", presetimgs.get(i));
            aList.add(hm);
        }
        // Keys used in Hashmap
        String[] from = {"presetimgs"};
        // Ids of views in listview_layout
        int[] to = {R.id.thumbnail};
        GridView g = (GridView) findViewById(R.id.grid);

        g.setAdapter(new CustomSimpleAdapter(this, aList, R.layout.cell, from, to));
        g.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent result = new Intent();
        result.putExtra("position", presetimgs.get(position));
        setResult(RESULT_OK, result);
        finish();
    }
}
