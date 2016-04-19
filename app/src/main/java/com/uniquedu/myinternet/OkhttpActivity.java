package com.uniquedu.myinternet;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cz.msebera.android.httpclient.entity.mime.FormBodyPartBuilder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkhttpActivity extends AppCompatActivity {

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
    @InjectView(R.id.button_gzip)
    Button buttonGzip;
    private OkHttpClient mOkHttpClient;
    private static final String TAG = "OKHTTP";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_okhttp);
        ButterKnife.inject(this);
        mOkHttpClient = new OkHttpClient();
    }

    @OnClick({R.id.button_get, R.id.button_post, R.id.button_gzip, R.id.button_file, R.id.button_down, R.id.button_ssl})
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
                download("http://h.hiphotos.baidu.com/image/h%3D200/sign=b1ca5dfd0b33874483c5287c610ed937/37d12f2eb9389b50082ce3ff8235e5dde6116e4f.jpg", Environment.getExternalStorageDirectory() + "/bb.jpg");
                break;
            case R.id.button_gzip:
                doGzip("http://192.168.149.2:8080/MyJsonFileTest/StudentServlet?action=gzip");
                break;
            case R.id.button_ssl:

                break;
        }
    }

    private void doGzip(String url) {
        Request request = new Request.Builder().get().url(url).build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "网络连接返回数据错误");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = response.body().byteStream();
                GZIPInputStream gzip = new GZIPInputStream(is);
                BufferedReader br = new BufferedReader(new InputStreamReader(gzip));
                String line = br.readLine();
                String result = "";
                while (line != null) {
                    result += line;
                    line = br.readLine();
                }
                Log.d(TAG, "网络连接返回数据" + result);
            }
        });
    }

    private void doGet(String url) {
        Request request = new Request.Builder().get().url(url).build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "网络连接返回数据错误");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                Log.d(TAG, "网络连接返回数据" + response.body().string());
            }
        });
    }

    private void download(String url, final String filePath) {
//        RequestBody body=
        Request request = new Request.Builder().url(url).build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = response.body().byteStream();
                File file = new File(filePath);
                if (!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                FileOutputStream fos = new FileOutputStream(file);
                DataInputStream in = new DataInputStream(is);
                DataOutputStream out = new DataOutputStream(fos);
                byte[] array = new byte[1024];
                int size = in.read(array);
                while (size != -1) {
                    out.write(array, 0, size);
                    size = in.read(array);
                }
                Log.d(TAG, "下载完成");
            }
        });
    }

    private void postFile(String url, String filePath) {
        File file = new File(filePath);
        RequestBody body = new MultipartBody.Builder().addFormDataPart("app", "app.png", MultipartBody.create(MultipartBody.FORM, file)).build();
        Request request = new Request.Builder().post(body).url(url).build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "网络连接返回数据错误");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "网络连接返回数据" + response.body().string());
            }
        });
    }

    private void doPost(String url, HashMap<String, String> params) {
        Iterator<String> iterator = params.keySet().iterator();
        FormBody.Builder builder = new FormBody.Builder();
        while (iterator.hasNext()) {
            String key = iterator.next();
            String value = params.get(key);
            builder.add(key, value);
        }
        RequestBody body = builder.build();
        Request request = new Request.Builder().post(body).url(url).build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "网络连接返回数据错误");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "网络连接返回数据" + response.body().string());
            }
        });
    }
}
