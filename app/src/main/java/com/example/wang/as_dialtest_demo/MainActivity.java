package com.example.wang.as_dialtest_demo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.start_btn)
    Button startBtn;

    @Bind(R.id.end_btn)
    Button endBtn;

    @OnClick(R.id.start_btn)
    void startLogin() {
        Intent sIntent = new Intent(this, DialTestService.class);
        startService(sIntent);
        Toast.makeText(this, "开始发送", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.end_btn)
    void stopLogin() {
        Intent sIntent = new Intent(this, DialTestService.class);
        stopService(sIntent);
        Toast.makeText(this, "停止发送", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
