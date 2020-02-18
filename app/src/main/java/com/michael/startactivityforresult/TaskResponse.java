package com.michael.startactivityforresult;

import android.widget.TextView;

public class TaskResponse {
    private int resultUpload;
    private boolean inProgress;

    public TaskResponse() {
        this.inProgress = false;
        this.resultUpload = 0;
    }

    public TaskResponse(int resultUpload, boolean inProgress) {
        this.resultUpload = resultUpload;
        this.inProgress = inProgress;
    }

    public boolean isInProgress() {
        return inProgress;
    }

    public void setInProgress(boolean inProgress) {
        this.inProgress = inProgress;
    }

    public int getResultUpload() {
        return resultUpload;
    }

    public void setResultUpload(int resultUpload) {
        this.resultUpload = resultUpload;
    }




}
