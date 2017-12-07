package heinmoethu.cryptofeel.asyncTasks;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import heinmoethu.cryptofeel.CryptoCollection;
import heinmoethu.cryptofeel.models.CryptoModel;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Twitter;
import twitter4j.TwitterException;


public class AnalyseTweets extends AsyncTask<String,String,String> {//Performing sentiment analysis and updating the tweetList
    private Twitter twitter;
    private CryptoModel crypto;
    private CountDownLatch cdl;

    AnalyseTweets(Twitter twitter, CryptoModel crypto, CountDownLatch cdl) {
        this.twitter=twitter;
        this.crypto=crypto;
        this.cdl=cdl;
    }

    @Override
    protected String doInBackground(String... strings) {
        String name = crypto.getTitle().split(" \\(")[0];
        Query query = new Query(name + " -filter:retweets -filter:links -filter:replies -filter:images");
        query.setCount(100);
        query.setLang("en");
        ArrayList<String> tweetList = new ArrayList<>();
        try {
            QueryResult result=twitter.search(query);
            List<twitter4j.Status> tweets = result.getTweets();
            HashMap<String,Boolean> dict= CryptoCollection.GetInstance().getDict();
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
            e.printStackTrace();
            tweetList.add("Rate Limit Reached");
            crypto.setSentiment(0);
        }
        crypto.setTweets(tweetList);
        cdl.countDown();
        return null;
    }
}
