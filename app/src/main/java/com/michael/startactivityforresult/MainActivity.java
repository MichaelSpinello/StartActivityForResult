package com.michael.startactivityforresult;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.api.services.drive.DriveScopes;
import static com.michael.startactivityforresult.DriveServiceHelper.getGoogleDriveService;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "TAG_MAIN_ACTIVITY";
    private static final int REQUEST_CODE_SIGN_IN = 100;
    private DriveServiceHelper mDriveServiceHelper;
    private MyViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        model = new ViewModelProvider(this).get(MyViewModel.class);
        if (model.getmDriveServiceHelper().getValue() == null)
            signIn();
        else {
            mDriveServiceHelper = model.getmDriveServiceHelper().getValue();
        }


        final androidx.lifecycle.Observer<DriveServiceHelper> observer = new Observer<DriveServiceHelper>() {
            @Override
            public void onChanged(DriveServiceHelper driveServiceHelper) {
                mDriveServiceHelper = driveServiceHelper;
            }
        };
        model.getmDriveServiceHelper().observe(this, observer);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Acquisisci"));
        tabLayout.addTab(tabLayout.newTab().setText("Lista file"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        ViewPager viewPager = findViewById(R.id.pager);
        PageAdapter pageAdapter = new PageAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pageAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }



    public class PageAdapter extends FragmentPagerAdapter{
        public PageAdapter(@NonNull FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT );
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position){

                case 0:
                    fragment = new MainFragment();
                    break;
                case 1:
                    fragment = new ListFragment();
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void signIn() {

        GoogleSignInOptions signInOptions =
            new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
                .build();
        if(this!= null) {
            GoogleSignInClient client = GoogleSignIn.getClient(this, signInOptions);
            startActivityForResult(client.getSignInIntent(), REQUEST_CODE_SIGN_IN);
        }
    }

    private void handleSignInResult(Intent result) {
        GoogleSignIn.getSignedInAccountFromIntent(result)
            .addOnSuccessListener(new OnSuccessListener<GoogleSignInAccount>() {
                @Override
                public void onSuccess(GoogleSignInAccount googleSignInAccount) {
                    Log.d(TAG, "Signed in as " + googleSignInAccount.getEmail());

                    if(this!= null) {
                        mDriveServiceHelper = new DriveServiceHelper(getGoogleDriveService(getApplicationContext(), googleSignInAccount, "appName"));
                        model.getmDriveServiceHelper().setValue(mDriveServiceHelper);
                    }
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
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                handleSignInResult(data);
            }
        }
    }

}
