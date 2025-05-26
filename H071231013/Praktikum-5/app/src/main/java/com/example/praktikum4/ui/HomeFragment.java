package com.example.praktikum4.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.praktikum4.R;
import com.example.praktikum4.adapter.BookAdapter;
import com.example.praktikum4.model.Book;
import com.example.praktikum4.repo.BookRepository;
import com.example.praktikum4.util.SwipeToDeleteCallback;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeFragment extends Fragment {
    private RecyclerView    recyclerView;
    private BookAdapter     bookAdapter;
    private BookRepository  bookRepository;
    private SearchView      searchView;
    private ProgressBar     progressBar;
    private Spinner         genreSpinner;
    private ExecutorService executorService;
    private Handler         mainHandler;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView    = view.findViewById(R.id.recycler_view);
        searchView      = view.findViewById(R.id.search_view);
        progressBar     = view.findViewById(R.id.progress_bar);
        genreSpinner    = view.findViewById(R.id.spinner_genre);

        executorService = Executors.newSingleThreadExecutor();
        mainHandler     = new Handler(Looper.getMainLooper());
        return view;
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bookRepository = BookRepository.getInstance(requireContext());

        // RecyclerView + Adapter
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        bookAdapter = new BookAdapter(requireContext(), bookRepository.getAllBooks());
        recyclerView.setAdapter(bookAdapter);

        // Swipe to delete
        new ItemTouchHelper(new SwipeToDeleteCallback(requireContext()) {
            @Override public void onSwiped(@NonNull RecyclerView.ViewHolder vh, int dir) {
                int pos = vh.getAdapterPosition();
                Book b = bookAdapter.getBookAt(pos);
                bookRepository.removeBook(b);
                bookAdapter.removeBook(pos);
            }
        }).attachToRecyclerView(recyclerView);

        // Search
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String q) { searchBooks(q); return true; }
            @Override public boolean onQueryTextChange(String q) { searchBooks(q); return true; }
        });

        // Genre Spinner
        List<String> genres = bookRepository.getGenres();
        genres.add(0, "All Genres");
        ArrayAdapter<String> ga = new ArrayAdapter<>(requireContext(),
                R.layout.spinner_item, genres);
        ga.setDropDownViewResource(R.layout.spinner_dropdown_item);
        genreSpinner.setAdapter(ga);
        genreSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                filterByGenre(p.getItemAtPosition(pos).toString());
            }
            @Override public void onNothingSelected(AdapterView<?> p) {}
        });
    }

    private void searchBooks(String query) {
        // 1) show loader
        progressBar.setVisibility(View.VISIBLE);

        // 2) run in background
        executorService.execute(() -> {
            try { Thread.sleep(500); } catch (InterruptedException ignored) {}

            List<Book> result = query.isEmpty()
                    ? bookRepository.getAllBooks()
                    : bookRepository.searchBooks(query);

            // 3) back to UI
            mainHandler.post(() -> {
                bookAdapter.updateBooks(result);
                progressBar.setVisibility(View.GONE);
                Animation fade = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in);
                recyclerView.startAnimation(fade);
            });
        });
    }

    private void filterByGenre(String genre) {
        progressBar.setVisibility(View.VISIBLE);

        executorService.execute(() -> {
            List<Book> list = genre.equals("All Genres")
                    ? bookRepository.getAllBooks()
                    : bookRepository.getBooksByGenre(genre);

            mainHandler.post(() -> {
                bookAdapter.updateBooks(list);
                progressBar.setVisibility(View.GONE);
                Animation fade = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in);
                recyclerView.startAnimation(fade);
            });
        });
    }

    @Override public void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
