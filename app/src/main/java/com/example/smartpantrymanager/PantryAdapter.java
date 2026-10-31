package com.example.smartpantrymanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom adapter that displays the user's pantry items in a RecyclerView.
 * Each row is built from the item_ingredient.xml layout.
 */
public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.ViewHolder> {

    /** Lets the Activity react when a row is tapped or its delete button is pressed. */
    public interface OnItemActionListener {
        void onItemClick(Ingredient ingredient);
        void onDeleteClick(Ingredient ingredient);
    }

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

        String expiry = item.getExpiryDate();
        if (expiry == null || expiry.trim().isEmpty()) {
            holder.tvExpiry.setText("No expiry date");
        } else {
            holder.tvExpiry.setText("Expires: " + expiry);
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(item));
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
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

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvIngredientName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvExpiry = itemView.findViewById(R.id.tvExpiry);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}