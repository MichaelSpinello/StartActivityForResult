package com.michael.startactivityforresult;


import android.graphics.Bitmap;
import android.os.AsyncTask;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

public class MyViewModel extends ViewModel {
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

        return taskResponse;
    }

    public void startTask(Bitmap mFotoToUpload) {
        new UploadTask().execute(mFotoToUpload);
    }


    public class UploadTask extends AsyncTask<Bitmap, Void, Boolean> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            taskResponse.setValue(new TaskResponse(0, true));

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
                taskResponse.setValue(new TaskResponse(1, false));
            else
                taskResponse.setValue(new TaskResponse(-1, false));
        }
    }
}
