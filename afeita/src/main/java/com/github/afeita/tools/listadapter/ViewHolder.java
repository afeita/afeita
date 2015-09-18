
package com.github.afeita.tools.listadapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * ViewHolder类，
 * 1、创建或复用ContentView。
 * 2、快速给ContentView中的控件赋值。
 * <br /> author: chenshufei
 * <br /> date: 15/8/17
 * <br /> email: chenshufei2@sina.com
 */
public class ViewHolder {
    private int position;
    private View contentView;
    private Context context;
    private SparseArray<View> views;

    private ViewHolder(Context context, int resourceId, int position, View convertView, ViewGroup parent) {
        contentView = LayoutInflater.from(context).inflate(resourceId, parent, false);
        contentView.setTag(this);
        this.position = position;
        this.context = context;
        views = new SparseArray<View>();
    }

    /**
     * 获取ViewHolder类实例，使用ViewHolder类实例来复用contentview
     * @param context
     * @param resourceId
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    public static ViewHolder getViewHolder(Context context, int resourceId, int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            return new ViewHolder(context, resourceId, position, convertView, parent);
        } else {
            return (ViewHolder) convertView.getTag();
        }
    }

    /**
     * 获取 布局ContentView中的控件
     * @param rId 控件的Id值
     * @param <T>
     * @return
     */
    public <T extends View> T getView(int rId){
        View view = views.get(rId);
        if (null == view){
            view = contentView.findViewById(rId);
            views.put(rId,view);
        }
        return (T) view;
    }

    /**
     * 获取Item布局ContentView
     * @return
     */
    public View getContentView(){
        return  contentView;
    }

    /**
     * 给布局ContentView中的Textview 设置值
     * @param textViewId 布局中Textview的Id值。
     * @param value 要设置的值。
     */
    public void setTextView(int textViewId,String value){
        TextView textView = getView(textViewId);
        textView.setText(value);
    }

    /**
     * 给布局ContentView中的Textview 设置值
     * @param textViewId 布局中Textview的Id值。
     * @param valueId 要设置的值Id。
     */
    public void setTextView(int textViewId,int valueId){
        String value = context.getResources().getString(valueId);
        setTextView(textViewId, value);
    }

    /**
     * 给布局ContentView中的Button 设置值
     * @param buttonId 布局中Button的Id值。
     * @param value 要设置的值。
     */
    public void setButton(int buttonId,String value){
        setButtonAndOnClickListener(buttonId, value, null);
    }

    /**
     * 给布局ContentView中的Button 设置值 并 设置onClickListener
     * @param buttonId
     * @param value
     * @param listener
     */
    public void setButtonAndOnClickListener(int buttonId,String value,View.OnClickListener listener){
        Button button = getView(buttonId);
        button.setText(value);
        if (null != listener){
            button.setOnClickListener(listener);
        }
    }

    /**
     * 给布局ContentView中的Button 设置值 并 设置onClickListener
     * @param buttonId
     * @param valueId
     * @param listener
     */
    public void setButtonAndOnClickListener(int buttonId,int valueId,View.OnClickListener listener){
        String value = context.getResources().getString(valueId);
        setButtonAndOnClickListener(buttonId, value, listener);
    }
    /**
     * 给布局ContentView中的Button 设置值
     * @param buttonId 布局中Button的Id值。
     * @param valueId 要设置的值id。
     */
    public void setButton(int buttonId,int valueId){
        String value = context.getResources().getString(valueId);
        setButton(buttonId, value);
    }


    /**
     * 设置ImageView的src resource 图片
     * @param imageViewId
     * @param rDId
     */
    public void setImageViewResource(int imageViewId,int rDId){
        ImageView imageView = getView(imageViewId);
        imageView.setImageResource(rDId);
    }

    /**
     * 设置ImageView的背景background resource 图片
     * @param imageViewId
     * @param rDId
     */
    public void setImageViewBackground(int imageViewId,int rDId){
        ImageView imageView = getView(imageViewId);
        imageView.setBackgroundResource(rDId);
    }



}