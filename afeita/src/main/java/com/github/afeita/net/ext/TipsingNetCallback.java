package com.github.afeita.net.ext;

import android.app.Activity;
import android.app.DialogFragment;

import com.github.afeita.tools.dialog.NetRequestTipsDialog;

import java.lang.ref.WeakReference;

/**
 *
 * <br /> author: chenshufei
 * <br /> date: 15/9/10
 * <br /> email: chenshufei2@sina.com
 */
public abstract class TipsingNetCallback<T> extends NetCallback<T> {

    // 使用弱引用，只是简单的在onStart与onFinish中使用下显示对话框，使用完后清除掉此实例对它的引用。
    // 防止请求过久时小概率适好activity被杀，activity无法销毁造成activity泄露。
    private WeakReference<Activity> mActivityRefercens;
    private WeakReference<DialogFragment> mDialogFragmentWeakReference;


    public TipsingNetCallback(Activity activity){
        mActivityRefercens = new WeakReference<Activity>(activity);
        mDialogFragmentWeakReference = new WeakReference<DialogFragment>(new NetRequestTipsDialog());
    }

    /**
     * 设置进来NetRequestTipsDialog子类的，进行自定义对话框样式 <br />
     * 用于请求网络时，开始弹出个自定义样式的looding对话框，请求结果关闭掉进度显示对话框
     * @param dialogFragment
     */
    public void setRequestTipsDialog(DialogFragment dialogFragment) {
        mDialogFragmentWeakReference.clear();
        mDialogFragmentWeakReference = null;
        mDialogFragmentWeakReference = new WeakReference<DialogFragment>(dialogFragment);
    }

    @Override
    public void onStart() {
        DialogFragment dialogFragment = mDialogFragmentWeakReference.get();
        Activity activity = mActivityRefercens.get();
        if (null != dialogFragment && !dialogFragment.isVisible()){
            if (null != activity && !activity.isFinishing()){
                dialogFragment.show(activity.getFragmentManager(),this.getClass().getSimpleName());
            }
        }
        onBegin();
    }

    /**
     * 与onStart()回调时间一对
     */
    public void onBegin() {
    }

    @Override
    public void onUpload(int fileNum,String currentUploadingFilename,long sumSize, long sumDonedSize, long sumSpendedTime) {

    }

    @Override
    public void onLoad(long loaded) {

    }

    @Override
    public void onCancle() {

    }


    @Override
    public void onError(Exception error) {

    }

    @Override
    public void onFinish(boolean isCancel) {
        DialogFragment dialogFragment = mDialogFragmentWeakReference.get();
        if (null != dialogFragment && dialogFragment.isVisible()){
            dialogFragment.dismiss();
            mActivityRefercens.clear();
            mDialogFragmentWeakReference.clear();
            mActivityRefercens = null;
            mDialogFragmentWeakReference = null;
        }
        onEnd(isCancel);
    }

    /**
     * 与onFinish(boolean isCancel)回调时间一对
     */
    public void onEnd(boolean isCancel) {

    }
}
