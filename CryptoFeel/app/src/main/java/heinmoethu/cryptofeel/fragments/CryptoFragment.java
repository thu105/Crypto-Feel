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
//                btn_cell_updated_time.setVisibility(View.GONE);
//                pb_cell_loading.setVisibility(View.VISIBLE);
//                try {
//                    String result = new UpdateItem().execute("https://api.coinmarketcap.com/v1/ticker/", crypto.getId()).get();
//                    updateViews();
//                    adapter = new TweetListAdapter(crypto.getTweets());
//                    rv_tweets.setAdapter(adapter);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                } catch (ExecutionException e) {
//                    e.printStackTrace();
//                }
//                pb_cell_loading.setVisibility(View.GONE);
//                btn_cell_updated_time.setVisibility(View.VISIBLE);
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

//    public class UpdateItem extends AsyncTask<String, String, String> {
//
//        @Override
//        protected String doInBackground(String... params) {
//            HttpsURLConnection connection = null;
//            BufferedReader reader = null;
//            StringBuilder builder = null;
//            try {
//                URL url = new URL(params[0] + params[1]);
//                connection = (HttpsURLConnection) url.openConnection();
//                connection.connect();
//
//                InputStream stream = connection.getInputStream();
//                reader = new BufferedReader(new InputStreamReader(stream));
//
//                builder = new StringBuilder();
//                String line = "";
//
//                while ((line = reader.readLine()) != null) {
//                    builder.append(line);
//                }
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            } finally {
//                if (connection != null)
//                    connection.disconnect();
//                try {
//                    if (reader != null)
//                        reader.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            if (builder != null) {
//                try {
//                    CryptoModel crypto = CryptoCollection.GetInstance().getCrypto(params[1]);
//                    JSONArray list = new JSONArray(builder.toString());
//                    ConfigurationBuilder cb = new ConfigurationBuilder().setDebugEnabled(true).setOAuthConsumerKey("b3f5iOVkPMXKERjbixFtHgQZu")
//                            .setOAuthConsumerSecret("rJFx6rk3izots2w3tJ5meOVcBjafDX5DKzXlx0zegarrdp20av")
//                            .setOAuthAccessToken("774286002663796736-Ag8jzsIfCb300vILqcwdJqqufu1HWIf")
//                            .setOAuthAccessTokenSecret("NVwS4IivV40ckELlnJ08Ikw5YzsPvHmteKoedU7ch9DHv");
//                    Twitter twitter = new TwitterFactory(cb.build()).getInstance();
//                    JSONObject item = list.getJSONObject(0);
//                    crypto.setPrice(+item.getDouble("price_usd"));
//                    crypto.setImg(item.getString("symbol").toLowerCase());
//                    crypto.setRank(item.getInt("rank"));
//                    crypto.setChange(item.getDouble("percent_change_1h"));
//                    crypto.setDate(DateTime.now());
//
//                    Query query = new Query(item.getString("name") + " -filter:retweets -filter:links -filter:replies -filter:images");
//                    query.setCount(1000);
//                    query.setLang("en");
//                    ArrayList<String> tweetList = new ArrayList<>();
//                    try {
//                        QueryResult result = twitter.search(query);
//                        List<twitter4j.Status> tweets = result.getTweets();
//                        double sentiment = 0;
//                        HashMap<String, Boolean> dict = CryptoCollection.GetInstance().getDict();
//                        for (int y = 0; y < tweets.size(); ++y) {
//                            String sentence = tweets.get(y).getText();
//                            if (y < 50)
//                                tweetList.add(sentence);
//                            String[] words = sentence.trim().split("\\s+");
//                            for (String word : words) {
//                                Boolean b = dict.get(word);
//                                if (b != null) {
//                                    if (b == Boolean.TRUE)
//                                        sentiment++;
//                                    else
//                                        sentiment--;
//                                }
//                            }
//                        }
//                        crypto.setSentiment(sentiment / tweets.size());
//                    } catch (TwitterException e) {
//                        e.printStackTrace();
//                        tweetList.add("Rate Limit Reached");
//                        crypto.setSentiment(0);
//                    }
//                    crypto.setTweets(tweetList);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//            return null;
//        }
////        @Override
////        protected void onPostExecute(String result) {
////            super.onPostExecute(result);
////            updateViews();
////            adapter = new TweetListAdapter(crypto.getTweets());
////            rv_tweets.setAdapter(adapter);
////        }
//    }
}
