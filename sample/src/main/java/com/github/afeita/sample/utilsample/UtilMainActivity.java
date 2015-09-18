
package com.github.afeita.sample.utilsample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.github.afeita.sample.R;

/**
 * <br /> author: chenshufei
 * <br /> date: 15/9/18
 * <br /> email: chenshufei2@sina.com
 */
public class UtilMainActivity extends Activity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_utilmain);
        initView();
    }

    private void initView() {
        findViewById(R.id.btn_helpadapter).setOnClickListener(UtilMainActivity.this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_helpadapter:
                startActivity(new Intent(UtilMainActivity.this,HelpAdapterActivity.class));
                break;
        }
    }
}
