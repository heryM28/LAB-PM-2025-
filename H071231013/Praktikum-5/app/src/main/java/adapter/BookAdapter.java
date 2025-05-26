package com.example.praktikum4.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.praktikum4.DetailActivity;
import com.example.praktikum4.R;
import com.example.praktikum4.model.Book;

import java.util.List;

/**
 * Adapter untuk menampilkan daftar buku dalam RecyclerView.
 */
public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {
    // List buku yang akan ditampilkan
    private List<Book> books;
    private final Context context;
    // Posisi terakhir yang dianimasikan untuk efek masuk
    private int lastPosition = -1;

    /**
     * Konstruktor adapter.
     * @param context Context aplikasi
     * @param books   Daftar buku
     */
    public BookAdapter(Context context, List<Book> books) {
        this.context = context;
        this.books = books;
    }

    /**
     * Membuat ViewHolder baru ketika RecyclerView membutuhkan.
     */
    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }

    /**
     * Mengikat data buku ke ViewHolder pada posisi tertentu.
     */
    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = books.get(position);

        // Set judul, penulis, dan tahun terbit
        holder.titleTextView.setText(book.getTitle());
        holder.authorTextView.setText(book.getAuthor());
        holder.yearTextView.setText(String.valueOf(book.getYear()));

        // Tampilkan ikon favorit sesuai status
        updateFavoriteIcon(holder.favoriteIcon, book.isFavorite());

        // === REVISI: muat cover dengan Glide ===
        String uri = book.getCoverUri();
        if (uri != null && !uri.isEmpty()) {
            Glide.with(context)
                    .load(uri)
                    .placeholder(R.drawable.placeholder_cover_foreground)
                    .error(R.drawable.placeholder_cover_foreground)
                    .into(holder.coverImageView);
        } else {
            holder.coverImageView.setImageResource(R.drawable.placeholder_cover_foreground);
        }

        // Set genre dan rating buku
        holder.genreTextView.setText(book.getGenre());
        holder.ratingTextView.setText(String.format("%.1f★", book.getRating()));

        // Listener untuk tombol favorit
        holder.favoriteIcon.setOnClickListener(v -> {
            book.toggleFavorite();
            updateFavoriteIcon(holder.favoriteIcon, book.isFavorite());

            Animation pulseAnimation = AnimationUtils.loadAnimation(context, R.anim.pulse_animation);
            holder.favoriteIcon.startAnimation(pulseAnimation);
        });

        // Listener klik pada item buku untuk membuka DetailActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("BOOK_ID", book.getId());
            context.startActivity(intent);
        });

        // Tambahkan animasi masuk pada item saat muncul
        setAnimation(holder.itemView, position);
    }

    /**
     * Memperbarui ikon favorit berdasarkan status.
     */
    private void updateFavoriteIcon(ImageView imageView, boolean isFavorite) {
        if (isFavorite) {
            imageView.setImageResource(R.drawable.ic_favorite);
            imageView.setColorFilter(context.getResources().getColor(R.color.colorAccent));
        } else {
            imageView.setImageResource(R.drawable.ic_favorite_outline);
            imageView.setColorFilter(context.getResources().getColor(R.color.colorGray));
        }
    }

    /**
     * Menambahkan animasi "fall down" pada item yang baru muncul.
     */
    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.item_animation_fall_down);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    /**
     * Memperbarui daftar buku dan me-reset animasi.
     */
    public void updateBooks(List<Book> newBooks) {
        this.books = newBooks;
        notifyDataSetChanged();
        lastPosition = -1;
    }

    /**
     * Menghapus buku pada posisi tertentu.
     */
    public void removeBook(int position) {
        books.remove(position);
        notifyItemRemoved(position);
    }

    /**
     * Mendapatkan objek Book di posisi tertentu.
     */
    public Book getBookAt(int position) {
        return books.get(position);
    }

    /**
     * ViewHolder untuk item buku.
     */
    static class BookViewHolder extends RecyclerView.ViewHolder {
        ImageView coverImageView;
        TextView titleTextView;
        TextView authorTextView;
        TextView yearTextView;
        ImageView favoriteIcon;
        TextView genreTextView;
        TextView ratingTextView;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            coverImageView = itemView.findViewById(R.id.iv_book_cover);
            titleTextView = itemView.findViewById(R.id.tv_book_title);
            authorTextView = itemView.findViewById(R.id.tv_book_author);
            yearTextView = itemView.findViewById(R.id.tv_book_year);
            favoriteIcon = itemView.findViewById(R.id.iv_favorite);
            genreTextView = itemView.findViewById(R.id.tv_book_genre);
            ratingTextView = itemView.findViewById(R.id.tv_book_rating);
        }
    }
}