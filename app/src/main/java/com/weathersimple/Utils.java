package com.weathersimple;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;

/**
 * Created by Diana on 05.08.2016 at 22:06.
 */
public class Utils {


    public static Drawable getDrawable(Context context, String name) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return context.getResources().getDrawable(context.getResources().getIdentifier("w" + name, "drawable", context.getPackageName()), null);
        } else {
            return context.getResources().getDrawable(context.getResources().getIdentifier("w" + name, "drawable", context.getPackageName()));
        }
    }
}
