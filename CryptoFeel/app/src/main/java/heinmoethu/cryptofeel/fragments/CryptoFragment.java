package heinmoethu.cryptofeel.fragments;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

import heinmoethu.cryptofeel.R;
import heinmoethu.cryptofeel.CryptoCollection;
import heinmoethu.cryptofeel.activities.CryptoActivity;
import heinmoethu.cryptofeel.adapters.TweetListAdapter;
import heinmoethu.cryptofeel.asyncTasks.UpdateItem;
import heinmoethu.cryptofeel.models.CryptoModel;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class CryptoFragment extends Fragment {
    private CryptoModel crypto;
    private TweetListAdapter adapter;
    Button btn_cell_updated_time;
    ImageView iv_cell_icon;
    TextView tv_cell_rank, tv_cell_title, tv_cell_price, tv_cell_change, tv_cell_sentiment;
    ProgressBar pb_cell_loading;
    RecyclerView rv_tweets;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String cryptoId = getArguments().getString(CryptoActivity.EXTRA_CRYPTO_ID);
        this.crypto = CryptoCollection.GetInstance().getCrypto(cryptoId);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crypto, container, false);
        pb_cell_loading= v.findViewById(R.id.pb_cell_loading);
        btn_cell_updated_time = v.findViewById(R.id.btn_cell_updated_time);
        iv_cell_icon = v.findViewById(R.id.iv_cell_icon);
        tv_cell_rank = v.findViewById(R.id.tv_cell_rank);
        tv_cell_title = v.findViewById(R.id.tv_cell_title);
        tv_cell_price = v.findViewById(R.id.tv_cell_price);
        tv_cell_change = v.findViewById(R.id.tv_cell_change);
        tv_cell_sentiment = v.findViewById(R.id.tv_cell_sentiment);

        updateViews();

        rv_tweets = v.findViewById(R.id.rv_tweets);
        this.adapter = new TweetListAdapter(this.crypto.getTweets());
        rv_tweets.setAdapter(adapter);
        rv_tweets.setLayoutManager(new LinearLayoutManager(getActivity()));

        btn_cell_updated_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new UpdateItem(adapter,btn_cell_updated_time,pb_cell_loading).execute("https://api.coinmarketcap.com/v1/ticker/", crypto.getId());
            }
        });

        return v;
    }

    private void updateViews() {
        tv_cell_title.setText(this.crypto.getTitle());
        tv_cell_change.setText("(" + Double.toString(this.crypto.getChange()) + "%)");
        if (crypto.getChange() < 0) {
            tv_cell_change.setTextColor(Color.parseColor("#DD0000"));
        } else {
            tv_cell_change.setTextColor(Color.parseColor("#00DD00"));
        }
        tv_cell_price.setText("$ " + Double.toString(this.crypto.getPrice()));
        tv_cell_rank.setText(Integer.toString(this.crypto.getRank()));
        try {
            Field id = R.drawable.class.getDeclaredField(crypto.getImg());
            iv_cell_icon.setImageResource(id.getInt(id));
        } catch (NoSuchFieldException e) {
            iv_cell_icon.setImageResource(R.drawable.icon_placeholder);
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        btn_cell_updated_time.setText("Updated: " + this.crypto.getDate().toString(DateTimeFormat.mediumTime()));
        tv_cell_sentiment.setText(String.format("%.2f", crypto.getSentiment()));
        if (crypto.getSentiment() < 0) {
            tv_cell_sentiment.setTextColor(Color.parseColor("#DD0000"));
        } else {
            tv_cell_sentiment.setTextColor(Color.parseColor("#00DD00"));
        }
    }
}
