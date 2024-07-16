package com.restaurant_reservation_application.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.restaurant_reservation_application.Model.Reservation;
import com.restaurant_reservation_application.R;

import java.util.List;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ReservationViewHolder> {
    List<Reservation> reservationList;
    Context context;

    public ReservationAdapter(List<Reservation> reservationList) {
        this.reservationList = reservationList;
    }

    @NonNull
    @Override
    public ReservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View inflate = LayoutInflater.from(context).inflate(R.layout.viewholder_list_tables_booking, parent, false);
        return new ReservationViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(ReservationViewHolder holder, int position) {
        Reservation reservation = reservationList.get(position);
        holder.tvName.setText(reservation.getName());
        holder.tvTime.setText(reservation.getStartTime());
        holder.tvPeople.setText(String.valueOf(reservation.getPeople()));
        holder.tvTable.setText(String.valueOf(reservation.getTableId()));
        // Implementing click listener for the Release button
        holder.btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReleaseConfirmationDialog(position, holder);
            }
        });
    }

    private void showReleaseConfirmationDialog(final int position, ReservationViewHolder holder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Release Seat Confirmation")
                .setMessage("Do you want to release this seat?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Perform action on OK
                        // Remove item from list or update its state
                            holder.btnAction.setVisibility(View.GONE); // Hide Release button
                        notifyDataSetChanged(); // Refresh RecyclerView
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User cancelled the dialog
                        dialog.dismiss();
                    }
                })
                .show();
    }
    @Override
    public int getItemCount() {
        return reservationList.size();
    }

    public class ReservationViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvTime, tvPeople, tvTable;
        AppCompatButton btnAction;

        public ReservationViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvPeople = itemView.findViewById(R.id.tv_number);
            tvTable = itemView.findViewById(R.id.tv_table_name);
            btnAction = itemView.findViewById(R.id.btn_action);
        }
    }
}
