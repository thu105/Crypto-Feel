package heinmoethu.cryptofeel.asyncTasks;

import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

import heinmoethu.cryptofeel.CryptoCollection;
import heinmoethu.cryptofeel.fragments.CryptoFragment;
import heinmoethu.cryptofeel.models.CryptoModel;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class UpdateList extends AsyncTask<String,Boolean,Void> {
    private RecyclerView.Adapter adapter;
    private SwipeRefreshLayout srl;
    private CountDownLatch cdl;

    public UpdateList(RecyclerView.Adapter adapter,SwipeRefreshLayout srl){
        this.adapter=adapter;
        this.srl=srl;
        this.cdl=new CountDownLatch(25);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        srl.setRefreshing(true);
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
        if (builder != null) {
            try {
                CryptoModel crypto;
                JSONArray list = new JSONArray(builder.toString());
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
                    crypto.setTweets(new ArrayList<String>());
                    cryptos.add(crypto);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            CryptoCollection.GetInstance().setCryptos(cryptos);

            publishProgress(true);
            try {
                cdl.await();//wait until countdown reaches 0
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Boolean... values) {
        super.onProgressUpdate(values);
        ConfigurationBuilder cb = new ConfigurationBuilder().setDebugEnabled(true).setOAuthConsumerKey("b3f5iOVkPMXKERjbixFtHgQZu")
                .setOAuthConsumerSecret("rJFx6rk3izots2w3tJ5meOVcBjafDX5DKzXlx0zegarrdp20av")
                .setOAuthAccessToken("774286002663796736-Ag8jzsIfCb300vILqcwdJqqufu1HWIf")
                .setOAuthAccessTokenSecret("NVwS4IivV40ckELlnJ08Ikw5YzsPvHmteKoedU7ch9DHv");
        Twitter twitter = new TwitterFactory(cb.build()).getInstance();
        List<CryptoModel> cryptos = CryptoCollection.GetInstance().getCryptos();

        ThreadPoolExecutor tpe = new ThreadPoolExecutor(25, 25, 500, TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>());
        for(int x=0; x<25;++x)//Running 25 threads for analysis, count one down from CountDownLatch every time one finishes
            new AnalyseTweets(twitter,cryptos.get(x),cdl).executeOnExecutor(tpe);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        srl.setRefreshing(false);
        adapter.notifyDataSetChanged();
    }
}
