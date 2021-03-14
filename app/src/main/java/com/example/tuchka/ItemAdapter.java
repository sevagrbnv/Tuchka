package com.example.tuchka;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemVH> {
    private static final String TAG = "ItemAdapter";

    List<Item> itemList;

    public ItemAdapter(List<Item> itemList){
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ItemVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.city_row, parent, false);
        return new ItemVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemVH holder, int position) {
        Item item = itemList.get(position);
        holder.name.setText(item.getName());
        holder.temp.setText(item.getTemp());
        IconHelper.setWeatherIconItem(holder.image, item.getIconId());
        holder.hum.setText(item.getHum());
        holder.press.setText(item.getPress());
        holder.wind.setText(item.getWind());

        boolean isExpanded = itemList.get(position).isExpanded();
        holder.expandableLayout.setVisibility(!isExpanded ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount(){
        return itemList.size();
    }

    class ItemVH extends RecyclerView.ViewHolder {
        private static final String TAG = "ItemVH";

        TextView name, temp, hum, press, wind;
        ImageView image;
        ExpandableLayout expandableLayout;
        LinearLayout mainLayout;
        ImageView delete, goToCity;

        public ItemVH(@NonNull final View itemView){
            super(itemView);

            name = itemView.findViewById(R.id.itemCityName);
            temp = itemView.findViewById(R.id.itemTemp);
            image = itemView.findViewById(R.id.itemIcon);
            hum = itemView.findViewById(R.id.itemHum);
            press = itemView.findViewById(R.id.itemPress);
            wind = itemView.findViewById(R.id.itemWind);
            delete = itemView.findViewById(R.id.deleteCity);
            goToCity = itemView.findViewById(R.id.goToCity);
            mainLayout = itemView.findViewById(R.id.mainLinLayout);
            expandableLayout = itemView.findViewById(R.id.expLayout);

            name.setOnClickListener(v -> {
                Item item = itemList.get(getAdapterPosition());
                item.setExpanded(!item.isExpanded());
                if (item.isExpanded())
                    expandableLayout.expand();
                notifyItemChanged(getAdapterPosition());
            });

            delete.setOnClickListener(v -> {
                itemList.remove(this);
                mainLayout.setVisibility(View.GONE);
                CityList.getCityList().remove(this.name.getText().toString());
                notifyItemChanged(getAdapterPosition());
                CityList.getCityList().remove(this);
                MainActivity.saveList();
            });

            goToCity.setOnClickListener(v -> {
                String city = this.name.getText().toString();
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                intent.putExtra("city", city);
                v.getContext().startActivity(intent);
                itemList.clear();
            });
        }
    }
}
