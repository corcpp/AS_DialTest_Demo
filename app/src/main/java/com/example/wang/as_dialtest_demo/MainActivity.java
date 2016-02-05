package com.example.wang.as_dialtest_demo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    public final static String TAG = MainActivity.class.getSimpleName();

    public final static String logFileName = "log.txt";

    private final String  email_1 = "871470258@qq.com";

    private final String  email_2 = "wangjiefenghy@chinamobile.com";


    private DBManager mDBManager;

    @Bind(R.id.start_btn)
    Button startBtn;

    @Bind(R.id.end_btn)
    Button endBtn;

    @Bind(R.id.delete_log_btn)
    Button checkLogBtn;

    @Bind(R.id.send_log_btn)
    Button sendLogBtn;

    @OnClick(R.id.start_btn)
    void startLogin() {
        Intent sIntent = new Intent(this, DialTestService.class);
        startService(sIntent);
        Toast.makeText(this, "开始发送请求", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.end_btn)
    void stopLogin() {
        Intent sIntent = new Intent(this, DialTestService.class);
        stopService(sIntent);
        Toast.makeText(this, "停止发送请求", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.delete_log_btn)
    void deleteLogFile() {
        Utils.deleteFile(this, logFileName);
    }

    @OnClick(R.id.send_log_btn)
    void sendLogByEmail() {

        String logContent = mDBManager.query().toString();
        Log.d(TAG, logContent);
        Utils.deleteFile(this, logFileName);
        Utils.saveToFile(this, logFileName, logContent);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Intent email = new Intent(android.content.Intent.ACTION_SEND);
                // 附件
                File file = new File(Environment.getExternalStorageDirectory() + File.separator + logFileName);

                Log.d(TAG, file.getPath().toString());
                //邮件发送类型：带附件的邮件
                email.setType("application/octet-stream");
                //邮件接收者（数组，可以是多位接收者
                String[] emailReciver = new String[]{ email_2};

                String  emailTitle = "拨测客户端日志";
                String emailContent = "附件呢？";
                //设置邮件地址
                email.putExtra(android.content.Intent.EXTRA_EMAIL, emailReciver);
                //设置邮件标题
                email.putExtra(android.content.Intent.EXTRA_SUBJECT, emailTitle);
                //设置发送的内容
                email.putExtra(android.content.Intent.EXTRA_TEXT, emailContent);
                //附件
                email.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                //调用系统的邮件系统
                startActivity(Intent.createChooser(email, "请选择邮件发送软件"));
            }
        }).start();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        DBManager.initialize(this);
        mDBManager = DBManager.getInstance();
    }

    @Override
    protected void onDestroy() {
        Toast.makeText(this, "停止发送", Toast.LENGTH_SHORT).show();
        Intent sIntent = new Intent(this, DialTestService.class);
        stopService(sIntent);

        super.onDestroy();
    }
}
