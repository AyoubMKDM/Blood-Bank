package com.AyoubMKDM.github.bloodbank;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private List<PostDataModel> posts;
    private Context context;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseUtil firebaseUtil;

    public PostAdapter(List<PostDataModel> posts, Context context) {
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
        holder.userName.setText(posts.get(position).getUserName());
        holder.userLocation.setText(posts.get(position).getUserLocation());
        holder.postReauestedBloodType.setText(posts.get(position).getPostRequestedBloodType());
        holder.postTitle.setText(posts.get(position).getPostTitle());
        holder.postBody.setText(posts.get(position).getPostTextBody());
        holder.postDateAdded.setText(posts.get(position).getPostDateAdding());
        holder.buttonCall.setOnClickListener(makeACall());
        holder.buttonShare.setOnClickListener(view -> sharePost(position));
        holder.buttonDelete.setOnClickListener(view -> onDeleteClick(view, position));
        holder.buttonEdit.setOnClickListener(view -> onEditClick(position));
        if (position >= 0) {
            if (posts.get(position).getUserId().equals(FirebaseUtil.sFirebaseAuth.getUid())) {
                holder.buttonEdit.setVisibility(View.VISIBLE);
                holder.buttonDelete.setVisibility(View.VISIBLE);
                holder.buttonCall.setVisibility(View.GONE);
                holder.buttonShare.setVisibility(View.GONE);
            }
        }
    }

    private void onEditClick(int position) {
        PostDataModel post = posts.get(position);
        Intent intent = new Intent(context,AddPostActivity.class);
        intent.putExtra("Post", post);
        context.startActivity(intent);

    }

    private void onDeleteClick(View view, int position) {
        //TODO implement delete function
        firebaseUtil.openFBReference(context, FirebaseUtil.PATH_POST);
        firebaseDatabase = firebaseUtil.sFirebaseDatabase;
        databaseReference = firebaseUtil.sDatabaseReference;
        databaseReference.child(posts.get(position).getPostId()).removeValue((error, ref) -> {
            if (error != null) {
                Toast.makeText(view.getContext(), "Couldn't Delete this request, "
                        + error.toString(), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(view.getContext(), "Your request has been Deleted "
                        , Toast.LENGTH_LONG).show();
            }
        });
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
        TextView userName;
        TextView userLocation;
        TextView postReauestedBloodType;
        TextView postTitle;
        TextView postBody;
        //TODO condition on the date to delete old posts
        TextView postDateAdded;
        ImageButton buttonDelete, buttonEdit, buttonCall, buttonShare;

        ViewHolder(final View itemView) {
            super(itemView);
            userName  = itemView.findViewById(R.id.textUserName);
            userLocation = itemView.findViewById(R.id.text_user_location);
            postReauestedBloodType = itemView.findViewById(R.id.textPostBloodType);
            postTitle = itemView.findViewById(R.id.textPostTitle);
            postBody = itemView.findViewById(R.id.textPost);
            postDateAdded  = itemView.findViewById(R.id.posting_date);
            buttonCall = itemView.findViewById(R.id.button_call_post);
            buttonShare = itemView.findViewById(R.id.button_share_post);
            buttonEdit = itemView.findViewById(R.id.button_edit_post);
            buttonDelete =  itemView.findViewById(R.id.button_delete_post);
            int position = getAdapterPosition();

        }
    }

}
//TODO limit the posts lines