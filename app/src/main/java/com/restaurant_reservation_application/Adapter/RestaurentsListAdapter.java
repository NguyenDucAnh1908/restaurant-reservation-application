package com.restaurant_reservation_application.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.restaurant_reservation_application.Model.Restaurents;
import com.restaurant_reservation_application.R;

import java.util.ArrayList;

public class RestaurentsListAdapter extends RecyclerView.Adapter<RestaurentsListAdapter.viewholder>{
    ArrayList<Restaurents> items;
    Context context;

    public RestaurentsListAdapter(ArrayList<Restaurents> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public RestaurentsListAdapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View inflate = LayoutInflater.from(context).inflate(R.layout.viewholder_list_restaurents, parent, false);
        return new viewholder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurentsListAdapter.viewholder holder, int position) {
        holder.titleTxt.setText(items.get(position).getName());
        holder.openTxt.setText(items.get(position).getOpen()+"AM");
        holder.closeTxt.setText(items.get(position).getClose()+"PM");
        holder.rateTxt.setText("" + items.get(position).getScore());
        //holder.timeTxt.setText(items.get(position).getTimeValue() + "min");

        Glide.with(context)
                .load(items.get(position).getImage())
                .transform(new CenterCrop(), new RoundedCorners(30))
                .into(holder.pic);

//        holder.itemView.setOnClickListener(v -> {
//            Intent intent = new Intent(context, DetailActivity.class);
//            intent.putExtra("object", items.get(position));
//            context.startActivity(intent);
//        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {
        TextView titleTxt, openTxt, closeTxt,rateTxt, timeTxt;
        ImageView pic;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.titleTxt);
            openTxt = itemView.findViewById(R.id.openTxt);
            rateTxt = itemView.findViewById(R.id.rateTxt);
            closeTxt = itemView.findViewById(R.id.closeTxt);
            pic = itemView.findViewById(R.id.img);
        }
    }
}
