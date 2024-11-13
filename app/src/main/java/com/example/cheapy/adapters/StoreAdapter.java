package com.example.cheapy.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cheapy.Cart.CartManager;
import com.example.cheapy.R;
import com.example.cheapy.entities.Category;
import com.example.cheapy.entities.Item;
import com.example.cheapy.entities.Store;
import com.example.cheapy.viewModels.CartViewModel;
import com.example.cheapy.viewModels.CartViewModelFactory;

import java.text.BreakIterator;
import java.util.List;
import java.util.stream.Collectors;

public class StoreAdapter extends ArrayAdapter<Store> {

    private int selectedPosition = -1;
    private List<Store> stores;
    private LayoutInflater inflater;

    private OnStoreSelectedListener listener;

    private Context context;

    private CartViewModel cartViewModel;

    private String token;

    StoreAdapter.ViewHolder viewHolder;


    public StoreAdapter(@NonNull ViewModelStoreOwner owner, List<Store> stores, String token) {
        super((Context) owner, 0, stores);
        this.context = (Context) owner;
        this.token = token;
        this.stores = stores;
        this.inflater = LayoutInflater.from(context);
        CartViewModelFactory factory = new CartViewModelFactory(token);
        this.cartViewModel = new ViewModelProvider(owner, factory).get(CartViewModel.class);

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

    public void displayTotalPrice(Store store) {
        List<Item> storeItems = CartManager.getInstance().getCartProducts().stream()
                .filter(item -> item.getStore() != null && item.getStore().getName().equals(store.getName())) // Ensure item and store are not null
                .collect(Collectors.toList());

        cartViewModel.fetchTotalPriceByStore(token, store.getName(), storeItems);
        cartViewModel.getTotalPriceLiveData().observe((LifecycleOwner) context, total -> {
            if (viewHolder != null && viewHolder.storeTotalPrice != null) {
                viewHolder.storeTotalPrice.setText(String.format("₪ %.2f", total));
            }
        });
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the convertView is null, if so, inflate a new view
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.store_item, parent, false);
            viewHolder = new StoreAdapter.ViewHolder();
            viewHolder.storeName = convertView.findViewById(R.id.storeNameTextView);
            viewHolder.storeCity = convertView.findViewById(R.id.storeCityTextView);
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
        displayTotalPrice(store);

        String imageResource = store.getImage();
        Glide.with(convertView.getContext())
                .load(imageResource)
                .into(viewHolder.storeImage);

        viewHolder.btnSelectStore.setChecked(position == selectedPosition);

        View.OnClickListener selectStoreListener = view -> {
            selectedPosition = position == selectedPosition ? -1 : position; // Toggle selection
            notifyDataSetChanged();
            if (listener != null) {
                listener.onStoreSelected(selectedPosition == -1 ? null : store); // Send selected store or null
            }
        };


        convertView.setOnClickListener(selectStoreListener);
        viewHolder.btnSelectStore.setOnClickListener(selectStoreListener);

        return convertView;
    }
    // ViewHolder class to optimize the performance of the ListView
    private static class ViewHolder {
        TextView storeName, storeCity, storeTotalPrice;
        ImageView storeImage;

        RadioButton btnSelectStore;


    }
}
