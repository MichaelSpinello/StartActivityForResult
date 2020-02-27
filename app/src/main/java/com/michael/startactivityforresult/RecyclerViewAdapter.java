package com.michael.startactivityforresult;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.google.api.services.drive.model.File;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter <RecyclerViewAdapter.FeedModelViewHolder>{
    private Context mContext;
    private List<File> mDriveFiles;
    private DriveServiceHelper mDriveServiceHelper;

    private ImageView mImageView;
    private TextView mDescription;
    private ProgressBar mSpinner;
    private static final String TAG = "TAG_RECYCLER";


    public static class FeedModelViewHolder extends RecyclerView.ViewHolder{
        private View driveFileView;

        public FeedModelViewHolder(View v){
            super(v);
            driveFileView = v;
        }
    }

    public RecyclerViewAdapter(Context context, List<File> driveFileList, DriveServiceHelper mDriveServiceHelper, FragmentActivity mFragmentActivity){
        mContext = context;
        mDriveFiles = driveFileList;
        this.mDriveServiceHelper = mDriveServiceHelper;
    }

    @NonNull
    @Override
    public FeedModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_drive_file, parent, false);
        FeedModelViewHolder holder = new FeedModelViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull FeedModelViewHolder holder, int position) {
        mImageView = holder.driveFileView.findViewById(R.id.imageview);
        mDescription = holder.driveFileView.findViewById(R.id.title);
        mSpinner = holder.driveFileView.findViewById(R.id.progressBar2);

        DownloadFilesTask downloadFilesTask = new DownloadFilesTask(mImageView, mSpinner, mDescription, mContext, mDriveServiceHelper);
        downloadFilesTask.execute(mDriveFiles.get(position));
    }

    @Override
    public int getItemCount() {
        return mDriveFiles.size();
    }




}
