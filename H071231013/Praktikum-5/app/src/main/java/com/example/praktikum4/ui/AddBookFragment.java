package com.example.praktikum4.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.praktikum4.R;
import com.example.praktikum4.model.Book;
import com.example.praktikum4.repo.BookRepository;
import com.example.praktikum4.util.BookCoverManager;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

public class AddBookFragment extends Fragment {
    private static final String TAG = "AddBookFragment";
    private EditText titleEditText;
    private EditText authorEditText;
    private EditText yearEditText;
    private EditText blurbEditText;
    private ImageView coverImageView;
    private Spinner genreSpinner;
    private RatingBar ratingBar;
    private TextInputLayout titleLayout;
    private TextInputLayout authorLayout;
    private TextInputLayout yearLayout;

    private BookRepository bookRepository;
    private BookCoverManager bookCoverManager;
    private Uri selectedImageUri;

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                    Uri sourceUri = result.getData().getData();
                    Log.d(TAG, "Gallery image selected: " + sourceUri);

                    // Save a copy of the image to internal storage
                    selectedImageUri = bookCoverManager.saveImageToInternalStorage(sourceUri);

                    if (selectedImageUri != null) {
                        Log.d(TAG, "Image saved to: " + selectedImageUri);
                        coverImageView.setImageURI(selectedImageUri);

                        // Add animation
                        Animation fadeIn = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in);
                        coverImageView.startAnimation(fadeIn);
                    } else {
                        Log.e(TAG, "Failed to save image from gallery");
                        Toast.makeText(requireContext(), "Failed to process image", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == getActivity().RESULT_OK) {
                    Uri sourceUri = bookCoverManager.getCurrentPhotoUri();
                    Log.d(TAG, "Camera image captured: " + sourceUri);

                    // Save a copy of the image to internal storage
                    selectedImageUri = bookCoverManager.saveImageToInternalStorage(sourceUri);

                    if (selectedImageUri != null) {
                        Log.d(TAG, "Image saved to: " + selectedImageUri);
                        coverImageView.setImageURI(selectedImageUri);

                        // Add animation
                        Animation fadeIn = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in);
                        coverImageView.startAnimation(fadeIn);
                    } else {
                        Log.e(TAG, "Failed to save image from camera");
                        Toast.makeText(requireContext(), "Failed to process image", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_book, container, false);

        // Initialize views
        titleEditText = view.findViewById(R.id.et_title);
        authorEditText = view.findViewById(R.id.et_author);
        yearEditText = view.findViewById(R.id.et_year);
        blurbEditText = view.findViewById(R.id.et_blurb);
        coverImageView = view.findViewById(R.id.iv_book_cover_add);
        genreSpinner = view.findViewById(R.id.spinner_genre);
        ratingBar = view.findViewById(R.id.rating_bar_add);
        Button galleryButton = view.findViewById(R.id.btn_choose_from_gallery);
        Button cameraButton = view.findViewById(R.id.btn_take_photo);
        Button addButton = view.findViewById(R.id.btn_add_book);
        titleLayout = view.findViewById(R.id.til_title);
        authorLayout = view.findViewById(R.id.til_author);
        yearLayout = view.findViewById(R.id.til_year);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize book repository and cover manager
        bookRepository = BookRepository.getInstance(requireContext());
        bookCoverManager = new BookCoverManager(requireContext());

        // Set up genre spinner
        setupGenreSpinner();

        // Set default year to current year
        yearEditText.setText(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));

        // Set up button click listeners
        view.findViewById(R.id.btn_choose_from_gallery).setOnClickListener(v -> {
            openGallery();
            // Add button animation
            v.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.button_click));
        });

        view.findViewById(R.id.btn_take_photo).setOnClickListener(v -> {
            openCamera();
            // Add button animation
            v.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.button_click));
        });

        view.findViewById(R.id.btn_add_book).setOnClickListener(v -> {
            addBook();
            // Add button animation
            v.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.button_click));
        });

        // Add animation to form
        Animation slideUp = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up);
        view.findViewById(R.id.add_book_form).startAnimation(slideUp);
    }

    private void setupGenreSpinner() {
        try {
            List<String> genres = bookRepository.getGenres();
            if (genres != null && !genres.isEmpty()) {
                ArrayAdapter<String> genreAdapter = new ArrayAdapter<>(
                        requireContext(),
                        R.layout.spinner_item,
                        genres
                );
                genreAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                genreSpinner.setAdapter(genreAdapter);
                Log.d(TAG, "Genre spinner set up with " + genres.size() + " genres");
            } else {
                Log.e(TAG, "Genre list is empty or null");
                Toast.makeText(requireContext(), "Error loading genres", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting up genre spinner: " + e.getMessage(), e);
            Toast.makeText(requireContext(), "Error setting up genre spinner", Toast.LENGTH_SHORT).show();
        }
    }

    private void openGallery() {
        Intent galleryIntent = bookCoverManager.createGalleryIntent();
        galleryLauncher.launch(galleryIntent);
    }

    private void openCamera() {
        try {
            Intent cameraIntent = bookCoverManager.createCameraIntent();
            if (cameraIntent != null) {
                cameraLauncher.launch(cameraIntent);
            } else {
                Toast.makeText(requireContext(), "Unable to open camera", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Log.e(TAG, "Error opening camera: " + e.getMessage(), e);
            Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void addBook() {
        // Validate input
        if (!validateInput()) {
            return;
        }

        // Get input values
        String title = titleEditText.getText().toString().trim();
        String author = authorEditText.getText().toString().trim();
        int year = Integer.parseInt(yearEditText.getText().toString().trim());
        String blurb = blurbEditText.getText().toString().trim();
        String genre = genreSpinner.getSelectedItem().toString();
        float rating = ratingBar.getRating();

        // Set default cover if none selected
        String coverUri = (selectedImageUri != null) ?
                selectedImageUri.toString() :
                "android.resource://com.example.praktikum4/drawable/placeholder_cover_foreground";

        Log.d(TAG, "Adding new book with cover URI: " + coverUri);

        // Create new book
        Book newBook = new Book(title, author, year, blurb, coverUri, genre, rating);

        // Add book to repository
        bookRepository.addBook(newBook);

        // Show success message
        Toast.makeText(requireContext(), "Book added successfully", Toast.LENGTH_SHORT).show();

        // Clear form
        clearForm();

        // Add success animation
        Animation successAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.success_animation);
        coverImageView.startAnimation(successAnim);
    }

    private boolean validateInput() {
        boolean isValid = true;

        // Validate title
        if (titleEditText.getText().toString().trim().isEmpty()) {
            titleLayout.setError("Title is required");
            isValid = false;
        } else {
            titleLayout.setError(null);
        }

        // Validate author
        if (authorEditText.getText().toString().trim().isEmpty()) {
            authorLayout.setError("Author is required");
            isValid = false;
        } else {
            authorLayout.setError(null);
        }

        // Validate year
        String yearStr = yearEditText.getText().toString().trim();
        if (yearStr.isEmpty()) {
            yearLayout.setError("Year is required");
            isValid = false;
        } else {
            try {
                int year = Integer.parseInt(yearStr);
                int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                if (year < 0 || year > currentYear) {
                    yearLayout.setError("Year must be between 0 and " + currentYear);
                    isValid = false;
                } else {
                    yearLayout.setError(null);
                }
            } catch (NumberFormatException e) {
                yearLayout.setError("Invalid year format");
                isValid = false;
            }
        }

        // Validate genre
        if (genreSpinner.getSelectedItem() == null) {
            Toast.makeText(requireContext(), "Please select a genre", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        return isValid;
    }

    private void clearForm() {
        titleEditText.setText("");
        authorEditText.setText("");
        yearEditText.setText(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
        blurbEditText.setText("");
        coverImageView.setImageResource(R.drawable.placeholder_cover_foreground);
        if (genreSpinner.getAdapter() != null && genreSpinner.getAdapter().getCount() > 0) {
            genreSpinner.setSelection(0);
        }
        ratingBar.setRating(0);
        selectedImageUri = null;

        // Clear errors
        titleLayout.setError(null);
        authorLayout.setError(null);
        yearLayout.setError(null);
    }
}