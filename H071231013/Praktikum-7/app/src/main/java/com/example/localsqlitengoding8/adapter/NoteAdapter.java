package com.example.localsqlitengoding8.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.localsqlitengoding8.FormActivity;
import com.example.localsqlitengoding8.R;
import com.example.localsqlitengoding8.model.Student;
import java.util.ArrayList;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {
    private final ArrayList<Student> list = new ArrayList<>();
    private final Activity activity;

    public NoteAdapter(Activity activity) {
        this.activity = activity;
    }

    public void setStudents(ArrayList<Student> items) {
        list.clear();
        list.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note, parent, false);
        return new ViewHolder(v);
    }

    //RecyclerView List & Item Card
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int pos) {
        Student s = list.get(pos);
        holder.tvTime.setText("Created at " + s.getCreatedAt());
        holder.tvTitle.setText(s.getTitle());
        holder.tvDesc.setText(s.getDescription());
        holder.card.setOnClickListener(v -> {
            Intent i = new Intent(activity, FormActivity.class);
            i.putExtra(FormActivity.EXTRA_STUDENT, s);
            activity.startActivityForResult(i, FormActivity.REQUEST_UPDATE);
        });
    }

    @Override public int getItemCount() { return list.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvTime, tvTitle, tvDesc;
        final CardView card;
        ViewHolder(@NonNull View v) {
            super(v);
            card   = v.findViewById(R.id.card_view);
            tvTime = v.findViewById(R.id.tv_created_at);
            tvTitle= v.findViewById(R.id.tv_title);
            tvDesc = v.findViewById(R.id.tv_description);
        }
    }
}
