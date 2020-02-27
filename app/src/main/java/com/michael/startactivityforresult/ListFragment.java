package com.michael.startactivityforresult;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class ListFragment extends Fragment {

    private DriveServiceHelper mDriveServiceHelper;
    private static final String TAG = "TAG_LIST_FRAGMENT";
    private RecyclerView mRecyclerView;
    private MyViewModel model;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerViewAdapter mRecyclerViewAdapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = new ViewModelProvider(getActivity()).get(MyViewModel.class);
        mDriveServiceHelper = model.getmDriveServiceHelper().getValue();
        final androidx.lifecycle.Observer<DriveServiceHelper> observer = new Observer<DriveServiceHelper>() {
            @Override
            public void onChanged(DriveServiceHelper driveServiceHelper) {
                mDriveServiceHelper = driveServiceHelper;
                query();
            }
        };
        model.getmDriveServiceHelper().observe(this, observer);
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
        mSwipeRefreshLayout = view.findViewById(R.id.refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                query();
                if(mRecyclerViewAdapter != null)
                    mRecyclerViewAdapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
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
                    if(getActivity()!= null) {
                        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        mRecyclerViewAdapter = new RecyclerViewAdapter(getActivity(), fileList.getFiles(), mDriveServiceHelper, getActivity());
                        mRecyclerView.setAdapter(mRecyclerViewAdapter);
                    }
                })
                .addOnFailureListener(exception -> Log.e(TAG, "Unable to query files.", exception));
        }
    }
}
