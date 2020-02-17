package com.michael.startactivityforresult;

import android.widget.TextView;

public class TaskResponse {
    public boolean isInProgress() {
        return inProgress;
    }

    public void setInProgress(boolean inProgress) {
        this.inProgress = inProgress;
    }

    private boolean inProgress;

    public int getResultUpload() {
        return resultUpload;
    }

    public void setResultUpload(int resultUpload) {
        this.resultUpload = resultUpload;
    }

    private int resultUpload;


}
