package com.michael.startactivityforresult;

import java.io.File;

public class TaskDownload {
    private boolean statoDownload;
    private java.io.File mFileIO;

    public TaskDownload() {
        this.statoDownload = false;
        this.mFileIO = null;
    }

    public TaskDownload(boolean statoDownload, File mFileIO) {
        this.statoDownload = statoDownload;
        this.mFileIO = mFileIO;
    }

    public boolean isStatoDownload() {
        return statoDownload;
    }

    public void setStatoDownload(boolean statoDownload) {
        this.statoDownload = statoDownload;
    }

    public File getmFileIO() {
        return mFileIO;
    }

    public void setmFileIO(File mFileIO) {
        this.mFileIO = mFileIO;
    }


}
