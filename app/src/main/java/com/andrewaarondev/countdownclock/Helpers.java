package com.andrewaarondev.countdownclock;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;

import de.greenrobot.event.EventBus;

public class Helpers {
    public static final int CASUEL = 0;
    public static final int CURSIVE = 1;
    public static final int MONOSPACE = 2;
    public static final int SANS_SERIF = 3;
    public static final int SANS_SERIF_CONDENSED = 4;
    public static final int SANS_SERIF_SMALLCAPS = 5;
    public static final int SERIF = 6;
    public static final int SERIF_MONOSPACE = 7;

    public static String getDateDifference(Calendar dateFrom, Calendar dateTo, boolean withoutTime, Resources r) {
        String y = " " + r.getString(R.string.year) + " ";
        String m = " " + r.getString(R.string.month) + " ";
        String w = " " + r.getString(R.string.week) + " ";
        String d = " " + r.getString(R.string.day) + " ";
        String h = " " + r.getString(R.string.hour) + " ";
        String mi = " " + r.getString(R.string.minute) + " ";
        String s = " " + r.getString(R.string.second) + " ";
        String ys = " " + r.getString(R.string.years) + " ";
        String ms = " " + r.getString(R.string.months) + " ";
        String ws = " " + r.getString(R.string.weeks) + " ";
        String ds = " " + r.getString(R.string.days) + " ";
        String hs = " " + r.getString(R.string.hours) + " ";
        String mis = " " + r.getString(R.string.minutes) + " ";
        String ss = " " + r.getString(R.string.seconds) + " ";
        String diff = " ";
        dateTo.set(Calendar.SECOND, 0);
        dateTo.set(Calendar.MILLISECOND, 0);
        if (dateFrom.getTimeInMillis() >= dateTo.getTimeInMillis()) return diff;
        DateTime dateTimeFrom = new DateTime(dateFrom);
        DateTime dateTimeTo = new DateTime(dateTo);
        Period period = new Period(dateTimeFrom, dateTimeTo);
        PeriodFormatter formatter;
        if (withoutTime) {
            formatter = new PeriodFormatterBuilder()
                    .appendYears().appendSuffix(y, ys)
                    .appendMonths().appendSuffix(m, ms)
                    .appendWeeks().appendSuffix(w, ws)
                    .appendDays().appendSuffix(d, ds)
                    .printZeroNever()
                    .toFormatter();
        } else {
            formatter = new PeriodFormatterBuilder()
                    .appendYears().appendSuffix(y, ys)
                    .appendMonths().appendSuffix(m, ms)
                    .appendWeeks().appendSuffix(w, ws)
                    .appendDays().appendSuffix(d, ds)
                    .appendHours().appendSuffix(h, hs)
                    .appendMinutes().appendSuffix(mi, mis)
                    .appendSeconds().appendSuffix(s, ss)
                    .printZeroNever()
                    .toFormatter();
        }

        diff = formatter.print(period);
        return diff;
    }

    public static void deleteFile(String inputPath, String inputFile) {
        new DeleteFile(inputPath, inputFile).start();
    }

    private static class DeleteFile extends Thread {
        String inputPath;
        String inputFile;

        DeleteFile(String inputPath, String inputFile) {
            this.inputFile = inputFile;
            this.inputPath = inputPath;
        }

        public void run() {
            try {
                new File(inputPath + "/" + inputFile).delete();
            } catch (Exception e) {
                Log.w("tag", e.getMessage());
            }
        }
    }

    public static void moveFile(String inputPath, String inputFile, String outputPath, String newFileName, long id) {
        new CopyFile(inputPath, inputFile, outputPath, newFileName, id, true).start();
    }

    public static Uri validUri(String path, String filename) {
        File check = new File(path, filename);
        if (check.exists()) return Uri.fromFile(check);
        return Uri.parse("android.resource://com.andrewaarondev.countdownclock/" + R.raw.sunset);
    }

    public static void copyFile(String inputPath, String inputFile, String outputPath, String newFileName, long id) {
        new CopyFile(inputPath, inputFile, outputPath, newFileName, id, false).start();
    }

    private static class CopyFile extends Thread {
        String inputPath;
        String inputFile;
        String outputPath;
        String newFileName;
        InputStream inputStream;
        long id;
        boolean delete;

        CopyFile(String inputPath, String inputFile, String outputPath, String newFileName, long id, boolean delete) {
            this.inputFile = inputFile;
            this.inputPath = inputPath;
            this.outputPath = outputPath;
            this.newFileName = newFileName;
            this.id = id;
            this.delete = delete;
        }

