package com.nsplay.vip;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.text.TextPaint;
import android.util.Log;
import android.util.TypedValue;
import android.widget.TextView;


import java.util.Locale;

/**
 * Created by 偉翔 on 2017/1/7.
 */
public class NPToolUtils {

    private static int resourcesID(Context context, String value_name, String resources_type) {                  //直接抓到檔案位置不藉由R

        return context.getResources().getIdentifier(value_name, resources_type, context.getPackageName());
    }

    public static int getIDFromDrawable(Context context, String value_name) {

        return resourcesID(context, value_name, "drawable");
    }

    public static String getStringFromXml(Context context, String value_name) {

        return context.getString(resourcesID(context, value_name, "string"));
    }

    public static int getIDFromLayout(Context context, String value_name) {

        return resourcesID(context, value_name, "layout");
    }

    public static int getIDFromItem(Context context, String value_name) {

        return resourcesID(context, value_name, "id");
    }

    public static void adjustTvTextWidthSize(TextView tv, int maxWidth, String text) {

        int avaiWidth = maxWidth - tv.getPaddingLeft() - tv.getPaddingRight() - 10;
        if (avaiWidth <= 0) {
            return;
        }
        TextPaint textPaintClone = new TextPaint(tv.getPaint());
        // note that Paint text size works in px not sp
        float trySize = textPaintClone.getTextSize();
        while (textPaintClone.measureText(text) > avaiWidth) {
            trySize = trySize - 2;
            textPaintClone.setTextSize(trySize);
        }
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, trySize);
    }

    public static float adjustTvTextHeightSize(TextView tv, int maxHeight, String text) {
        int avaiHeight = maxHeight - tv.getPaddingTop() - tv.getPaddingBottom() - 10;
        Log.d("abc", "avaiHeight = " + avaiHeight);
        if (avaiHeight <= 0) {
            return 20;
        }
        TextPaint textPaintClone = new TextPaint(tv.getPaint());
        // note that Paint text size works in px not sp
        float trySize = textPaintClone.getTextSize();
        while (textPaintClone.getFontMetrics().descent - textPaintClone.getFontMetrics().ascent > avaiHeight) {
            Log.d("abc", "height12 = " + (textPaintClone.getFontMetrics().descent - textPaintClone.getFontMetrics().ascent));
            trySize = trySize - 2;
            textPaintClone.setTextSize(trySize);
        }
        return trySize;
    }




}
