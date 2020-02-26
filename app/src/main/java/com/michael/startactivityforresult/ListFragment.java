package com.michael.startactivityforresult;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ListFragment extends Fragment {

    private DriveServiceHelper mDriveServiceHelper;
    private static final String TAG = "TAG_LIST_FRAGMENT";
    private static final int REQUEST_CODE_SIGN_IN = 100;
    private RecyclerView mRecyclerView;
    private MyViewModel model;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = new ViewModelProvider(this).get(MyViewModel.class);
        mDriveServiceHelper = model.getmDriveServiceHelper();
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
        query();

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
