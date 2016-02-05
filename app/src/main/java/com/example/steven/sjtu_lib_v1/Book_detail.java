package com.example.steven.sjtu_lib_v1;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.steven.sjtu_lib_v1.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link book_detail.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link book_detail#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Book_detail extends DialogFragment {

    private String url="http://ourex.lib.sjtu.edu.cn/primo_library/libweb/action/display.do?tabs=locationsTab&gathStatTab=true&ct=display&fn=search&doc=sjtulibxw000311379&indx=1&recIds=sjtulibxw000311379&recIdxs=0&elementId=0&renderMode=poppedOut&displayMode=full&frbrVersion=3&dscnt=0&scp.scps=scope%3A%28SJT%29%2Cscope%3A%28sjtu_metadata%29%2Cscope%3A%28sjtu_sfx%29%2Cscope%3A%28sjtulibzw%29%2Cscope%3A%28sjtulibxw%29%2CDuxiuBook&tab=default_tab&dstmp=1454668891915&vl(freeText0)=git&vid=chinese";
    private Element element;
    private OnFragmentInteractionListener mListener;
    AlertDialog.Builder builder;

//    public Book_detail(Element element) {
//        this.element=element;
//    }

    public Book_detail() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment book_detail.
     */
    // TODO: Rename and change types and number of parameters
//    public static book_detail newInstance(String param1, String param2) {
//        book_detail fragment = new book_detail();
//        Bundle args = new Bundle();
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
        String temp=get_string(url);
        builder=new AlertDialog.Builder(getActivity());
        builder.setTitle("BOOK");
        builder.setMessage(temp);
        return builder.show();
    }

    private String get_string(String url) {
        final List<String> data = new ArrayList<String>();
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder()
                    .url(url)
                    .build();
        Call call=client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String html = response.body().string();
                Document doc = Jsoup.parse(html);
                Elements elements = doc.getElementsByClass("EXLLocationTableColumn3");
                for (Element i : elements) {
                    data.add(elements.text());
                }
            }
        });
        return data.toString();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TextView textView = new TextView(getActivity());
        textView.setText(R.string.hello_blank_fragment);
        return textView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
