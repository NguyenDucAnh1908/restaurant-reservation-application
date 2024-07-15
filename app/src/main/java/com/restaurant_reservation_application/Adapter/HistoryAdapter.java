package com.restaurant_reservation_application.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.restaurant_reservation_application.Activity.ReserveDetailActivity;
import com.restaurant_reservation_application.Activity.RestaurentDetailActivity;
import com.restaurant_reservation_application.Model.Reservation;
import com.restaurant_reservation_application.R;
import com.restaurant_reservation_application.databinding.ViewholderHistoryBinding;
import com.restaurant_reservation_application.databinding.ViewholderPopularBinding;

import java.util.ArrayList;


public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    ArrayList<Reservation> items;
    Context context;
    private AdapterView.OnItemClickListener listener;

    public HistoryAdapter(ArrayList<Reservation> items) {
        this.items = items;
    }
    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        this.listener = listener;
    }


    @NonNull
    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.viewholder_history, parent, false);
        context = parent.getContext();
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.ViewHolder holder, int position) {
        Reservation reservation = items.get(position);
        if (reservation != null) {
            holder.titleTxt.setText(reservation.getName());
            holder.dateAndTimeTxt.setText(reservation.getDate() + " | " + reservation.getStartTime());
            holder.peopleTxt.setText(String.valueOf(reservation.getPeople()));

            // Xử lý sự kiện khi người dùng nhấn vào mục
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ReserveDetailActivity.class);
                    intent.putExtra("reservationId", items.get(position).getId()); // Assuming getId() returns the reservationId
                    context.startActivity(intent);
                }
            });

        } else {
            Log.e("AdapterData", "Reservation at position " + position + " is null");
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTxt, dateAndTimeTxt, peopleTxt, tableTypeTxt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.titleTxt);
            dateAndTimeTxt = itemView.findViewById(R.id.dateAndTimeTxt);
            peopleTxt = itemView.findViewById(R.id.peopleTxt);
        }
    }
}
