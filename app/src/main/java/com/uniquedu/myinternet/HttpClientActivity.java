package com.uniquedu.myinternet;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class HttpClientActivity extends AppCompatActivity {

    @InjectView(R.id.button_get)
    Button buttonGet;
    @InjectView(R.id.button_post)
    Button buttonPost;
    @InjectView(R.id.button_file)
    Button buttonFile;
    @InjectView(R.id.button_down)
    Button buttonDown;
    @InjectView(R.id.button_ssl)
    Button buttonSsl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_http_client);
        ButterKnife.inject(this);
    }

    @OnClick({R.id.button_get, R.id.button_post, R.id.button_file, R.id.button_down, R.id.button_ssl})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_get:
                break;
            case R.id.button_post:
                break;
            case R.id.button_file:
                break;
            case R.id.button_down:
                break;
            case R.id.button_ssl:
                break;
        }
    }
}
