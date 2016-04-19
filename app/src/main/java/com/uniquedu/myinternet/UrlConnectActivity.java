package com.uniquedu.myinternet;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.uniquedu.myinternet.thread.MyDownloadThread;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class UrlConnectActivity extends AppCompatActivity {

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
    @InjectView(R.id.progressbar)
    ProgressBar progressbar;
    private final String LINE_END = "\r\n";
    //用于判断发送的下载进度的信息
    private static final int DOWNLOAD_PERCENT = 0X2345;
    private static final String TAG = "URLCONNECTION";
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWNLOAD_PERCENT:
                    progressbar.setProgress(msg.arg1);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_url_connect);
        ButterKnife.inject(this);
    }

    @OnClick({R.id.button_get, R.id.button_post, R.id.button_file, R.id.button_down, R.id.button_ssl})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_get:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //该URL替换成自己的
                        doGet("http://192.168.149.2:8080/MyJsonFileTest/StudentServlet?action=findlist");
                    }
                }).start();

                break;
            case R.id.button_post:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //该URL替换成自己的
                        doPost("http://192.168.149.2:8080/MyJsonFileTest/StudentServlet", "action=findlist");
                    }
                }).start();

                break;
            case R.id.button_file:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //这里上传的是Donwload文件夹下的aa.png文件
                        postFile("http://192.168.149.2:8080/MyJsonFileTest/UploadFile", new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/aa.png"));
        }
    }).start();
                break;
            case R.id.button_down:
                //下载文件
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        download("http://192.168.149.2:8080/MyJsonFileTest/android/app-release.apk", Environment.getExternalStorageDirectory() + "/cc.png", 3);
                    }
                }).start();
                break;
            case R.id.button_ssl:
                break;
        }
    }

    class PercentThread extends Thread {
        List<MyDownloadThread> threads;
        int length;

        public PercentThread(int length, List<MyDownloadThread> threads) {
            this.threads = threads;
            this.length = length;
        }

        @Override
        public void run() {
            float sum = 0;
            while (sum < length) {
                sum = 0;
                for (MyDownloadThread thread : threads) {
                    sum += thread.getDownLength();
                }
                Message msg = handler.obtainMessage();
                msg.what = DOWNLOAD_PERCENT;
                msg.arg1 = (int) (sum / length * 100);
                Log.d(TAG, "下载的进度是" + sum / length);
                handler.sendMessage(msg);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Log.d(TAG, "下载百分比的进程结束");
        }
    }

    /**
     * 启动下载文件
     *
     * @param urls     文件的url
     * @param filePath 要保存的位置
     * @param num      多线程下载，线程数目，这里最大限制为5，最小限制为0
     */
    private void download(String urls, String filePath, int num) {
        Log.d(TAG, "文件地址--------" + filePath);
        //这里是限制下载的多线程的数量
        if (num < 0) num = 0;
        if (num > 5) num = 5;
        try {
            URL url = new URL(urls);
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            }
//			1通过在 URL 上调用 openConnection 方法创建连接对象。
            URLConnection connection = url.openConnection();
            //2处理设置参数和一般请求属性。对网络资源的读取提交
            connection.setDoInput(true);//默认值是true
            connection.setDoOutput(true);//默认值为false
            int length = connection.getContentLength();//得到文件的长度
            Log.d(TAG, "该文件的总长度为" + length);
            List<MyDownloadThread> threads = new ArrayList<>();
            for (int i = 0; i < num; i++) {
                // 文件长度和线程数可能不整除，最后一个线程的下载长度应当加上未整出的部分
                // 开始位置 结束位置
                if (i < num - 1) {
                    MyDownloadThread thread = new MyDownloadThread(url, file, length / num * i,
                            length / num * (i + 1));
                    threads.add(thread);
                    thread.start();
                } else {
                    MyDownloadThread thread = new MyDownloadThread(url, file, length / num * i, length);
                    threads.add(thread);
                    thread.start();
                }
            }
            PercentThread percentThread = new PercentThread(length, threads);
            percentThread.start();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void postFile(String urls, File file) {
        Log.d(TAG, file.getAbsolutePath());
        InputStream inputStream = null;
        HttpURLConnection urlConnection = null;
        FileInputStream fileInput = null;
        DataOutputStream requestStream = null;
        BufferedReader br = null;
        try {
            // 创建连接
            URL url = new URL(urls);
            urlConnection = (HttpURLConnection) url.openConnection();
            //  生成一个随机的边界标示
            Random random = new Random();
            byte[] randomBytes = new byte[16];
            random.nextBytes(randomBytes);
            String boundary = Base64.encodeToString(randomBytes, Base64.NO_WRAP);//该boundary为边间标示

            /* for POST request */
            urlConnection.setDoOutput(true);//允许使用输出流
            urlConnection.setDoInput(true);//允许使用输入流
            urlConnection.setUseCaches(false);//不允许使用缓存
            urlConnection.setRequestMethod("POST");//提交的方法为post
            // 构建Entity form,创建上传文件的表单
            urlConnection.setRequestProperty("Connection", "Keep-Alive");//上传文件时保持连接
            urlConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);//创建上传的类型为文件
            urlConnection.setRequestProperty("Cache-Control", "no-cache");//不使用缓存


            /* upload file stream */
            fileInput = new FileInputStream(file);
            requestStream = new DataOutputStream(urlConnection.getOutputStream());
            String nikeName = "myfile";
            requestStream = new DataOutputStream(urlConnection.getOutputStream());
            //写入文件的头文件
            requestStream.writeBytes("--" + boundary + LINE_END);
            requestStream.writeBytes("Content-Disposition: form-data; name=\"" + nikeName + "\"; filename=\"" + file.getName() + "\"" + LINE_END);
            requestStream.writeBytes("Content-Type:application/octet-stream; charset=UTF-8" + LINE_END);
            requestStream.writeBytes(LINE_END);
            // 写图像字节内容
            int bytesRead;
            byte[] buffer = new byte[1024];
            while ((bytesRead = fileInput.read(buffer)) != -1) {
                requestStream.write(buffer, 0, bytesRead);
            }
            requestStream.writeBytes(LINE_END);
            requestStream.writeBytes("--" + boundary + "--" + LINE_END);
            requestStream.flush();
            fileInput.close();

            // try to get response
            int statusCode = urlConnection.getResponseCode();
            if (statusCode == 200) {
                br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String result = "";
                String line = null;
                while ((line = br.readLine()) != null) {
                    result += line;
                }
                Log.d(TAG, result);
            }
        } catch (Exception e) {
            Log.d(TAG, "上传文件错误");
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (requestStream != null) {
                try {
                    requestStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileInput != null) {
                try {
                    fileInput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }


    private void doPost(String urls, String params) {
        StringBuffer buffer = null;
        try {
            URL url = new URL(urls);
            URLConnection connection = url.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            httpConnection.setRequestMethod("POST");//设置连接网络的方法
            //设置编码格式
            //设置接受的数据类型
            httpConnection.setRequestProperty("Accept-Charset", "utf-8");
            //设置可以序列化的java对象
            httpConnection.setRequestProperty("Context-Type", "application/x-www-form-urlencoded");
            // 设置可以读取服务器返回的内容默认为true
            //httpConnection.setDoInput(true);
            //设置服务器接收客户端串入的内容。默认为false
            httpConnection.setDoOutput(true);
            //设置不可已接受缓存内容。
            httpConnection.setUseCaches(false);
            //提交数据
            httpConnection.getOutputStream().write(params.getBytes());
            int code = httpConnection.getResponseCode();
            if (code == HttpURLConnection.HTTP_OK) {
                System.out.println("获得的连接状态码是：" + code);

                //从服务器读数据
                InputStream is = httpConnection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String line = br.readLine();
                buffer = new StringBuffer();
                while (line != null) {
                    buffer.append(line);
                    line = br.readLine();
                }
                System.out.println(buffer.toString());
            } else {
                Log.d(TAG, "网络连接错误");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void doGet(String urls) {
        StringBuffer buffer = null;
        try {
            URL url = new URL(urls);
            URLConnection urlConnection = url.openConnection();
            HttpURLConnection httpurlconn = (HttpURLConnection) urlConnection;
            httpurlconn.setRequestMethod("GET");
            //设置编码格式
            //设置接受的数据类型
            httpurlconn.setRequestProperty("Accept-Charset", "utf-8");
            //设置可以序列化的java对象
            httpurlconn.setRequestProperty("Context-Type", "application/x-www-form-urlencoded");

            int code = httpurlconn.getResponseCode();
            Log.d(TAG, "获得的状态码是：" + code);
            if (code == HttpsURLConnection.HTTP_OK) {
                InputStream is = httpurlconn.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String line = br.readLine();
                buffer = new StringBuffer();
                while (line != null) {
                    buffer.append(line);
                    line = br.readLine();
                }
                System.out.println(buffer.toString());
            } else {
                Log.d(TAG, "网络连接错误");
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
