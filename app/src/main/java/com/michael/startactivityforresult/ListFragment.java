package com.michael.startactivityforresult;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.client.googleapis.media.MediaHttpDownloader;
import com.google.api.client.googleapis.media.MediaHttpDownloaderProgressListener;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static com.michael.startactivityforresult.DriveServiceHelper.getGoogleDriveService;

public class ListFragment extends Fragment {


    private DriveServiceHelper mDriveServiceHelper;
    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE_SIGN_IN = 100;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private RecyclerView mRecyclerView;
    private ImageView mImageview;
    private List<File> mFiles;
    private ArrayList<Bitmap> mBitmap;
    private java.io.File mFileIO;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setContentView(R.layout.fragment_list);
        //mRecyclerView = (RecyclerView) getActivity().findViewById(R.id.recycler_view);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_list, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mRecyclerView =  view.findViewById(R.id.recycler_view);

    }

    @Override
    public void onStart() {
        super.onStart();

        signIn();

    }

    private void signIn() {

        Log.d(TAG, "Requesting sign-in");

        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
                        .build();
        if (getActivity() != null) {
            GoogleSignInClient client = GoogleSignIn.getClient(getActivity(), signInOptions);

            startActivityForResult(client.getSignInIntent(), REQUEST_CODE_SIGN_IN);
        }
    }

    private void handleSignInResult(Intent result) {
        GoogleSignIn.getSignedInAccountFromIntent(result)
                .addOnSuccessListener(new OnSuccessListener<GoogleSignInAccount>() {
                    @Override
                    public void onSuccess(GoogleSignInAccount googleSignInAccount) {
                        Log.d("LOG", "Signed in as " + googleSignInAccount.getEmail());

                        if (getActivity() != null)
                            mDriveServiceHelper = new DriveServiceHelper(getGoogleDriveService(getActivity().getApplicationContext(), googleSignInAccount, "appName"));

                        Log.d("LOG", "handleSignInResult: " + mDriveServiceHelper);
                        query();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("", "Unable to sign in.", e);
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SIGN_IN) {
            Log.d(TAG, "result code " + resultCode);
            if (resultCode == Activity.RESULT_OK && data != null) {
                handleSignInResult(data);
            }
        }
    }

    private void readFile(String fileId) {
        if (mDriveServiceHelper != null) {
            Log.d(TAG, "Reading file " + fileId);

            mDriveServiceHelper.readFile(fileId)
                    .addOnSuccessListener(nameAndContent -> {
                        String name = nameAndContent.first;
                        String content = nameAndContent.second;


                        setReadWriteMode(fileId);
                    })
                    .addOnFailureListener(exception ->
                            Log.e(TAG, "Couldn't read file.", exception));
        }
    }

    private void query() {

        if (mDriveServiceHelper != null) {
            Log.d(TAG, "Querying for files.");

            mDriveServiceHelper.queryFiles()
                .addOnSuccessListener(fileList -> {
                    mFiles = fileList.getFiles();
                    new DownloadFilesTask().execute();

                    Log.d("TAG", "nel listener");
                    if(getActivity()!= null) {
                        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        mRecyclerView.setAdapter(new RecyclerViewAdapter(getActivity(), fileList.getFiles(), mBitmap));
                        Log.d("TAG", "sto per uscire dal listener");
                    }


                })
                .addOnFailureListener(exception -> Log.e(TAG, "Unable to query files.", exception));
        }

    }
    /**
     * Updates the UI to read-only mode.
     */
    private void setReadOnlyMode() {
       // mDocContentEditText.setEnabled(false);
    }
    private void setReadWriteMode(String fileId) {
        //mFileTitleEditText.setEnabled(true);
        //mDocContentEditText.setEnabled(true);
        //mOpenFileId = fileId;
    }

    public class DownloadFilesTask  extends AsyncTask <Void, Void, ArrayList<Bitmap>>{

        @Override
        protected ArrayList<Bitmap> doInBackground(Void... voids) {
            java.io.File outputDir = getActivity().getCacheDir();
            OutputStream outputStream = null;
            String path;

            for (File file : mFiles) {
                try {
                    path = "/temp/" + file.getName() + file.getFileExtension();
                    outputStream = new FileOutputStream(path);
                    //java.io.File outputFile = java.io.File.createTempFile(file.getName(), file.getFileExtension(), outputDir);
                    //InputStream inputStream = new FileInputStream(outputFile);

                    Drive.Files.Get request = mDriveServiceHelper.getmDriveService().files().get(file.getId());
                    request.getMediaHttpDownloader().setProgressListener(new MediaHttpDownloaderProgressListener() {
                        @Override
                        public void progressChanged(MediaHttpDownloader downloader) throws IOException {
                            switch (downloader.getDownloadState()) {
                                case MEDIA_IN_PROGRESS:
                                    Log.d("TAG", "Download in progress");
                                    Log.d("TAG", "Download percentage: " + downloader.getProgress());
                                    break;
                                case MEDIA_COMPLETE:
                                    Log.d("TAG", "Download Completed!");
                                    break;
                            }
                        }
                    });
                    request.executeMediaAndDownloadTo(outputStream);
                        mFileIO = new java.io.File(path);
                    if(mFileIO != null){
                        mBitmap.add(BitmapFactory.decodeFile(path));
                    }
                } catch (FileNotFoundException e) {
                    e.getMessage();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            }
            return mBitmap;
        }

    }
}
