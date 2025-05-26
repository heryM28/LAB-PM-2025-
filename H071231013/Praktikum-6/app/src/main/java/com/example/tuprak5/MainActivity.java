package com.example.tuprak5;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tuprak5.model.Character;
import com.example.tuprak5.service.ApiService;
import com.example.tuprak5.ui.CharacterAdapter;
import com.example.tuprak5.utils.NetworkUtils;

import java.util.List;

// Activity utama untuk menampilkan daftar karakter
public class MainActivity extends AppCompatActivity {

    // RecyclerView untuk daftar karakter
    private RecyclerView recyclerView;
    // Adapter untuk RecyclerView
    private CharacterAdapter adapter;
    // Service untuk memanggil API
    private ApiService apiService;
    // Tombol untuk memuat halaman selanjutnya
    private Button loadMoreButton;
    // ProgressBar saat memuat data
    private ProgressBar progressBar;
    // TextView untuk pesan saat offline
    private TextView offlineMessageView;

    // Halaman data saat ini
    private int currentPage = 1;
    // Nomor halaman berikutnya, -1 jika tidak ada
    private int nextPage = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Aktifkan mode edge-to-edge agar konten tampil di balik sistem bars
        EdgeToEdge.enable(this);
        // Atur layout activity
        setContentView(R.layout.activity_main);

        // Inisialisasi view dari layout
        recyclerView = findViewById(R.id.recycler_view);
        loadMoreButton = findViewById(R.id.load_more_button);
        progressBar = findViewById(R.id.progress_bar);
        offlineMessageView = findViewById(R.id.offline_message);

        // Setup RecyclerView dengan layout linear
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CharacterAdapter();
        recyclerView.setAdapter(adapter);

        // Inisialisasi ApiService untuk panggilan API
        apiService = new ApiService();

        // Set listener pada tombol "Load More"
        loadMoreButton.setOnClickListener(v -> {
            if (nextPage > 0) {
                // Jika ada halaman berikutnya, update currentPage dan muat data
                currentPage = nextPage;
                loadCharacters(currentPage);
            }
        });

        // Muat data awal dengan cek jaringan
        checkNetworkAndLoadData();

        // Atur padding untuk edge-to-edge sesuai inset sistem
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Periksa jaringan kembali saat activity muncul kembali
        checkNetworkAndLoadData();
    }

    /**
     * Cek koneksi internet dan muat data jika online
     */
    private void checkNetworkAndLoadData() {
        if (NetworkUtils.isNetworkAvailable(this)) {
            // Jika online, sembunyikan pesan offline dan tampilkan RecyclerView
            offlineMessageView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            // Hanya muat data awal jika belum ada data
            if (adapter.getItemCount() == 0) {
                loadCharacters(currentPage);
            }
        } else {
            // Jika offline, sembunyikan ProgressBar, RecyclerView, dan tombol load more
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            loadMoreButton.setVisibility(View.GONE);
            // Tampilkan pesan offline
            offlineMessageView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Memuat karakter dari API berdasarkan halaman
     * @param page Nomor halaman yang ingin dimuat
     */
    private void loadCharacters(int page) {
        // Tampilkan ProgressBar dan sembunyikan tombol load more
        progressBar.setVisibility(View.VISIBLE);
        loadMoreButton.setVisibility(View.GONE);

        // Panggil API untuk mendapatkan data karakter
        apiService.getCharacters(page, new ApiService.ApiCallback() {
            @Override
            public void onSuccess(List<Character> characters, int nextPageNum) {
                // Sembunyikan ProgressBar setelah data diterima
                progressBar.setVisibility(View.GONE);

                if (page == 1) {
                    // Jika halaman pertama, set data baru
                    adapter.setCharacters(characters);
                } else {
                    // Jika bukan halaman pertama, tambahkan data
                    adapter.addCharacters(characters);
                }

                // Update nomor halaman berikutnya
                nextPage = nextPageNum;

                // Tampilkan tombol load more jika masih ada halaman
                loadMoreButton.setVisibility(nextPage > 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onError(String errorMessage) {
                // Sembunyikan ProgressBar jika error
                progressBar.setVisibility(View.GONE);
                // Tampilkan toast dengan pesan error
                Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();

                if (adapter.getItemCount() == 0) {
                    // Jika data kosong (gagal load pertama), tampilkan pesan offline
                    recyclerView.setVisibility(View.GONE);
                    offlineMessageView.setVisibility(View.VISIBLE);
                } else {
                    // Jika data sudah ada sebelumnya, sembunyikan tombol load more saja
                    loadMoreButton.setVisibility(View.GONE);
                }
            }
        });
    }
}