package com.restaurant_reservation_application.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.restaurant_reservation_application.Model.FoodOrder;
import com.restaurant_reservation_application.Model.Foods;
import com.restaurant_reservation_application.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FoodOrderListAdapter extends RecyclerView.Adapter<FoodOrderListAdapter.ViewHolder> {
    private List<FoodOrder> foodOrderList;

    public FoodOrderListAdapter(List<FoodOrder> foodOrderList) {
        this.foodOrderList = foodOrderList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_list_food, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoodOrder foodOrder = foodOrderList.get(position);
        holder.description.setText(foodOrder.getDescription());
        holder.name.setText(foodOrder.getName());
        holder.price.setText("$" + foodOrder.getPrice());
        Picasso.get().load(foodOrder.getImageName()).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return foodOrderList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView description, name, price;
        public ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            description = itemView.findViewById(R.id.DescriptionTxt);
            name = itemView.findViewById(R.id.titleTxt);
            price = itemView.findViewById(R.id.priceTxt);
            image = itemView.findViewById(R.id.img);
        }
    }
}


