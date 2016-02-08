package com.example.steven.learn_paging;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.paging.listview.PagingListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @Bind(R.id.paging_list_view) PagingListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        List<String> data=new ArrayList<String>();
        data.add("hello");
        data.add("hello");
        data.add("hello");
        data.add("hello");
        data.add("hello");
        data.add("hello");
        data.add("hello");
        data.add("hello");
        data.add("hello");
        data.add("hello");
        data.add("hello");
        data.add("hello");
        data.add("hello");
        data.add("hello");
        data.add("hello");
        data.add("hello");

        lv.setHasMoreItems(true);
        lv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, data));
        lv.setPagingableListener(new PagingListView.Pagingable() {
            @Override
            public void onLoadMoreItems() {
                Toast.makeText(getApplicationContext(),"hello",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
