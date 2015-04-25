package com.mdlawson.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import butterknife.ButterKnife;
import butterknife.InjectView;

// Base activity, all activities should extend this to get butterknife/actionbar
public class BaseActivity extends AppCompatActivity {
    @InjectView(R.id.toolbar) Toolbar toolbar;
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        ButterKnife.inject(this);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
    }
}
    