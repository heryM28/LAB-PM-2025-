package com.example.tuprak5.model;

/**
 * Model untuk menyimpan data karakter.
 */
public class Character {
    // ID unik untuk setiap karakter
    private int id;
    // Nama karakter
    private String name;
    // Status hidup karakter (contoh: Alive, Dead, Unknown)
    private String status;
    // Spesies karakter (contoh: Human, Alien)
    private String species;
    // URL atau path gambar karakter
    private String image;

    /**
     * Konstruktor untuk menginisialisasi objek Character dengan semua atribut.
     *
     * @param id      ID unik karakter
     * @param name    Nama karakter
     * @param status  Status hidup karakter
     * @param species Spesies karakter
     * @param image   URL/path gambar karakter
     */
    public Character(int id, String name, String status, String species, String image) {
        this.id = id;         // Set ID karakter
        this.name = name;     // Set nama karakter
        this.status = status; // Set status hidup karakter
        this.species = species; // Set spesies karakter
        this.image = image;   // Set URL/path gambar karakter
    }

    /**
     * Mendapatkan ID karakter.
     *
     * @return id karakter
     */
    public int getId() {
        return id; // Kembalikan ID karakter
    }

    /**
     * Mendapatkan nama karakter.
     *
     * @return nama karakter
     */
    public String getName() {
        return name; // Kembalikan nama karakter
    }

    /**
     * Mendapatkan status hidup karakter.
     *
     * @return status karakter
     */
    public String getStatus() {
        return status; // Kembalikan status karakter
    }

    /**
     * Mendapatkan spesies karakter.
     *
     * @return spesies karakter
     */
    public String getSpecies() {
        return species; // Kembalikan spesies karakter
    }

    /**
     * Mendapatkan URL atau path gambar karakter.
     *
     * @return URL/path gambar karakter
     */
    public String getImage() {
        return image; // Kembalikan URL/path gambar karakter
    }
}
