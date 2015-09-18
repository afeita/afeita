package com.github.afeita.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.github.afeita.sample.netsample.NetSampleActivity;
import com.github.afeita.sample.utilsample.UtilMainActivity;

public class MainActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

    }

    private void initView() {
        findViewById(R.id.btn_afeitanet).setOnClickListener(MainActivity.this);
        findViewById(R.id.btn_utilsample).setOnClickListener(MainActivity.this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_afeitanet:
                startActivity(new Intent(this, NetSampleActivity.class));
                break;
            case R.id.btn_utilsample:
                startActivity(new Intent(this, UtilMainActivity.class));
                break;

        }
    }
}
