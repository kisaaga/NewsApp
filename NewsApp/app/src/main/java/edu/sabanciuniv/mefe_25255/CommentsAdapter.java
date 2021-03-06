package edu.sabanciuniv.mefe_25255;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.sabanciuniv.mefe_25255.model.CommentItem;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentsViewHolder> {

    List<CommentItem> commentItems;
    Context context;

    public CommentsAdapter(List<CommentItem> commentItems, Context context) {
        this.commentItems = commentItems;
        this.context = context;
    }

    @NonNull
    @Override
    public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.comment_row_layout, parent, false);
        return new CommentsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsViewHolder holder, final int position) {
        holder.username.setText(commentItems.get(position).getName());
        holder.commentText.setText(commentItems.get(position).getMessage());
    }

    @Override
    public int getItemCount() {
        return commentItems.size();
    }

    class CommentsViewHolder extends RecyclerView.ViewHolder {
        TextView username;
        TextView commentText;
        ConstraintLayout root;

        public CommentsViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            commentText = itemView.findViewById(R.id.commenttext);
            root = itemView.findViewById(R.id.comment_container);

        }
    }
}
