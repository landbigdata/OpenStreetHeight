package oss.technion.openstreetheight.section.photo.view;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.Fragment;

import com.evernote.android.state.State;
import com.evernote.android.state.StateSaver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.Action;
import butterknife.ButterKnife;
import butterknife.OnClick;
import oss.technion.openstreetheight.BuildConfig;
import oss.technion.openstreetheight.R;
import oss.technion.openstreetheight.section.photo.presenter.PhotoPresenter;


public class PhotoFragment extends Fragment implements PhotoView {
    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final String PHOTO_FILE_NAME = "building_photo";

    private PhotoPresenter presenter = new PhotoPresenter(this);
    private ActionBar actionBar;


    @State String imagePath;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        if (savedInstanceState != null) {
            StateSaver.restoreInstanceState(this, savedInstanceState);
            StateSaver.restoreInstanceState(presenter, savedInstanceState);
        }

        presenter.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        presenter.onStop();
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        StateSaver.saveInstanceState(this, outState);
        StateSaver.saveInstanceState(presenter, outState);


        super.onSaveInstanceState(outState);
    }


    @OnClick(R.id.photo_make_shot)
    void onMakeShot() {
        presenter.onMakeShotButtonClick();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_TAKE_PHOTO:
                    // handle case when image is not rotated by camera automatically
                    fixPhotoOrientationIfNeeded(imagePath);

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(imagePath, options);

                    int imageHeight = options.outHeight;
                    int imageWidth = options.outWidth;

                    presenter.onPhotoObtained(imagePath, imageHeight, imageWidth);
                    break;
            }
        }
    }


    private void fixPhotoOrientationIfNeeded(String imagePath) {
        float photoRotation = 0;

        ExifInterface exifInterface = null;

        try {
            exifInterface = new ExifInterface(imagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int orientation = exifInterface.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);

        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                photoRotation = 0;
                break;

            case ExifInterface.ORIENTATION_ROTATE_90:
                photoRotation = 90;
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                photoRotation = 180;
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                photoRotation = 270;
                break;
        }


        if (photoRotation != 0) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);

            FileOutputStream out = null;
            try {
                out = new FileOutputStream(imagePath);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Matrix rotateMatrix = new Matrix();
            rotateMatrix.postRotate(photoRotation);

            Bitmap toSave = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), rotateMatrix, true);

            toSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                PHOTO_FILE_NAME,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        return image;
    }

    @Override
    public void showCameraActivity() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create the File where the photo should go
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            // Error occurred while creating the File
            ex.printStackTrace();
        }

        // Continue only if the File was successfully created

        imagePath = photoFile.getAbsolutePath();

        Uri photoURI = FileProvider.getUriForFile(getContext(),
                BuildConfig.APPLICATION_ID + ".provider",
                photoFile);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);


        takePictureIntent.setClipData(ClipData.newRawUri("", photoURI));
        takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);


        getActivity().startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
    }

    @Override
    public void showBackButton(boolean isShowBackButton) {
        actionBar.setDisplayShowHomeEnabled(isShowBackButton);
        actionBar.setDisplayHomeAsUpEnabled(isShowBackButton);
    }

}
