package com.example.lenovo.ptjob_company.com.Entry;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by lenovo on 2017/1/19.
 */
public class GetStatusThread extends Thread{
    String url;
    String iID;
    String pID;
    Handler handler;
    Message msg;
    public GetStatusThread(String url, String iID, String pID,Handler handler){
        this.url=url;
        this.iID=iID;

        this.pID=pID;
        this.handler=handler;
    }
    public void doGet() {
        URL httpURL;
        HttpURLConnection connection = null;
        try {
            url = url + "?iID=" + URLEncoder.encode(iID, "UTF-8")+"&pID="+URLEncoder.encode(pID, "UTF-8");
            Log.i("url", url);
            httpURL = new URL(url);


            connection = (HttpURLConnection) httpURL.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(5000);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            if (connection.getResponseCode() == 200) {

                msg = new Message();
                msg.what = 200;
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String str = "";

                StringBuilder sb = new StringBuilder();
                while ((str = br.readLine()) != null) {
                    sb.append(str);
                }
                msg.obj = sb;
                handler.sendMessage(msg);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }
    }

    @Override
    public void run() {
        doGet();
    }
}
