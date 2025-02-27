package com.example.cheapy.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelStoreOwner;

import com.bumptech.glide.Glide;
import com.example.cheapy.R;
import com.example.cheapy.entities.Store;

import java.util.List;

public class StoreAdapter extends ArrayAdapter<Store> {

    private int selectedPosition = -1;
    private List<Store> stores;
    private LayoutInflater inflater;

    private OnStoreSelectedListener listener;

    private Context context;


    private String token;

    StoreAdapter.ViewHolder viewHolder;


    public StoreAdapter(@NonNull ViewModelStoreOwner owner, List<Store> stores, String token) {
        super((Context) owner, 0, stores);
        this.context = (Context) owner;
        this.token = token;
        this.stores = stores;
        this.inflater = LayoutInflater.from(context);

    }

    // Interface to communicate selected store
    public interface OnStoreSelectedListener {
        void onStoreSelected(Store store);
    }

    public void setOnStoreSelectedListener(OnStoreSelectedListener listener) {
        this.listener = listener;
    }


    // Method to update the categories list
    public void setStores(List<Store> stores) {
        this.clear();
        if (stores != null) {
            addAll(stores);
        }
        notifyDataSetChanged();
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the convertView is null, if so, inflate a new view
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.store_item, parent, false);
            viewHolder = new StoreAdapter.ViewHolder();
            viewHolder.storeName = convertView.findViewById(R.id.storeNameTextView);
            viewHolder.storeCity = convertView.findViewById(R.id.storeCityTextView);
            viewHolder.storeLocation = convertView.findViewById(R.id.storeLocationTextView);
            viewHolder.storeImage = convertView.findViewById(R.id.storeImageView);
            viewHolder.storeTotalPrice = convertView.findViewById(R.id.storeTotalPriceTextView);
            viewHolder.btnSelectStore = convertView.findViewById(R.id.radioButtonSelectStore);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (StoreAdapter.ViewHolder) convertView.getTag();
        }

        Store store = stores.get(position);

        viewHolder.storeName.setText(store.getName());
        viewHolder.storeCity.setText(store.getCity());

        viewHolder.storeLocation.setText(store.getNameLocation());

        if (viewHolder != null && viewHolder.storeTotalPrice != null) {
            viewHolder.storeTotalPrice.setText(String.format("₪ %.2f", store.getTotalPrice()));
        }

        String imageResource = store.getImage();
        Glide.with(convertView.getContext())
                .load(imageResource)
                .into(viewHolder.storeImage);

        viewHolder.btnSelectStore.setChecked(position == selectedPosition);

        View.OnClickListener selectStoreListener = view -> {
            selectedPosition = position == selectedPosition ? -1 : position;
            notifyDataSetChanged();
            if (listener != null) {
                listener.onStoreSelected(selectedPosition == -1 ? null : store);
            }
        };


        convertView.setOnClickListener(selectStoreListener);
        viewHolder.btnSelectStore.setOnClickListener(selectStoreListener);

        return convertView;
    }
    // ViewHolder class to optimize the performance of the ListView
    private static class ViewHolder {
        TextView storeName, storeCity, storeTotalPrice, storeLocation;
        ImageView storeImage;

        RadioButton btnSelectStore;


    }
}
