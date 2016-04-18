package com.uniquedu.myinternet.thread;

import android.util.Log;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class MyDownloadThread extends Thread {
    private int start;// 下载的开始位置
    private int end;// 下载的结束位置
    private File file;// 要保存到的文件
    private URL url;
    private int downLength = 0;// 已下载
    private static final String TAG="MyDownloadThread";
    /**
     * @param url   要下载的文件的地址
     * @param file  下载后的文件保存位置
     * @param start 要下载的开始位置
     * @param end   结束位置
     */
    public MyDownloadThread(URL url, File file, int start, int end) {
        this.file = file;
        this.url = url;
        this.start = start;
        this.end = end;
        Log.d(TAG,"下载线程启动");
    }

    public int getDownLength() {
        return downLength;
    }

    @Override
    public void run() {
        DataInputStream is = null;// 读取的网上的文件的流
        HttpURLConnection connection = null;
        try {
            //文件的分段写入读取，在这个地方此对象用于文件的写入
            RandomAccessFile access = new RandomAccessFile(file, "rw");
            int needLength = end - start;//要下载的这一段内容的长度
            System.out.println(
                    Thread.currentThread().getName() + "下载文件的开始：" + start + "  结束：" + end + " 要下载的长度" + needLength);
            // 因为要从文件的中间部分开始写，此处使用了文件随机访问流
            connection = (HttpURLConnection) url.openConnection();//打开网络连接
            Log.d(TAG,"打开网络连接");
            //设置超时的时间，5000毫秒
            connection.setConnectTimeout(5000);
            Log.d(TAG,"设置网络超时");
            connection.setRequestMethod("GET");
            Log.d(TAG,"设置连接方法");
            // 截取流的位置
            connection.setRequestProperty("Range", "bytes=" + start + "-" + end);
            Log.d(TAG,"开始截取连接");
            int code = connection.getResponseCode();
            Log.d(TAG,"得到连接值" + code);
            if (code == 206) {//因为加载了Range信息，所以这里的返回成功的值应该为206
                is = new DataInputStream(connection.getInputStream());//将输入流封装成字节流
                Log.d(TAG,"将流进行封装");
                access.seek(start);
                Log.d(TAG,"移动文件位置");
                // 移动写的开始位置
                // 创建字节缓冲数组
                byte[] buffer = new byte[1024];
                //从is中读取了num个字节，将num个字节放入到buffer中
                int num = is.read(buffer);
                while (num != -1) {
                    downLength += num;
                    access.write(buffer, 0, num);
                    //从buffer中取0~num的字节将这些字节写入到文件
                    num = is.read(buffer);
                }
                Log.d(TAG,"该线程的下载完毕");
                access.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
