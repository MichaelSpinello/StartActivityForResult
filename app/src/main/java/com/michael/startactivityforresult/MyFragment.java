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
    private UploadTask uploadTask;
    private UploadTask.OnUploadTaskListener mListener;

    public UploadTask getUploadTask() {
        return uploadTask;
    }

    public void setUploadTask(UploadTask uploadTask) {
        this.uploadTask = uploadTask;
    }

    public UploadTask.OnUploadTaskListener getmListener() {
        return mListener;
    }

    public void setmListener(UploadTask.OnUploadTaskListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public static class UploadTask extends AsyncTask<Bitmap, Void, Boolean> {

        protected boolean inProgress;
        private OnUploadTaskListener listener;
        private int resultUpload;

        public boolean isInProgress(){
            return inProgress;
        }

        public int getResultUpload(){
            return resultUpload;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            inProgress = true;
            resultUpload = 0;
            if(listener != null)
                listener.onSuccessReceiver(resultUpload, inProgress);
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
                resultUpload = 1;
            else
                resultUpload = -1;
            inProgress = false;
            if(listener != null)
                listener.onSuccessReceiver(resultUpload, inProgress);

        }

        public void setOnUploadTaskListener( MyFragment.UploadTask.OnUploadTaskListener listener){
            this.listener = listener;
        }

        public interface OnUploadTaskListener{
            void onSuccessReceiver(int upload, Boolean inProgress);
        }
    }

}
