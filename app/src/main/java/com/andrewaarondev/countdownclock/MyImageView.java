package com.andrewaarondev.countdownclock;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Calendar;


public class MyImageView extends ImageView {
    private Countdown cd;
    public float left;
    public float top;
    public float boxWidth;
    public float boxHeight;
    private float smallestSize = 48f;

    public MyImageView(Context context) {
        super(context);
    }

    public MyImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setParams(Countdown cd) {
        this.cd = cd;
    }

    @Override
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (cd == null) return;
        Paint paint = new Paint();
        if (cd.isWatermark()) {
            paint.setColor(Color.RED);
            paint.setAlpha(160);
            paint.setStyle(Paint.Style.FILL);
            float bordersize = dpToPx(25);
            float widthofbox = dpToPx(160);
            float heightofbox = dpToPx(40);
            float wleftx = canvas.getWidth() - widthofbox - bordersize;
            float wtopy = canvas.getHeight() - heightofbox - bordersize;
            float wrightx = wleftx + widthofbox;
            float wbottomy = wtopy + heightofbox;
            canvas.drawRoundRect(new RectF(wleftx, wtopy, wrightx, wbottomy), 10, 10, paint);
            paint.setAlpha(255);
            paint.setColor(Color.WHITE);
            float boxmargin = dpToPx(8);
            setTextSizeForWidth(paint, widthofbox - boxmargin * 2, getResources().getString(R.string.app_name));
            paint.setTextSize(paint.getTextSize() - 1F);
            float lineheight = heightofbox / 2 - boxmargin;
            canvas.drawText(getResources().getString(R.string.app_name), wleftx + boxmargin, wtopy + boxmargin + lineheight, paint);
            setTextSizeForWidth(paint, widthofbox - boxmargin * 2, getResources().getString(R.string.byme));
            paint.setTextSize(paint.getTextSize() - 1F);
            canvas.drawText(getResources().getString(R.string.byme), wleftx + boxmargin, wtopy + lineheight * 2 + boxmargin, paint);
        }
        //return if event is pasted
        if (cd.isPast()) return;

        RemainingInfo ri;
        if (cd.isShowM()) ri = new RemainingInfo(Calendar.getInstance(), cd.getDate());
        else ri = new RemainingInfo(Calendar.getInstance(), cd.getDate(), true);


        int years = ri.getYears();
        int months = ri.getMonths();
        int weeks = ri.getWeeks();
        int days = ri.getDays();
        int hours = (cd.isNoSpecificTime() ? 23 : ri.getHours());
        int minutes = (cd.isNoSpecificTime() ? 59 : ri.getMinutes());
        int seconds = (cd.isNoSpecificTime() ? 59 : ri.getSeconds());

        if (years > 0 && !cd.isShowY()) {
            if (cd.isShowM()) months = months + years * 12;
            else weeks = weeks + years * 52;
            years = 0;
        }

        if (weeks > 0 && !cd.isShowW()) {
            days = days + weeks * 7;
            weeks = 0;
        }
        if (days > 0 && !cd.isShowD()) {
            hours = hours + days * 24;
            days = 0;
        }
        if (minutes > 0 && !cd.isShowMI()) {
            seconds = seconds + minutes * 60;
            minutes = 0;
        }

        if ((!cd.isShowS() || cd.isNoSpecificTime()) && seconds > 0) minutes++;
        if ((!cd.isShowMI() || cd.isNoSpecificTime()) && minutes > 0) hours++;
        if ((!cd.isShowH() || cd.isNoSpecificTime()) && hours > 0) days++;
        if (!cd.isShowD() && days > 0) weeks++;
        if (!cd.isShowW() && weeks > 0) months++;
        if (!cd.isShowM() && months > 0) years++;

        Resources r = getResources();
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

        //calculate positions for numbers
        ArrayList<String> testString = new ArrayList<>();
        int countFields = 0;
        if (cd.isShowY() && years > 0) {
            countFields++;
            testString.add((years == 1 ? y : ys));
        }
        if (cd.isShowM() && months > 0) {
            countFields++;
            testString.add((months == 1 ? m : ms));
        }
        if (cd.isShowW() && weeks > 0) {
            countFields++;
            testString.add((weeks == 1 ? w : ws));
        }
        if (cd.isShowD() && days > 0) {
            countFields++;
            testString.add((days == 1 ? d : ds));

        }
        if (!cd.isNoSpecificTime()) {
            if (cd.isShowH() && hours > 0) {
                countFields++;
                testString.add((hours == 1 ? h : hs));
            }
            if (cd.isShowMI() && minutes > 0) {
                countFields++;
                testString.add((minutes == 1 ? mi : mis));
            }
            if (cd.isShowS() && seconds > 0) {
                countFields++;
                testString.add((seconds == 1 ? s : ss));
            }
        }
        if (countFields == 0) return;


        //draw the background rectangle
        boxWidth = canvas.getWidth() * 0.8F;
        boxHeight = canvas.getHeight() * 0.4F;
        if (boxHeight > dpToPx(200)) boxHeight = dpToPx(200);

