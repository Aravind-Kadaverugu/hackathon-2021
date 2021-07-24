package com.example.recorder1;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FileUploadUtility {

    public static void doFileUpload(final String selectedPath, final Handler handler) {

        new Thread(() -> {
            try{
                OkHttpClient client = new OkHttpClient().newBuilder()
                        .build();
                MediaType mediaType = MediaType.parse("text/plain");
                RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("file",selectedPath,
                                RequestBody.create(MediaType.parse("application/octet-stream"),
                                        new File(selectedPath)))
                        .build();
                Request request = new Request.Builder()
                        .url("http://192.168.1.17:9081/know-your-caller/create-user")
                        .method("POST", body)
                        .build();
                Response response = client.newCall(request).execute();
                response.body();
                Log.d("Res: ", String.valueOf(response.body()));
            } catch (Exception e)
            {
                e.printStackTrace();
                Log.d("Exception",e.getMessage());
            }
        }).start();

    }

    private static String processResponse(HttpURLConnection conn, String responseFromServer) {
        DataInputStream inStream;
        try {
            inStream = new DataInputStream(conn.getInputStream());
            String str;

            while ((str = inStream.readLine()) != null) {
                responseFromServer = str;
            }
            inStream.close();

        } catch (IOException ioex) {
            Log.e("Debug", "error: " + ioex.getMessage(), ioex);
        }
        return responseFromServer;
    }

    static void sendMessageBack(String responseFromServer, int success, Handler handler) {
        Message message = new Message();
        message.obj = responseFromServer;
        message.arg1 = success;
        handler.sendMessage(message);
    }
}

