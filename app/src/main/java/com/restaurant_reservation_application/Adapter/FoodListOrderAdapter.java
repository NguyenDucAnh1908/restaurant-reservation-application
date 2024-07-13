package com.restaurant_reservation_application.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.restaurant_reservation_application.Activity.DetailActivity;
import com.restaurant_reservation_application.Helper.ManagmentCart;
import com.restaurant_reservation_application.Model.Foods;
import com.restaurant_reservation_application.R;

import java.util.ArrayList;

public class FoodListOrderAdapter extends RecyclerView.Adapter<FoodListOrderAdapter.viewholder> {
    ArrayList<Foods> items;
    Context context;

    public FoodListOrderAdapter(ArrayList<Foods> items) {
        this.items = items;
    }


    @NonNull
    @Override
    public FoodListOrderAdapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View inflate = LayoutInflater.from(context).inflate(R.layout.viewholder_list_food_cart_item, parent, false);
        return new viewholder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodListOrderAdapter.viewholder holder, int position) {
        holder.titleTxt.setText(items.get(position).getName());
        holder.priceTxt.setText("$" + items.get(position).getPrice()+"VND");
        holder.DescriptionTxt.setText(items.get(position).getDescription());

        Glide.with(context)
                .load(items.get(position).getImage())
                .transform(new CenterCrop(), new RoundedCorners(30))
                .into(holder.pic);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("object", items.get(position));
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {
        TextView titleTxt, priceTxt, DescriptionTxt;
        ImageView pic;
        public viewholder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.titleTxt);
            priceTxt = itemView.findViewById(R.id.priceTxt);
            DescriptionTxt = itemView.findViewById(R.id.DescriptionTxt);
            pic = itemView.findViewById(R.id.img);
        }
    }
}