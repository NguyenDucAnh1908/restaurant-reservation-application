package com.restaurant_reservation_application.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.restaurant_reservation_application.Model.Reservation;
import com.restaurant_reservation_application.R;

import java.util.List;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ReservationViewHolder> {
    private List<Reservation> reservationList;
    private Context context;

    public ReservationAdapter(List<Reservation> reservationList) {
        this.reservationList = reservationList;
    }

    @NonNull
    @Override
    public ReservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_list_tables_booking, parent, false);
        return new ReservationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReservationViewHolder holder, int position) {
        Reservation reservation = reservationList.get(position);
        holder.tvName.setText(reservation.getName());
        holder.tvTime.setText(reservation.getStartTime() + " - " + reservation.getEndTime());
        holder.tvPeople.setText(String.valueOf(reservation.getPeople()));
        holder.tvTable.setText(String.valueOf(reservation.getTableId()));
    }

    @Override
    public int getItemCount() {
        return reservationList.size();
    }

    class ReservationViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvTime, tvPeople, tvTable;

        public ReservationViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvPeople = itemView.findViewById(R.id.tv_number);
            tvTable = itemView.findViewById(R.id.tv_table_name);
        }
    }
}
