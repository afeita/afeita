package com.github.afeita.tools.dialog;


import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 请求loading...，提示的对话框，可以重写此对话框，提供新的显示样式。
 * <br /> author: chenshufei
 * <br /> date: 15/9/10
 * <br /> email: chenshufei2@sina.com
 */
public class NetRequestTipsDialog extends DialogFragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //去掉标题
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        Context context = inflater.getContext();

        int tenDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,10,context.getResources().getDisplayMetrics());
        int fourtyDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,40,context.getResources().getDisplayMetrics());
        int twentyDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,20,context.getResources().getDisplayMetrics());

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(lp);

        ProgressBar progressBar = new ProgressBar(context);
        LinearLayout.LayoutParams pLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        pLp.gravity = Gravity.CENTER_VERTICAL;
        pLp.topMargin = tenDp;
        pLp.bottomMargin = tenDp;
        pLp.leftMargin = twentyDp;
        progressBar.setLayoutParams(pLp);

        TextView textView = new TextView(context);
        String tipsOnLoading = "正在加载数据中...";
        textView.setText(tipsOnLoading);
        LinearLayout.LayoutParams tLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        tLp.gravity = Gravity.CENTER_VERTICAL;
        tLp.topMargin = tenDp;
        tLp.bottomMargin = tenDp;
        tLp.leftMargin = fourtyDp;
        tLp.rightMargin = twentyDp;
        textView.setLayoutParams(tLp);

        linearLayout.addView(progressBar);
        linearLayout.addView(textView);
        return linearLayout;
    }

}
