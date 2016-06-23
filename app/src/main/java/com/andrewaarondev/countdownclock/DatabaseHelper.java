package com.andrewaarondev.countdownclock;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Process;

import java.util.ArrayList;
import java.util.Calendar;

import de.greenrobot.event.EventBus;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "countdown.db";
    private static final int SCHEMA_VERSION = 2;
    private static final String TABLE = "dates";
    private static DatabaseHelper singleton = null;
    private Context ctxt;
    public static final int DEFAULT_BG = -374164814;
    public static final int DEFAULT_FONT = -5431265;
    public static final int DEFAULT_POSITION_X = 76;
    public static final int DEFAULT_POSITION_Y = 42;


    synchronized static DatabaseHelper getInstance(Context ctxt) {
        if (singleton == null) {
            singleton = new DatabaseHelper(ctxt.getApplicationContext());
        }
        return (singleton);
    }

    private DatabaseHelper(Context ctxt) {
        super(ctxt, DATABASE_NAME, null, SCHEMA_VERSION);
        this.ctxt = ctxt;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE + " (position INTEGER PRIMARY KEY, title TEXT, datetime INT, fontcolour INT, bgcolour INT, watermark INT, font INT, positionx INT, positiony INT, nospecifictime INT,showY INT,showM INT,showW INT,showD int,showH int,showMI int,showS INT);");
        ContentValues cv = new ContentValues();
        Calendar now = Calendar.getInstance();
        cv.put("title", "Vacation");
        cv.put("fontcolour", DEFAULT_FONT);
        cv.put("bgcolour", DEFAULT_BG);
        cv.put("positionx",DEFAULT_POSITION_X);
        cv.put("positiony",DEFAULT_POSITION_Y);
        cv.put("watermark", 1);
        cv.put("font", 0);
        cv.put("showY", 1);
        cv.put("showM", 1);
        cv.put("showW", 1);
        cv.put("showD", 1);
        cv.put("showH", 1);
        cv.put("showMI", 1);
        cv.put("showS", 1);
        cv.put("nospecifictime", 0);
        cv.put("datetime", now.getTimeInMillis());
        db.insert(TABLE, null, cv);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE + " ADD COLUMN " + "showY" + " INT;");
            db.execSQL("ALTER TABLE " + TABLE + " ADD COLUMN " + "showM" + " INT;");
            db.execSQL("ALTER TABLE " + TABLE + " ADD COLUMN " + "showW" + " INT;");
            db.execSQL("ALTER TABLE " + TABLE + " ADD COLUMN " + "showD" + " INT;");
            db.execSQL("ALTER TABLE " + TABLE + " ADD COLUMN " + "showH" + " INT;");
            db.execSQL("ALTER TABLE " + TABLE + " ADD COLUMN " + "showMI" + " INT;");
            db.execSQL("ALTER TABLE " + TABLE + " ADD COLUMN " + "showS" + " INT;");
            db.execSQL("UPDATE " + TABLE + " SET showY = 1,showM = 1,showW = 1,showD = 1,showH = 1,showMI = 1,showS = 1");
        }
    }

    void getCountdowns() {
        new GetCountdowns().start();
    }

    void newItem() {
        new NewItem().start();
    }

    void updateItem(Countdown c) {
        new UpdateItem(c).start();
    }

    void deleteItem(Countdown c) {
        new DeleteItem(c).start();
    }

    private class DeleteItem extends Thread {
        Countdown c;

        DeleteItem(Countdown c) {
            this.c = c;
        }

        public void run() {
            getReadableDatabase().delete(TABLE, "position = ?", new String[]{c.getId() + ""});
            new GetCountdowns().start();
        }
    }

    private class UpdateItem extends Thread {
        Countdown c;

        UpdateItem(Countdown c) {
            this.c = c;
        }

        public void run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            String strFilter = "position=" + c.getId();
            ContentValues args2 = new ContentValues();
            args2.put("datetime", c.getDate().getTimeInMillis());
            args2.put("title", c.getTitle());
            args2.put("fontcolour", c.getFontColour());
            args2.put("bgcolour", c.getBgColour());
            args2.put("positionx", c.getPositionX());
            args2.put("positiony", c.getPositionY());
            args2.put("watermark", c.isWatermark());
            args2.put("font", c.getFont());
            args2.put("showY", c.isShowY());
            args2.put("showM", c.isShowM());
            args2.put("showW", c.isShowW());
            args2.put("showD", c.isShowD());
            args2.put("showH", c.isShowH());
            args2.put("showMI", c.isShowMI());
            args2.put("showS", c.isShowS());
            args2.put("nospecifictime", c.isNoSpecificTime());
            getWritableDatabase().update(TABLE, args2, strFilter, null);
        }
    }

    private class NewItem extends Thread {
        public void run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            ContentValues cv3 = new ContentValues();
            Calendar now = Calendar.getInstance();
            cv3.put("title", ctxt.getResources().getString(R.string.newevent));
            cv3.put("fontcolour", DEFAULT_FONT);
            cv3.put("bgcolour", DEFAULT_BG);
            cv3.put("positionx", DEFAULT_POSITION_X);
            cv3.put("positiony", DEFAULT_POSITION_Y);
            cv3.put("watermark", 1);
            cv3.put("font", 0);
            cv3.put("showY", 1);
            cv3.put("showM", 1);
            cv3.put("showW", 1);
            cv3.put("showD", 1);
            cv3.put("showH", 1);
            cv3.put("showMI", 1);
            cv3.put("showS", 1);
            cv3.put("nospecifictime", 0);
            cv3.put("datetime", now.getTimeInMillis());
            getReadableDatabase().insert(TABLE, null, cv3);
            new GetCountdowns().start();
        }
    }

    private class GetCountdowns extends Thread {
        public void run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            Cursor c = getReadableDatabase().rawQuery("SELECT * FROM " + TABLE + " ORDER BY position DESC", null);
            ArrayList<Countdown> result = new ArrayList<>();
            c.moveToFirst();
            while (!c.isAfterLast()) {
                result.add(new Countdown(c.getInt(0), c.getString(1), c.getLong(2), c.getInt(3), c.getInt(4), (c.getInt(5) == 1), c.getInt(6), c.getInt(7), c.getInt(8), (c.getInt(9) == 1), (c.getInt(10) == 1), (c.getInt(11) == 1), (c.getInt(12) == 1), (c.getInt(13) == 1), (c.getInt(14) == 1), (c.getInt(15) == 1), (c.getInt(16) == 1)));
                c.moveToNext();
            }
            EventBus.getDefault().postSticky(new ListLoadedEvent(result));
            c.close();
        }
    }
}
