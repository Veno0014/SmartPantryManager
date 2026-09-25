package com.example.smartpantrymanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class PantryApt extends BaseAdapter {

    // Pantry data
    private Context context;
    private ArrayList<PantryItems> pantryItems;
    private OnPantryActionListener listener;

    // Edit and Delete actions
    public interface OnPantryActionListener {
        void onEdit(PantryItems item);
        void onDelete(PantryItems item);
    }

    public PantryApt(Context context, ArrayList<PantryItems> pantryItems, OnPantryActionListener listener) {
        this.context = context;
        this.pantryItems = pantryItems;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return pantryItems.size();
    }

    @Override
    public PantryItems getItem(int position) {
        return pantryItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return pantryItems.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if(convertView == null){

            // Connect Adapter to items_pantry.xml
            convertView = LayoutInflater.from(context).inflate(R.layout.items_pantry, parent, false);

            holder = new ViewHolder();

            holder.txtName = convertView.findViewById(R.id.txtItemName);
            holder.txtQuantity = convertView.findViewById(R.id.txtItemQuantity);
            holder.txtExpiry = convertView.findViewById(R.id.txtItemExpiry);
            holder.btnEdit = convertView.findViewById(R.id.btnEditIngredient);
            holder.btnDelete = convertView.findViewById(R.id.btnDeleteIngredient);

            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        PantryItems item = getItem(position);

        // Names of Ingredients
        holder.txtName.setText(item.getName());

        // Units
        String quantity;

        if(item.getQuantity() == Math.floor(item.getQuantity())){
            quantity = String.valueOf((int) item.getQuantity());
        } else {
            quantity = String.valueOf(item.getQuantity());
        }

        holder.txtQuantity.setText(quantity + " " + item.getUnit());

        // Expiration date
        if(item.getExpiryDate() == null || item.getExpiryDate().isEmpty()){
            holder.txtExpiry.setText("No expiry date");
        } else {
            holder.txtExpiry.setText("Expires: " + item.getExpiryDate());
        }

        // Edit button
        holder.btnEdit.setOnClickListener(v -> listener.onEdit(item));

        // Delete button
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(item));

        return convertView;
    }

    // Stores item controls
    private static class ViewHolder {

        TextView txtName;
        TextView txtQuantity;
        TextView txtExpiry;
        Button btnEdit;
        Button btnDelete;
    }
}