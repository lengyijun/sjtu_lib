package com.example.steven.sjtu_lib_v1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
    @Bind(R.id.listView)ListView plistiview;

//    footerview
    ProgressBar footerProgressBar;
    ImageView footerImageView;
    TextView footerTextView;

    String base_url="http://ourex.lib.sjtu.edu.cn/primo_library/libweb/action/search." +
            "do?fn=search&tab=default_tab&vid=chinese&scp.scps=scope%3A%28SJT%29%2Csc" +
            "ope%3A%28sjtu_metadata%29%2Cscope%3A%28sjtu_sfx%29%2Cscope%3A%28sjtulib" +
            "zw%29%2Cscope%3A%28sjtulibxw%29%2CDuxiuBook&vl%28freeText0%29=";
    String url;

    static public List<String> NextUrls=new ArrayList<String>();
    public List<Element> book_elements=new ArrayList<Element>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        get_intent_extra();

        plistiview.setAdapter(new BookItemAdapter(this, 0, book_elements));
        get_list_from_url(url);

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

    private void get_intent_extra() {
        String bookname=getIntent().getExtras().getString("bookname");
        this.url=base_url+bookname;
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
        Book_detail bookDetail=new Book_detail(book_elements.get(position));
        bookDetail.show(getFragmentManager(), "book");
    }

    public class NextAsyncTask extends MultiAsynctask<Void,Void,Void> {
        int saved_postion;

        @Override
        public void onResult(Void Void) {
            plistiview.invalidateViews();
            plistiview.setSelection(saved_postion);
            Toast.makeText(getApplicationContext(),"nextasynctask"+book_elements.size(),Toast.LENGTH_SHORT).show();
        }

        @Override
        public Void onTask(Void... params) {
            synchronized (this){
                while (NextUrls.size() == 0 || book_elements.size() == 0) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            get_list_from_url(NextUrls.get(0));
            NextUrls.clear();

            return null;
        }

        @Override
        public void onPrepare() {
//            saved_postion=wholeBooks.size()-1;
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
//            Toast.makeText(getApplicationContext(),"refresh_url"+book_elements.size(),Toast.LENGTH_SHORT).show();
            plistiview.invalidateViews();
            if(elements.size()!=0){
                MainActivity.this.NextUrls.add(elements.first().attr("href"));
            }
        }

    }

}
