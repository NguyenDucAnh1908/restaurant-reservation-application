package com.restaurant_reservation_application.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.restaurant_reservation_application.Model.Reservation;
import com.restaurant_reservation_application.R;
import com.restaurant_reservation_application.databinding.ViewholderHistoryBinding;
import com.restaurant_reservation_application.databinding.ViewholderPopularBinding;

import java.util.ArrayList;


public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    ArrayList<Reservation> items;
    Context context;

    public HistoryAdapter(ArrayList<Reservation> items) {
        this.items = items;
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
        } else {
            Log.e("AdapterData", "Reservation at position " + position + " is null");
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTxt, dateAndTimeTxt, peopleTxt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.titleTxt);
            dateAndTimeTxt = itemView.findViewById(R.id.dateAndTimeTxt);
            peopleTxt = itemView.findViewById(R.id.peopleTxt);
        }
    }
}
