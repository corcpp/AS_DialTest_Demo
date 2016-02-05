package com.example.wang.as_dialtest_demo;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.WindowManager;

import com.cmcc.sso.sdk.auth.AuthnConstants;
import com.cmcc.sso.sdk.auth.AuthnHelper;
import com.cmcc.sso.sdk.auth.TokenListener;
import com.cmcc.sso.sdk.util.SsoSdkConstants;

import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;
/**
 * Created by wang on 2016/2/4.
 */
public class DialTestService extends Service{

    private static final String TAG = DialTestService.class.getSimpleName();
    private static final int SHOW_LOGIN_PROGRESS = 0x11;
    private static final int DISMISS_LOGIN_PROGRESS = 0x12;


    private static final int period = 30 * 1000;
    private Timer timer;
    private AuthnHelper mAuthnHelper;
    private TokenListener listener;
    private ProgressDialog mProgressDialog = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "onStartCommand ...");

        mAuthnHelper = new AuthnHelper(this);
        mAuthnHelper.setDefaultUI(false);

        listener = new TokenListener() {
            @Override
            public void onGetTokenComplete(JSONObject jsonObject) {

                mHandler.sendEmptyMessage(DISMISS_LOGIN_PROGRESS);
                int resultCode = jsonObject.optInt(SsoSdkConstants.VALUES_KEY_RESULT_CODE, -1);

                if(resultCode == AuthnConstants.CLIENT_CODE_SUCCESS) {
                    Log.d("result code: ", resultCode + "");

                    //协商成功后，清除ks
                    mAuthnHelper.cleanSSO(new TokenListener() {
                        @Override
                        public void onGetTokenComplete(JSONObject jsonObject) {
                        }
                    });
                }
            }
        };

        timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                mHandler.sendEmptyMessage(SHOW_LOGIN_PROGRESS);
                mAuthnHelper.getAccessToken(HostConfig.APP_ID, HostConfig.APP_KEY, null, SsoSdkConstants.LOGIN_TYPE_WAP + "," +SsoSdkConstants.LOGIN_TYPE_DATASMS, listener);
            }
        }, 0, period);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {

        Log.d(TAG, "onCreate ...");
        super.onCreate();
    }

    @Override
    public void onDestroy() {

        if(timer !=null) {
            timer.cancel();
        }
        super.onDestroy();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case SHOW_LOGIN_PROGRESS:
                    if(!Utils.isBackground(DialTestService.this)) {
                        showProgressDialog("别急！，Logining ....");
                    }
                    break;

                case DISMISS_LOGIN_PROGRESS:
                    dismisProgressDialog();
                    break;

                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };



    private void showProgressDialog(String message)
    {
        if(mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(DialTestService.this);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setMessage(message);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            mProgressDialog.show();
        }
    }

    private void dismisProgressDialog()
    {
        if ( mProgressDialog != null )
        {
            mProgressDialog.cancel();
            mProgressDialog = null;
        }

    }
}