        paint.setColor(cd.getBgColour());
        paint.setStyle(Paint.Style.FILL);
        float leftx = cd.getPositionX();
        float topy = cd.getPositionY();
        float rightx = cd.getPositionX() + boxWidth;
        float bottomy = cd.getPositionY() + boxHeight;
        canvas.drawRoundRect(new RectF(leftx, topy, rightx, bottomy), 10, 10, paint);

        //set text options
        paint.setColor(cd.getFontColour());
        Typeface face = Typeface.createFromAsset(getResources().getAssets(), Helpers.fontString(cd.getFont()));
        paint.setTypeface(face);


        //calculate starting position and space between
        float margin = canvas.getHeight() * 0.05F;
        float fieldWidth = (boxWidth - margin - margin) / countFields;
        smallestSize = 48f;
        //check each field for smallest text size
        for (String sec : testString) {
            getSmallestSize(paint, fieldWidth, boxHeight * 0.3F, " " + sec);
        }
        //need a max size for lower resolution
        // if (smallestSize > 28) smallestSize = 28;

        paint.setTextSize(smallestSize - 1F);
        float spaceUnder = boxHeight * 0.15F + (paint.getTextSize() / 2);
        float startX = cd.getPositionX() + margin + fieldWidth / 2;
        float startY = cd.getPositionY() + margin + (paint.getTextSize() / 2);
        paint.setTextAlign(Paint.Align.CENTER);
        //put the numbers and labels below
        if (cd.isShowY() && years > 0) {
            canvas.drawText(years + "", startX, startY, paint);
            canvas.drawText((years == 1 ? y : ys), startX, startY + spaceUnder, paint);
            startX += fieldWidth;
        }
        if (cd.isShowM() && months > 0) {
            canvas.drawText(months + "", startX, startY, paint);
            canvas.drawText((months == 1 ? m : ms), startX, startY + spaceUnder, paint);
            startX += fieldWidth;
        }
        if (cd.isShowW() && weeks > 0) {
            canvas.drawText(weeks + "", startX, startY, paint);
            canvas.drawText((weeks == 1 ? w : ws), startX, startY + spaceUnder, paint);
            startX += fieldWidth;
        }
        if (cd.isShowD() && days > 0) {
            canvas.drawText(days + "", startX, startY, paint);
            canvas.drawText((days == 1 ? d : ds), startX, startY + spaceUnder, paint);
            startX += fieldWidth;
        }
        if (!cd.isNoSpecificTime()) {
            if (cd.isShowH() && hours > 0) {
                canvas.drawText(hours + "", startX, startY, paint);
                canvas.drawText((hours == 1 ? h : hs), startX, startY + spaceUnder, paint);
                startX += fieldWidth;
            }
            if (cd.isShowMI() && minutes > 0) {
                canvas.drawText(minutes + "", startX, startY, paint);
                canvas.drawText((minutes == 1 ? mi : mis), startX, startY + spaceUnder, paint);
                startX += fieldWidth;
            }
            if (cd.isShowS() && seconds > 0) {
                canvas.drawText(seconds + "", startX, startY, paint);
                canvas.drawText((seconds == 1 ? s : ss), startX, startY + spaceUnder, paint);
            }
        }
        float xPos = (boxWidth / 2) + cd.getPositionX();
        float yPos = cd.getPositionY() + boxHeight - margin;
        // put the 'until event' text
        float maxwid = setTextSizeForWidthAndHeight(paint, boxWidth * 0.7F, boxHeight * 0.3F, getResources().getString(R.string.until) + " " + cd.getTitle());
        paint.setTextSize(maxwid);
        canvas.drawText(getResources().getString(R.string.until) + " " + cd.getTitle(), xPos, yPos, paint);
    }

    private void setTextSizeForWidth(Paint paint, float desiredWidth,
                                     String text) {
        final float testTextSize = 48f;
        paint.setTextSize(testTextSize);
        Rect bounds = new Rect();
        int len = (text.length() <= 3 ? 3 : text.length());
        paint.getTextBounds(text, 0, len, bounds);
        float desiredTextSize = testTextSize * desiredWidth / bounds.width();
        paint.setTextSize(desiredTextSize);
    }

    private float setTextSizeForWidthAndHeight(Paint paint, float desiredWidth, float desiredHeight,
                                               String text) {
        final float testTextSize = 48f;
        paint.setTextSize(testTextSize);
        Rect bounds = new Rect();
        int len = (text.length() <= 3 ? 3 : text.length());
        paint.getTextBounds(text, 0, len, bounds);
        float desiredTextSize = testTextSize * desiredWidth / bounds.width();
        float or = testTextSize * desiredHeight / bounds.height();
        if (or < desiredTextSize) return or;
        return desiredTextSize;
    }

    private void getSmallestSize(Paint paint, float desiredWidth, float desiredHeight,
                                 String text) {
        final float testTextSize = 48f;
        paint.setTextSize(testTextSize);
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        float desiredTextSize = testTextSize * desiredWidth / bounds.width();
        float or = testTextSize * desiredHeight / bounds.height();
        if (or < desiredTextSize) desiredTextSize = or;
        if (desiredTextSize < smallestSize) smallestSize = desiredTextSize;
    }

    public float dpToPx(int pixels) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pixels, getResources().getDisplayMetrics());
    }
}