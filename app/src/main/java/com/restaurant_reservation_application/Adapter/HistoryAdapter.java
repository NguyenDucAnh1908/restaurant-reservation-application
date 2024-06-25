package com.restaurant_reservation_application.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.restaurant_reservation_application.Model.HistoryModel;
import com.restaurant_reservation_application.R;

import java.util.List;

public class HistoryAdapter extends BaseAdapter {
    private Context context;
    private List<HistoryModel> historyItems;

    public HistoryAdapter(Context context, List<HistoryModel> historyItems) {
        this.context = context;
        this.historyItems = historyItems;
    }

    @Override
    public int getCount() {
        return historyItems.size();
    }

    @Override
    public Object getItem(int position) {
        return historyItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_history, parent, false);
        }

        TextView tvRestaurantName = convertView.findViewById(R.id.tv_restaurant_name);
        TextView tvTimeAgo = convertView.findViewById(R.id.tv_time_ago);
        TextView tvStatus = convertView.findViewById(R.id.tv_status);
        TextView tvCancelBooking = convertView.findViewById(R.id.tv_cancel_booking);
        TextView tvDateTime = convertView.findViewById(R.id.tv_date_time);
        TextView tvGuests = convertView.findViewById(R.id.tv_guests);

        HistoryModel item = historyItems.get(position);

        tvRestaurantName.setText(item.getRestaurantName());
        tvTimeAgo.setText(item.getTimeAgo());
        tvStatus.setText(item.getStatus());
        tvCancelBooking.setText(item.getCancelBooking());
        tvDateTime.setText(item.getDateTime());
        tvGuests.setText(item.getGuests());

        return convertView;
    }
}
