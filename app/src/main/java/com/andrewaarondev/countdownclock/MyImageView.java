package com.andrewaarondev.countdownclock;

import android.content.Context;
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
            paint.setTextSize(paint.getTextSize() -1F);
            float lineheight = heightofbox / 2 - boxmargin;
            canvas.drawText(getResources().getString(R.string.app_name), wleftx + boxmargin, wtopy + boxmargin + lineheight, paint);
            setTextSizeForWidth(paint, widthofbox - boxmargin * 2, getResources().getString(R.string.byme));
            paint.setTextSize(paint.getTextSize() -1F);
            canvas.drawText(getResources().getString(R.string.byme), wleftx + boxmargin, wtopy + lineheight *2 + boxmargin, paint);
        }
        //return if event is pasted
        RemainingInfo ri = new RemainingInfo(Calendar.getInstance(), cd.getDate());
        if (cd.isPast()) return;

        //calculate positions for numbers
        ArrayList<String> testString = new ArrayList<>();
        int countFields = 0;
        if (ri.getYears() > 0) {
            countFields++;
            testString.add((ri.getYears() == 1 ? getResources().getString(R.string.year) : getResources().getString(R.string.years)));
        }
        if (ri.getMonths() > 0) {
            countFields++;
            testString.add((ri.getMonths() == 1 ? getResources().getString(R.string.month) : getResources().getString(R.string.months)));
        }
        if (ri.getWeeks() > 0) {
            countFields++;
            testString.add((ri.getWeeks() == 1 ? getResources().getString(R.string.week) : getResources().getString(R.string.weeks)));
        }
        if (ri.getDays() > 0) {
            countFields++;
            testString.add((ri.getDays() == 1 ? getResources().getString(R.string.day) : getResources().getString(R.string.days)));

        }
        if (!cd.isNoSpecificTime()) {
            if (ri.getHours() > 0) {
                countFields++;
                testString.add((ri.getHours() == 1 ? getResources().getString(R.string.hour) : getResources().getString(R.string.hours)));
            }
            if (ri.getMinutes() > 0) {
                countFields++;
                testString.add((ri.getMinutes() == 1 ? getResources().getString(R.string.minute) : getResources().getString(R.string.minutes)));
            }
            if (ri.getSeconds() > 0) {
                countFields++;
                testString.add((ri.getSeconds() == 1 ? getResources().getString(R.string.second) : getResources().getString(R.string.seconds)));
            }
        }
        if (countFields == 0) return;


        //draw the background rectangle
        boxWidth = canvas.getWidth() * 0.8F;
        boxHeight = canvas.getHeight() * 0.4F;

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
        for (String s: testString) {
            getSmallestSize(paint,fieldWidth," " + s);
        }
        //need a max size for lower resolutions
        // if (smallestSize > 28) smallestSize = 28;
        paint.setTextSize(smallestSize -1F);
        float spaceUnder = boxHeight * 0.15F + (paint.getTextSize() / 2);
        float startX = cd.getPositionX() + margin + fieldWidth / 2;
        float startY = cd.getPositionY() + margin + (paint.getTextSize() / 2);
        paint.setTextAlign(Paint.Align.CENTER);

        //put the numbers and labels below
        if (ri.getYears() > 0) {
            canvas.drawText(ri.getYears() + "", startX, startY, paint);
            canvas.drawText(getResources().getString(R.string.years), startX, startY + spaceUnder, paint);
            startX += fieldWidth;
        }
        if (ri.getMonths() > 0) {
            canvas.drawText(ri.getMonths() + "", startX, startY, paint);
            canvas.drawText(getResources().getString(R.string.months), startX, startY + spaceUnder, paint);
            startX += fieldWidth;
        }
        if (ri.getWeeks() > 0) {
            canvas.drawText(ri.getWeeks() + "", startX, startY, paint);
            canvas.drawText(getResources().getString(R.string.weeks), startX, startY + spaceUnder, paint);
            startX += fieldWidth;
        }
        if (ri.getDays() > 0) {
            canvas.drawText(ri.getDays() + "", startX, startY, paint);
            canvas.drawText(getResources().getString(R.string.days), startX, startY + spaceUnder, paint);
            startX += fieldWidth;
        }
        if (!cd.isNoSpecificTime()) {
            if (ri.getHours() > 0) {
                canvas.drawText(ri.getHours() + "", startX, startY, paint);
                canvas.drawText(getResources().getString(R.string.hours), startX, startY + spaceUnder, paint);
                startX += fieldWidth;
            }
            if (ri.getMinutes() > 0) {
                canvas.drawText(ri.getMinutes() + "", startX, startY, paint);
                canvas.drawText(getResources().getString(R.string.minutes), startX, startY + spaceUnder, paint);
                startX += fieldWidth;
            }
            if (ri.getSeconds() > 0) {
                canvas.drawText(ri.getSeconds() + "", startX, startY, paint);
                canvas.drawText(getResources().getString(R.string.seconds), startX, startY + spaceUnder, paint);
            }
        }
        float xPos = (boxWidth / 2) + cd.getPositionX();
        float yPos = cd.getPositionY() + boxHeight - margin;
        // put the 'until event' text
        setTextSizeForWidth(paint,boxWidth * 0.7F,getResources().getString(R.string.until) + " " + cd.getTitle());
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
    private void getSmallestSize(Paint paint, float desiredWidth,
                                     String text) {
        final float testTextSize = 48f;
        paint.setTextSize(testTextSize);
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        float desiredTextSize = testTextSize * desiredWidth / bounds.width();
        if (desiredTextSize < smallestSize) smallestSize = desiredTextSize;
        paint.setTextSize(desiredTextSize);
    }
    public float dpToPx(int pixels) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pixels, getResources().getDisplayMetrics());
    }
}