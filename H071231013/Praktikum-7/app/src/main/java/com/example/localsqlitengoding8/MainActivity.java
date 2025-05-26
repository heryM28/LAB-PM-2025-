package com.example.localsqlitengoding8;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localsqlitengoding8.adapter.NoteAdapter;
import com.example.localsqlitengoding8.db.StudentHelper;
import com.example.localsqlitengoding8.model.Student;
import com.example.localsqlitengoding8.util.MappingHelper;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private StudentHelper helper;
    private NoteAdapter adapter; // RecyclerView List & Item Card
    private RecyclerView rv; // RecyclerView List & Item Card
    private TextView tvEmpty, tvReset;
    private ArrayList<Student> allNotes = new ArrayList<>(); // RecyclerView List & Item Card

    //Toolbar & Title “Notes”
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1) Toolbar(Bar bertuliskan “No data”)
        MaterialToolbar bar = findViewById(R.id.toolbar_main);
        setSupportActionBar(bar);

        // 2) RecyclerView (daftar catatan)
        rv      = findViewById(R.id.rv_notes);
        tvEmpty = findViewById(R.id.tv_empty);
        tvReset = findViewById(R.id.tv_reset);
        helper  = new StudentHelper(this);
        helper.open();

        // 3) RecyclerView (daftar catatan) + Adapter
        adapter = new NoteAdapter(this);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        // 4) FAB “Add” //FAB “+” → Buka Form Add
        FloatingActionButton fab = findViewById(R.id.fab_add);
        fab.setOnClickListener(v -> startActivityForResult(
                new Intent(MainActivity.this, FormActivity.class),
                FormActivity.REQUEST_ADD
        ));

        // 5) Search icon listener
        ImageView ivSearch = findViewById(R.id.iv_search);

        // buka AlertDialog dengan EditText untuk keyword
        // filter allNotes → tampilkan hasil atau “No match…”
        ivSearch.setOnClickListener(v -> {
            final EditText input = new EditText(this);
            input.setHint("Type keyword...");
            new AlertDialog.Builder(this)
                    .setTitle("Search Notes")
                    .setView(input)
                    .setPositiveButton("Search", (dlg, which) -> {
                        String q = input.getText()
                                .toString()
                                .trim()
                                .toLowerCase(Locale.ROOT);
                        // filter allNotes
                        ArrayList<Student> filtered = new ArrayList<>();
                        for (Student s : allNotes) {
                            if (s.getTitle().toLowerCase(Locale.ROOT).contains(q) ||
                                    s.getDescription().toLowerCase(Locale.ROOT).contains(q)) {
                                filtered.add(s);
                            }
                        }
                        // update UI
                        if (filtered.isEmpty()) {
                            rv.setVisibility(View.GONE);
                            tvEmpty.setText("No match for \"" + q + "\"");
                            tvEmpty.setVisibility(View.VISIBLE);
                        } else {
                            rv.setVisibility(View.VISIBLE);
                            tvEmpty.setVisibility(View.GONE);
                            adapter.setStudents(filtered);
                        }
                        // show Reset button
                        tvReset.setVisibility(View.VISIBLE);
                    })
                    .setNegativeButton("Cancel", (dlg, which) -> {
                        // on Cancel, restore full list
                        resetView();
                    })
                    .show();
        });

        // 6) Reset button listener(tampilkan tv_reset)
        tvReset.setOnClickListener(v -> resetView());
    }

    // RecyclerView List & Item Card
    @Override
    protected void onResume() {
        super.onResume();
        loadNotes();
    }

    /**
     * Memuat dari database ke dalam allNotes dan adapter, menyembunyikan reset
     */
    // RecyclerView List Item Card dan Load data
    private void loadNotes() {
        Cursor c = helper.getAll();
        ArrayList<Student> list = MappingHelper.mapCursorToList(c);
        allNotes.clear();
        allNotes.addAll(list);

        if (list.isEmpty()) {
            rv.setVisibility(View.GONE);
            tvEmpty.setText(getString(R.string.no_data));
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            rv.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
            // sembunyikan atau tampilkan tvEmpty sesuai list kosong/isi
            adapter.setStudents(list);
        }
        tvReset.setVisibility(View.GONE);
    }

    /**
     * Restores the full list view and hides reset/empty views.
     */
    private void resetView() {
        adapter.setStudents(allNotes);
        tvReset.setVisibility(View.GONE);
        tvEmpty.setVisibility(View.GONE);
        rv.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        helper.close();
    }
}
