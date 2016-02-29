package com.example.wang.as_dialtest_demo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cmcc.sso.sdk.util.LogUtil;

import java.io.File;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    public final static String TAG = MainActivity.class.getSimpleName();

    public final static String logFileName = "log.txt";

    private final String  email_1 = "871470258@qq.com";

    private final String  email_2 = "wangjiefenghy@chinamobile.com";

    private boolean sendFlag = false;

    private AlertDialog dialog;

    EditText mTimesEt;

    private long clickTime = 0;

    private DBManager mDBManager;

    @Bind(R.id.start_dial_btn)
    Button startDialBtn;

    @Bind(R.id.analysis_log_btn)
    Button checkLogBtn;

    @Bind(R.id.send_log_btn)
    Button sendLogBtn;

    @Bind(R.id.analysis_result_tv)
    TextView anysisResultTv;

    @OnClick(R.id.start_dial_btn)
    void startLogin() {

        if( !sendFlag ) {
            sendFlag = true;
            startDialBtn.setText("停止拨测");
            Toast.makeText(this, "开始发送请求", Toast.LENGTH_SHORT).show();
            Intent sIntent = new Intent(this, DialTestService.class);
            startService(sIntent);
        } else {
            sendFlag = false;
            startDialBtn.setText("开始拨测");
            Toast.makeText(this, "停止发送请求", Toast.LENGTH_SHORT).show();
            Intent sIntent = new Intent(this, DialTestService.class);
            stopService(sIntent);
        }
    }


    @OnClick(R.id.analysis_log_btn)
    void analysisLog() {
//        Utils.deleteFile(this, logFileName);
        int sum = 0, failCount = 0;
        List<Record> records =  DBManager.getInstance().query();
        for(Record record : records) {
            ++sum;
            if(record.getResult() == 0) {
                ++failCount;
            }
        }

        double percent = (sum - failCount + 0.0) / sum * 100;
        anysisResultTv.setText("总计: " + sum + "  成功: " + (sum - failCount)
                + "  失败: " + failCount + "  百分比: " + percent + "%");


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
                String emailContent = "拨测结果仅供参考哦 O(∩_∩)O~~ ";
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

        //初始化数据库
        DBManager.initialize(this);
        mDBManager = DBManager.getInstance();

        mTimesEt = new EditText(this);
//        mTimesEt.setHint("请输入拨测频率（大于30的整数，n秒/次)");
        mTimesEt.setTextSize(15f);
        mTimesEt.setText("30");

        new AlertDialog.Builder(this)
                .setTitle("输入拨测频率（秒）：")
                .setCancelable(false)
                .setView(mTimesEt)
                .setPositiveButton("确定", mTimeCommitListener)
                .setNegativeButton("取消", mTimeCommitListener).show();

    }

    private DialogInterface.OnClickListener mTimeCommitListener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:

                    int period = 30;
                    try {
                        period  =  Integer.parseInt(mTimesEt.getText().toString().trim());
                    } catch (NumberFormatException ex) {
                        ex.printStackTrace();
                    }
                    LogUtil.debug(TAG, "拨测周期：" + period + "");

                    if(period < 30) {
                        dialog.dismiss();
                        Toast.makeText(MainActivity.this, "频率 30 秒", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Toast.makeText(MainActivity.this, "频率" + period + "秒", Toast.LENGTH_SHORT).show();
                    DialTestService.period = period * 1000;
                    dialog.dismiss();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    Toast.makeText(MainActivity.this, "频率 30 秒", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    break;
            }

        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exit() {
        if( System.currentTimeMillis() - clickTime > 2000) {
            Toast.makeText(getApplicationContext(), "再按一次后退键退出程序", Toast.LENGTH_SHORT).show();
            clickTime = System.currentTimeMillis();
        } else {
            Log.d(TAG, "Exit Applicetion");
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "退出程序，停止服务-->清除DB");

        //停止service
        Intent sIntent = new Intent(this, DialTestService.class);
        stopService(sIntent);

        //删除数据库，注意删除后下次重新启动必须正确初始化，DBManager和MyDataBaseHelper，所以要退出进程才行
        this.deleteDatabase(MyDataBaseHelper.DATABASE_NAME);
        super.onDestroy();
        //退出进程
        System.exit(0);

    }
}
