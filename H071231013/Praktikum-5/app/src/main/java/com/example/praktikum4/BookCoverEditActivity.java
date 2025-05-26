package com.example.praktikum4;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.praktikum4.model.Book;
import com.example.praktikum4.util.BookCoverManager;

import java.io.IOException;

public class BookCoverEditActivity extends AppCompatActivity {
    private static final String TAG = "BookCoverEditActivity";
    private Book book;
    private ImageView coverImageView;
    private BookCoverManager bookCoverManager;
    private Uri selectedImageUri;

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri sourceUri = result.getData().getData();
                    Log.d(TAG, "Gallery image selected: " + sourceUri);

                    // Save a copy of the image to internal storage
                    selectedImageUri = bookCoverManager.saveImageToInternalStorage(sourceUri);
                    if (selectedImageUri != null) {
                        Log.d(TAG, "Image saved to: " + selectedImageUri);
                        // Load with Glide for consistency
                        Glide.with(this)
                                .load(selectedImageUri)
                                .placeholder(R.drawable.placeholder_cover_foreground)
                                .error(R.drawable.placeholder_cover_foreground)
                                .into(coverImageView);
                        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
                        coverImageView.startAnimation(fadeIn);
                    } else {
                        Log.e(TAG, "Failed to save image from gallery");
                        Toast.makeText(this, "Failed to process image", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Uri sourceUri = bookCoverManager.getCurrentPhotoUri();
                    Log.d(TAG, "Camera image captured: " + sourceUri);

                    selectedImageUri = bookCoverManager.saveImageToInternalStorage(sourceUri);
                    if (selectedImageUri != null) {
                        Log.d(TAG, "Image saved to: " + selectedImageUri);
                        Glide.with(this)
                                .load(selectedImageUri)
                                .placeholder(R.drawable.placeholder_cover_foreground)
                                .error(R.drawable.placeholder_cover_foreground)
                                .into(coverImageView);
                        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
                        coverImageView.startAnimation(fadeIn);
                    } else {
                        Log.e(TAG, "Failed to save image from camera");
                        Toast.makeText(this, "Failed to process image", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_cover_edit);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Book Cover");

        // Views
        coverImageView = findViewById(R.id.iv_book_cover_edit);
        Button galleryButton = findViewById(R.id.btn_choose_from_gallery);
        Button cameraButton = findViewById(R.id.btn_take_photo);
        Button saveButton   = findViewById(R.id.btn_save_cover);

        // Manager
        bookCoverManager = new BookCoverManager(this);

        // Ambil buku dari Intent
        int bookId = getIntent().getIntExtra("BOOK_ID", -1);
        if (bookId != -1) {
            book = Book.findById(bookId);
            if (book != null) {
                // Tampilkan cover awal dengan Glide
                String uriStr = book.getCoverUri();
                if (uriStr != null && !uriStr.isEmpty()) {
                    try {
                        Uri uri = Uri.parse(uriStr);
                        Glide.with(this)
                                .load(uri)
                                .placeholder(R.drawable.placeholder_cover_foreground)
                                .error(R.drawable.placeholder_cover_foreground)
                                .into(coverImageView);
                        selectedImageUri = uri;
                    } catch (Exception e) {
                        Log.e(TAG, "Error loading cover image", e);
                        coverImageView.setImageResource(R.drawable.placeholder_cover_foreground);
                    }
                } else {
                    coverImageView.setImageResource(R.drawable.placeholder_cover_foreground);
                }
            } else {
                Toast.makeText(this, "Error loading book", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(this, "Error loading book", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Listener tombol
        galleryButton.setOnClickListener(v -> {
            openGallery();
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.button_click));
        });
        cameraButton.setOnClickListener(v -> {
            openCamera();
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.button_click));
        });
        saveButton.setOnClickListener(v -> {
            saveCover();
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.button_click));
        });
    }

    private void openGallery() {
        Intent intent = bookCoverManager.createGalleryIntent();
        galleryLauncher.launch(intent);
    }

    private void openCamera() {
        try {
            Intent intent = bookCoverManager.createCameraIntent();
            if (intent != null) {
                cameraLauncher.launch(intent);
            } else {
                Toast.makeText(this, "Unable to open camera", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Log.e(TAG, "Error opening camera", e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void saveCover() {
        if (selectedImageUri != null) {
            String uriString = selectedImageUri.toString();
            book.setCoverUri(uriString);
            // TODO: persist perubahan ke database/repository di sini
            Toast.makeText(this, "Cover saved successfully", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra("BOOK_ID", book.getId());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Please select an image first", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
