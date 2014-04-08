package org.arnolds.agileappproject.agileappmodule.utils;

import android.content.Context;
import android.util.Log;

import org.arnolds.agileappproject.agileappmodule.R;

import java.lang.reflect.Field;

public abstract class AgileAppModuleUtils {

    public static String getString(Context context, String variableName, String defaultRet) {
        String ret = defaultRet;

        try {
            Field resourceField = R.string.class.getDeclaredField(variableName);
            int resourceId = resourceField.getInt(resourceField);
            ret = context.getString(resourceId);
        }
        catch (NoSuchFieldException e) {
//            Log.wtf("debug", e.getClass().getName(), e);
        }
        catch (IllegalAccessException e) {
            Log.wtf("debug", e.getClass().getName(), e);
        }

        return ret;
    }


    public static int getDrawableAsId(String variableName, int defaultRet) {
        int ret = defaultRet;

        try {
            Field resourceField = R.drawable.class.getDeclaredField(variableName);
            ret = resourceField.getInt(resourceField);
        }
        catch (NoSuchFieldException e) {
            Log.wtf("debug", e.getClass().getName(), e);
        }
        catch (IllegalAccessException e) {
            Log.wtf("debug", e.getClass().getName(), e);
        }

        return ret;
    }
}
