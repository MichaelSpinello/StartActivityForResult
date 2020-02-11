package com.michael.startactivityforresult;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.savedstate.SavedStateRegistry;
import androidx.savedstate.SavedStateRegistryController;
import androidx.savedstate.SavedStateRegistryOwner;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.os.Environment.DIRECTORY_PICTURES;
import static android.os.Environment.getExternalStoragePublicDirectory;

public class MainActivity extends AppCompatActivity {

    private Button mButton;
    private Button mUpload;
    private TextView mDescription;
    private ImageView mImageView;
    private Bitmap mFoto;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private Intent takePictureIntent;
    private String mPhotoDirectory = "";
    private static final String IMAGE_KEY = "img";
    private static final String DESCRIPTION = "dsc";

    private final String MY_SAVED_STATE_KEY = "my_saved_state";
    //private final String SOME_VALUE_KEY = "some_value";
   // private String someValue;

    SavedStateRegistry.SavedStateProvider savedStateProvider =
            new SavedStateRegistry.SavedStateProvider() {
        @NonNull
        @Override
        public Bundle saveState() {
            //saveState().putString(SOME_VALUE_KEY, someValue);
            Bundle bundle = new Bundle();
            if(mPhotoDirectory!= null && mDescription != null) {

                bundle.putString(IMAGE_KEY, mPhotoDirectory);
                bundle.putString(DESCRIPTION, (String) mDescription.getText());
            }
            return bundle;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getSavedStateRegistry().registerSavedStateProvider(MY_SAVED_STATE_KEY, savedStateProvider);

        setContentView(R.layout.activity_main);

        mButton = findViewById(R.id.button);
        mUpload = findViewById(R.id.upload);
        mDescription = findViewById(R.id.description);
        mImageView = findViewById(R.id.imageview);
        if(savedInstanceState != null) {
            //someValue = getSavedStateRegistry().consumeRestoredStateForKey(MY_SAVED_STATE_KEY)
             //        .getString(SOME_VALUE_KEY);
            Bundle restoredState = getSavedStateRegistry().consumeRestoredStateForKey(MY_SAVED_STATE_KEY);
            if(restoredState != null){
                mPhotoDirectory = restoredState.getString(IMAGE_KEY);
                mDescription.setText(restoredState.getString(DESCRIPTION));
            }

           // mPhotoDirectory = savedInstanceState.getString(IMAGE_KEY);
            //mDescription.setText(savedInstanceState.getString(DESCRIPTION));
        }


        mButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }

        });

        final ConstraintLayout layout = findViewById(R.id.root_layout);
        ViewTreeObserver vto = layout.getViewTreeObserver();
        if(mPhotoDirectory != null) {
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        layout.getViewTreeObserver()
                                .removeOnGlobalLayoutListener(this);
                    } else {
                        layout.getViewTreeObserver()
                                .removeGlobalOnLayoutListener(this);
                    }
                    setPic(mPhotoDirectory);
                }
            });

            mUpload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new UploadTask().execute((Void)null);
                }
            });
        }
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
            setPic(mPhotoDirectory);
        }
        else {
            mDescription.setText("Immagine non acquisita. Riprovare");
            setPic(null);
        }
    }

    private File createImageFile() throws IOException{
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageNameFile = "Foto_" + timeStamp;
        File storageDir = getExternalFilesDir(DIRECTORY_PICTURES);
        File image = File.createTempFile(imageNameFile, ".jpg", storageDir);
        mPhotoDirectory = image.getAbsolutePath();
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
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            float angle;
            switch (orientation) {
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
            mFoto = Bitmap.createScaledBitmap(mFoto, mImageView.getWidth(),
                    mImageView.getHeight(), true);
            mImageView.setImageBitmap(mFoto);

        }
    }


    /*@Override
    protected void onPause() {
        super.onPause();
       // Bundle savedInstanceState = takePictureIntent.getExtras();
        //String tmp = savedInstanceState.getString(MediaStore.EXTRA_OUTPUT);
        //onSaveInstanceState(MainActivity, mPhotoDirectory);
        Bundle bundle = new Bundle();
        bundle.putString("foto_acquisita", mPhotoDirectory);
        onSaveInstanceState(bundle);
    }*/

   /* @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        savedStateProvider.saveState();*/
       // someValue = getSavedStateRegistry().consumeRestoredStateForKey(MY_SAVED_STATE_KEY)
       //         .getString(SOME_VALUE_KEY);
        //outState.putString(IMAGE_KEY, mPhotoDirectory);
       // outState.putString(DESCRIPTION, (String)mDescription.getText());
    //}

}
