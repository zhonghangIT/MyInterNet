package com.uniquedu.myinternet;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class VolleyActivity extends AppCompatActivity {

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
    private RequestQueue queue;
    private static final String TAG = "volleyActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volley);
        ButterKnife.inject(this);
        queue = Volley.newRequestQueue(this);
    }

    @OnClick({R.id.button_get, R.id.button_post, R.id.button_file, R.id.button_down, R.id.button_ssl})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_get:
                doGet("http://192.168.149.2:8080/MyJsonFileTest/StudentServlet?action=findlist");
                break;
            case R.id.button_post:
                HashMap<String, String> params = new HashMap<>();
                params.put("action", "findlist");
                doPost("http://192.168.149.2:8080/MyJsonFileTest/StudentServlet", params);
                break;
            case R.id.button_file:
                break;
            case R.id.button_down:
                break;
            case R.id.button_ssl:
                break;
        }
    }

    private void doGet(String url) {
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "连接网络错误" + error.networkResponse.statusCode);
            }
        });
        queue.add(request);
    }

    private void doPost(String url, final HashMap<String, String> params) {
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "连接网络错误" + error.networkResponse.statusCode);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };
        queue.add(request);
    }
}
