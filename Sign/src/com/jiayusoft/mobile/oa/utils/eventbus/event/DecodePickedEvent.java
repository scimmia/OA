package com.jiayusoft.mobile.oa.utils.eventbus.event;

import android.graphics.Bitmap;
import com.google.zxing.Result;

/**
 * Created by ASUS on 2014/8/14.
 */
public class DecodePickedEvent {
    Result result;
    Bitmap barcode;

    public Result getResult() {
        return result;
    }

    public Bitmap getBarcode() {
        return barcode;
    }

    public DecodePickedEvent(Result result, Bitmap barcode) {
        this.result = result;
        this.barcode = barcode;
    }
}
