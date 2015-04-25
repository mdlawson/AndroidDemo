package com.mdlawson.app;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.mdlawson.app.model.Item;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

// Adapters serve as a bridge between data structures and view elements
public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemHolder> {

    public static final String API = "http://jsonplaceholder.typicode.com/posts";

    private final Context context;
    RecyclerView.LayoutManager lm;
    List<Item> items;

    // Holders are a nice model for accessing data on a view. Butterknife can be used to get views into props
    public static class ItemHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.item_text) TextView text;
        @InjectView(R.id.item_title) TextView title;
        // each data item is just a string in this case
        public ItemHolder(View v) {
            super(v);
            ButterKnife.inject(this, v);
        }
    }

    public ItemAdapter(Context context, RecyclerView.LayoutManager lm) {
        this.context = context;
        this.lm = lm;
        items = new ArrayList<>();
        refresh();
    }

    @Override // Called when a new view/viewholder is needed, inflates a new one from the layout XML
    public ItemAdapter.ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false));
    }

    @Override
    public void onBindViewHolder(final ItemHolder holder, int position) {
        // Called when a view holder needs data, could be a newly created one or an old one being reused
        Item item = items.get(position);
        holder.title.setText(item.getTitle());
        holder.text.setText(item.getBody());
    }

    @Override // called to get the size of the dataset
    public int getItemCount() {
        return items.size();
    }

    public void refresh() {
        Ion.with(context).load(API) // HTTP GET to the api,
                .as(new TypeToken<List<Item>>() { // try to parse results to a List of Items
                })
                .setCallback(new FutureCallback<List<Item>>() {
                    @Override
                    // Called on success or failure, with exception if one occured
                    public void onCompleted(Exception e, List<Item> result) {
                        if (e != null) {
                            Logger.e(e, "Bum");
                        } else {
                            items.clear();
                            items.addAll(result);
                            notifyDataSetChanged(); // Tells the recycler view everything changed
                            Logger.d("Items Loaded yay");
                        }

                }
            });
    }

    public void create(final Item item) {
        Ion.with(context).load("POST", API) // HTTP Post to the api
                .setJsonPojoBody(item) // convert item to json automatically
                .as(new TypeToken<Item>(){}) // read created item
                .setCallback(new FutureCallback<Item>() {
                    @Override
                    public void onCompleted(Exception e, Item result) {
                        if (e != null) {
                            Logger.e(e, "Bum");
                        } else {
                            items.add(0, result); // add to storage
                            notifyItemInserted(0); // notify the first item changed for animation
                            lm.scrollToPosition(0); // scroll to top to see new item
                            Logger.d("Item added");
                        }
                    }
                });
    }

}