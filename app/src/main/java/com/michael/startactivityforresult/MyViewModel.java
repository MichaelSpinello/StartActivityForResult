package com.michael.startactivityforresult;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import java.io.File;


public class MyViewModel extends ViewModel {
    private String mDescription;
    private String mStatus;
    private String mPhotoDirectory;
    private MutableLiveData<TaskResponse> taskResponse;

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

    public void startTask(String mPhotoDirectory, DriveServiceHelper driveServiceHelper) {
        taskResponse.setValue(new TaskResponse(0, true));
        File file = new File (mPhotoDirectory);
        driveServiceHelper.uploadFile(file, "image/jpg", null)
            .addOnSuccessListener(new OnSuccessListener<GoogleDriveFileHolder>() {
                @Override
                public void onSuccess(GoogleDriveFileHolder googleDriveFileHolder) {
                    Gson gson = new Gson();
                    Log.d("success: ", "onSuccess: " + gson.toJson(googleDriveFileHolder));
                    taskResponse.setValue(new TaskResponse(1, false));
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Log.d("success: ", "onFailure: " + e.getMessage());
                    taskResponse.setValue(new TaskResponse(-1, false));
                    e.printStackTrace(System.out);
                }
            });
    }

}
