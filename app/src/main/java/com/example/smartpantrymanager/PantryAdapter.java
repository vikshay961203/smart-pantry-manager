package com.example.smartpantrymanager;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Custom adapter that displays the user's pantry items in a RecyclerView.
 * Each row is built from the item_ingredient.xml layout.
 * Expiry dates are colour-coded: red = expired, orange = expiring soon.
 */
public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.ViewHolder> {

    /** Lets the Activity react when a row is tapped or its delete button is pressed. */
    public interface OnItemActionListener {
        void onItemClick(Ingredient ingredient);
        void onDeleteClick(Ingredient ingredient);
    }

    private static final int STATUS_OK = 0;
    private static final int STATUS_SOON = 1;
    private static final int STATUS_EXPIRED = 2;

    private static final int COLOR_EXPIRED = Color.parseColor("#E53935");
    private static final int COLOR_SOON = Color.parseColor("#FB8C00");

    private List<Ingredient> ingredients = new ArrayList<>();
    private final OnItemActionListener listener;

    public PantryAdapter(OnItemActionListener listener) {
        this.listener = listener;
    }

    /** Replaces the list shown on screen and refreshes it. */
    public void setIngredients(List<Ingredient> newList) {
        this.ingredients = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ingredient, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Ingredient item = ingredients.get(position);

        holder.tvName.setText(item.getName());
        holder.tvQuantity.setText(formatQuantity(item.getQuantity()) + " " + item.getUnit());

        // Expiry text and colour depend on how close the date is
        String expiry = item.getExpiryDate();
        int status = getExpiryStatus(expiry);

        if (expiry == null || expiry.trim().isEmpty()) {
            holder.tvExpiry.setText("No expiry date");
        } else if (status == STATUS_EXPIRED) {
            holder.tvExpiry.setText("Expired: " + expiry);
        } else if (status == STATUS_SOON) {
            holder.tvExpiry.setText("Expires soon: " + expiry);
        } else {
            holder.tvExpiry.setText("Expires: " + expiry);
        }

        // Set the colour every time, because rows are recycled while scrolling
        if (status == STATUS_EXPIRED) {
            holder.tvExpiry.setTextColor(COLOR_EXPIRED);
        } else if (status == STATUS_SOON) {
            holder.tvExpiry.setTextColor(COLOR_SOON);
        } else {
            holder.tvExpiry.setTextColor(holder.defaultExpiryColor);
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(item));
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    /** Works out whether a yyyy-MM-dd date is expired, expiring soon, or fine. */
    private int getExpiryStatus(String expiry) {
        if (expiry == null || expiry.trim().isEmpty()) {
            return STATUS_OK;
        }
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            format.setLenient(false);
            Date expiryDate = format.parse(expiry.trim());
            if (expiryDate == null) {
                return STATUS_OK;
            }

            // Start of today
            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            if (expiryDate.before(today.getTime())) {
                return STATUS_EXPIRED;
            }

            Calendar limit = (Calendar) today.clone();
            limit.add(Calendar.DAY_OF_YEAR, AppSettings.EXPIRY_WARNING_DAYS);
            if (!expiryDate.after(limit.getTime())) {
                return STATUS_SOON;
            }
        } catch (ParseException e) {
            // unreadable date: treat as normal
        }
        return STATUS_OK;
    }

    /** Shows 250.0 as "250" and 1.5 as "1.5". */
    private String formatQuantity(double quantity) {
        if (quantity == Math.floor(quantity)) {
            return String.valueOf((int) quantity);
        }
        return String.valueOf(quantity);
    }

    /** Holds references to the views in one row so they are not looked up repeatedly. */
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvQuantity;
        TextView tvExpiry;
        ImageButton btnDelete;
        int defaultExpiryColor;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvIngredientName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvExpiry = itemView.findViewById(R.id.tvExpiry);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            defaultExpiryColor = tvExpiry.getCurrentTextColor();
        }
    }
}