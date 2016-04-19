package com.uniquedu.myinternet.request;

import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

/**
 * Created by ZhongHang on 2016/4/19.
 */
public class PostFileRequest extends Request<String> {
    private final Response.Listener<String> mListener;
    private String BOUNDARY;
    private HashMap<String, File> mFiles;
    private final String LINE_END = "\r\n";

    public PostFileRequest(int method, String url, HashMap<String, File> mFiles, Response.Listener<String> listener, Response.ErrorListener errolistener) {
        super(method, url, errolistener);
        setShouldCache(false);//不使用缓存
        //加长响应的时间
        setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        this.mListener = listener;
        this.mFiles = mFiles;
        //  生成一个随机的边界标示
        Random random = new Random();
        byte[] randomBytes = new byte[16];
        random.nextBytes(randomBytes);
        BOUNDARY = Base64.encodeToString(randomBytes, Base64.NO_WRAP);//该boundary为边间标示
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        //得到文件的byte数组
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        StringBuffer sb = new StringBuffer();
        Iterator<String> iterator = mFiles.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            File file = mFiles.get(key);
            /*第一行界标*/
            //`"--" + BOUNDARY + "\r\n"`
            sb.append("--" + BOUNDARY);
            sb.append(LINE_END);
            /*第二行 上传文件的标头*/
            //Content-Disposition: form-data; name="参数的名称"; filename="上传的文件名" + "\r\n"
            sb.append("Content-Disposition: form-data;");
            sb.append(" name=\"");
            sb.append(key);
            sb.append("\"");
            sb.append("; filename=\"");
            sb.append(key);
            sb.append("\"");
            sb.append(LINE_END);
            /*第三行  文件的类型*/
            //Content-Type: 文件的 mime 类型 + "\r\n"
            sb.append("Content-Type: application/octet-stream; charset=UTF-8");
            sb.append(LINE_END);
            /*第四行  上传文件的头文件的结尾*/
            //"\r\n"
            sb.append(LINE_END);
            try {
                os.write(sb.toString().getBytes("utf-8"));
                /*第五行*/
                //文件的二进制数据 + "\r\n"
                int bytesRead;
                byte[] buffer = new byte[1024];
                FileInputStream fileInput = new FileInputStream(file);
                while ((bytesRead = fileInput.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
                os.write(LINE_END.getBytes());
                String line = "--" + BOUNDARY + "--" + LINE_END;
                os.write(line.getBytes());
                os.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //最后上传文件的表单的结尾
        String endLine = "--" + BOUNDARY + "--" + "\r\n";
        try {
            os.write(endLine.toString().getBytes("utf-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return os.toByteArray();
    }

    @Override
    public String getBodyContentType() {
        //得到文件的类型
        return "multipart/form-data;boundary=" + BOUNDARY;
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        //解析网络的结果
        String parsed;
        try {
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
        }
        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(String response) {
        //交付返回的结果
        mListener.onResponse(response);
    }
}
