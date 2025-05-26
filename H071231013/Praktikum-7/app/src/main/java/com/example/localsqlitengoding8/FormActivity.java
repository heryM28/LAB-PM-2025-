package com.example.localsqlitengoding8;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.localsqlitengoding8.db.DatabaseContract;
import com.example.localsqlitengoding8.db.StudentHelper;
import com.example.localsqlitengoding8.model.Student;
import com.google.android.material.appbar.MaterialToolbar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class FormActivity extends AppCompatActivity {
    public static final String EXTRA_STUDENT  = "extra_student";
    public static final int REQUEST_ADD      = 100;
    public static final int REQUEST_UPDATE   = 101;

    private EditText etTitle, etDesc;
    private Button btnSave;
    private StudentHelper helper;
    private Student note;
    private boolean isEdit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        MaterialToolbar bar = findViewById(R.id.toolbar_form);
        setSupportActionBar(bar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        bar.setNavigationOnClickListener(v -> onBackPressed());

        etTitle = findViewById(R.id.et_title);
        etDesc  = findViewById(R.id.et_description);
        btnSave = findViewById(R.id.btn_save);

        helper = new StudentHelper(this);
        helper.open();

        //Form Edit, Update & Konfirmasi Update
        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_STUDENT)) {
            isEdit = true;
            note   = intent.getParcelableExtra(EXTRA_STUDENT);
            bar.setTitle(getString(R.string.edit));
            etTitle.setText(note.getTitle());
            etDesc.setText(note.getDescription());
            btnSave.setText(getString(R.string.update));
        }

        // kalau EXTRA_STUDENT tidak ada → mode Add
        btnSave.setOnClickListener(v -> {
            if (isEdit) {
                // (Edit akan ditangani di bagian berikut)
                new AlertDialog.Builder(this)
                        .setTitle(R.string.update)
                        .setMessage(R.string.confirm_update)
                        .setNegativeButton(R.string.cancel, null)
                        .setPositiveButton(R.string.yes,
                                (dlg, which) -> saveNote())
                        .show();
            } else {
                saveNote();
            }
        });
    }

    private void saveNote() {
        String title = etTitle.getText().toString().trim();
        String desc  = etDesc.getText().toString().trim();

        ContentValues cv = new ContentValues();
        cv.put(DatabaseContract.StudentColumns.TITLE, title);
        cv.put(DatabaseContract.StudentColumns.DESCRIPTION, desc);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Makassar"));
        String now = sdf.format(new Date()); // format timestamp

        Intent resultIntent = new Intent();
        if (!isEdit) {
            // Mode tambah
            cv.put(DatabaseContract.StudentColumns.CREATED_AT, now);
            long id = helper.insert(cv);
            note = new Student((int) id, title, desc, now);
            resultIntent.putExtra(EXTRA_STUDENT, note);
            setResult(RESULT_OK, resultIntent);
        } else {
            // Mode edit: update created_at dengan waktu sekarang
            cv.put(DatabaseContract.StudentColumns.CREATED_AT, now);
            helper.update(note.getId(), cv);
            note.setTitle(title);
            note.setDescription(desc);
            note.setCreatedAt(now); // update di objek
            resultIntent.putExtra(EXTRA_STUDENT, note);
            setResult(RESULT_OK, resultIntent);
        }
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isEdit) {
            getMenuInflater().inflate(R.menu.menu_form, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_delete) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.delete)
                    .setMessage(R.string.confirm_delete)
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.yes,
                            (DialogInterface dialog, int which) -> {
                                helper.delete(note.getId());
                                setResult(RESULT_CANCELED);
                                finish();
                            })
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Konfirmasi “Cancel” saat Back-press (Add & Edit)
    @Override
    public void onBackPressed() {
        String action = isEdit
                ? getString(R.string.edit)
                : getString(R.string.add);
        new AlertDialog.Builder(this)
                .setTitle(R.string.cancel)
                .setMessage(getString(R.string.cancel_confirm, action))
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.yes,
                        (DialogInterface d, int w) -> super.onBackPressed())
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        helper.close();
    }
}
