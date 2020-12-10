package com.example.fitnessapp.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.fitnessapp.R;

public class WeightLossMenuActivity extends AppCompatActivity {

    private ListView mListView1, mListView2, mListView3;
    private final String[] proteinArray = {"Beef", "Chicken", "Pork", "Lamb", "Eggs", "Seafood",
            "Plant-based(Tofu)"};
    private final String[] vegeArray = {"Broccoli", "Cauliflower", "Spinach", "Tomatoes", "Kale",
            "Brussels Sprouts", "Cabbage", "Swiss chard", "Lettuce", "Cucumber"};
    private final String[] fatsArray = {"Olive oil", "Avocado Oil"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_loss_menu);
        initializeUI();
        setupAdapters();
        setupListViews();
    }

    private void initializeUI() {
        mListView1 = findViewById(R.id.weightLostProteinListView);
        mListView2 = findViewById(R.id.weightLostVegeListView);
        mListView3 = findViewById(R.id.weightLostFatsListView);
    }

    private void setupAdapters() {
        mListView1.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, proteinArray));
        mListView2.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, vegeArray));
        mListView3.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, fatsArray));
    }

    private void setupListViews() {
        ListUtils.setDynamicHeight(mListView1);
        ListUtils.setDynamicHeight(mListView2);
        ListUtils.setDynamicHeight(mListView3);
    }

    public static class ListUtils {
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
