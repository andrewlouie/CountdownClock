package com.andrewaarondev.countdownclock;

import android.app.Application;
import android.content.Intent;

public class MyApplication extends Application {
    public int requestCode;
    public int resultCode;
    public Intent data;

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public Intent getData() {
        return data;
    }

    public void setData(Intent data) {
        this.data = data;
    }
}
