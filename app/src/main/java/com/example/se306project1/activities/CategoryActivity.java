package com.example.se306project1.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.se306project1.R;
import com.example.se306project1.adapters.CategoryAdapter;
import com.example.se306project1.adapters.TopPickAdapter;
import com.example.se306project1.database.FireStoreCallback;
import com.example.se306project1.database.LikesDatabase;
import com.example.se306project1.models.TechnicCategory;
import com.example.se306project1.models.StarWarCategory;
import com.example.se306project1.models.CityCategory;
import com.example.se306project1.models.ICategory;
import com.example.se306project1.models.IProduct;
import com.example.se306project1.utilities.ActivityState;
import com.example.se306project1.utilities.ContextState;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CategoryActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private List<ICategory> categories;

    Drawer drawer;
    ProductSearcher productSearcher;
    ViewHolder viewHolder;

    class ViewHolder {
        private final RecyclerView categoryRecyclerView = findViewById(R.id.category_recycler_view);
        private final RecyclerView topPickRecyclerView = findViewById(R.id.top_pick_product_recycler_view);
        ProgressBar topPickProgressbar = findViewById(R.id.top_pick_progressbar);
        ;
    }

    public static void start(AppCompatActivity activity) {
        Intent intent = new Intent(activity.getBaseContext(), CategoryActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        ActivityState.getInstance().setCurrentActivity(this);
        ContextState.getInstance().setCurrentContext(getApplicationContext());

        this.viewHolder = new ViewHolder();
        this.categories = new ArrayList<>();
        this.drawer = new Drawer();
        this.productSearcher = new ProductSearcher();

        this.fillTopPicks(4);
        this.fillCategories();

        this.setCategoryAdapter();
        this.drawer.initialise();
        this.productSearcher.initialise();
    }

    private void setCategoryAdapter() {
        CategoryAdapter categoryAdapter = new CategoryAdapter(this.categories);
        RecyclerView.LayoutManager categoryLayoutManager = new LinearLayoutManager(
                getApplicationContext(),
                LinearLayoutManager.VERTICAL,
                false
        );
        this.viewHolder.categoryRecyclerView.setLayoutManager(categoryLayoutManager);
        this.viewHolder.categoryRecyclerView.setItemAnimator(new DefaultItemAnimator());
        this.viewHolder.categoryRecyclerView.setAdapter(categoryAdapter);
    }

    private void setTopProductAdapter(List<IProduct> list) {
        RecyclerView.LayoutManager topPickLayoutManager = new LinearLayoutManager(
                getApplicationContext(),
                LinearLayoutManager.HORIZONTAL,
                false
        );
        this.viewHolder.topPickRecyclerView.setLayoutManager(topPickLayoutManager);
        this.viewHolder.topPickRecyclerView.setItemAnimator(new DefaultItemAnimator());
        this.viewHolder.topPickRecyclerView.setAdapter(new TopPickAdapter(list));
        this.viewHolder.topPickProgressbar.setVisibility(View.GONE);
        this.viewHolder.topPickRecyclerView.setVisibility(View.VISIBLE);
    }

    private void fillCategories() {
        this.categories.add(new TechnicCategory());
        this.categories.add(new StarWarCategory());
        this.categories.add(new CityCategory());
    }

    private void fillTopPicks(int size) {
        LikesDatabase likesDatabase = LikesDatabase.getInstance();
        likesDatabase.getAllProducts(new FireStoreCallback() {
            @Override
            public <T> void Callback(T value) {
                List<IProduct> products = (List<IProduct>) value;
                products.sort(new Comparator<IProduct>() {
                    @Override
                    public int compare(IProduct p1, IProduct p2) {
                        return (p2.getLikesNumber() - p1.getLikesNumber());
                    }
                });
                List<IProduct> res = new ArrayList<>();
                res.addAll(products.subList(0, size));
                setTopProductAdapter(res);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return this.drawer.setUp(item, super.onOptionsItemSelected(item));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return this.productSearcher.onCreateOptionsMenu(menu, super.onCreateOptionsMenu(menu));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return this.drawer.onNavigationItemSelected(item, true);
    }

}
