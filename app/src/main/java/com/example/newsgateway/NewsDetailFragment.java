package com.example.newsgateway;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewsDetailFragment extends Fragment {

    private static final String TAG = "NewsDetailFragment";
    public NewsDetailFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static NewsDetailFragment newInstance(Article article,int index,int max) {
        NewsDetailFragment f = new NewsDetailFragment();
        Bundle bdl = new Bundle(1);
        bdl.putSerializable("ARTICLE_DATA", article);
        bdl.putSerializable("INDEX", index);
        bdl.putSerializable("TOTAL_COUNT", max);
        f.setArguments(bdl);
        return f;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragment_layout = inflater.inflate(R.layout.fragment_news_detail, container, false);
        setRetainInstance(true);
        if (getArguments() == null)
            return fragment_layout;

        final Article currentArticle = (Article) getArguments().getSerializable("ARTICLE_DATA");

        if (currentArticle == null)
            return fragment_layout;

        int index = getArguments().getInt("INDEX");
        int total = getArguments().getInt("TOTAL_COUNT");

        final String artileURL = currentArticle.getArticlrURL();

        TextView articleHeadline = fragment_layout.findViewById(R.id.articleheadlinetxt);
        articleHeadline.setText(currentArticle.getArticleTitle());
        articleHeadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickFlag(artileURL);
            }
        });

        TextView articleDate = fragment_layout.findViewById(R.id.articledatetxt);
        if (currentArticle.getArticleDate().contains("null")) {
            articleDate.setVisibility(View.GONE);

        } else {

            //Date date = (currentArticle.getArticleDate().split("T"));

            String[] sampledate = currentArticle.getArticleDate().split("T");
          //  Log.d(TAG, "onCreateView: "+date[0].toString());
            String time = sampledate[1].substring(0,5);
            //articleDate.setText(date[0] +" "+time);
            Date date;
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                date = dateFormat.parse(sampledate[0]);
                DateFormat formatter = new SimpleDateFormat("EEE dd, yyyy"); //If you need time just put specific format for time like 'HH:mm:ss'
                String dateStr = formatter.format(date);
                articleDate.setText(dateStr +" "+ time);

            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }





        }


        TextView articleAuthor = fragment_layout.findViewById(R.id.articleauthortxt);
        if (currentArticle.getAuthorName().contains("null")) {
            articleAuthor.setVisibility(View.GONE);

        } else {
            Log.d(TAG, "onCreateView: "+currentArticle.getAuthorName() +"  " + currentArticle.getAuthorName().length());
            articleAuthor.setText(currentArticle.getAuthorName());
        }


        final ImageView articleImageView = fragment_layout.findViewById(R.id.articleImageView);

        final String imageURl = currentArticle.getUrlToImage();


        if (imageURl.length()<=1) {


            //articleImageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            String uri = "@drawable/noimage";
            int imageResource = getResources().getIdentifier(uri, null, getContext().getPackageName());

            Drawable res = getResources().getDrawable(imageResource);
            articleImageView.setImageDrawable(res);
        } else {
            int wid = fragment_layout.findViewById(R.id.articleImageView).getWidth();
            Picasso.get().load(imageURl)
                     // resize(wid, wid)
                    .error(R.drawable.brokenimage)
                    .placeholder(R.drawable.loading)
                    .into(articleImageView, new Callback() {
                        @Override
                        public void onSuccess() {

                            Log.d(TAG, "onSuccess: " + imageURl);
                            articleImageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    clickFlag(artileURL);
                                }
                            });
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.d(TAG, "onError: " + imageURl);
                        }
                    });
        }






        TextView articleDetails = fragment_layout.findViewById(R.id.articledetailtxt);
        if(currentArticle.getArticleDescription().length() <= 1)
        {
            articleDetails.setVisibility(View.GONE);
        }
        else
        {
            articleDetails.setText(currentArticle.getArticleDescription());
            articleDetails.setMovementMethod(new ScrollingMovementMethod());
            articleDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickFlag(artileURL);
                }
            });
        }


        TextView pageNum = fragment_layout.findViewById(R.id.pagetxt);
        pageNum.setText(String.format(Locale.US, "%d of %d", index, total));


        return fragment_layout;
    }
    public void clickFlag(String name) {

        Log.d(TAG, "clickFlag: "+name);
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(name));
        startActivity(i);


    }

}
