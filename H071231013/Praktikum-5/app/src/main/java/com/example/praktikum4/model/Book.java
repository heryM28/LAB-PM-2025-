package com.example.praktikum4.model;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Book implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String TAG = "Book";
    private static int nextId = 1;
    // Static list untuk menyimpan semua buku (sebagai pengganti database)
    private static List<Book> allBooks = new ArrayList<>();

    private int id;
    private String title;
    private String author;
    private int year;
    private String blurb;
    private String coverUri;
    private boolean isFavorite;
    private String genre;
    private float rating;

    public Book(String title, String author, int year, String blurb, String coverUri, String genre, float rating) {
        this.id = nextId++;
        this.title = title;
        this.author = author;
        this.year = year;
        this.blurb = blurb;
        this.coverUri = coverUri;
        this.isFavorite = false;
        this.genre = genre;
        this.rating = rating;

        Log.d(TAG, "Created new book: " + title + " with cover URI: " + coverUri);
    }

    // Static method untuk menambahkan buku ke list statis
    public static void addToStaticList(Book book) {
        allBooks.add(book);
        Log.d(TAG, "Added book to static list: " + book.getTitle() + " (ID: " + book.getId() + ")");
    }

    // Static method untuk mendapatkan semua buku
    public static List<Book> getAllBooks() {
        return new ArrayList<>(allBooks);
    }

    // Static method untuk mencari buku berdasarkan ID
    public static Book findById(int id) {
        for (Book book : allBooks) {
            if (book.getId() == id) {
                return book;
            }
        }
        Log.e(TAG, "Book not found with ID: " + id);
        return null;
    }

    // Static method untuk menghapus buku dari list statis
    public static void removeFromStaticList(Book book) {
        allBooks.remove(book);
        Log.d(TAG, "Removed book from static list: " + book.getTitle() + " (ID: " + book.getId() + ")");
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getBlurb() {
        return blurb;
    }

    public void setBlurb(String blurb) {
        this.blurb = blurb;
    }

    public String getCoverUri() {
        return coverUri;
    }

    public void setCoverUri(String coverUri) {
        Log.d(TAG, "Setting cover URI for " + title + ": " + coverUri);
        this.coverUri = coverUri;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public void toggleFavorite() {
        isFavorite = !isFavorite;
        Log.d(TAG, "Toggled favorite for " + title + ": " + isFavorite);
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Book book = (Book) obj;
        return id == book.id;
    }
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

}