package com.uniquedu.myinternet;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_INTERNET = 0X231;
    private static final int PERMISSION_READ = 0X232;
    private static final int PERMISSION_WRITE = 0X233;
    private static final int PERMISSION_MOUNT = 0X234;
    @InjectView(R.id.button_check)
    Button buttonCheck;
    @InjectView(R.id.button_url_connextion)
    Button buttonUrlConnextion;
    @InjectView(R.id.button_httpclient)
    Button buttonHttpclient;
    @InjectView(R.id.button_volley)
    Button buttonVolley;
    @InjectView(R.id.button_okhttp)
    Button buttonOkhttp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        //判断有无该权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            //申请网络连接权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET},
                    PERMISSION_INTERNET);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //申请网络连接权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_WRITE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //申请网络连接权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_READ);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS)
                != PackageManager.PERMISSION_GRANTED) {
            //申请网络连接权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS},
                    PERMISSION_MOUNT);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_INTERNET) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //权限申请成功
            } else {
                //权限没有通过
                Toast.makeText(MainActivity.this, "没有权限下一步无法进行", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void checkUrl() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null) {
            // 没有联网
            Toast.makeText(MainActivity.this, "没有连接网络", Toast.LENGTH_SHORT).show();
        } else {
            // 联网成功
            String name = info.getTypeName(); // 得到网络类型
            info.getState();
            Toast.makeText(MainActivity.this, "连接网络" + name, Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick({R.id.button_check, R.id.button_url_connextion, R.id.button_httpclient, R.id.button_volley, R.id.button_okhttp})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_check:
                checkUrl();
                break;
            case R.id.button_url_connextion:
                startActivity(new Intent(getApplicationContext(), UrlConnectActivity.class));
                break;
            case R.id.button_httpclient:
                startActivity(new Intent(getApplicationContext(), HttpClientActivity.class));
                break;
            case R.id.button_volley:
                startActivity(new Intent(getApplicationContext(), VolleyActivity.class));
                break;
            case R.id.button_okhttp:
                startActivity(new Intent(getApplicationContext(), OkhttpActivity.class));
                break;
        }
    }
}
