package com.buffalo.adsdk.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.buffalo.adsdk.R;
import com.buffalo.utils.Commons;
import com.buffalo.utils.NetworkUtil;


/**
 * Created by chenhao on 16/5/11.
 */
public class DownloadCheckDialog {
    public interface DownloadCheckListener{
        void handleDownload();
        void cancelDownload();
    }

    public static void showDialog(Context context, final DownloadCheckListener listener){
        if(context == null || listener == null){
            return;
        }
        if (NetworkUtil.isMobileNetWork(context)) {
            //取消状态栏
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = (View) inflater.inflate(R.layout.gps_dialog, null);
            final Dialog dialog = new AlertDialog.Builder(context).create();
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

            //取消按钮
            TextView tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);
            tv_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    listener.cancelDownload();
                }
            });

            TextView tv_download = (TextView) view.findViewById(R.id.tv_download);
            tv_download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    listener.handleDownload();
                }
            });
            if (Commons.isMiui()) {
                dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
            } else {
                dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            }
            dialog.show();
            dialog.getWindow().setContentView(view);
        } else {
            listener.handleDownload();
        }

    }
}
