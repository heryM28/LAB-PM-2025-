package com.example.tuprak5.service;

import android.os.Handler;
import android.os.Looper;

import com.example.tuprak5.model.Character;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Service untuk mengambil data karakter dari API Rick and Morty.
 */
public class ApiService {
    // URL dasar endpoint API karakter
    private static final String BASE_URL = "https://rickandmortyapi.com/api/character";
    // Klien HTTP OkHttp untuk melakukan permintaan jaringan
    private final OkHttpClient client = new OkHttpClient();
    // Handler untuk mengirim hasil panggilan kembali ke thread utama (UI)
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    /**
     * Callback yang dipanggil saat permintaan API selesai.
     */
    public interface ApiCallback {
        /**
         * Dipanggil saat data berhasil diambil.
         *
         * @param characters Daftar karakter yang diambil
         * @param nextPage   Halaman berikutnya, -1 jika tidak ada
         */
        void onSuccess(List<Character> characters, int nextPage);

        /**
         * Dipanggil saat terjadi kesalahan.
         *
         * @param errorMessage Pesan kesalahan
         */
        void onError(String errorMessage);
    }

    /**
     * Mengambil daftar karakter dari API berdasarkan nomor halaman.
     *
     * @param page     Nomor halaman yang akan diambil
     * @param callback Callback untuk menangani hasil
     */
    public void getCharacters(int page, final ApiCallback callback) {
        // Buat URL lengkap dengan parameter halaman
        String url = BASE_URL + "?page=" + page;

        // Membangun objek Request untuk OkHttp
        Request request = new Request.Builder()
                .url(url)
                .build();

        // Enqueue permintaan secara asinkron
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Jika gagal (misal: jaringan), kirim error ke UI thread
                mainHandler.post(() -> callback.onError("Network error: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // Cek kode respons, jika tidak sukses kirim error
                if (!response.isSuccessful()) {
                    mainHandler.post(() ->
                            callback.onError("API error: " + response.code()));
                    return;
                }

                try {
                    // Baca body respons sebagai string
                    String jsonData = response.body().string();
                    // Parse string menjadi JSONObject
                    JSONObject jsonObject = new JSONObject(jsonData);
                    // Ambil objek 'info' untuk paging
                    JSONObject info = jsonObject.getJSONObject("info");

                    // Cek apakah ada halaman berikutnya
                    int nextPage = -1;
                    if (!info.isNull("next")) {
                        String nextUrl = info.getString("next");
                        if (nextUrl != null && !nextUrl.isEmpty()) {
                            // Extract nomor halaman dari URL next
                            String[] parts = nextUrl.split("page=");
                            if (parts.length > 1) {
                                nextPage = Integer.parseInt(parts[1]);
                            }
                        }
                    }
                    // Salin nilai nextPage untuk digunakan dalam lambda
                    final int finalNextPage = nextPage;

                    // Ambil array 'results' berisi data karakter
                    JSONArray results = jsonObject.getJSONArray("results");
                    List<Character> characters = new ArrayList<>();

                    // Iterasi setiap elemen di array
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject characterObj = results.getJSONObject(i);
                        int id = characterObj.getInt("id");
                        String name = characterObj.getString("name");
                        String status = characterObj.getString("status");
                        String species = characterObj.getString("species");
                        String image = characterObj.getString("image");

                        // Buat objek Character dan tambahkan ke list
                        Character character = new Character(id, name, status, species, image);
                        characters.add(character);
                    }

                    // Kirim hasil sukses ke UI thread dengan data karakter dan nextPage
                    mainHandler.post(() -> callback.onSuccess(characters, finalNextPage));

                } catch (JSONException e) {
                    // Tangani kesalahan parsing JSON
                    mainHandler.post(() ->
                            callback.onError("JSON parsing error: " + e.getMessage()));
                }
            }
        });
    }
}
