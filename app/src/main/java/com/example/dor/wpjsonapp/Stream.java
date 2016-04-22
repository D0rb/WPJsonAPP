package com.example.dor.wpjsonapp;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Stream {
    public static InputStream openHttpGetConnection(String url)
            throws Exception {
        InputStream inputStream = null;
        HttpClient httpClient = new DefaultHttpClient();
        HttpResponse httpResponse = httpClient.execute(new HttpGet(url));
        inputStream = httpResponse.getEntity().getContent();
        return inputStream;

    }
    public static String downloadSource(String url) {
        int BUFFER_SIZE = 1024;

        InputStream inputStream = null;
        try {
            inputStream = openHttpGetConnection(url);
        } catch (Exception e) {
            // TODO: handle exception
        }
        int bytesRead;
        String str = "";
        try {
            InputStreamReader isr = new InputStreamReader(inputStream, "UTF-8");
            char[] inputBuffer = new char[BUFFER_SIZE];

            while ((bytesRead = isr.read(inputBuffer, 0, BUFFER_SIZE)) > 0) {
                String read = new String(inputBuffer, 0, bytesRead);
                str += read;
            }
        } catch (Exception e) {
        }
        return str;

    }
}
