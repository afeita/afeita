
package com.github.afeita.tools.listadapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.Arrays;
import java.util.List;

/**
 * 通用Adapter类，使用示例如下：
 * new Helper(xxxxActivity.this,xxxxLayoutId,xxxxDatas){
 *     public void inflateViewData(ViewHolder viewHolder, int position){
 *         viewHolder.setTextView(xxxTextViewId,"xxx文本值");
 *         viewHolder.setButton(xxxButtonId,"xxxx按钮显示文字")
 *     }
 * };
 * <br /> author: chenshufei
 * <br /> date: 15/8/17
 * <br /> email: chenshufei2@sina.com
 */
public abstract class HelperAdapter<T> extends BaseAdapter {

    private List<T> datas;
    private Context context;
    private int layoutId;

    public HelperAdapter(Context context, int layoutId, List<T> datas) {
        this.context = context;
        this.datas = datas;
        this.layoutId = layoutId;
    }

    public HelperAdapter(Context context,int layoutId,T[] datas){
        this(context,layoutId,Arrays.asList(datas));
    }

    public void setDatas(List<T> datas){
        this.datas = datas;
        notifyDataSetChanged();
    }

    public void setDatas(T[] datas){
        setDatas(Arrays.asList(datas));
    }

    @Override
    public int getCount() {
        return datas == null? 0 : datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = ViewHolder.getViewHolder(context, layoutId, position, convertView, parent);
        inflateViewData(viewHolder, position);
        return viewHolder.getContentView();
    }

    /**
     * 从ViewHolder中获取控件，并斌相应的值
     * @param viewHolder
     * @param position
     */
    protected abstract void inflateViewData(ViewHolder viewHolder, int position);

}