package com.example.praktikum4;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.praktikum4.model.Book;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class DetailActivity extends AppCompatActivity {
    private static final String TAG = "DetailActivity";
    private Book book;
    private ImageView coverImageView;
    private TextView titleTextView;
    private TextView authorTextView;
    private TextView yearTextView;
    private TextView blurbTextView;
    private TextView genreTextView;
    private RatingBar ratingBar;
    private FloatingActionButton fabFavorite;
    private FloatingActionButton fabEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Book Details");

        coverImageView = findViewById(R.id.iv_book_cover_detail);
        titleTextView = findViewById(R.id.tv_book_title_detail);
        authorTextView = findViewById(R.id.tv_book_author_detail);
        yearTextView = findViewById(R.id.tv_book_year_detail);
        blurbTextView = findViewById(R.id.tv_book_blurb_detail);
        genreTextView = findViewById(R.id.tv_book_genre_detail);
        ratingBar = findViewById(R.id.rating_bar_detail);
        fabFavorite = findViewById(R.id.fab_favorite);
        fabEdit = findViewById(R.id.fab_edit);

        int bookId = getIntent().getIntExtra("BOOK_ID", -1);
        if (bookId != -1) {
            book = Book.findById(bookId);
            if (book != null) {
                Log.d(TAG, "Loaded book: " + book.getTitle() + ", Cover URI: " + book.getCoverUri());
                displayBookDetails();
                setupFabButtons();
            } else {
                Log.e(TAG, "Book not found with ID: " + bookId);
                Toast.makeText(this, "Error loading book details", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Log.e(TAG, "Invalid book ID");
            Toast.makeText(this, "Error loading book details", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void displayBookDetails() {
        titleTextView.setText(book.getTitle());
        authorTextView.setText(book.getAuthor());
        yearTextView.setText(String.valueOf(book.getYear()));
        blurbTextView.setText(book.getBlurb());
        genreTextView.setText(book.getGenre());
        ratingBar.setRating(book.getRating());

        // === REVISI: muat cover dengan Glide ===
        String uriStr = book.getCoverUri();
        if (uriStr != null && !uriStr.isEmpty()) {
            try {
                Uri coverUri = Uri.parse(uriStr);
                Log.d(TAG, "Loading cover image from URI: " + coverUri);
                Glide.with(this)
                        .load(coverUri)
                        .placeholder(R.drawable.placeholder_cover_foreground)
                        .error(R.drawable.placeholder_cover_foreground)
                        .into(coverImageView);
            } catch (Exception e) {
                Log.e(TAG, "Error loading cover image: " + e.getMessage(), e);
                coverImageView.setImageResource(R.drawable.placeholder_cover_foreground);
            }
        } else {
            coverImageView.setImageResource(R.drawable.placeholder_cover_foreground);
        }

        updateFavoriteButton();
    }

    private void setupFabButtons() {
        fabFavorite.setOnClickListener(v -> {
            book.toggleFavorite();
            updateFavoriteButton();
            Animation pulseAnimation = AnimationUtils.loadAnimation(this, R.anim.pulse_animation);
            fabFavorite.startAnimation(pulseAnimation);
            String message = book.isFavorite() ? "Added to favorites" : "Removed from favorites";
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        });

        fabEdit.setOnClickListener(v -> {
            Log.d(TAG, "Edit button clicked for book: " + book.getId());
            Intent intent = new Intent(this, BookCoverEditActivity.class);
            intent.putExtra("BOOK_ID", book.getId());
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }

    private void updateFavoriteButton() {
        if (book.isFavorite()) {
            fabFavorite.setImageResource(R.drawable.ic_favorite);
            fabFavorite.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccent));
        } else {
            fabFavorite.setImageResource(R.drawable.ic_favorite_outline);
            fabFavorite.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));
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
    protected void onResume() {
        super.onResume();
        if (book != null) {
            Book updatedBook = Book.findById(book.getId());
            if (updatedBook != null) {
                book = updatedBook;
                displayBookDetails();
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
