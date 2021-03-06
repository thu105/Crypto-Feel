package heinmoethu.cryptofeel.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import heinmoethu.cryptofeel.R;
import heinmoethu.cryptofeel.adapters.CryptoListAdapter;
import heinmoethu.cryptofeel.asyncTasks.UpdateList;
import heinmoethu.cryptofeel.models.CryptoModel;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class CryptoListFragment extends Fragment {
    private CryptoListAdapter adapter;
    private SwipeRefreshLayout srl;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crypto_list,container,false);
        srl = v.findViewById(R.id.srl_crypto);
        RecyclerView rv_cryptoList=v.findViewById(R.id.rv_cryptos);
        adapter=new CryptoListAdapter();
        rv_cryptoList.setAdapter(adapter);
        rv_cryptoList.setLayoutManager(new LinearLayoutManager(getActivity()));
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });

        if(CryptoCollection.GetInstance().getDict()==null) {

            try {
                InputStream is = getActivity().getAssets().open("negative-words.txt");
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String word;
                HashMap<String, Boolean> dict = new HashMap<>();
                while ((word = br.readLine()) != null) {
                    dict.put(word, Boolean.FALSE);
                }
                br.close();
                is.close();
                is = getActivity().getAssets().open("positive-words.txt");
                br = new BufferedReader(new InputStreamReader(is));
                while ((word = br.readLine()) != null) {
                    dict.put(word, Boolean.TRUE);
                }
                br.close();
                is.close();
                CryptoCollection.GetInstance().setDict(dict);
            } catch (IOException e) {
                e.printStackTrace();
            }
            new UpdateList(adapter,srl).execute("https://api.coinmarketcap.com/v1/ticker/?limit=25");
        }

        return v;
    }

    private void refreshData() {
        new UpdateList(adapter,srl).execute("https://api.coinmarketcap.com/v1/ticker/?limit=25");
    }

    @Override
    public void onResume() {
        super.onResume();
        this.adapter.notifyDataSetChanged();
    }
}
