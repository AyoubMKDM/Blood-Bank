package com.AyoubMKDM.github.bloodbank;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

    private List<UserDataModel> mUsers;
    private Context context;

    public UsersAdapter(List<UserDataModel> dataSet, Context context) {
        this.mUsers = dataSet;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_model, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.userName.setText(mUsers.get(position).getUserName());
        holder.userPhoneNumber.setText(mUsers.get(position).getUserPhoneNumber());
        holder.userBloodType.setText(mUsers.get(position).getUserBloodType());
    }


    @Override
    public int getItemCount() {
        return mUsers.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        TextView userPhoneNumber;
        TextView userBloodType;
        ViewHolder(final View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.text_user_name);
            userPhoneNumber = itemView.findViewById(R.id.text_user_phoneNumber);
            userBloodType = itemView.findViewById(R.id.text_user_bloodtype);
        }

    }

}

