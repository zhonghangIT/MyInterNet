package com.uniquedu.myinternet;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.uniquedu.myinternet.imagecache.LruImageCache;
import com.uniquedu.myinternet.request.PostFileRequest;

import java.io.File;
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
    @InjectView(R.id.networkImageView)
    NetworkImageView networkImageView;
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
                postFile("http://192.168.149.2:8080/MyJsonFileTest/UploadFile", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/aa.png");
                break;
            case R.id.button_down:
                LruImageCache lruImageCache = LruImageCache.instance();

                ImageLoader imageLoader = new ImageLoader(queue,lruImageCache);

                networkImageView.setDefaultImageResId(R.mipmap.ic_launcher);
                networkImageView.setErrorImageResId(R.mipmap.ic_launcher);
                networkImageView.setImageUrl("http://img2.imgtn.bdimg.com/it/u=3565965899,4220043727&fm=206&gp=0.jpg",imageLoader);
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

    private void postFile(String url, String filePath) {
        //这里使用了自己定义的Request
        HashMap<String, File> params = new HashMap<>();
        File file = new File(filePath);
        params.put(file.getName(), file);
        PostFileRequest request = new PostFileRequest(Request.Method.POST, url, params, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "上传返回的信息" + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "连接网络错误" + error.networkResponse.statusCode);
            }
        });
        queue.add(request);
    }
}
