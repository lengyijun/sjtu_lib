package com.example.steven.sjtu_lib_v1.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.steven.sjtu_lib_v1.R;
import com.example.steven.sjtu_lib_v1.adapter.BookItemAdapter;
import com.example.steven.sjtu_lib_v1.view.SuperSwipeRefreshLayout;
import com.yolanda.multiasynctask.MultiAsynctask;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import okhttp3.Call;

public class MainActivity extends AppCompatActivity {
    @Bind(R.id.swipe_refresh)SuperSwipeRefreshLayout superSwipeRefreshLayout;
    @Bind(R.id.listView)SwipeMenuListView plistiview;

//    footerview
    ProgressBar footerProgressBar;
    ImageView footerImageView;
    TextView footerTextView;

    String base_url="http://ourex.lib.sjtu.edu.cn/primo_library/libweb/action/search." +
            "do?fn=search&tab=default_tab&vid=chinese&scp.scps=scope%3A%28SJT%29%2Csc" +
            "ope%3A%28sjtu_metadata%29%2Cscope%3A%28sjtu_sfx%29%2Cscope%3A%28sjtulib" +
            "zw%29%2Cscope%3A%28sjtulibxw%29%2CDuxiuBook&vl%28freeText0%29=";
    String url;

    String NextUrls;
    public List<Element> book_elements=new ArrayList<Element>();
    BookItemAdapter bookItemAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        get_intent_extra();
        plistview_init();
        superSwipelayout_init();

        get_list_from_url(url);
    }

    private void superSwipelayout_init() {
        superSwipeRefreshLayout.setTargetScrollWithLayout(false);
        superSwipeRefreshLayout.setFooterView(createFootview());
        superSwipeRefreshLayout.setOnPushLoadMoreListener(new SuperSwipeRefreshLayout.OnPushLoadMoreListener() {
            @Override
            public void onLoadMore() {
                new NextAsyncTask().execute();
            }

            @Override
            public void onPushDistance(int distance) {

            }

            @Override
            public void onPushEnable(boolean enable) {

            }
        });
    }

    private void plistview_init() {
        SwipeMenuCreator creator=new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem more_info=new SwipeMenuItem(getApplicationContext());
                more_info.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9, 0xCE)));
                more_info.setWidth(dp2px(90));
                more_info.setTitle("详细信息");
                more_info.setTitleSize(18);
                more_info.setTitleColor(Color.WHITE);
                menu.addMenuItem(more_info);
            }
        };
        plistiview.setMenuCreator(creator);
        bookItemAdapter=new BookItemAdapter(this, 0, book_elements);
        plistiview.setAdapter(bookItemAdapter);
        plistiview.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        show_detail_info(position);
                }
                return false;
            }
        });
    }

    private void show_detail_info(int position) {
        Element doc=book_elements.get(position);
        Elements MultipleLink=doc.getElementsByClass("EXLBriefResultsDisplayMultipleLink");
        Intent intent=new Intent();
        if(MultipleLink.isEmpty()){
            Element tosend=doc.getElementsByClass("EXLSummaryContainer").first();
            tosend.getElementsByTag("script").remove();
            tosend.getElementsByClass("EXLResultAvailability").remove();
            tosend.getElementsByTag("noscript").remove();
            
            intent.setClass(MainActivity.this,Single_detail.class);
            intent.putExtra("detail", tosend.toString());
            startActivity(intent);
        }else {
            intent.setClass(MainActivity.this,MainActivity.class);
            intent.putExtra("url",MultipleLink.attr("href"));
            startActivity(intent);
        }
    }

    private void get_intent_extra() {
        String bookname=getIntent().getExtras().getString("bookname");
        String url_intent=getIntent().getExtras().getString("url");
        if (bookname!=null){
            this.url=base_url+bookname;
        }else{
            if(url_intent!=null){
                this.url=url_intent;
            }
        }
    }

    private View createFootview() {
        View footerView= LayoutInflater.from(superSwipeRefreshLayout.getContext())
            .inflate(R.layout.layout_footer,null);
        footerProgressBar = (ProgressBar) footerView
                .findViewById(R.id.footer_pb_view);
        footerImageView = (ImageView) footerView
                .findViewById(R.id.footer_image_view);
        footerTextView = (TextView) footerView
                .findViewById(R.id.footer_text_view);
        footerProgressBar.setVisibility(View.GONE);
        footerImageView.setVisibility(View.VISIBLE);
        footerImageView.setImageResource(R.drawable.down_arrow);
        footerTextView.setText("上拉加载更多...");
        return footerView;
    }

    private void get_list_from_url(String url) {
        OkHttpUtils .get()
                .url(url)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        Toast.makeText(getApplicationContext(), "fail to connect", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response) {
                        Document doc = Jsoup.parse(response);

                        new Refrsh_next_url().execute(doc);
                        Elements elements = doc.getElementsByClass("EXLSummary");
                        for (Element i : elements) {
                            if (!i.getElementsMatchingText("馆藏信息").isEmpty()) {
                                book_elements.add(i);
                            }
                        }
                    }
                });
    }

    @OnItemClick(R.id.listView) void onItemSelected(int position){
        Toast.makeText(getApplicationContext(),book_elements.size()+"<<"+position,Toast.LENGTH_SHORT).show();
        Book_detail_dialog bookDetail=new Book_detail_dialog(book_elements.get(position));
        bookDetail.show(getFragmentManager(), "book");
    }

    public class NextAsyncTask extends MultiAsynctask<Void,Void,Void> {
        int saved_postion;

        @Override
        public void onResult(Void Void) {
            bookItemAdapter.notifyDataSetChanged();
            plistiview.setSelection(saved_postion);
            Toast.makeText(getApplicationContext(),"nextasynctask"+book_elements.size(),Toast.LENGTH_SHORT).show();
        }

        @Override
        public Void onTask(Void... params) {
            synchronized (this){
                while (NextUrls.length() == 0 || book_elements.size() == 0) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            get_list_from_url(NextUrls);

            return null;
        }

        @Override
        public void onPrepare() {
            saved_postion=plistiview.getFirstVisiblePosition();
        }
    }

    private class Refrsh_next_url extends MultiAsynctask<Object,Void,Elements>{

        @Override
        public Elements onTask(Object... objects) {
            Element come_in= (Element) objects[0];
            Elements elements=come_in .getElementsByAttributeValue("title", "下一页");
            return elements;
        }

        @Override
        public void  onResult(Elements elements) {
            bookItemAdapter.notifyDataSetChanged();
            if(elements.size()!=0){
                NextUrls=elements.first().attr("href");
            }
        }

    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }
}
