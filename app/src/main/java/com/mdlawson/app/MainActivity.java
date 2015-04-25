package com.mdlawson.app;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.mdlawson.app.events.ItemsLoaded;
import com.mdlawson.app.model.Item;

// Simple activity
public class MainActivity extends BaseActivity {

    // Butterknife injections, automatically grabs view with given id from the the layout and assigns it to property
    @InjectView(R.id.items_list) RecyclerView itemsView;
    @InjectView(R.id.swipe) SwipeRefreshLayout swipe;
    // Item adapter represents our data model
    private ItemAdapter items;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // When activity is created, load the layout
        // Non butterknife/layout reliant things can happen here
        bus.register(this); // we must have event listeners (onEvent) defined
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        itemsView.setHasFixedSize(true);

        // Recycleviews are an efficient way to display a list of items
        // Linearlayout manager makes a normal listy thing
        RecyclerView.LayoutManager lm = new LinearLayoutManager(this);
        itemsView.setLayoutManager(lm);

        // Create a new item adapter, represents a data store
        items = new ItemAdapter(this, lm);
        itemsView.setAdapter(items); // bind it to the view
        itemsView.setItemAnimator(new DefaultItemAnimator());

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                items.refresh();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu, loads menu items into action bar from xml
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) { // Handle action button presses here
            case R.id.action_settings: {
                return true;
            }
            case R.id.action_add: {
                //items.create(new Item("New item", "Bleeeh"));
                createDialog();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void createDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.item_create)
                .setView(R.layout.create_dialog)
                .setCancelable(true)
                .setPositiveButton(R.string.create_action, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DialogHolder dialog = new DialogHolder(dialogInterface);
                        items.create(new Item(dialog.title.getText().toString(),
                                              dialog.body.getText().toString()));
                    }
                }).show();
    }

    public void onEvent(ItemsLoaded e) {
        swipe.setRefreshing(false);
    }

    class DialogHolder {
        @InjectView(R.id.edit_title) EditText title;
        @InjectView(R.id.edit_body) EditText body;
        public DialogHolder(DialogInterface dialogInterface) {
            ButterKnife.inject(this, (Dialog) dialogInterface);
        }
    }
}
