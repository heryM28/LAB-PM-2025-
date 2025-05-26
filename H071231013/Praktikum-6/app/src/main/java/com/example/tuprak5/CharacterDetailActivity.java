package com.example.tuprak5;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tuprak5.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

// Activity untuk menampilkan detail karakter
public class CharacterDetailActivity extends AppCompatActivity {

    // ImageView untuk menampilkan gambar karakter
    private ImageView characterImage;
    // TextView untuk nama karakter
    private TextView nameTextView;
    // TextView untuk status karakter
    private TextView statusTextView;
    // TextView untuk spesies karakter
    private TextView speciesTextView;
    // Pesan yang muncul saat offline
    private TextView offlineMessageView;
    // ProgressBar saat memuat data dari jaringan
    private ProgressBar progressBar;

    // Menyimpan ID karakter yang diterima dari Intent
    private int characterId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Menggunakan layout untuk menampilkan detail karakter
        setContentView(R.layout.activity_character_detail);

        // Inisialisasi view dari layout
        characterImage = findViewById(R.id.detail_character_image);
        nameTextView = findViewById(R.id.detail_character_name);
        statusTextView = findViewById(R.id.detail_character_status);
        speciesTextView = findViewById(R.id.detail_character_species);
        offlineMessageView = findViewById(R.id.detail_offline_message);
        progressBar = findViewById(R.id.detail_progress_bar);

        // Ambil data karakter yang diteruskan dari Activity sebelumnya
        characterId = getIntent().getIntExtra("character_id", -1);
        String name = getIntent().getStringExtra("character_name");
        String status = getIntent().getStringExtra("character_status");
        String species = getIntent().getStringExtra("character_species");
        String imageUrl = getIntent().getStringExtra("character_image");

        // Cek koneksi internet sebelum memuat data
        if (NetworkUtils.isNetworkAvailable(this)) {
            // Jika online, sembunyikan pesan offline
            offlineMessageView.setVisibility(View.GONE);
            // Tampilkan nama, status, dan spesies pada TextView
            nameTextView.setText(name);
            statusTextView.setText("Status: " + status);
            speciesTextView.setText("Species: " + species);

            // Muat gambar menggunakan Picasso
            Picasso.get()
                    .load(imageUrl)                          // URL gambar karakter
                    .placeholder(R.drawable.ic_launcher_foreground) // placeholder sambil load
                    .error(R.drawable.ic_launcher_background)       // gambar jika load gagal
                    .into(characterImage);                         // target ImageView
        } else {
            // Jika offline, sembunyikan ProgressBar dan tampilkan pesan
            progressBar.setVisibility(View.GONE);
            offlineMessageView.setVisibility(View.VISIBLE);
            // Tampilkan Toast notifikasi
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }
}
