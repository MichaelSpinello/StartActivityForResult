package com.michael.startactivityforresult;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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

    private void query() {

        if (mDriveServiceHelper != null) {
            Log.d(TAG, "Querying for files.");

            mDriveServiceHelper.queryFiles()
                    .addOnSuccessListener(fileList -> {
                                //StringBuilder builder = new StringBuilder();
                        /*ArrayList<Bitmap> bitmapList = new ArrayList<Bitmap>();
                        for (File file : fileList.getFiles()) {
                            //builder.append(file.getName()).append("\n");
                            URL url = null;
                        try {
                            url = new URL("https://drive.google.com/file/d/" + file.getId() + "/view?usp=sharing");
                            Log.d("url: ", url.toString());
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }

                        HttpURLConnection connection = null;
                        Bitmap bmp = null;
                        try{
                            connection = (HttpURLConnection) url.openConnection();

                            connection.connect();

                            InputStream inputStream = connection.getInputStream();

                            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

                            bmp = BitmapFactory.decodeStream(bufferedInputStream);


                        }catch(IOException e){
                            e.printStackTrace();
                        }finally{
                            // Disconnect the http url connection
                            connection.disconnect();
                        }
                            bitmapList.add(bmp);
                        }*/
                                //String fileNames = builder.toString();

                                //mDocContentEditText.setText(fileNames);

                                //setReadOnlyMode();
                                //Picasso.with(getActivity()).load("https://drive.google.com/file/d/1UwheS6mwpjA1FbbBv0_bNj-GhI0oGUP-/view")
                                //        .into(mImageview);

                        Log.d("TAG", "nel listener");
                        if(getActivity()!= null) {
                            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                            mRecyclerView.setAdapter(new RecyclerViewAdapter(getActivity(), fileList.getFiles()));
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
}
