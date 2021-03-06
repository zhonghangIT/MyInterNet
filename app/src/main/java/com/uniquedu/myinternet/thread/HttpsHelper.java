package com.uniquedu.myinternet.thread;

import android.content.Context;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.conn.scheme.Scheme;
import cz.msebera.android.httpclient.conn.ssl.SSLConnectionSocketFactory;
import cz.msebera.android.httpclient.conn.ssl.SSLSocketFactory;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

/**
 * Created by ZhongHang on 2016/4/21.
 */
public class HttpsHelper {


    private static final String KEY_STORE_TYPE_BKS = "bks";
    private static final String KEY_STORE_TYPE_P12 = "PKCS12";
    private static final String SCHEME_HTTPS = "https";
    private static final int HTTPS_PORT = 8443;
    private static final String KEY_STORE_CLIENT_PATH = "client.jks";
    private static final String KEY_STORE_TRUST_PATH = "server.bks";
    private static final String KEY_STORE_PASSWORD = "123456";
    private static final String KEY_STORE_TRUST_PASSWORD = "123456";
    private static KeyStore keyStore;
    private static KeyStore trustStore;

    public static HttpClient getSslHttpClient(Context pContext) {
        HttpClient httpsClient = new DefaultHttpClient();
        try {
            SSLSocketFactory socketFactory = getSocketFactory(pContext);

            Scheme sch = new Scheme(SCHEME_HTTPS, socketFactory, HTTPS_PORT);
            httpsClient.getConnectionManager().getSchemeRegistry().register(sch);

        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return httpsClient;
    }

    public static SSLSocketFactory getSocketFactory(Context pContext)
            throws KeyStoreException, IOException, NoSuchAlgorithmException,
            KeyManagementException, UnrecoverableKeyException {
        // Client's certificate. Server will verify it.
//        keyStore = KeyStore.getInstance(KEY_STORE_TYPE_P12);

        // Server's certificate. Client will verify it.
        trustStore = KeyStore.getInstance(KEY_STORE_TYPE_BKS);

        InputStream ksIn = pContext.getResources().getAssets().open(KEY_STORE_CLIENT_PATH);
        InputStream tsIn = pContext.getResources().getAssets().open(KEY_STORE_TRUST_PATH);
        try {
//            keyStore.load(ksIn, KEY_STORE_PASSWORD.toCharArray());
            trustStore.load(tsIn, KEY_STORE_TRUST_PASSWORD.toCharArray());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeStream(ksIn);
            closeStream(tsIn);
        }
//        SSLConnectionSocketFactory socketFactory=new SSLConnectionSocketFactory();
//        SSLSocketFactory socketFactory = new SSLSocketFactory(keyStore, KEY_STORE_PASSWORD, trustStore);
        SSLSocketFactory socketFactory = new SSLSocketFactory(null, KEY_STORE_PASSWORD, trustStore);

        return socketFactory;
    }

    private static void closeStream(InputStream ksIn) {
        try {
            ksIn.close();
        } catch (Exception ignore) {
        }
    }
}
