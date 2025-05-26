package com.example.praktikum4.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

public class FavoritesFragment extends Fragment {
    private RecyclerView    recyclerView;
    private BookAdapter     bookAdapter;
    private TextView        emptyView;
    private ProgressBar     progressBar;
    private BookRepository  bookRepository;
    private ExecutorService executorService;
    private Handler         mainHandler;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        recyclerView    = view.findViewById(R.id.recycler_view_favorites);
        emptyView       = view.findViewById(R.id.empty_view);
        progressBar     = view.findViewById(R.id.progress_bar_favorites);

        executorService = Executors.newSingleThreadExecutor();
        mainHandler     = new Handler(Looper.getMainLooper());
        return view;
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bookRepository = BookRepository.getInstance(requireContext());

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        loadFavoriteBooks();

        // swipe delete from favorites
        new ItemTouchHelper(new SwipeToDeleteCallback(requireContext()) {
            @Override public void onSwiped(@NonNull RecyclerView.ViewHolder vh, int dir) {
                int pos = vh.getAdapterPosition();
                Book b = bookAdapter.getBookAt(pos);
                b.setFavorite(false);
                bookAdapter.removeBook(pos);
                if (bookAdapter.getItemCount()==0) {
                    recyclerView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                    emptyView.startAnimation(
                            AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in));
                }
            }
        }).attachToRecyclerView(recyclerView);
    }

    private void loadFavoriteBooks() {
        // show loader
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);

        executorService.execute(() -> {
            try { Thread.sleep(500); } catch (InterruptedException ignored) {}

            List<Book> favs = bookRepository.getFavoriteBooks();

            mainHandler.post(() -> {
                progressBar.setVisibility(View.GONE);
                if (favs.isEmpty()) {
                    emptyView.setVisibility(View.VISIBLE);
                    emptyView.startAnimation(
                            AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in));
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    bookAdapter = new BookAdapter(requireContext(), favs);
                    recyclerView.setAdapter(bookAdapter);
                    recyclerView.startAnimation(
                            AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in));
                }
            });
        });
    }

    @Override public void onResume() {
        super.onResume();
        loadFavoriteBooks();
    }

    @Override public void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
