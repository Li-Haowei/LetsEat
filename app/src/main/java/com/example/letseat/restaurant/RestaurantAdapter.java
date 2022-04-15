package com.example.letseat.restaurant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.letseat.R;

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
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurant_adapter_layout, null));
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantAdapter.MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return restaurantLists.size();
    }

    public void updateRestaurantList(List<RestaurantList> restaurantLists){
        this.restaurantLists= restaurantLists;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView name, rating, price, location;
        private ImageView restaurantImg;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            restaurantImg = itemView.findViewById(R.id.restaurantPic);
            name = itemView.findViewById(R.id.name);
            rating = itemView.findViewById(R.id.rating);
            price = itemView.findViewById(R.id.price);
            location = itemView.findViewById(R.id.location);
        }
    }
}
