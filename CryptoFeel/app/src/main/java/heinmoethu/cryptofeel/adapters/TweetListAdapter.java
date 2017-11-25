package heinmoethu.cryptofeel.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import heinmoethu.cryptofeel.R;

public class TweetListAdapter extends RecyclerView.Adapter<TweetListAdapter.TweetViewHolder> {
    private ArrayList<String> tweets;

    public TweetListAdapter(ArrayList<String> tweets) {
        this.tweets=tweets;
    }

    @Override
    public TweetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.cell_tweet,parent,false);
        return new TweetViewHolder(v);
    }

    @Override
    public void onBindViewHolder(TweetListAdapter.TweetViewHolder holder, int position) {
        String tweet = tweets.get(position);
        holder.setup(tweet);
    }

    public int getItemCount(){
        return tweets.size();
    }


    class TweetViewHolder extends RecyclerView.ViewHolder{
        private String tweet;
        private TextView tv_tweet;
        TweetViewHolder(View itemView){
            super(itemView);
            this.tv_tweet=itemView.findViewById(R.id.tv_tweet);
        }
        void setup(String tweet){
            this.tweet=tweet;
            this.tv_tweet.setText(tweet);
        }

    }

}
