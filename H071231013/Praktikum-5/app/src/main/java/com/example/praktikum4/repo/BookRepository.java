package com.example.praktikum4.repo;

import android.content.Context;
import android.util.Log;

import com.example.praktikum4.R;
import com.example.praktikum4.model.Book;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class BookRepository {
    private static final String TAG = "BookRepository";
    private static BookRepository instance;
    private final List<String> genres;
    private static boolean isInitialized = false;
    private final Context context;

    private BookRepository(Context context) {
        Log.d(TAG, "Initializing BookRepository");
        this.context = context.getApplicationContext();
        genres = new ArrayList<>();

        // Add genres
        genres.add("Fiction");
        genres.add("Non-Fiction");
        genres.add("Science Fiction");
        genres.add("Fantasy");
        genres.add("Mystery");
        genres.add("Romance");
        genres.add("Thriller");
        genres.add("Biography");

        // Initialize dummy data once
        if (!isInitialized && Book.getAllBooks().isEmpty()) {
            Log.d(TAG, "Initializing dummy books");
            initializeDummyBooks();
            isInitialized = true;
        }
    }

    public static synchronized BookRepository getInstance(Context context) {
        if (instance == null) {
            instance = new BookRepository(context);
        }
        return instance;
    }

    private void initializeDummyBooks() {
        addBookToRepository(new Book(
                "To Kill a Mockingbird",
                "Harper Lee",
                1960,
                "A lawyer in the Depression-era South defends a black man charged with the rape of a white woman.",
                "android.resource://" + context.getPackageName() + "/" + R.drawable.book_cover_8,
                "Fiction",
                4.5f
        ));
        addBookToRepository(new Book(
                "1984",
                "George Orwell",
                1949,
                "A dystopian novel set in a totalitarian society where critical thought is suppressed.",
                "android.resource://" + context.getPackageName() + "/" + R.drawable.book_cover_2,
                "Science Fiction",
                4.7f
        ));
        addBookToRepository(new Book(
                "Pride and Prejudice",
                "Jane Austen",
                1813,
                "The story follows Elizabeth Bennet as she deals with issues of manners, upbringing, and marriage.",
                "android.resource://" + context.getPackageName() + "/" + R.drawable.book_cover_3,
                "Romance",
                4.3f
        ));
        addBookToRepository(new Book(
                "The Great Gatsby",
                "F. Scott Fitzgerald",
                1925,
                "The story primarily concerns the young and mysterious millionaire Jay Gatsby and his passion for Daisy Buchanan.",
                "android.resource://" + context.getPackageName() + "/" + R.drawable.book_cover_4,
                "Fiction",
                4.2f
        ));
        addBookToRepository(new Book(
                "Moby Dick",
                "Herman Melville",
                1851,
                "The saga of Captain Ahab and his monomaniacal pursuit of the white whale.",
                "android.resource://" + context.getPackageName() + "/" + R.drawable.book_cover_5,
                "Fiction",
                4.0f
        ));
        addBookToRepository(new Book(
                "The Lord of the Rings",
                "J.R.R. Tolkien",
                1954,
                "An epic high-fantasy novel about the quest to destroy the One Ring.",
                "android.resource://" + context.getPackageName() + "/" + R.drawable.book_cover_6,
                "Fantasy",
                4.9f
        ));
        addBookToRepository(new Book(
                "The Hobbit",
                "J.R.R. Tolkien",
                1937,
                "The adventure of Bilbo Baggins, a hobbit who embarks on an unexpected journey.",
                "android.resource://" + context.getPackageName() + "/" + R.drawable.book_cover_7,
                "Fantasy",
                4.8f
        ));
        addBookToRepository(new Book(
                "Harry Potter and the Philosopher's Stone",
                "J.K. Rowling",
                1997,
                "The first novel in the Harry Potter series, featuring a young wizard's adventures at Hogwarts.",
                "android.resource://" + context.getPackageName() + "/" + R.drawable.book_cover_1,
                "Fantasy",
                4.7f
        ));
        addBookToRepository(new Book(
                "The Catcher in the Rye",
                "J.D. Salinger",
                1951,
                "A teenager's perspective on the phoniness of adulthood.",
                "android.resource://" + context.getPackageName() + "/" + R.drawable.book_cover_9,
                "Fiction",
                4.1f
        ));
        addBookToRepository(new Book(
                "Brave New World",
                "Aldous Huxley",
                1932,
                "A dystopian novel set in a futuristic World State with genetically modified citizens.",
                "android.resource://" + context.getPackageName() + "/" + R.drawable.book_cover_10,
                "Science Fiction",
                4.4f
        ));
        addBookToRepository(new Book(
                "The Alchemist",
                "Paulo Coelho",
                1988,
                "A philosophical novel about a young shepherd's journey to the Egyptian pyramids.",
                "android.resource://" + context.getPackageName() + "/" + R.drawable.book_cover_11,
                "Fiction",
                4.3f
        ));
        addBookToRepository(new Book(
                "Crime and Punishment",
                "Fyodor Dostoevsky",
                1866,
                "The mental anguish and moral dilemmas of a poor ex-student who murders a pawnbroker.",
                "android.resource://" + context.getPackageName() + "/" + R.drawable.book_cover_12,
                "Fiction",
                4.2f
        ));
        addBookToRepository(new Book(
                "The Odyssey",
                "Homer",
                -800,
                "An ancient Greek epic poem following Odysseus's adventures returning home after the Trojan War.",
                "android.resource://" + context.getPackageName() + "/" + R.drawable.book_cover_13,
                "Fiction",
                4.0f
        ));
        addBookToRepository(new Book(
                "The Divine Comedy",
                "Dante Alighieri",
                1320,
                "An epic journey through Hell, Purgatory, and Paradise.",
                "android.resource://" + context.getPackageName() + "/" + R.drawable.book_cover_14,
                "Fiction",
                4.1f
        ));
        addBookToRepository(new Book(
                "Don Quixote",
                "Miguel de Cervantes",
                1605,
                "The story of a knight-errant and his adventures.",
                "android.resource://" + context.getPackageName() + "/" + R.drawable.book_cover_15,
                "Fiction",
                4.2f
        ));

        Log.d(TAG, "Added " + Book.getAllBooks().size() + " dummy books");
    }

    public List<Book> getAllBooks() {
        List<Book> sorted = new ArrayList<>(Book.getAllBooks());
        Collections.sort(sorted, (b1, b2) -> Integer.compare(b2.getYear(), b1.getYear()));
        return sorted;
    }

    public List<Book> getFavoriteBooks() {
        return Book.getAllBooks().stream()
                .filter(Book::isFavorite)
                .collect(Collectors.toList());
    }

    public List<Book> searchBooks(String query) {
        String lower = query.toLowerCase();
        return Book.getAllBooks().stream()
                .filter(b -> b.getTitle().toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }

    public List<Book> getBooksByGenre(String genre) {
        return Book.getAllBooks().stream()
                .filter(b -> b.getGenre().equals(genre))
                .collect(Collectors.toList());
    }

    public void addBook(Book book) {
        addBookToRepository(book);
    }

    private void addBookToRepository(Book book) {
        Book.addToStaticList(book);
    }

    public void removeBook(Book book) {
        Book.removeFromStaticList(book);
    }

    public List<String> getGenres() {
        return genres;
    }
}