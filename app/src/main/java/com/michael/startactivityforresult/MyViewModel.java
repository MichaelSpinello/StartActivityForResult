package com.michael.startactivityforresult;


import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

public class MyViewModel extends ViewModel {
   // private MutableLiveData<Modello> modello;
    private String mDescription;
    private String mStatus;
    private String mPhotoDirectory;
    private MutableLiveData<TaskResponse> taskResponse;
    private UploadTask uploadTask;

    public UploadTask getUploadTask() {
        return uploadTask;
    }

    public void setUploadTask(UploadTask uploadTask) {
        this.uploadTask = uploadTask;
    }

    public String getmDescription() {
        return mDescription;
    }

    public void setmDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public String getmStatus() {
        return mStatus;
    }

    public void setmStatus(String mStatus) {
        this.mStatus = mStatus;
    }

    public String getmPhotoDirectory() {
        return mPhotoDirectory;
    }

    public void setmPhotoDirectory(String mPhotoDirectory) {
        this.mPhotoDirectory = mPhotoDirectory;
    }



    public MutableLiveData<TaskResponse> getTaskResponse() {
        if(taskResponse == null)
            taskResponse = new MutableLiveData<TaskResponse>();
        updateModello();
        return taskResponse;
    }

    private void updateModello() {


    }



    public static class UploadTask extends AsyncTask<Bitmap, Void, Boolean> {

        private boolean inProgress;
        private int resultUpload;
        private MyViewModel.UploadTask.OnUploadTaskListener listener;

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
        public void setOnUploadTaskListener( MyViewModel.UploadTask.OnUploadTaskListener listener){
            this.listener = listener;
        }

        public interface OnUploadTaskListener{
            void onSuccessReceiver(int resultUpload, Boolean inProgress);
        }
    }
}
