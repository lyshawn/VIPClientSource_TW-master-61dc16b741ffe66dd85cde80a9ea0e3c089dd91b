package com.nsplay.vip;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by JP-PC on 2017/1/6.
 */

public class NPResolutionCounter {

    public static String getScreenResolution(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        int longside;
        int shortside;
        if (width > height) {
            longside = width;
            shortside = height;
        } else {
            longside = height;
            shortside = width;
        }
        int mygcd = gcd(longside, shortside);
        //16:9
        countbannerratio("16:9", 854, 480);
        countbannerratio("16:9", 1280, 720);
        countbannerratio("16:9", 1920, 1080);
        //16:10
        countbannerratio("16:10", 1680, 1050);
        countbannerratio("16:10", 1920, 1200);
        countbannerratio("16:10", 2560, 1600);
        //3:2
        countbannerratio("3:2", 720, 480);
        countbannerratio("3:2", 1152, 768);
        countbannerratio("3:2", 1280, 854);
        countbannerratio("3:2", 1440, 960);
        //4:3
        countbannerratio("4:3", 800, 600);//16:10
        countbannerratio("4:3", 1024, 768);//3:2
        countbannerratio("4:3", 1280, 960);//5:4
        countbannerratio("4:3", 1400, 1050);//4:3
        countbannerratio("4:3", 1600, 1200);//16:9
        countbannerratio("4:3", 2048, 1536);//16:10
        //5:4
        countbannerratio("5:4", 1280, 1024);//3:2
        countbannerratio("5:4", 2560, 2048);//5:4

        return "{width=" + width + ",height=" + height + "}" +
                ",{resolution = " + longside / mygcd + ":" + shortside / mygcd + "}";
    }

    public static Bundle countbannerratio(String ratiostring, int screenpixellongside, int screenpixelshortside) {
        int ratio = 0;
        int comparelong = screenpixellongside / 16;
        int compareshort = screenpixelshortside / 9;
        if (comparelong > compareshort) {
            ratio = compareshort;
        } else {
            ratio = comparelong;
        }
        //取餘數
        int remainder = ratio / 7;

//        int remainder = 20;

        if (remainder == 0) {
            ratio -= 3;
        } else {
            ratio -= remainder + 1;
        }

        int counterbannerlongside = ratio * 16;
        int counterbannershortside = ratio * 9;

        if (screenpixelshortside - counterbannershortside <= 60 || screenpixellongside - counterbannerlongside <= 60) {
            ratio -= 1;
            counterbannerlongside = ratio * 16;
            counterbannershortside = ratio * 9;
        }

        int titlebutton = (int) (ratio * 1.3f);

        int webviewlongland = titlebutton + counterbannerlongside;
        int webviewshortland = counterbannershortside + (titlebutton / 2);

        int webviewlongport = titlebutton + counterbannerlongside + (titlebutton / 2);
        int webviewshortport = counterbannershortside;
        Log.e("NPTOOLLIST", "{比例=" + ratiostring +
                ",螢幕大小=" + screenpixellongside + ":" + screenpixelshortside +
                ",Banner頁面大小=" + counterbannerlongside +
                ":" + counterbannershortside +
                ",頁簽大小=" + titlebutton + ":" + titlebutton +
                ",X 鈕大小" + titlebutton / 2 + ":" + titlebutton / 2 +
                "},{整體大小橫式=" + webviewlongland + ":" + webviewshortland + "},{整體大小直式=" + webviewlongport + ":" + webviewshortport + "}");

        Bundle b = new Bundle();

        b.putInt("bannerlongside", counterbannerlongside);

        b.putInt("bannershortside", counterbannershortside);

        b.putInt("titlebutton", titlebutton);

        b.putInt("cancelbutton", titlebutton / 2);

        b.putInt("webviewlongland", webviewlongland);

        b.putInt("webviewshortland", webviewshortland);

        b.putInt("webviewlongport", webviewlongport);

        b.putInt("webviewshortport", webviewshortport);

        return b;
    }

    public static Bundle countnewsbannerratio(String ratiostring, int screenpixellongside, int screenpixelshortside) {
        int ratio = 0;
        int comparelong = screenpixellongside / 16;
        int compareshort = screenpixelshortside / 9;
        if (comparelong > compareshort) {
            ratio = compareshort;
        } else {
            ratio = comparelong;
        }
        //取餘數
        int remainder = ratio / 6;

//        int remainder = 20;

        if (remainder == 0) {
            ratio -= 3;
        } else {
            ratio -= remainder + 1;
        }

        int counterbannerlongside = ratio * 16;
        int counterbannershortside = ratio * 9;

        if (screenpixelshortside - counterbannershortside <= 60 || screenpixellongside - counterbannerlongside <= 60) {
            ratio -= 1;
            counterbannerlongside = ratio * 16;
            counterbannershortside = ratio * 9;
        }

        int titlebutton = (int) (ratio * 1.8f);

        int webviewlongland = titlebutton + counterbannerlongside;
        int webviewshortland = counterbannershortside + (titlebutton / 2);

        int webviewlongport = titlebutton + counterbannerlongside + (titlebutton / 2);
        int webviewshortport = counterbannershortside;
        Log.e("NPTOOLLIST", "{比例=" + ratiostring +
                ",螢幕大小=" + screenpixellongside + ":" + screenpixelshortside +
                ",Banner頁面大小=" + counterbannerlongside +
                ":" + counterbannershortside +
                ",頁簽大小=" + titlebutton + ":" + titlebutton +
                ",X 鈕大小" + titlebutton / 2 + ":" + titlebutton / 2 +
                "},{整體大小橫式=" + webviewlongland + ":" + webviewshortland + "},{整體大小直式=" + webviewlongport + ":" + webviewshortport + "}");
        Bundle b = new Bundle();
        b.putInt("titlebutton", titlebutton);
        b.putInt("bannerlongside", counterbannerlongside);
        b.putInt("bannershortside", counterbannershortside);
        b.putInt("webviewlongland", webviewlongland);
        b.putInt("webviewshortland", webviewshortland);
        b.putInt("webviewlongport", webviewlongport);
        b.putInt("webviewshortport", webviewshortport);
        Log.d("abc", "webviewlongland = " + webviewlongland);
        Log.d("abc", "webviewshortland = " + webviewshortland);
        return b;
    }

    //最大公因數算法
    public static int gcd(int m, int n) {
        return n == 0 ? m : gcd(n, m % n);
    }
}
