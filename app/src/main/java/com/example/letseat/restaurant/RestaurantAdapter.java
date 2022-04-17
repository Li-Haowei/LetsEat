package com.example.letseat.restaurant;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.letseat.R;
import com.example.letseat.tools.ImageLoadTask;
import com.example.letseat.userMatching.Post;

import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.MyViewHolder> {
    private List<RestaurantList> restaurantLists;
    private final Context context;

    public RestaurantAdapter(List<RestaurantList> restaurantLists, Context context) {
        this.restaurantLists = restaurantLists;
        this.context = context;
    }

    @NonNull
    @Override
    public RestaurantAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder((LayoutInflater.from(parent.getContext())).inflate(R.layout.restaurant_adapter_layout, null));
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantAdapter.MyViewHolder holder, int position) {
        RestaurantList list2 = restaurantLists.get(position);
        new ImageLoadTask(list2.getImgUrl(),holder.restaurantImg).execute();
        holder.name.setText(list2.getName());
        holder.price.setText(list2.getPrice());
        holder.location.setText(list2.getLocation());
        holder.rating.setText(list2.getRating());

        holder.location.setOnClickListener(view -> {
            String loc = list2.getLocation();
            loc = loc.replace(' ', '+');
            Intent geoLocationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + loc));
            context.startActivity(geoLocationIntent);
        });

        // Allow user to post dinning request.
        // Added by Zack
        holder.postButton.setOnClickListener(view -> {
            Post.makePost(list2.getImgUrl(), list2.getName());
        });
    }

    @Override
    public int getItemCount() {
        return restaurantLists.size();
    }

    public void updateRestaurantList(List<RestaurantList> restaurantLists){
        this.restaurantLists= restaurantLists;
        notifyDataSetChanged();
    }
    public void clear() {
        int size = restaurantLists.size();
        restaurantLists.clear();
        notifyItemRangeRemoved(0, size);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView name, rating, price, location;
        private ImageView restaurantImg;
        // Added by Zack
        private Button postButton;
        private LinearLayout rootLayout;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            restaurantImg = itemView.findViewById(R.id.restaurantPic);
            name = itemView.findViewById(R.id.name);
            rating = itemView.findViewById(R.id.rating);
            price = itemView.findViewById(R.id.price);
            location = itemView.findViewById(R.id.location);
            rootLayout = itemView.findViewById(R.id.rootLayout);
            postButton = itemView.findViewById(R.id.btnPost);

        }
    }
}
