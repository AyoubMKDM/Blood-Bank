package com.AyoubMKDM.github.bloodbank;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    public static final String USER_ID = "UserID";
    private List<PostDataModel> posts;
    private Context context;
    FirebaseDatabase mDB;
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
                .inflate(R.layout.post_model, parent, false);
        firebaseUtil.openFBReference(context, FirebaseUtil.PATH_POST);
        mDB = firebaseUtil.sDB;
        databaseReference = firebaseUtil.sDatabaseReference;
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
        holder.buttonCall.setOnClickListener(view -> onCallClick(position));
        holder.buttonShare.setOnClickListener(view -> onShareClick(position));
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
        holder.header.setOnClickListener(view -> {
            Intent intent = new Intent(context, ProfileActivity.class);
            intent.putExtra(USER_ID,posts.get(position).getUserId());
            Toast.makeText(context, holder.userName.getText().toString()
                    ,Toast.LENGTH_LONG).show();
            context.startActivity(intent);
        });
//        holder.header.setOnClickListener(view -> {
//            Intent intent = new Intent(context, ProfileActivity.class);
//            intent.putExtra(USER_ID,posts.get(position).getUserId());
//        });
    }

    private void onEditClick(int position) {
        PostDataModel post = posts.get(position);
        Intent intent = new Intent(context,AddPostActivity.class);
        intent.putExtra("Post", post);
        context.startActivity(intent);

    }

    private void onDeleteClick(View view, int position) {
        //TODO implement delete function
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

    private void onCallClick(int position) {
        //TODO clean it
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(FirebaseUtil.USER_PATH);
        reference.orderByKey().equalTo(posts.get(position).getUserId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userPhoneNumber = "tel:";

                for(DataSnapshot datas: dataSnapshot.getChildren()){
                    userPhoneNumber += datas.child("userPhoneNumber").getValue().toString();
                }
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse(userPhoneNumber));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (intent.resolveActivity(context.getPackageManager()) != null) {
                    startCalling(intent);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(context, "Error: " + databaseError.toString(), Toast.LENGTH_LONG).show();
                Log.e("Adapter", "onCancelled: Error" + databaseError.toString());
            }
        });
    }

    private void startCalling(Intent intent) {
        int permissionCheck = ContextCompat.checkSelfPermission(context,
                Manifest.permission.CALL_PHONE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.CALL_PHONE}, 123);
        } else {
            context.startActivity(intent);
        }
    }

    private void onShareClick(int position) {
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
        LinearLayout header;
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
            header = itemView.findViewById(R.id.header);

        }
    }

}
//TODO limit the posts lines