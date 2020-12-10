package com.example.fitnessapp.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.fitnessapp.R;

public class NormalDietMenuActivity extends AppCompatActivity {

    private ListView mListView11;
    private final String[] foodGroupsArray ={"Fruits","Vegetables","Grains","Dairy","Protein"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal_diet_menu);
        setupListView();
    }

    private void setupListView() {
        mListView11 = findViewById(R.id.balancedDietList);
        mListView11.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, foodGroupsArray));
        ListUtils3.setDynamicHeight(mListView11);
    }

    public static class ListUtils3{
        public static void setDynamicHeight(ListView mListView) {
            ListAdapter mListAdapter = mListView.getAdapter();
            if (mListAdapter == null) {
                // when adapter is null
                return;
            }
            int height = 0;
            int desiredWidth = View.MeasureSpec.makeMeasureSpec(mListView.getWidth(), View.MeasureSpec.UNSPECIFIED);
            for (int i = 0; i < mListAdapter.getCount(); i++) {
                View listItem = mListAdapter.getView(i, null, mListView);
                listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                height += listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = mListView.getLayoutParams();
            params.height = height + (mListView.getDividerHeight() * (mListAdapter.getCount() - 1));
            mListView.setLayoutParams(params);
            mListView.requestLayout();
        }
    }
}

