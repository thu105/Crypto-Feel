package heinmoethu.cryptofeel.asyncTasks;

import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
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
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class UpdateList extends AsyncTask<String,Integer,Void> {
    private RecyclerView.Adapter adapter;
    private SwipeRefreshLayout srl;
    private ProgressBar pb;

    public UpdateList(RecyclerView.Adapter adapter,SwipeRefreshLayout srl, ProgressBar pb){
        this.adapter=adapter;
        this.srl=srl;
        this.pb = pb;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        srl.setVisibility(View.INVISIBLE);
        pb.setProgress(0);
        pb.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        pb.setProgress(pb.getProgress()+values[0]);
    }

    @Override
    protected Void doInBackground(String... params) {
        HttpsURLConnection connection = null;
        BufferedReader reader = null;
        StringBuilder builder = null;
        try {
            URL url = new URL(params[0]);
            connection = (HttpsURLConnection) url.openConnection();
            connection.connect();

            InputStream stream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(stream));

            builder = new StringBuilder();
            String line;

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
        ArrayList<CryptoModel> cryptos = new ArrayList<>();
        HashMap<String,Boolean> dict = CryptoCollection.GetInstance().getDict();
        if (builder != null) {
            try {
                CryptoModel crypto;
                JSONArray list = new JSONArray(builder.toString());
                ConfigurationBuilder cb = new ConfigurationBuilder().setDebugEnabled(true).setOAuthConsumerKey("b3f5iOVkPMXKERjbixFtHgQZu")
                        .setOAuthConsumerSecret("rJFx6rk3izots2w3tJ5meOVcBjafDX5DKzXlx0zegarrdp20av")
                        .setOAuthAccessToken("774286002663796736-Ag8jzsIfCb300vILqcwdJqqufu1HWIf")
                        .setOAuthAccessTokenSecret("NVwS4IivV40ckELlnJ08Ikw5YzsPvHmteKoedU7ch9DHv");
                Twitter twitter = new TwitterFactory(cb.build()).getInstance();
                ArrayList<String> tweetList;


                for (int x = 0; x < list.length(); ++x) {
                    crypto = new CryptoModel();
                    JSONObject item = list.getJSONObject(x);
                    crypto.setId(item.getString("id"));
                    crypto.setTitle(item.getString("name") + " (" + item.getString("symbol") + ")");
                    crypto.setPrice(item.getDouble("price_usd"));
                    crypto.setImg(item.getString("symbol").toLowerCase());
                    crypto.setRank(item.getInt("rank"));
                    crypto.setChange(item.getDouble("percent_change_1h"));
                    crypto.setDate(DateTime.now());

                    Query query = new Query(item.getString("name") + " -filter:retweets -filter:links -filter:replies -filter:images");
                    query.setLang("en");
                    query.setCount(100);
                    tweetList = new ArrayList<>();
                    try {
                        QueryResult result = twitter.search(query);
                        List<twitter4j.Status> tweets = result.getTweets();

                        double sentiment=0;
                        for (int y = 0; y < tweets.size(); ++y) {
                            String sentence=tweets.get(y).getText();
                            if (y<50 )
                                tweetList.add(sentence);
                            String[] words = sentence.trim().split("\\s+");
                            for (String word:words) {
                                Boolean b = dict.get(word);
                                if(b!=null){
                                    if(b==Boolean.TRUE)
                                        sentiment++;
                                    else
                                        sentiment--;
                                }
                            }
                        }
                        crypto.setSentiment(sentiment/tweets.size());

                    } catch (TwitterException e) {
                        tweetList.add("Rate Limit Reached");
                        crypto.setSentiment(0);
                        e.printStackTrace();
                    }
                    crypto.setTweets(tweetList);
                    cryptos.add(crypto);
                    publishProgress(4);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            CryptoCollection.GetInstance().setCryptos(cryptos);

        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        pb.setVisibility(View.GONE);
        srl.setVisibility(View.VISIBLE);
        srl.setRefreshing(false);
        adapter.notifyDataSetChanged();
    }
}
