package com.michael.startactivityforresult;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    //private MyViewModel model;
    private Fragment mMainFragment;
    private static final String KEY_STATE_FRAGMENT = "custom_activity_state";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        //model = new ViewModelProvider(this).get(MyViewModel.class);

        FragmentManager fm = getSupportFragmentManager();


         /*   mMainFragment = fm.findFragmentById(R.id.fragment_container);
            if (mMainFragment == null) {
                mMainFragment = new MainFragment();
                fm.beginTransaction().add(mMainFragment, KEY_STATE_FRAGMENT).commit();
            }
*/
        mMainFragment = fm.findFragmentById(R.id.fragment_container);
        if (mMainFragment == null) {
            mMainFragment = new ListFragment();
            fm.beginTransaction().add(mMainFragment, KEY_STATE_FRAGMENT).commit();
        }
            //FragmentTransaction fragmentTransaction = fm.beginTransaction();
            //fragmentTransaction.replace(R.id.fragment_container, mMainFragment);
            //fragmentTransaction.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }


}
