package com.example.movieapp.Adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieapp.Activities.DetailActivity2;
import com.example.movieapp.Activities.ProfileActivity;
import com.example.movieapp.Domain.NotificationMessage;
import com.example.movieapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    private ArrayList<NotificationMessage> messageList;
    public NotificationAdapter(ArrayList<NotificationMessage> messageList) {
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        NotificationMessage message = messageList.get(position);
        holder.bind(message);

        holder.itemView.setOnClickListener(v -> {
            Intent intent;
            if (message.getType().equals("movie")) {
                intent = new Intent(holder.itemView.getContext(), DetailActivity2.class);
                intent.putExtra("slug", message.getSlug());
            } else if (message.getType().equals("profile")) {
                intent = new Intent(holder.itemView.getContext(), ProfileActivity.class);
            } else {
                return;
            }
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        private TextView contentTextView;
        private TextView timestampTextView;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            contentTextView = itemView.findViewById(R.id.notification_content);
            timestampTextView = itemView.findViewById(R.id.notification_timestamp);
        }

        public void bind(NotificationMessage message) {
            contentTextView.setText(message.getContent());
            String timestamp = formatDate(message.getTimestamp());
            timestampTextView.setText(timestamp);
        }

        private String formatDate(long timestamp) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            return sdf.format(new Date(timestamp));
        }
    }
}

