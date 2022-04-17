package com.example.letseat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.MyViewHolder> {
    private List<String> friendsList;
    private final Context context;
    public FriendsAdapter(List<String> friendsList, Context context) {
        this.friendsList = friendsList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder((LayoutInflater.from(parent.getContext())).inflate(R.layout.friend_adapter_layout, null));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String friend = friendsList.get(position);
        holder.name.setText(friend);
    }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }

    public void updateFriendsList(List<String> friendsList){
        this.friendsList= friendsList;
        notifyDataSetChanged();
    }
    public void clear() {
        int size = friendsList.size();
        friendsList.clear();
        notifyItemRangeRemoved(0, size);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView name;
        private LinearLayout rootLayout;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            rootLayout = itemView.findViewById(R.id.rootLayout);
        }
    }
}
