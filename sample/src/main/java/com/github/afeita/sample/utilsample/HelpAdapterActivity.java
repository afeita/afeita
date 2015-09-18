
package com.github.afeita.sample.utilsample;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.github.afeita.sample.R;
import com.github.afeita.tools.listadapter.HelperAdapter;
import com.github.afeita.tools.listadapter.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * <br /> author: chenshufei
 * <br /> date: 15/9/18
 * <br /> email: chenshufei2@sina.com
 */
public class HelpAdapterActivity extends Activity{

    private ListView lv_helpadpater_list;
    private HelperAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_helpadapter_list);
        initView();
        doLogic();
    }

    private void doLogic() {
        final List<Bean> beans = new ArrayList<Bean>();
        for (int i=0;i<20;i++){
            beans.add(new Bean("标题"+i,"内容描述..."+i));
        }

        adapter = new HelperAdapter(HelpAdapterActivity.this, R.layout.item_helpadapter_list, beans) {
            @Override
            protected void inflateViewData(ViewHolder viewHolder, int position) {
                Bean bean = beans.get(position);
                viewHolder.setTextView(R.id.tv_title, bean.getTitile());
                viewHolder.setButton(R.id.btn_des, bean.getDes());
            }
        };

        lv_helpadpater_list.setAdapter(adapter);
    }

    private void initView() {
        lv_helpadpater_list = (ListView) findViewById(R.id.lv_helpadpater_list);
    }

    private class Bean{
        private String titile;
        private String des;

        public Bean(String titile,String des){
            this.titile = titile;
            this.des = des;
        }

        public String getTitile() {
            return titile;
        }

        public void setTitile(String titile) {
            this.titile = titile;
        }

        public String getDes() {
            return des;
        }

        public void setDes(String des) {
            this.des = des;
        }
    }
}
