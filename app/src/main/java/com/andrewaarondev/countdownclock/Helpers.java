package com.andrewaarondev.countdownclock;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;

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

    public static String getDateDifference(Calendar dateFrom, Calendar dateTo, boolean withoutTime) {
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
                    .appendYears().appendSuffix(" year ", " years ")
                    .appendMonths().appendSuffix(" month ", " months ")
                    .appendWeeks().appendSuffix(" week ", " weeks ")
                    .appendDays().appendSuffix(" day ", " days ")
                    .printZeroNever()
                    .toFormatter();
        } else {
            formatter = new PeriodFormatterBuilder()
                    .appendYears().appendSuffix(" year ", " years ")
                    .appendMonths().appendSuffix(" month ", " months ")
                    .appendWeeks().appendSuffix(" week ", " weeks ")
                    .appendDays().appendSuffix(" day ", " days ")
                    .appendHours().appendSuffix(" hour ", " hours ")
                    .appendMinutes().appendSuffix(" minute ", " minutes ")
                    .appendSeconds().appendSuffix(" second ", " seconds ")
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
        new MoveFile(inputPath, inputFile, outputPath, newFileName, id).start();
    }

    private static class MoveFile extends Thread {
        String inputPath;
        String inputFile;
        String outputPath;
        String newFileName;
        long id;

        MoveFile(String inputPath, String inputFile, String outputPath, String newFileName, long id) {
            this.inputPath = inputPath;
            this.inputFile = inputFile;
            this.outputPath = outputPath;
            this.newFileName = newFileName;
            this.id = id;
        }

        public void run() {
            InputStream in;
            OutputStream out;
            try {

                //create output directory if it doesn't exist
                File dir = new File(outputPath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }


                in = new FileInputStream(inputPath + "/" + inputFile);
                out = new FileOutputStream(outputPath + "/" + newFileName);

                byte[] buffer = new byte[1024];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
                in.close();
                in = null;

                // write the output file
                out.flush();
                out.close();
                out = null;

                // delete the original file
                new File(inputPath + "/" + inputFile).delete();


            } catch (Exception e) {
                Log.e("tag", e.getMessage());
            } finally {
                EventBus.getDefault().postSticky(new FileCopiedEvent(id));
            }
        }
    }

    public static Uri validUri(String path, String filename) {
        File check = new File(path, filename);
        if (check.exists()) return Uri.fromFile(check);
        return Uri.parse("android.resource://com.andrewaarondev.countdownclock/" + R.raw.sunset);
    }

    public static void copyFile(String inputPath, String inputFile, String outputPath, String newFileName, long id) {
        new CopyFile(inputPath, inputFile, outputPath, newFileName, id).start();
    }

    private static class CopyFile extends Thread {
        String inputPath;
        String inputFile;
        String outputPath;
        String newFileName;
        InputStream inputStream;
        long id;

        CopyFile(String inputPath, String inputFile, String outputPath, String newFileName, long id) {
            this.inputFile = inputFile;
            this.inputPath = inputPath;
            this.outputPath = outputPath;
            this.newFileName = newFileName;
            this.id = id;
        }

        CopyFile(InputStream inputStream, String outputPath, String newFileName, long id) {
            this.inputStream = inputStream;
            this.outputPath = outputPath;
            this.newFileName = newFileName;
            this.id = id;
        }

        public void run() {
            InputStream in = inputStream;
            OutputStream out;
            try {
                //create output directory if it doesn't exist
                File dir = new File(outputPath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                if (in == null) in = new FileInputStream(inputPath + "/" + inputFile);
                out = new FileOutputStream(outputPath + "/" + newFileName);
                byte[] buffer = new byte[1024];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
                in.close();
                in = null;
                // write the output file
                out.flush();
                out.close();
                out = null;
            } catch (Exception e) {
                Log.e("tag", e.getMessage());
            } finally {
                EventBus.getDefault().postSticky(new FileCopiedEvent(id));
            }
        }
    }

    public static void copyFile(InputStream inputStream, String outputPath, String newFileName, long id) {
        new CopyFile(inputStream, outputPath, newFileName, id).start();
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
}
