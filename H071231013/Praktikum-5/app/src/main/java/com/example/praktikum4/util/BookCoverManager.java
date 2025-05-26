package com.example.praktikum4.util;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BookCoverManager {
    private final Context context;
    private Uri currentPhotoUri;
    private static final String TAG = "BookCoverManager";

    public BookCoverManager(Context context) {
        this.context = context;
    }

    public Intent createGalleryIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        return intent;
    }

    public Intent createCameraIntent() throws IOException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Create the File where the photo should go
        File photoFile = createImageFile();

        // Continue only if the File was successfully created
        if (photoFile != null) {
            currentPhotoUri = FileProvider.getUriForFile(
                    context,
                    "com.example.praktikum4.fileprovider",
                    photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoUri);
            return takePictureIntent;
        }

        return null;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
    }

    public Uri getCurrentPhotoUri() {
        return currentPhotoUri;
    }

    public void setCurrentPhotoUri(Uri uri) {
        this.currentPhotoUri = uri;
    }

    // Method to save a copy of the selected image to app's private storage
    public Uri saveImageToInternalStorage(Uri sourceUri) {
        try {
            // Get input stream from the content resolver
            InputStream inputStream = context.getContentResolver().openInputStream(sourceUri);
            if (inputStream == null) {
                Log.e(TAG, "Failed to open input stream from URI: " + sourceUri);
                Toast.makeText(context, "Failed to process image", Toast.LENGTH_SHORT).show();
                return null;
            }

            // Decode the image
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();

            if (bitmap == null) {
                Log.e(TAG, "Failed to decode bitmap from URI: " + sourceUri);
                Toast.makeText(context, "Failed to process image", Toast.LENGTH_SHORT).show();
                return null;
            }

            // Create a file to save the image
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String imageFileName = "BOOK_COVER_" + timeStamp + ".jpg";
            File storageDir = new File(context.getFilesDir(), "book_covers");

            if (!storageDir.exists()) {
                boolean dirCreated = storageDir.mkdirs();
                if (!dirCreated) {
                    Log.e(TAG, "Failed to create directory: " + storageDir.getAbsolutePath());
                    Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show();
                    return null;
                }
            }

            File imageFile = new File(storageDir, imageFileName);
            FileOutputStream fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.flush();
            fos.close();

            Log.d(TAG, "Image saved successfully at: " + imageFile.getAbsolutePath());

            // Return the URI for the saved image
            return Uri.fromFile(imageFile);

        } catch (Exception e) {
            Log.e(TAG, "Error saving image: " + e.getMessage(), e);
            Toast.makeText(context, "Error saving image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    // Get real path from URI
    public String getRealPathFromURI(Uri contentUri) {
        String result = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                result = cursor.getString(column_index);
            }
            cursor.close();
        }

        if (result == null) {
            result = "Not found";
        }

        return result;
    }
}