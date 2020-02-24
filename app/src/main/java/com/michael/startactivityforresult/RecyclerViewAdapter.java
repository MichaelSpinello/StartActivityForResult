package com.michael.startactivityforresult;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter <RecyclerViewAdapter.FeedModelViewHolder>{
    private Context mContext;
    private List<File> mDriveFiles;
    private ArrayList<Bitmap> mBitmap;

    public static class FeedModelViewHolder extends RecyclerView.ViewHolder{
        private View driveFileView;

        public FeedModelViewHolder(View v){
            super(v);
            driveFileView = v;
        }
    }

    public RecyclerViewAdapter(Context context, List<File> driveFileList, ArrayList<Bitmap> mBitmap){
        mContext = context;
        mDriveFiles = driveFileList;
        this.mBitmap = mBitmap;
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
        final File driveFeedModel = mDriveFiles.get(position);
        ImageView imageView = holder.driveFileView.findViewById(R.id.imageview);
        ((TextView)holder.driveFileView.findViewById(R.id.title)).setText(driveFeedModel.getThumbnailLink());
        URL url = null;
        try {
            url = new URL("https://drive.google.com/uc?export=download&id=" + driveFeedModel.getId());
            Log.d("url: ", url.toString());
            //Picasso.with(mContext).load(url.toString()).into(imageView);
            imageView.setImageBitmap(mBitmap.get(position));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        String fileId = "0BwwA4oUTeiV1UVNwOHItT0xfa2M";



        try {
            Log.d("Thumbnail: ", driveFeedModel.getId());
            Log.d("Thumbnail: ", driveFeedModel.getMimeType());
            Log.d("Thumbnail: ", driveFeedModel.getKind());
            Log.d("Thumbnail: ", "___________________");
        }catch (NullPointerException e){
            Log.d("Thumbnail: ", "nulla: " + e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return mDriveFiles.size();
    }
}
