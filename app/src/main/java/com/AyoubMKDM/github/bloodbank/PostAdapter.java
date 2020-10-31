package com.AyoubMKDM.github.bloodbank;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private         List<DataModelPost> posts;
    private Context context;

    public PostAdapter(List<DataModelPost> posts, Context context) {
        this.posts = posts;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_post_item, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.postType.setText(posts.get(position).getType());
        holder.postTitle.setText(posts.get(position).getPostTitle());
        holder.postBody.setText(posts.get(position).getPostTextBody());
        holder.buttonCall.setOnClickListener(makeACall());
        holder.buttonShare.setOnClickListener(view -> sharePost(position));
    }

    private View.OnClickListener makeACall() {
        Intent intent = new Intent();
        return null;
    }

    private void sharePost(int position) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, posts.get(position).getPostTextBody());
            sendIntent.setType("text/plain");

            if (sendIntent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(sendIntent);
            }
    }


    @Override
    public int getItemCount() {
        return posts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView postType;
        TextView postTitle;
        TextView postBody;
        Button buttonCall, buttonShare;

        ViewHolder(final View itemView) {
            super(itemView);
            postType = itemView.findViewById(R.id.textPostType);
            postTitle = itemView.findViewById(R.id.textPostTitle);
            postBody = itemView.findViewById(R.id.textPost);
            buttonCall = itemView.findViewById(R.id.button_call_post);
            buttonShare= itemView.findViewById(R.id.button_share_post);
        }
    }

}
