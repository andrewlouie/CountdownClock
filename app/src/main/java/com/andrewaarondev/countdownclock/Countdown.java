package com.andrewaarondev.countdownclock;

import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.DateTime;

import java.util.Calendar;

public class Countdown implements Parcelable {
    private long id;
    private String title;
    private Calendar date;
    private int positionX;
    private int positionY;
    private boolean watermark;
    private int fontColour;
    private int bgColour;
    private int font;
    private boolean noSpecificTime;
    private boolean showY;
    private boolean showM;
    private boolean showD;
    private boolean showW;
    private boolean showH;
    private boolean showMI;
    private boolean showS;

    public void resetShowing() {
        showY = false;
        showM = false;
        showW = false;
        showD = false;
        showH = false;
        showMI = false;
        showS = false;
    }

    public boolean isShowY() {
        return showY;
    }

    public void setShowY(boolean showY) {
        this.showY = showY;
    }

    public boolean isShowM() {
        return showM;
    }

    public void setShowM(boolean showM) {
        this.showM = showM;
    }

    public boolean isShowD() {
        return showD;
    }

    public void setShowD(boolean showD) {
        this.showD = showD;
    }

    public boolean isShowW() {
        return showW;
    }

    public void setShowW(boolean showW) {
        this.showW = showW;
    }

    public boolean isShowH() {
        return showH;
    }

    public void setShowH(boolean showH) {
        this.showH = showH;
    }

    public boolean isShowMI() {
        return showMI;
    }

    public void setShowMI(boolean showMI) {
        this.showMI = showMI;
    }

    public boolean isShowS() {
        return showS;
    }

    public void setShowS(boolean showS) {
        this.showS = showS;
    }

    public boolean isPast() {
        Calendar dateFrom = Calendar.getInstance();
        Calendar dateTo = date;
        dateTo.set(Calendar.SECOND, 0);
        dateTo.set(Calendar.MILLISECOND, 0);
        if (dateFrom.getTimeInMillis() >= dateTo.getTimeInMillis()) return true;
        DateTime dateTimeFrom = new DateTime(dateFrom);
        DateTime dateTimeTo = new DateTime(dateTo);
        if (noSpecificTime) dateTimeTo = dateTimeTo.withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0);
        return dateTimeFrom.isAfter(dateTimeTo);
    }

    public boolean isNoSpecificTime() {
        return noSpecificTime;
    }

    public void setNoSpecificTime(boolean noSpecificTime) {
        this.noSpecificTime = noSpecificTime;
    }

    public int getPositionX() {
        return positionX;
    }

    public void setPositionX(int positionX) {
        this.positionX = positionX;
    }

    public int getPositionY() {
        return positionY;
    }

    public void setPositionY(int positionY) {
        this.positionY = positionY;
    }

    public boolean isWatermark() {
        return watermark;
    }

    public void setWatermark(boolean watermark) {
        this.watermark = watermark;
    }

    public int getBgColour() {
        return bgColour;
    }

    public int getFontColour() {
        return fontColour;
    }

    public void setFontColour(int fontColour) {
        this.fontColour = fontColour;
    }

    public void setBgColour(int bgColour) {
        this.bgColour = bgColour;
    }

    public int getFont() {
        return font;
    }

    public void setFont(int font) {
        this.font = font;
    }

    Countdown(int id, String title, long dateTime, int fontColour, int bgColour, boolean watermark, int font, int positionX, int positionY, boolean noSpecificTime, boolean showY, boolean showM, boolean showW, boolean showD, boolean showH, boolean showMI, boolean showS) {
        this.id = id;
        this.title = title;
        this.date = Calendar.getInstance();
        this.date.setTimeInMillis(dateTime);
        this.fontColour = fontColour;
        this.bgColour = bgColour;
        this.watermark = watermark;
        this.positionX = positionX;
        this.positionY = positionY;
        this.font = font;
        this.noSpecificTime = noSpecificTime;
        this.showD = showD;
        this.showH = showH;
        this.showY = showY;
        this.showM = showM;
        this.showW = showW;
        this.showMI = showMI;
        this.showS = showS;
    }

    public Calendar getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public long getId() {
        return id;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFilename() {
        return "AA" + id + ".jpg";
    }

    protected Countdown(Parcel in) {
        id = in.readLong();
        title = in.readString();
        date = (Calendar) in.readValue(Calendar.class.getClassLoader());
        positionX = in.readInt();
        positionY = in.readInt();
        watermark = in.readByte() != 0x00;
        noSpecificTime = in.readByte() != 0x00;
        showY = in.readByte() != 0x00;
        showM = in.readByte() != 0x00;
        showW = in.readByte() != 0x00;
        showD = in.readByte() != 0x00;
        showH = in.readByte() != 0x00;
        showMI = in.readByte() != 0x00;
        showS = in.readByte() != 0x00;
        fontColour = in.readInt();
        bgColour = in.readInt();
        font = in.readInt();

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeValue(date);
        dest.writeInt(positionX);
        dest.writeInt(positionY);
        dest.writeByte((byte) (watermark ? 0x01 : 0x00));
        dest.writeByte((byte) (noSpecificTime ? 0x01 : 0x00));
        dest.writeByte((byte) (showY ? 0x01 : 0x00));
        dest.writeByte((byte) (showM ? 0x01 : 0x00));
        dest.writeByte((byte) (showW ? 0x01 : 0x00));
        dest.writeByte((byte) (showD ? 0x01 : 0x00));
        dest.writeByte((byte) (showH ? 0x01 : 0x00));
        dest.writeByte((byte) (showM ? 0x01 : 0x00));
        dest.writeByte((byte) (showS ? 0x01 : 0x00));
        dest.writeInt(fontColour);
        dest.writeInt(bgColour);
        dest.writeInt(font);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Countdown> CREATOR = new Parcelable.Creator<Countdown>() {
        @Override
        public Countdown createFromParcel(Parcel in) {
            return new Countdown(in);
        }

        @Override
        public Countdown[] newArray(int size) {
            return new Countdown[size];
        }
    };
}