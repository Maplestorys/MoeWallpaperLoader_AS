package com.maplestory.moewallpaperloader.utils;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


/**
 * Created by 智 on 2015/8/11.
 */
public class HttpClientHelper {

    private static HttpClient httpClient;

    private HttpClientHelper() {
    }

    public static synchronized HttpClient getHttpClient() {

        if (null == httpClient) {
            // 初始化工作
            try {
                KeyStore trustStore = KeyStore.getInstance(KeyStore
                        .getDefaultType());
                trustStore.load(null, null);
                SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);
                sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);  //允许所有主机的验证

                HttpParams params = new BasicHttpParams();
                HttpProtocolParams.setUserAgent(params,"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.130 Safari/537.36");
                HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
                HttpProtocolParams.setContentCharset(params,
                        HTTP.DEFAULT_CONTENT_CHARSET);
                HttpProtocolParams.setUseExpectContinue(params, true);

                // 设置连接管理器的超时
                ConnManagerParams.setTimeout(params, 10000);
                // 设置连接超时
                HttpConnectionParams.setConnectionTimeout(params, 10000);
                // 设置socket超时
                HttpConnectionParams.setSoTimeout(params, 10000);

                // 设置http https支持
                SchemeRegistry schReg = new SchemeRegistry();
                schReg.register(new Scheme("http", PlainSocketFactory
                        .getSocketFactory(), 80));
                schReg.register(new Scheme("https", sf, 443));

                ClientConnectionManager conManager = new ThreadSafeClientConnManager(
                        params, schReg);

                httpClient = new DefaultHttpClient(conManager, params);
            } catch (Exception e) {
                e.printStackTrace();
                return new DefaultHttpClient();
            }
        }
        return httpClient;
    }


}