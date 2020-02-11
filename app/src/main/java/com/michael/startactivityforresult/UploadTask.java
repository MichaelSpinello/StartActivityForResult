package com.michael.startactivityforresult;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

public class UploadTask extends AsyncTask <Bitmap, Void, Boolean>{

    protected Boolean inProgress;
    private MainActivity context;
    private OnUploadTaskListener listener;
    private int upload;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        inProgress = true;
        upload = 0;
        listener.onSuccessReceiver(upload, inProgress);
    }

    @Override
    protected Boolean doInBackground(Bitmap... bitmaps) {
        try {
            URL url = new URL("https://www.dropbox.com/it/");
            Thread.sleep(10000);
            //InputStream inputStream = url.openConnection().getInputStream();
            return new Random().nextBoolean();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean success) {
        super.onPostExecute(success);
        if(success)
            upload = 1;
        else
            upload = -1;
        inProgress = false;
        listener.onSuccessReceiver(upload, inProgress);
    }

    public void setOnUploadTaskListener(MainActivity context, OnUploadTaskListener listener){
        this.listener = listener;
        this.context = context;
    }

    public interface OnUploadTaskListener{
        void onSuccessReceiver(int upload, Boolean inProgress);
    }
}