        CopyFile(InputStream inputStream, String outputPath, String newFileName, long id, boolean delete) {
            this.inputStream = inputStream;
            this.outputPath = outputPath;
            this.newFileName = newFileName;
            this.id = id;
            this.delete = delete;
        }

        public void run() {
            InputStream in = inputStream;
            OutputStream out = null;
            try {
                //create output directory if it doesn't exist
                File dir = new File(outputPath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                if (in == null) in = new FileInputStream(inputPath + "/" + inputFile);
                out = new FileOutputStream(outputPath + "/" + newFileName);
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                int max = getMaxTextureSize();
                //very high resolution images need to be resized:
                Bitmap scaled;
                if (bitmap.getWidth() > max || bitmap.getHeight() > max) {
                    if (bitmap.getWidth() <= bitmap.getHeight()) {
                        int nw = bitmap.getWidth() * ((max) / bitmap.getHeight());
                        scaled = Bitmap.createScaledBitmap(bitmap, nw, max, true);
                    } else {
                        int nh = bitmap.getHeight() * ((max) / bitmap.getHeight());
                        scaled = Bitmap.createScaledBitmap(bitmap, max, nh, true);
                    }
                } else {
                    scaled = bitmap;
                }
                scaled.compress(Bitmap.CompressFormat.PNG, 100, out);
            } catch (Exception e) {
                Log.e("tag", e.getMessage());
                e.printStackTrace();
            } finally {
                try {
                    if (in != null) {
                        in.close();
                        in = null;
                    }
                } catch (IOException ex) {
                    Log.w("tag", "Something went wrong");
                }
                try {
                    // write the output file
                    if (out != null) {
                        out.flush();
                        out.close();
                        out = null;
                    }
                } catch (IOException ex) {
                    Log.w("tag", "Something went wrong");
                }
                EventBus.getDefault().postSticky(new FileCopiedEvent(id));
                if (delete) new File(inputPath + "/" + inputFile).delete();
            }
        }
    }

    public static void copyFile(InputStream inputStream, String outputPath, String newFileName, long id) {
        new CopyFile(inputStream, outputPath, newFileName, id, false).start();
    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index;
            if (cursor != null) {
                column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                return cursor.getString(column_index);
            }
            return "";
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static void addImageToGallery(final String filePath, final Context context) {

        ContentValues values = new ContentValues();

        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.MediaColumns.DATA, filePath);

        context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    public static String fontString(int font) {
        switch (font) {
            case CASUEL:
                return "fonts/ComingSoon.ttf";
            case CURSIVE:
                return "fonts/DancingScript-Regular.ttf";
            case MONOSPACE:
                return "fonts/droid-sans-mono.ttf";
            case SANS_SERIF:
                return "fonts/Roboto-Regular.ttf";
            case SANS_SERIF_CONDENSED:
                return "fonts/Roboto-Condensed.ttf";
            case SANS_SERIF_SMALLCAPS:
                return "fonts/CarroisGothicSC-Regular.ttf";
            case SERIF:
                return "fonts/NotoSerif-Regular.ttf";
            case SERIF_MONOSPACE:
                return "fonts/CutiveMono-Regular.ttf";
        }
        return "";
    }

    public static int getMaxTextureSize() {
        // Safe minimum default size
        final int IMAGE_MAX_BITMAP_DIMENSION = 2048;

        // Get EGL Display
        EGL10 egl = (EGL10) EGLContext.getEGL();
        EGLDisplay display = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);

        // Initialise
        int[] version = new int[2];
        egl.eglInitialize(display, version);

        // Query total number of configurations
        int[] totalConfigurations = new int[1];
        egl.eglGetConfigs(display, null, 0, totalConfigurations);

        // Query actual list configurations
        EGLConfig[] configurationsList = new EGLConfig[totalConfigurations[0]];
        egl.eglGetConfigs(display, configurationsList, totalConfigurations[0], totalConfigurations);

        int[] textureSize = new int[1];
        int maximumTextureSize = 0;

        // Iterate through all the configurations to located the maximum texture size
        for (int i = 0; i < totalConfigurations[0]; i++) {
            // Only need to check for width since opengl textures are always squared
            egl.eglGetConfigAttrib(display, configurationsList[i], EGL10.EGL_MAX_PBUFFER_WIDTH, textureSize);

            // Keep track of the maximum texture size
            if (maximumTextureSize < textureSize[0])
                maximumTextureSize = textureSize[0];
        }

        // Release
        egl.eglTerminate(display);

        // Return largest texture size found, or default
        return Math.max(maximumTextureSize, IMAGE_MAX_BITMAP_DIMENSION);
    }
}
