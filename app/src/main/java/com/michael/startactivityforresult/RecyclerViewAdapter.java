package com.michael.startactivityforresult;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import com.google.api.client.googleapis.media.MediaHttpDownloader;
import com.google.api.client.googleapis.media.MediaHttpDownloaderProgressListener;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.squareup.picasso.Picasso;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter <RecyclerViewAdapter.FeedModelViewHolder>{
    private Context mContext;
    private List<File> mDriveFiles;
    private DriveServiceHelper mDriveServiceHelper;
    private java.io.File mFileIO;
    private ImageView mImageView;
    private TextView mDescription;
    private File driveFeedModel;
    private OutputStream outputStream = null;
    private ProgressBar mSpinner;
    private boolean statoDownload;
    private MyViewModel model;


    public static class FeedModelViewHolder extends RecyclerView.ViewHolder{
        private View driveFileView;

        public FeedModelViewHolder(View v){
            super(v);
            driveFileView = v;
        }
    }

    public RecyclerViewAdapter(Context context, List<File> driveFileList, DriveServiceHelper mDriveServiceHelper){
        mContext = context;
        mDriveFiles = driveFileList;
        this.mDriveServiceHelper = mDriveServiceHelper;
    }

    @NonNull
    @Override
    public FeedModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_drive_file, parent, false);
        FeedModelViewHolder holder = new FeedModelViewHolder(v);
        //Picasso.with(this).load("www.journaldev.com").placeholder(R.drawable.placeholder).into(imageView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull FeedModelViewHolder holder, int position) {
        driveFeedModel = mDriveFiles.get(position);
        mImageView = holder.driveFileView.findViewById(R.id.imageview);
        mDescription = holder.driveFileView.findViewById(R.id.title);
        mSpinner = holder.driveFileView.findViewById(R.id.progressBar2);
        ((TextView)holder.driveFileView.findViewById(R.id.title)).setText(driveFeedModel.getThumbnailLink());
        URL url = null;
        try {
            url = new URL("https://drive.google.com/uc?export=download&id=" + driveFeedModel.getId());
            Log.d("url: ", url.toString());
            //Picasso.with(mContext).load(url.toString()).into(imageView);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


        new DownloadFilesTask().execute(driveFeedModel);
    }

    @Override
    public int getItemCount() {
        return mDriveFiles.size();
    }


    public class DownloadFilesTask  extends AsyncTask<File, Void, Void> {

        @Override
        protected Void doInBackground(File... files) {
            String s = mContext.getFilesDir() + "/temporanei5";
            java.io.File outputDir = new java.io.File(s);
            if(!outputDir.exists())
                outputDir.mkdirs();


                try {
                    mFileIO = new java.io.File(outputDir.getPath(), driveFeedModel.getName());
                    if(!mFileIO.exists() || mFileIO == null) {
                        mFileIO.createNewFile();
                        outputStream = new FileOutputStream(mFileIO.getPath());
                        Drive.Files.Get request = mDriveServiceHelper.getmDriveService().files().get(driveFeedModel.getId());
                        request.getMediaHttpDownloader().setProgressListener(new MediaHttpDownloaderProgressListener() {
                            @Override
                            public void progressChanged(MediaHttpDownloader downloader) throws IOException {
                                switch (downloader.getDownloadState()) {
                                    case MEDIA_IN_PROGRESS:
                                        statoDownload = true;
                                        mSpinner.setVisibility(View.VISIBLE);
                                        Log.d("TAG", "Download in progress");
                                        Log.d("TAG", "Download percentage: " + downloader.getProgress());
                                        break;
                                    case MEDIA_COMPLETE:
                                        statoDownload = false;
                                        mSpinner.setVisibility(View.INVISIBLE);
                                        Log.d("TAG", "Download Completed!");
                                        break;
                                }
                            }
                        });

                        request.executeMediaAndDownloadTo(outputStream);
                    }
                } catch (FileNotFoundException e) {
                    e.getMessage();
                } catch (IOException e1) {
                    Log.e("ERRORE", e1.getMessage());
                }

            return null;
        }

        @Override
        protected void onPostExecute(Void avoid) {
            if(statoDownload == true){
                mSpinner.setVisibility(View.VISIBLE);
            }
            else
                mSpinner.setVisibility(View.INVISIBLE);
            if(mFileIO.exists())
                if(mFileIO != null){
                    Log.d("TAG", "Istruzione picasso");
                    Picasso.with(mContext).load(mFileIO).centerCrop().resize(100, 100).into(mImageView);
                    mDescription.setText(driveFeedModel.getName());
                }
        }
    }
}
