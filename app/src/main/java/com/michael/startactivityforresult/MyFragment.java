package com.michael.startactivityforresult;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

public class MyFragment extends Fragment {

    private static final String TAG = "custom_activity_state";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public static class UploadTask extends AsyncTask<Bitmap, Void, Boolean> {

        protected Boolean inProgress;
        private MainActivity context;
        private MyFragment.UploadTask.OnUploadTaskListener listener;
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

        public void setOnUploadTaskListener(MainActivity context, MyFragment.UploadTask.OnUploadTaskListener listener){
            this.listener = listener;
            this.context = context;
        }

        public interface OnUploadTaskListener{
            void onSuccessReceiver(int upload, Boolean inProgress);
        }
    }

}
