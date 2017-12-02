package heinmoethu.cryptofeel.asyncTasks;


import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import heinmoethu.cryptofeel.CryptoCollection;
import heinmoethu.cryptofeel.models.CryptoModel;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class UpdateItem extends AsyncTask<String,String,Void> {
    Button ut;
    RecyclerView.Adapter adapter;
    ProgressBar pb;

    public UpdateItem(RecyclerView.Adapter adapter,Button ut,  ProgressBar pb){
        this.ut=ut;
        this.adapter=adapter;
        this.pb=pb;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        ut.setVisibility(View.INVISIBLE);
        pb.setVisibility(View.VISIBLE);
    }

    @Override
    protected Void doInBackground(String... params) {
        HttpsURLConnection connection = null;
        BufferedReader reader = null;
        StringBuilder builder = null;
        try {
            URL url = new URL(params[0] + params[1]);
            connection = (HttpsURLConnection) url.openConnection();
            connection.connect();

            InputStream stream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(stream));

            builder = new StringBuilder();
            String line = "";

            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null)
                connection.disconnect();
            try {
                if (reader != null)
                    reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (builder != null) {
            try {
                CryptoModel crypto = CryptoCollection.GetInstance().getCrypto(params[1]);
                JSONArray list = new JSONArray(builder.toString());
                ConfigurationBuilder cb = new ConfigurationBuilder().setDebugEnabled(true).setOAuthConsumerKey("b3f5iOVkPMXKERjbixFtHgQZu")
                        .setOAuthConsumerSecret("rJFx6rk3izots2w3tJ5meOVcBjafDX5DKzXlx0zegarrdp20av")
                        .setOAuthAccessToken("774286002663796736-Ag8jzsIfCb300vILqcwdJqqufu1HWIf")
                        .setOAuthAccessTokenSecret("NVwS4IivV40ckELlnJ08Ikw5YzsPvHmteKoedU7ch9DHv");
                Twitter twitter = new TwitterFactory(cb.build()).getInstance();
                JSONObject item = list.getJSONObject(0);
                crypto.setPrice(+item.getDouble("price_usd"));
                crypto.setImg(item.getString("symbol").toLowerCase());
                crypto.setRank(item.getInt("rank"));
                crypto.setChange(item.getDouble("percent_change_1h"));
                crypto.setDate(DateTime.now());

                Query query = new Query(item.getString("name") + " -filter:retweets -filter:links -filter:replies -filter:images");
                query.setLang("en");
                query.setCount(100);
                ArrayList<String> tweetList = crypto.getTweets();
                tweetList.clear();
                try {
                    QueryResult result = twitter.search(query);
                    List<twitter4j.Status> tweets = result.getTweets();
                    double sentiment = 0;
                    HashMap<String, Boolean> dict = CryptoCollection.GetInstance().getDict();
                    for (int y = 0; y < tweets.size(); ++y) {
                        String sentence = tweets.get(y).getText();
                        if (y < 50)
                            tweetList.add(sentence);
                        String[] words = sentence.trim().split("\\s+");
                        for (String word : words) {
                            Boolean b = dict.get(word);
                            if (b != null) {
                                if (b == Boolean.TRUE)
                                    sentiment++;
                                else
                                    sentiment--;
                            }
                        }
                    }
                    crypto.setSentiment(sentiment / tweets.size());
                } catch (TwitterException e) {
                    e.printStackTrace();
                    tweetList.add("Rate Limit Reached");
                    crypto.setSentiment(0);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        ut.setVisibility(View.VISIBLE);
        pb.setVisibility(View.INVISIBLE);
        adapter.notifyDataSetChanged();
    }
}
