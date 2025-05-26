package com.example.tuprak5.ui;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tuprak5.CharacterDetailActivity;
import com.example.tuprak5.R;
import com.example.tuprak5.model.Character;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

// Adapter untuk menampilkan daftar karakter dalam RecyclerView
public class CharacterAdapter extends RecyclerView.Adapter<CharacterAdapter.CharacterViewHolder> {

    // Daftar karakter yang akan ditampilkan
    private List<Character> characters = new ArrayList<>();

    /**
     * Mengatur ulang daftar karakter dan memberi tahu adapter untuk refresh tampilan
     * @param characters List karakter baru
     */
    public void setCharacters(List<Character> characters) {
        this.characters = characters; // assign list baru
        notifyDataSetChanged();       // beri tahu data berubah
    }

    /**
     * Menambahkan karakter tambahan ke daftar tanpa menghapus yang lama
     * @param newCharacters List karakter yang akan ditambahkan
     */
    public void addCharacters(List<Character> newCharacters) {
        int startPosition = characters.size();   // posisi awal insert
        this.characters.addAll(newCharacters);   // tambahkan semua
        notifyItemRangeInserted(startPosition, newCharacters.size()); // beri tahu rentang baru
    }

    /**
     * Membuat ViewHolder baru saat diperlukan oleh RecyclerView
     * @param parent ViewGroup induk
     * @param viewType Tipe tampilan (jika lebih dari satu jenis item)
     * @return CharacterViewHolder baru
     */
    @NonNull
    @Override
    public CharacterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout item karakter
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.character_item, parent, false);
        return new CharacterViewHolder(view);
    }

    /**
     * Mengikat data karakter ke ViewHolder pada posisi tertentu
     * @param holder ViewHolder tempat data akan di-bind
     * @param position Posisi data dalam list
     */
    @Override
    public void onBindViewHolder(@NonNull CharacterViewHolder holder, int position) {
        Character character = characters.get(position); // ambil karakter pada posisi
        holder.bind(character);                          // bind data ke view
    }

    /**
     * Mengembalikan jumlah item dalam adapter
     * @return jumlah karakter
     */
    @Override
    public int getItemCount() {
        return characters.size(); // kembalikan ukuran list
    }

    /**
     * ViewHolder untuk menampung tampilan item karakter
     */
    static class CharacterViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;        // ImageView untuk foto karakter
        private final TextView nameTextView;     // TextView untuk nama
        private final TextView statusTextView;   // TextView untuk status
        private final TextView speciesTextView;  // TextView untuk spesies

        public CharacterViewHolder(@NonNull View itemView) {
            super(itemView);
            // Inisialisasi view dari layout
            imageView = itemView.findViewById(R.id.character_image);
            nameTextView = itemView.findViewById(R.id.character_name);
            statusTextView = itemView.findViewById(R.id.character_status);
            speciesTextView = itemView.findViewById(R.id.character_species);
        }

        /**
         * Melakukan bind data karakter ke tampilan
         * @param character objek karakter yang akan ditampilkan
         */
        public void bind(Character character) {
            // Set teks nama, status, dan spesies
            nameTextView.setText(character.getName());          // set nama karakter
            statusTextView.setText(character.getStatus());      // set status (Alive, Dead, etc.)
            speciesTextView.setText(character.getSpecies());    // set spesies karakter

            // Load gambar karakter dengan Picasso
            Picasso.get()
                    .load(character.getImage())                  // URL gambar
                    .placeholder(R.drawable.ic_launcher_foreground) // placeholder selama load
                    .error(R.drawable.ic_launcher_background)      // gambar jika error
                    .into(imageView);                             // target ImageView

            // Set listener ketika item diklik untuk membuka detail activity
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(itemView.getContext(), CharacterDetailActivity.class);
                // Kirim data karakter ke activity detail
                intent.putExtra("character_id", character.getId());
                intent.putExtra("character_name", character.getName());
                intent.putExtra("character_status", character.getStatus());
                intent.putExtra("character_species", character.getSpecies());
                intent.putExtra("character_image", character.getImage());
                itemView.getContext().startActivity(intent);   // mulai activity detail
            });
        }
    }
}