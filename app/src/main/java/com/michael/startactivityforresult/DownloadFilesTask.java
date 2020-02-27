package com.michael.startactivityforresult;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.api.client.googleapis.media.MediaHttpDownloader;
import com.google.api.client.googleapis.media.MediaHttpDownloaderProgressListener;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class DownloadFilesTask  extends AsyncTask<File, Void, java.io.File> {

    private ImageView mImageView;
    private ProgressBar mSpinner;
    TextView mDescription;
    private Context mContext;
    private DriveServiceHelper mDriveServiceHelper;
    private static final String TAG = "TAG_ASYNC";
    private boolean statoDownload;
    private java.io.File mFileIO;

    public DownloadFilesTask(ImageView mImageView, ProgressBar mSpinner, TextView mDescription, Context mContext, DriveServiceHelper mDriveServiceHelper) {
        this.mImageView = mImageView;
        this.mSpinner = mSpinner;
        this.mDescription = mDescription;
        this.mContext = mContext;
        this.mDriveServiceHelper = mDriveServiceHelper;
        statoDownload = false;
        mFileIO = null;
    }


    @Override
    protected java.io.File doInBackground(File... files) {
        for (File file : files) {
            String s = mContext.getCacheDir().toString();
            java.io.File outputDir = new java.io.File(s);
            if (!outputDir.exists()) {
                Log.d(TAG, "creo la cartella");
                outputDir.mkdirs();
            }
            try {

                Log.d(TAG, "creo il file per: " + file.getName());
                mFileIO = new java.io.File(outputDir.getPath(), file.getName());
                if (!mFileIO.exists() || mFileIO == null) {
                    Log.d(TAG, "creo nuovo file per: " + file.getName());
                    mFileIO.createNewFile();
                    OutputStream outputStream = new FileOutputStream(mFileIO.getPath());

                    Drive.Files.Get request = mDriveServiceHelper.getmDriveService().files().get(file.getId());
                    request.getMediaHttpDownloader().setProgressListener(new MediaHttpDownloaderProgressListener() {
                        @Override
                        public void progressChanged(MediaHttpDownloader downloader) throws IOException {
                            switch (downloader.getDownloadState()) {
                                case MEDIA_IN_PROGRESS:
                                    statoDownload = true;
                                    Log.d(TAG, "Download in progress di: " + file.getName());
                                    Log.d(TAG, "Download percentage: " + downloader.getProgress());
                                    break;
                                case MEDIA_COMPLETE:
                                    statoDownload = false;
                                    Log.d(TAG, "Download Completed! di: " + file.getName());

                                    break;
                            }
                        }
                    });

                    Log.d(TAG, "eseguo il download di: " + file.getName());
                    request.executeMediaAndDownloadTo(outputStream);
                }
            } catch (FileNotFoundException e) {
                e.getMessage();
            } catch (IOException e1) {
                Log.e(TAG, e1.getMessage());
            }

        }

        return mFileIO;
    }

    @Override
    protected void onPostExecute(java.io.File file) {
        Log.d(TAG, "istruzione ONPOST per: " + file.getName());
        if (statoDownload == true) {
            Log.d(TAG, "faccio apparire lo spinner per: " + file.getName());
            mSpinner.setVisibility(View.VISIBLE);
            //new DownloadFilesTask().execute(driveFeedModel);
        } else {
            Log.d(TAG, "Tolgo lo spinner per: " + file.getName());
            mSpinner.setVisibility(View.INVISIBLE);
        }
        if (file.exists())
            if (file != null) {
                Log.d(TAG, "Istruzione picasso di: " + file.getName());
                Picasso.with(mContext).load(file).centerCrop().resize(100, 100).into(mImageView);
                mDescription.setText(file.getName());
            }
    }
}