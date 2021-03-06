package com.michael.startactivityforresult;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import static android.os.Environment.DIRECTORY_PICTURES;
import static com.michael.startactivityforresult.DriveServiceHelper.getGoogleDriveService;

public class MainActivity extends AppCompatActivity {

    private Button mButton;
    private Button mUpload;
    private TextView mDescription;
    private TextView mStatus;
    private ImageView mImageView;
    private Bitmap mFoto;
    private Bitmap mFotoToUpload = null;
    private ProgressBar mSpinner;
    private final RotateAnimation rotate = new RotateAnimation(0, 180,
            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
    private Intent takePictureIntent;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private MyViewModel model;
    private String mPhotoDirectory;
    private boolean inProgress;
    private int resultUpload;
    private DriveServiceHelper mDriveServiceHelper;
    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE_SIGN_IN = 100;

    @Override
    protected void onStart() {
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
        GoogleSignInClient client = GoogleSignIn.getClient(this, signInOptions);

        startActivityForResult(client.getSignInIntent(), REQUEST_CODE_SIGN_IN);
    }

    private void handleSignInResult(Intent result) {
        GoogleSignIn.getSignedInAccountFromIntent(result)
            .addOnSuccessListener(new OnSuccessListener<GoogleSignInAccount>() {
            @Override
            public void onSuccess(GoogleSignInAccount googleSignInAccount) {
                Log.d("LOG", "Signed in as " + googleSignInAccount.getEmail());

                mDriveServiceHelper = new DriveServiceHelper(getGoogleDriveService(getApplicationContext(), googleSignInAccount, "appName"));

                Log.d("LOG", "handleSignInResult: " + mDriveServiceHelper);
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
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        model = new ViewModelProvider(this).get(MyViewModel.class);
        rotate.setInterpolator(new LinearInterpolator());
        rotate.setDuration(10000);
        mButton = findViewById(R.id.button);
        mUpload = findViewById(R.id.upload);
        mDescription = findViewById(R.id.description);
        mStatus = findViewById(R.id.status);
        mImageView = findViewById(R.id.imageview);
        mSpinner = findViewById(R.id.progressBar);
        mSpinner.setVisibility(View.GONE);

        final ConstraintLayout mLayout = findViewById(R.id.root_layout);
        ViewTreeObserver vto = mLayout.getViewTreeObserver();

        if (model.getmPhotoDirectory() != null)
            mPhotoDirectory = model.getmPhotoDirectory();
        if(model.getmDescription() != null)
            mDescription.setText(model.getmDescription());
        if(model.getmStatus() != null)
            mStatus.setText(model.getmStatus());

        model.getTaskResponse().observe(this, taskResponse -> {
            inProgress = taskResponse.isInProgress();
            resultUpload = taskResponse.getResultUpload();
            checkStatus(inProgress, resultUpload);
        });

        if(mPhotoDirectory != null) {
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        mLayout.getViewTreeObserver()
                                .removeOnGlobalLayoutListener(this);
                    } else {
                        mLayout.getViewTreeObserver()
                                .removeGlobalOnLayoutListener(this);
                    }
                    setPic(mPhotoDirectory);
                }
            });
        }

        mButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!inProgress) {
                    dispatchTakePictureIntent();
                }
            }
        });


        mUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPhotoDirectory != null && !inProgress) {
                    model.startTask(mPhotoDirectory, mDriveServiceHelper);
                }
            }
        });
    }

    private void dispatchTakePictureIntent(){
        takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(photoFile != null){
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.michael.startactivityforresult", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            mDescription.setText("Immagine acquisita correttamente");
        }
        else if(requestCode == REQUEST_CODE_SIGN_IN){
            if(resultCode == Activity.RESULT_OK && data != null){
                handleSignInResult(data);
            }
        }
        else {
            mDescription.setText("Immagine non acquisita. Riprovare");
            mPhotoDirectory = null;
        }
        setPic(mPhotoDirectory);
        model.setmDescription((String)mDescription.getText());
    }

    private File createImageFile() throws IOException{
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageNameFile = "Foto_" + timeStamp;
        File storageDir = getExternalFilesDir(DIRECTORY_PICTURES);
        File image = File.createTempFile(imageNameFile, ".jpg", storageDir);
        mPhotoDirectory = image.getAbsolutePath();
        model.setmPhotoDirectory(mPhotoDirectory);
        return image;
    }

    private void setPic(String mPhotoDirectory) {
        mFoto = BitmapFactory.decodeFile(mPhotoDirectory);
        if (mFoto != null) {
            ExifInterface exif = null;
            try {
                exif = new ExifInterface(mPhotoDirectory);
            } catch (IOException e) {
                e.printStackTrace();
            }
            int photoOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            float angle;
            switch (photoOrientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    angle = 270f;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    angle = 180f;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    angle = 90f;
                    break;
                case ExifInterface.ORIENTATION_NORMAL:
                    angle = 0f;
                    break;
                default:
                    angle = 0f;
                    break;
            }
            Matrix matrix = new Matrix();
            mImageView.setScaleType(ImageView.ScaleType.MATRIX);
            matrix.postRotate(angle);
            mFoto = Bitmap.createBitmap(mFoto, 0, 0, mFoto.getWidth(),
                    mFoto.getHeight(), matrix, true);
            mImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            mImageView.setImageBitmap(mFoto);
            mFotoToUpload = mFoto;

        }
    }

    public void checkStatus (boolean inProgress, int resultUpload) {
        if (inProgress){
            mSpinner.setVisibility(View.VISIBLE);
            mSpinner.startAnimation(rotate);
        } else {
            mSpinner.setVisibility(View.INVISIBLE);

        }
        switch (resultUpload) {
            case -1:
                mStatus.setText("NON caricata:");
                break;
            case 0:
                mStatus.setText("uploading:");
                break;
            case 1:
                mStatus.setText("caricata");
                break;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
