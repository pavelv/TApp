package com.pavelv.touchapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class DownloadPhotoTask extends AsyncTask<String, Integer, Integer> {

    private Context context;
    private Listener listener;
    private String storagePath;
    private ProgressDialog progressDialog;
    private InputStream inputStream;
    private OutputStream outputStream;
    private Integer statusCode = SUCCESS;

    public static final int SUCCESS = 0;
    public static final int ERROR = -1;

    public DownloadPhotoTask(Context context, Listener listener, String storagePath) {
        this.context = context;
        this.listener = listener;
        this.storagePath = storagePath;
    }

    public static interface Listener {
        void onPhotoDownloadComplete(Integer statusCode);
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Downloading photo...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        super.onPreExecute();
    }

    @Override
    protected Integer doInBackground(String... url) {
        try {
            URL imageUrl = new URL(url[0]);
            URLConnection connection = imageUrl.openConnection();

            // use a filename constructed from the file length so photo is only downloaded once
            int fileLength = connection.getContentLength();
            String pictureFileName = "/TOUCH_" + fileLength + ".jpg";

            inputStream = new BufferedInputStream(imageUrl.openStream());
            outputStream = new FileOutputStream(storagePath + pictureFileName);

            byte data[] = new byte[1024];
            int count;
            while ((count = inputStream.read(data)) != -1) {
                outputStream.write(data, 0, count);
            }

        } catch (Exception e) {
            statusCode = ERROR;

        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return statusCode;
    }

    @Override
    protected void onPostExecute(Integer statusCode) {
        super.onPostExecute(statusCode);
        listener.onPhotoDownloadComplete(statusCode);
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
