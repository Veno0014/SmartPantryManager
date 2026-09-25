package com.example.smartpantrymanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class RecipesApt extends BaseAdapter {

    // Recipe data
    private Context context;
    private ArrayList<Recipes> recipes;

    //Adpator constructor
    public RecipesApt(Context context, ArrayList<Recipes> recipes) {
        this.context = context;
        this.recipes = recipes;
    }

    @Override
    public int getCount() {
        return recipes.size();
    }

    @Override
    public Recipes getItem(int position) {
        return recipes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return recipes.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if(convertView == null) {

            // Connects adapter to items_recipes.xml
            convertView = LayoutInflater.from(context).inflate(R.layout.items_recipes, parent, false);

            holder = new ViewHolder();

            holder.txtRecipeName = convertView.findViewById(R.id.txtRecipeName);
            holder.txtRecipeMethod = convertView.findViewById(R.id.txtRecipeMethod);

            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        Recipes recipe = getItem(position);

        // Display recipe details
        holder.txtRecipeName.setText(recipe.getName());
        holder.txtRecipeMethod.setText(recipe.getMethod());

        return convertView;
    }

    // Stores recipe row components
    private static class ViewHolder {
        TextView txtRecipeName;
        TextView txtRecipeMethod;
    }
}