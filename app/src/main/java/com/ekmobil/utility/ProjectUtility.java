package com.ekmobil.utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Button;

import com.ekmobil.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by melih on 25.03.2018.
 */

public class ProjectUtility {

    public static Point getScreenSizes(Activity activity) {

        Point size = new Point();
        WindowManager w = activity.getWindowManager();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            w.getDefaultDisplay().getSize(size);
//            screenWidth = size.x;
//            screenheight = size.y;
        } else {
            Display d = w.getDefaultDisplay();
            size.x = d.getWidth();
            size.y = d.getHeight();
        }

        return size;
    }

    public static void showDialog(Context context, String title, String message, String positiveButtonLabel, String negativeButtonLabel, DialogInterface.OnClickListener positiveListener, DialogInterface.OnClickListener negativeListener) {
        if (!((Activity) context).isFinishing()) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setMessage(message);
            alertDialogBuilder.setTitle(title);


            if (positiveButtonLabel != null) {
                if (positiveButtonLabel.equalsIgnoreCase(context.getString(R.string.messages_ok))) {
                    alertDialogBuilder.setCancelable(false);
                }
                alertDialogBuilder.setPositiveButton(positiveButtonLabel, positiveListener);
            }

            if (negativeButtonLabel != null) {
                alertDialogBuilder.setNegativeButton(negativeButtonLabel, negativeListener);
            }

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

            if (positiveButtonLabel != null) {
                Button btnPositive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                if (btnPositive != null)
                    btnPositive.setTextColor(Color.BLACK);
            }

            if (negativeButtonLabel != null) {
                Button btnNegative = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                if (btnNegative != null) {
                    btnNegative.setTextColor(Color.BLACK);
                }
            }
        }
    }

    public static String getJsonString(JSONObject jsonObject, String key) {
        try {
            if (!jsonObject.isNull(key)) {
                return jsonObject.getString(key);
            } else {
                return "";
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getJsonValue(JSONObject jsonObject, String key) {
        try {
            if (!jsonObject.isNull(key)) {
                Object o = jsonObject.get(key);

//                if (o instanceof Long)
                return String.valueOf(o);
//                else
//                    return o.toString();
            } else {
                return "";
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

}
