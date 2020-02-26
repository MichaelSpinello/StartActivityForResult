package com.michael.startactivityforresult;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.google.api.services.drive.DriveScopes;


import static com.michael.startactivityforresult.DriveServiceHelper.getGoogleDriveService;

public class ListFragment extends Fragment {

    private DriveServiceHelper mDriveServiceHelper;
    private static final String TAG = "TAG_LIST_FRAGMENT";
    private static final int REQUEST_CODE_SIGN_IN = 100;
    private RecyclerView mRecyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "sono nell'oncreateview del fragmentlist");
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

            Log.d(TAG, "sto per far partire l'intent");
            startActivityForResult(client.getSignInIntent(), REQUEST_CODE_SIGN_IN);
        }
    }

    private void handleSignInResult(Intent result) {
        GoogleSignIn.getSignedInAccountFromIntent(result)
                .addOnSuccessListener(new OnSuccessListener<GoogleSignInAccount>() {
                    @Override
                    public void onSuccess(GoogleSignInAccount googleSignInAccount) {
                        Log.d(TAG, "Signed in as " + googleSignInAccount.getEmail());

                        if (getActivity() != null)
                            mDriveServiceHelper = new DriveServiceHelper(getGoogleDriveService(getActivity().getApplicationContext(), googleSignInAccount, "appName"));

                        Log.d(TAG, "handleSignInResult: " + mDriveServiceHelper);
                        query();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Unable to sign in.", e);
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d(TAG, "result code " + resultCode);
        Log.d(TAG, "request code " + requestCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SIGN_IN) {
            Log.d(TAG, "result code " + resultCode);
            if(resultCode == Activity.RESULT_OK)
                Log.d(TAG, "Activity.RESULT_OK? true");
            if(data != null)
                Log.d(TAG, "data!= null: true");
            if (resultCode == Activity.RESULT_OK && data != null) {
                Log.d(TAG, "chiamo handlesigninresult " + resultCode);
                handleSignInResult(data);
            }
        }
    }

    private void query() {

        if (mDriveServiceHelper != null) {
            Log.d(TAG, "Querying for files.");

            mDriveServiceHelper.queryFiles()
                .addOnSuccessListener(fileList -> {
                    Log.d(TAG, "nel listener");
                    if(getActivity()!= null) {
                        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        mRecyclerView.setAdapter(new RecyclerViewAdapter(getActivity(), fileList.getFiles(), mDriveServiceHelper));
                        Log.d(TAG, "sto per uscire dal listener");
                    }
                })
                .addOnFailureListener(exception -> Log.e(TAG, "Unable to query files.", exception));
        }

    }

}
