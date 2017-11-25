package heinmoethu.cryptofeel;

import android.os.AsyncTask;

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
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

import heinmoethu.cryptofeel.adapters.CryptoListAdapter;
import heinmoethu.cryptofeel.models.CryptoModel;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class CryptoCollection {
    private static CryptoCollection collection;
    private HashMap<String, Boolean> dict;
    private CryptoListAdapter cAdapter;

    public static CryptoCollection GetInstance() {
        if (collection == null)
            collection = new CryptoCollection();
        return collection;
    }

    private List<CryptoModel> cryptos;

    private CryptoCollection() {
        cryptos = new ArrayList<>();
    }

    public HashMap<String, Boolean> getDict() {
        return dict;
    }

    public void setDict(HashMap<String, Boolean> dict) {
        this.dict = dict;
    }

    public void createList() {
        cryptos = new ArrayList<>();
        try {
            String result = new GetList().execute("https://api.coinmarketcap.com/v1/ticker/?limit=25").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public List<CryptoModel> getCryptos() {
        return this.cryptos;
    }

    public void setCryptos(ArrayList<CryptoModel> cryptos) {
        this.cryptos = cryptos;
    }

    public void addCrypto(CryptoModel crypto) {
        cryptos.add(crypto);
    }

    public CryptoModel getCrypto(String id) {
        for (CryptoModel crypto : this.cryptos) {
            if (crypto.getId().equals(id))
                return crypto;
        }
        return null;
    }

    public void setcAdapter(CryptoListAdapter cAdapter) {
        this.cAdapter = cAdapter;
    }

    public CryptoListAdapter getcAdapter() {
        return cAdapter;
    }

    public class GetList extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
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
                        query.setCount(500);
                        query.setLang("en");
                        try {
                            QueryResult result = twitter.search(query);
                            List<twitter4j.Status> tweets = result.getTweets();
                            tweetList = new ArrayList<>();
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
                            tweetList = new ArrayList<>();
                            tweetList.add("Rate Limit Reached");
                            crypto.setSentiment(0);
                            e.printStackTrace();
                        }
                        crypto.setTweets(tweetList);
                        cryptos.add(crypto);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            return "true";
        }

//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            cAdapter.notifyDataSetChanged();
//        }

    }

}
