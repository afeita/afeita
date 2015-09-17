package com.github.afeita.sample.netsample;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.afeita.sample.R;


/**
 * Created by chenshufei on 15/9/7.
 */
public class UploadFileProgreeDialog extends Dialog {

    private ProgressBar pb_progress;
    private TextView tv_file;
    private TextView tv_progress;
    private TextView tv_progress_precent;

    private int mScreenWidth;

    public UploadFileProgreeDialog(Context context) {
        super(context);
    }

    public UploadFileProgreeDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected UploadFileProgreeDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_uploadfile_tipsprogress);

        initView();
    }

    @Override
    public void show() {
        super.show();
        if (0 == mScreenWidth){
            WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
            Display defaultDisplay = windowManager.getDefaultDisplay();
            mScreenWidth = defaultDisplay.getWidth();
        }
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.width = (int) (mScreenWidth);
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(layoutParams);
    }

    private void initView() {
        tv_file = (TextView) findViewById(R.id.tv_file);
        pb_progress = (ProgressBar) findViewById(R.id.pb_progress);
        tv_progress = (TextView) findViewById(R.id.tv_progress);
        tv_progress_precent = (TextView) findViewById(R.id.tv_progress_precent);
    }

    public void setFileTip(String fileName){
        tv_file.setText(fileName);
    }

    public void setProgress(int progress,String strProgress,String progressPrecent){
        pb_progress.setProgress(progress);
        tv_progress.setText(strProgress);
        tv_progress_precent.setText("完成："+progressPrecent);
    }

}
