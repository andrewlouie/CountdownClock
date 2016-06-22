package com.andrewaarondev.countdownclock;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;
import yuku.ambilwarna.AmbilWarnaDialog;

public class DetailsFragment extends Fragment implements
        OnDateChangedListener,
        OnTimeChangedListener,
        TextWatcher,
        Button.OnClickListener,
        SeekBar.OnSeekBarChangeListener,
        SwitchCompat.OnCheckedChangeListener,
        View.OnTouchListener {
    private static final int FONTS = 0;
    private static final int COLOURS = 1;
    private static final int IMPORTS = 2;
    private static final int CONTENT_REQUEST = 1337;
    private static final int RESULT_LOAD_IMAGE = 1382;
    private static final int RESULT_LOAD_PRESET = 3827;
    private static final int PERMISSION_REQ_CAMERA = 183;
    private static final int PERMISSION_REQ_GALLERY = 164;
    private static final int PERMISSION_EXPORT = 197;
    private static final String TEMP_FILE = "countdownclocktempfile.jpg";
    private View result;
    DatePicker datePicker = null;
    TimePicker timePicker = null;
    TextView title = null;
    Countdown cd = null;
    MyImageView imgView;
    private boolean loading = false;

    Timer timer;
    boolean isRunning = false;

    File output;
    Handler handler;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        loading = true;
        result = inflater.inflate(R.layout.details, null);
        Calendar datetime = new GregorianCalendar();
        datePicker = (DatePicker) result.findViewById(R.id.dp_datepicker);
        timePicker = (TimePicker) result.findViewById(R.id.tp_timepicker);
        imgView = (MyImageView) result.findViewById(R.id.imageView);
        imgView.setOnTouchListener(this);
        handler = new Handler();

        final Runnable r = new Runnable() {
            public void run() {
                imgView.invalidate();
                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(r, 1000);
        title = (EditText) result.findViewById(R.id.editText);
        title.addTextChangedListener(this);
        datePicker.init(datetime.get(Calendar.YEAR), datetime.get(Calendar.MONTH),
                datetime.get(Calendar.DAY_OF_MONTH), this);
        timePicker.setOnTimeChangedListener(this);
        result.findViewById(R.id.deletebtn).setOnClickListener(this);
        result.findViewById(R.id.btn_watermark).setOnClickListener(this);
        result.findViewById(R.id.btn_import).setOnClickListener(this);
        result.findViewById(R.id.btn_colour).setOnClickListener(this);
        result.findViewById(R.id.btn_font).setOnClickListener(this);
        result.findViewById(R.id.btn_camera).setOnClickListener(this);
        result.findViewById(R.id.btn_presets).setOnClickListener(this);
        result.findViewById(R.id.btn_gallery).setOnClickListener(this);
        result.findViewById(R.id.font0).setOnClickListener(this);
        result.findViewById(R.id.font1).setOnClickListener(this);
        result.findViewById(R.id.font2).setOnClickListener(this);
        result.findViewById(R.id.font3).setOnClickListener(this);
        result.findViewById(R.id.font4).setOnClickListener(this);
        result.findViewById(R.id.font5).setOnClickListener(this);
        result.findViewById(R.id.font6).setOnClickListener(this);
        result.findViewById(R.id.font7).setOnClickListener(this);
        result.findViewById(R.id.fontcolourbox).setOnClickListener(this);
        result.findViewById(R.id.bgcolourbox).setOnClickListener(this);
        Button time_btn = (Button)result.findViewById(R.id.time_btn);
        if (time_btn != null) time_btn.setOnClickListener(this);
        Button date_btn = (Button)result.findViewById(R.id.date_btn);
        if (date_btn != null) {
            date_btn.setOnClickListener(this);
            date_btn.setSelected(true);
        }
        ((SwitchCompat) result.findViewById(R.id.switch1)).setOnCheckedChangeListener(this);
        ((SeekBar) result.findViewById(R.id.seekFontA)).setOnSeekBarChangeListener(this);
        ((SeekBar) result.findViewById(R.id.seekFontR)).setOnSeekBarChangeListener(this);
        ((SeekBar) result.findViewById(R.id.seekFontG)).setOnSeekBarChangeListener(this);
        ((SeekBar) result.findViewById(R.id.seekFontB)).setOnSeekBarChangeListener(this);
        ((SeekBar) result.findViewById(R.id.seekBgA)).setOnSeekBarChangeListener(this);
        ((SeekBar) result.findViewById(R.id.seekBgR)).setOnSeekBarChangeListener(this);
        ((SeekBar) result.findViewById(R.id.seekBgG)).setOnSeekBarChangeListener(this);
        ((SeekBar) result.findViewById(R.id.seekBgB)).setOnSeekBarChangeListener(this);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) fontCompatibilityForOlderVersions();
        loading = false;
        return result;
    }

    public void fontCompatibilityForOlderVersions() {
        TextView myTextView = (TextView) result.findViewById(R.id.font0);
        Typeface typeFace = Typeface.createFromAsset(getActivity().getAssets(), Helpers.fontString(Helpers.CASUEL));
        myTextView.setTypeface(typeFace);
        myTextView = (TextView) result.findViewById(R.id.font1);
        typeFace = Typeface.createFromAsset(getActivity().getAssets(), Helpers.fontString(Helpers.CURSIVE));
        myTextView.setTypeface(typeFace);
        myTextView = (TextView) result.findViewById(R.id.font2);
        typeFace = Typeface.createFromAsset(getActivity().getAssets(), Helpers.fontString(Helpers.MONOSPACE));
        myTextView.setTypeface(typeFace);
        myTextView = (TextView) result.findViewById(R.id.font3);
        typeFace = Typeface.createFromAsset(getActivity().getAssets(), Helpers.fontString(Helpers.SANS_SERIF));
        myTextView.setTypeface(typeFace);
        myTextView = (TextView) result.findViewById(R.id.font4);
        typeFace = Typeface.createFromAsset(getActivity().getAssets(), Helpers.fontString(Helpers.SANS_SERIF_CONDENSED));
        myTextView.setTypeface(typeFace);
        myTextView = (TextView) result.findViewById(R.id.font5);
        typeFace = Typeface.createFromAsset(getActivity().getAssets(), Helpers.fontString(Helpers.SANS_SERIF_SMALLCAPS));
        myTextView.setTypeface(typeFace);
        myTextView = (TextView) result.findViewById(R.id.font6);
        typeFace = Typeface.createFromAsset(getActivity().getAssets(), Helpers.fontString(Helpers.SERIF));
        myTextView.setTypeface(typeFace);
        myTextView = (TextView) result.findViewById(R.id.font7);
        typeFace = Typeface.createFromAsset(getActivity().getAssets(), Helpers.fontString(Helpers.SERIF_MONOSPACE));
        myTextView.setTypeface(typeFace);
    }

    public void expandMenu(int i) {
        Button btn_import = (Button) result.findViewById(R.id.btn_import);
        Button btn_colours = (Button) result.findViewById(R.id.btn_colour);
        Button btn_fonts = (Button) result.findViewById(R.id.btn_font);
        LinearLayout is = (LinearLayout) result.findViewById(R.id.importselector);
        LinearLayout cs = (LinearLayout) result.findViewById(R.id.colourselector);
        LinearLayout fs = (LinearLayout) result.findViewById(R.id.fontselector);
        switch (i) {
            case IMPORTS:
                btn_colours.setSelected(false);
                btn_fonts.setSelected(false);
                cs.setAlpha(0);
                fs.setAlpha(0);
                cs.setVisibility(View.GONE);
                fs.setVisibility(View.GONE);
                if (is.getAlpha() > 0) {
                    is.animate().alpha(0);
                    btn_import.setSelected(false);
                    is.setVisibility(View.GONE);
                } else {
                    is.animate().alpha(1);
                    btn_import.setSelected(true);
                    is.setVisibility(View.VISIBLE);
                }
                break;
            case FONTS:
                btn_colours.setSelected(false);
                btn_import.setSelected(false);
                cs.setAlpha(0);
                is.setAlpha(0);
                cs.setVisibility(View.GONE);
                is.setVisibility(View.GONE);
                if (fs.getAlpha() > 0) {
                    fs.animate().alpha(0);
                    btn_fonts.setSelected(false);
                    fs.setVisibility(View.GONE);
                } else {
                    fs.animate().alpha(1);
                    btn_fonts.setSelected(true);
                    fs.setVisibility(View.VISIBLE);
                }
                break;
            case COLOURS:
                btn_fonts.setSelected(false);
                btn_import.setSelected(false);
                fs.setAlpha(0);
                is.setAlpha(0);
                fs.setVisibility(View.GONE);
                is.setVisibility(View.GONE);
                if (cs.getAlpha() > 0) {
                    cs.animate().alpha(0);
                    btn_colours.setSelected(false);
                    cs.setVisibility(View.GONE);
                } else {
                    cs.animate().alpha(1);
                    btn_colours.setSelected(true);
                    cs.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    public void loadCountdown(Countdown cd) {
        loading = true;
        if (datePicker != null) {
            this.cd = cd;
            Calendar datetime = cd.getDate();
            datePicker.updateDate(datetime.get(Calendar.YEAR), datetime.get(Calendar.MONTH), datetime.get(Calendar.DAY_OF_MONTH));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                timePicker.setHour(datetime.get(Calendar.HOUR_OF_DAY));
                timePicker.setMinute(datetime.get(Calendar.MINUTE));
            }
            else {
                timePicker.setCurrentHour(datetime.get(Calendar.HOUR_OF_DAY));
                timePicker.setCurrentMinute(datetime.get(Calendar.MINUTE));
            }
            title.setText(cd.getTitle());
            title.clearFocus();
            imgView.setParams(cd);
            Picasso.with(getActivity().getBaseContext()).load(Helpers.validUri(getActivity().getBaseContext().getFilesDir().toString(), cd.getFilename())).error(R.drawable.loading).noFade().into(imgView);
            result.findViewById(R.id.btn_watermark).setSelected(cd.isWatermark());
            Button btn_import = (Button) result.findViewById(R.id.btn_import);
            Button btn_colours = (Button) result.findViewById(R.id.btn_colour);
            Button btn_fonts = (Button) result.findViewById(R.id.btn_font);
            LinearLayout is = (LinearLayout) result.findViewById(R.id.importselector);
            LinearLayout cs = (LinearLayout) result.findViewById(R.id.colourselector);
            LinearLayout fs = (LinearLayout) result.findViewById(R.id.fontselector);
            ((SwitchCompat) result.findViewById(R.id.switch1)).setChecked(cd.isNoSpecificTime());
            btn_colours.setSelected(false);
            btn_import.setSelected(false);
            cs.setAlpha(0);
            is.setAlpha(0);
            cs.setVisibility(View.GONE);
            is.setVisibility(View.GONE);
            btn_fonts.setSelected(false);
            fs.setAlpha(0);
            fs.setVisibility(View.GONE);
            updateSliders();
            selectFont();
            if (getActivity() != null)
                save(getActivity().getExternalCacheDir(), MainActivity.SHARE_FILE_NAME, true);
        }
        loading = false;
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        if (cd != null && !loading) {
            Calendar datetime = cd.getDate();
            datetime.set(year, monthOfYear, dayOfMonth);
            cd.setDate(datetime);
            updateDb();
        }
    }

    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        if (cd != null && !loading) {
            Calendar datetime = cd.getDate();
            datetime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            datetime.set(Calendar.MINUTE, minute);
            cd.setDate(datetime);
            updateDb();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //for interface only
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (cd != null && !loading && getActivity() != null) {
            cd.setTitle(s.toString());
            updateDb();
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        //for interface only
    }

    public void updateDb() {
        DatabaseHelper db = DatabaseHelper.getInstance(getActivity().getBaseContext());
        db.updateItem(cd);
        imgView.invalidate();
        timer = new Timer();
        if (!isRunning) {
            timer.schedule(new SaveTask(), 3000);
            isRunning = true;
        }
    }

    class SaveTask extends TimerTask {
        public void run() {
            if (getActivity() != null)
                save(getActivity().getExternalCacheDir(), MainActivity.SHARE_FILE_NAME, true);
            isRunning = false;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.date_btn:
                result.findViewById(R.id.dp_datepicker).setVisibility(View.VISIBLE);
                result.findViewById(R.id.tp_timepicker).setVisibility(View.GONE);
                result.findViewById(R.id.date_btn).setSelected(true);
                result.findViewById(R.id.time_btn).setSelected(false);
                break;
            case R.id.time_btn:
                result.findViewById(R.id.dp_datepicker).setVisibility(View.GONE);
                result.findViewById(R.id.tp_timepicker).setVisibility(View.VISIBLE);
                result.findViewById(R.id.date_btn).setSelected(false);
                result.findViewById(R.id.time_btn).setSelected(true);
                break;
            case R.id.btn_watermark:
                if (cd != null && !loading && getActivity() != null) {
                    cd.setWatermark(!cd.isWatermark());
                    ((Button) v).setSelected(cd.isWatermark());
                    updateDb();
                }
                break;
            case R.id.deletebtn:
                AlertDialog.Builder rembuilder = new AlertDialog.Builder(this.getActivity());
                rembuilder.setTitle(getResources().getString(R.string.confirmdelete));
                rembuilder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Helpers.deleteFile(getActivity().getBaseContext().getFilesDir().toString(), cd.getFilename());
                        DatabaseHelper db = DatabaseHelper.getInstance(getActivity().getBaseContext());
                        if (cd != null && !loading) db.deleteItem(cd);
                        if (!getResources().getBoolean(R.bool.widescreen))
                            getActivity().onBackPressed();
                    }
                });
                rembuilder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                rembuilder.show();

                break;
            case R.id.btn_colour:
                expandMenu(COLOURS);
                break;
            case R.id.btn_font:
                expandMenu(FONTS);
                break;
            case R.id.btn_import:
                expandMenu(IMPORTS);
                break;
            case R.id.font0:
                cd.setFont(Helpers.CASUEL);
                selectFont();
                expandMenu(FONTS);
                updateDb();
                break;
            case R.id.font1:
                cd.setFont(Helpers.CURSIVE);
                selectFont();
                expandMenu(FONTS);
                updateDb();
                break;
            case R.id.font2:
                cd.setFont(Helpers.MONOSPACE);
                selectFont();
                expandMenu(FONTS);
                updateDb();
                break;
            case R.id.font3:
                cd.setFont(Helpers.SANS_SERIF);
                selectFont();
                expandMenu(FONTS);
                updateDb();
                break;
            case R.id.font4:
                cd.setFont(Helpers.SANS_SERIF_CONDENSED);
                selectFont();
                expandMenu(FONTS);
                updateDb();
                break;
            case R.id.font5:
                cd.setFont(Helpers.SANS_SERIF_SMALLCAPS);
                selectFont();
                expandMenu(FONTS);
                updateDb();
                break;
            case R.id.font6:
                cd.setFont(Helpers.SERIF);
                selectFont();
                expandMenu(FONTS);
                updateDb();
                break;
            case R.id.font7:
                cd.setFont(Helpers.SERIF_MONOSPACE);
                selectFont();
                expandMenu(FONTS);
                updateDb();
                break;
            case R.id.bgcolourbox:
                int bg = cd.getBgColour();
                AmbilWarnaDialog dialog = new AmbilWarnaDialog(this.getActivity(), bg, new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        cd.setBgColour(color);
                        updateSliders();
                        updateDb();
                    }

                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) {
                        //interface only
                    }
                });

                dialog.show();
                break;
            case R.id.fontcolourbox:
                int fontc = cd.getFontColour();
                AmbilWarnaDialog fontcdialog = new AmbilWarnaDialog(this.getActivity(), fontc, new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        cd.setFontColour(color);
                        updateSliders();
                        updateDb();
                    }

                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) {
                        //interface only
                    }
                });

                fontcdialog.show();
                break;
            case R.id.btn_camera:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!canAccessReadExternal() || !canAccessWriteExternal()) {
                        String[] permissions = {
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        };
                        requestPermissions(permissions, PERMISSION_REQ_CAMERA);
                    } else launchCamera();
                } else launchCamera();
                break;
            case R.id.btn_gallery:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!canAccessReadExternal()) {
                        String[] permissions = {
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                        };
                        requestPermissions(permissions, PERMISSION_REQ_GALLERY);
                    } else launchGallery();
                } else launchGallery();
                break;
            case R.id.btn_presets:
                Intent i3 = new Intent(this.getActivity(), PresetsActivity.class);
                startActivityForResult(i3, RESULT_LOAD_PRESET);
                break;
        }
    }

    public void launchGallery() {
        Intent i2 = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i2, RESULT_LOAD_IMAGE);
    }

    public void launchCamera() {
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        dir.mkdirs();
        output = new File(dir, TEMP_FILE);
        if (output.exists()) {
            output.delete();
        }
        i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(output));
        startActivityForResult(i, CONTENT_REQUEST);
    }

    private boolean hasPermission(String perm) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return (PackageManager.PERMISSION_GRANTED == getActivity().checkSelfPermission(perm));
        }
        return true;
    }

    private boolean canAccessReadExternal() {
        return (hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE));
    }

    private boolean canAccessWriteExternal() {
        return (hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE));
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQ_CAMERA:
                if (canAccessReadExternal() && canAccessWriteExternal()) {
                    launchCamera();
                } else {
                    new AlertDialog.Builder(getActivity())
                            .setTitle(getResources().getString(R.string.permissiontitle))
                            .setMessage(getResources().getString(R.string.permissionneeded))
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //interface only
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
                break;

            case PERMISSION_REQ_GALLERY:
                if (canAccessReadExternal()) {
                    launchGallery();
                } else {
                    new AlertDialog.Builder(getActivity())
                            .setTitle(getResources().getString(R.string.permissiontitle))
                            .setMessage(getResources().getString(R.string.permissionneeded))
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //interface only
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
                break;
            case PERMISSION_EXPORT:
                if (canAccessReadExternal()) {
                    exportAllowed();
                }
                else {
                    new AlertDialog.Builder(getActivity())
                            .setTitle(getResources().getString(R.string.permissiontitle))
                            .setMessage(getResources().getString(R.string.permissionneeded))
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //interface only
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
        }
    }


    private void selectFont() {
        result.findViewById(R.id.font0).setSelected(false);
        result.findViewById(R.id.font1).setSelected(false);
        result.findViewById(R.id.font2).setSelected(false);
        result.findViewById(R.id.font3).setSelected(false);
        result.findViewById(R.id.font4).setSelected(false);
        result.findViewById(R.id.font5).setSelected(false);
        result.findViewById(R.id.font6).setSelected(false);
        result.findViewById(R.id.font7).setSelected(false);
        switch (cd.getFont()) {
            case Helpers.CASUEL:
                result.findViewById(R.id.font0).setSelected(true);
                break;
            case Helpers.CURSIVE:
                result.findViewById(R.id.font1).setSelected(true);
                break;
            case Helpers.MONOSPACE:
                result.findViewById(R.id.font2).setSelected(true);
                break;
            case Helpers.SANS_SERIF:
                result.findViewById(R.id.font3).setSelected(true);
                break;
            case Helpers.SANS_SERIF_CONDENSED:
                result.findViewById(R.id.font4).setSelected(true);
                break;
            case Helpers.SANS_SERIF_SMALLCAPS:
                result.findViewById(R.id.font5).setSelected(true);
                break;
            case Helpers.SERIF:
                result.findViewById(R.id.font6).setSelected(true);
                break;
            case Helpers.SERIF_MONOSPACE:
                result.findViewById(R.id.font7).setSelected(true);
                break;
        }
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    public void onResume() {
        EventBus.getDefault().registerSticky(this);
        super.onResume();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (cd != null && !loading && getActivity() != null) {
            cd.setFontColour(Color.argb(
                    ((SeekBar) result.findViewById(R.id.seekFontA)).getProgress(),
                    ((SeekBar) result.findViewById(R.id.seekFontR)).getProgress(),
                    ((SeekBar) result.findViewById(R.id.seekFontG)).getProgress(),
                    ((SeekBar) result.findViewById(R.id.seekFontB)).getProgress()
            ));
            cd.setBgColour(Color.argb(
                    ((SeekBar) result.findViewById(R.id.seekBgA)).getProgress(),
                    ((SeekBar) result.findViewById(R.id.seekBgR)).getProgress(),
                    ((SeekBar) result.findViewById(R.id.seekBgG)).getProgress(),
                    ((SeekBar) result.findViewById(R.id.seekBgB)).getProgress()
            ));
            updateSliders();
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
//interface only
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        updateDb();
    }

    private void updateSliders() {
        int colour = cd.getFontColour();
        ((SeekBar) result.findViewById(R.id.seekFontA)).setProgress(Color.alpha(colour));
        ((SeekBar) result.findViewById(R.id.seekFontR)).setProgress(Color.red(colour));
        ((SeekBar) result.findViewById(R.id.seekFontG)).setProgress(Color.green(colour));
        ((SeekBar) result.findViewById(R.id.seekFontB)).setProgress(Color.blue(colour));
        int fontc = Color.argb(Color.alpha(colour), Color.red(colour), Color.green(colour), Color.blue(colour));
        colour = cd.getBgColour();
        ((SeekBar) result.findViewById(R.id.seekBgA)).setProgress(Color.alpha(colour));
        ((SeekBar) result.findViewById(R.id.seekBgR)).setProgress(Color.red(colour));
        ((SeekBar) result.findViewById(R.id.seekBgG)).setProgress(Color.green(colour));
        ((SeekBar) result.findViewById(R.id.seekBgB)).setProgress(Color.blue(colour));
        int bg = Color.argb(Color.alpha(colour), Color.red(colour), Color.green(colour), Color.blue(colour));
        result.findViewById(R.id.fontcolourbox).setBackgroundColor(fontc);
        result.findViewById(R.id.bgcolourbox).setBackgroundColor(bg);
        imgView.invalidate();
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(FileCopiedEvent event) {
        if (cd == null || cd.getId() != event.getId()) return;
        File file = new File(getActivity().getBaseContext().getFilesDir().toString(), cd.getFilename());
        Picasso picasso = Picasso.with(getActivity().getBaseContext());
        picasso.invalidate(file);
        picasso.load(file).error(R.drawable.loading).into(imgView);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CONTENT_REQUEST) {
            if (resultCode == MainActivity.RESULT_OK) {
                Helpers.moveFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString(), TEMP_FILE, getActivity().getBaseContext().getFilesDir().toString(), cd.getFilename(), cd.getId());
            }
        } else if (requestCode == RESULT_LOAD_IMAGE) {
            if (resultCode == MainActivity.RESULT_OK) {
                Uri selectedImage = data.getData();
                String url = data.getData().toString();
                if (url.startsWith("content://com.google.android.apps.photos.content")) {
                    try {
                        InputStream is = getActivity().getContentResolver().openInputStream(selectedImage);
                        if (is != null) {
                            Bitmap pictureBitmap = BitmapFactory.decodeStream(is);
                            OutputStream out = new FileOutputStream(getActivity().getBaseContext().getFilesDir().toString() + "/" + cd.getFilename());
                            pictureBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                        }
                    } catch (FileNotFoundException e) {
                        Log.w("tag", e.getMessage());
                    }
                } else {
                    File temp = new File(Helpers.getRealPathFromURI(this.getActivity().getBaseContext(), selectedImage));
                    Helpers.copyFile(temp.getParent(), temp.getName(), getActivity().getBaseContext().getFilesDir().toString(), cd.getFilename(), cd.getId());
                }
            }
        } else if (requestCode == RESULT_LOAD_PRESET) {
            if (resultCode == MainActivity.RESULT_OK) {
                int returnedresult = data.getIntExtra("position", 0);
                InputStream inputStream = getResources().openRawResource(returnedresult);
                Helpers.copyFile(inputStream, getActivity().getBaseContext().getFilesDir().toString(), cd.getFilename(), cd.getId());
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (cd != null && !loading && getActivity() != null) {
            cd.setNoSpecificTime(isChecked);
            updateDb();
        }
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(FileSavedEvent event) {
        Helpers.addImageToGallery(event.getFilename(), getActivity().getBaseContext());
        new AlertDialog.Builder(getActivity())
                .setTitle(getResources().getString(R.string.exporttitle))
                .setMessage(getResources().getString(R.string.exported))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //interface only
                    }
                })
                .setIcon(R.drawable.btn_check_on)
                .show();
    }

    public void export() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!canAccessWriteExternal()) {
                String[] permissions = {
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                };
                requestPermissions(permissions, PERMISSION_EXPORT);
            } else exportAllowed();
        } else exportAllowed();
    }
    public void exportAllowed() {
        save(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/Countdown"), "AA" + cd.getId(), false);
    }

    public void save(File myDir, String filename, boolean overwrite) {
        new SaveFile(myDir, filename, overwrite).start();
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getX() < cd.getPositionX() ||
                event.getX() > cd.getPositionX() + imgView.boxWidth ||
                event.getY() < cd.getPositionY() ||
                event.getY() > cd.getPositionY() + imgView.boxHeight ||
                cd.isPast()) {
            ((MyScrollView) result.findViewById(R.id.myScrollView)).setEnableScrolling(true);
            return true;
        }
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            imgView.left = event.getX();
            imgView.top = event.getY();
            ((MyScrollView) result.findViewById(R.id.myScrollView)).setEnableScrolling(false);
        } else {
            int startPositionX = cd.getPositionX();
            int startPositionY = cd.getPositionY();
            float newposX = startPositionX + ((int) event.getX() - (int) imgView.left);
            float newposY = startPositionY + ((int) event.getY() - (int) imgView.top);
            if (newposY > imgView.getHeight() - imgView.boxHeight)
                newposY = imgView.getHeight() - imgView.boxHeight;
            if (newposX > imgView.getWidth() - imgView.boxWidth)
                newposX = imgView.getWidth() - imgView.boxWidth;
            if (newposX < 0) newposX = 0;
            if (newposY < 0) newposY = 0;
            cd.setPositionX((int)newposX);
            cd.setPositionY((int)newposY);
            imgView.left = event.getX();
            imgView.top = event.getY();
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            ((MyScrollView) result.findViewById(R.id.myScrollView)).setEnableScrolling(true);
            updateDb();
        }
        // draw
        imgView.invalidate();
        return true;
    }

    private class SaveFile extends Thread {
        File myDir;
        String filename;
        boolean overwrite;

        SaveFile(File myDir, String filename, boolean overwrite) {
            this.myDir = myDir;
            this.filename = filename;
            this.overwrite = overwrite;
        }

        public void run() {
            imgView.setDrawingCacheEnabled(true);
            imgView.buildDrawingCache();
            Bitmap bmap = imgView.getDrawingCache();
            myDir.mkdirs();
            String fname = filename + ".png";
            File file = new File(myDir, fname);
            int num = 0;
            while (file.exists()) {
                if (overwrite) file.delete();
                else {
                    num++;
                    fname = filename + "_" + num + ".png";
                    file = new File(myDir, fname);
                }
            }
            try {
                FileOutputStream out = new FileOutputStream(file);
                if (bmap != null) bmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            imgView.setDrawingCacheEnabled(false);
            if (!overwrite)
                EventBus.getDefault().post(new FileSavedEvent(myDir.toString() + "/" + fname));
        }
    }
}
