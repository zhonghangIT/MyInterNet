package com.uniquedu.myinternet;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.uniquedu.myinternet.thread.HttpsHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.net.ssl.SSLContext;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpException;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.HttpStatus;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.ParseException;
import cz.msebera.android.httpclient.StatusLine;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.config.RequestConfig;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.CloseableHttpResponse;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.conn.ssl.SSLConnectionSocketFactory;
import cz.msebera.android.httpclient.conn.ssl.TrustSelfSignedStrategy;
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;
import cz.msebera.android.httpclient.impl.client.CloseableHttpClient;
import cz.msebera.android.httpclient.impl.client.HttpClients;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.ssl.SSLContexts;
import cz.msebera.android.httpclient.util.EntityUtils;

public class HttpClientActivity extends AppCompatActivity {

    @InjectView(R.id.button_get)
    Button buttonGet;
    @InjectView(R.id.button_post)
    Button buttonPost;
    @InjectView(R.id.button_file)
    Button buttonFile;
    @InjectView(R.id.button_down)
    Button buttonDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_http_client);
        ButterKnife.inject(this);
    }

    @OnClick({R.id.button_get, R.id.button_post, R.id.button_file, R.id.button_down})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_get:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        doHttpClientGet("http://192.168.149.2:8080/MyJsonFileTest/StudentServlet?action=findlist");
                    }
                }).start();
                break;
            case R.id.button_post:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("action", "findlist");
                        doHttpClientPost("http://192.168.149.2:8080/MyJsonFileTest/StudentServlet", params);
                    }
                }).start();

                break;
            case R.id.button_file:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        uploadFile("http://192.168.149.2:8080/MyJsonFileTest/UploadFile", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/aa.png");
                    }
                }).start();
                break;
            case R.id.button_down:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        download("http://192.168.149.2:8080/MyJsonFileTest/android/aa.png", Environment.getExternalStorageDirectory() + "/test.png");
                    }
                }).start();
                break;
        }
    }


    private void download(String url, String path) {
        HttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        try {
            HttpResponse response = client.execute(httpGet);
            InputStream in = response.getEntity().getContent();
            FileOutputStream out = new FileOutputStream(new File(path));
            byte[] b = new byte[1024];
            int len = 0;
            while ((len = in.read(b)) != -1) {
                out.write(b, 0, len);
            }
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpGet.releaseConnection();
        }
        System.out.println("download, success!!");
    }


    /**
     * @param url      上传文件网址
     * @param filePath 文件的路径
     */
    private void uploadFile(String url, String filePath) {
        //创建HttpClient客户端
        HttpClient httpclient = HttpClients.createDefault();
        try {
            HttpPost httppost = new HttpPost(url);//创建提交的方法为post
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addBinaryBody("file1", new File(filePath));
            builder.addTextBody("normal", "normalfiled");
            httppost.setEntity(builder.build());
            HttpResponse response = httpclient.execute(httppost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                System.out.println("服务器正常响应.....");
                HttpEntity resEntity = response.getEntity();
                System.out.println(EntityUtils.toString(resEntity));//httpclient自带的工具类读取返回数据
                System.out.println(resEntity.getContent());
                EntityUtils.consume(resEntity);
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                httpclient.getConnectionManager().shutdown();
            } catch (Exception ignore) {

            }
        }
    }

    private void doHttpClientGet(String url) {
        StringBuffer buffer = null;
        HttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet(url);
        try {
            get.setConfig(RequestConfig.DEFAULT);
//            get.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            //执行get方法获得服务器返回的所有数据
            HttpResponse response = client.execute(get);
            //HttpClient获得服务器返回的表头。
            StatusLine statusLine = response.getStatusLine();
            //获得状态码
            int code = statusLine.getStatusCode();
            if (code == HttpURLConnection.HTTP_OK) {
                //得到数据的实体。
                HttpEntity entity = response.getEntity();
                //得到数据的输入流。
                InputStream is = entity.getContent();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String line = br.readLine();
                buffer = new StringBuffer();
                while (line != null) {
                    System.out.println(line);
                    buffer.append(line);
                    line = br.readLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void doHttpClientPost(String url, HashMap<String, String> params) {
        StringBuffer buffer = null;
        HttpClient client = HttpClients.createDefault();
        ;
        HttpPost post = new HttpPost(url);
        //设置传入的数据
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<>();
        Iterator<String> iterator = params.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            nameValuePairs.add(new BasicNameValuePair(key, params.get(key)));
        }
        try {
            //设置传递的参数格式
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
            post.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            //执行get方法获得服务器返回的所有数据
            HttpResponse response = client.execute(post);
            //HttpClient获得服务器返回的表头。
            StatusLine statusLine = response.getStatusLine();
            //获得状态码
            int code = statusLine.getStatusCode();
            if (code == HttpURLConnection.HTTP_OK) {
                //得到数据的实体。
                HttpEntity entity = response.getEntity();
                //得到数据的输入流。
                InputStream is = entity.getContent();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String line = br.readLine();
                buffer = new StringBuffer();
                while (line != null) {
                    System.out.println(line);
                    buffer.append(line);
                    line = br.readLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
