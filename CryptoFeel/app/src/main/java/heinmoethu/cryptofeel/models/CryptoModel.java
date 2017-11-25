package heinmoethu.cryptofeel.models;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.UUID;

public class CryptoModel {
    private String id,title,img;
    private double price,change,sentiment;
    private int rank;
    private ArrayList<String> tweets;
    private DateTime date;

    public CryptoModel(){
        this.date=DateTime.now();
        this.tweets= new ArrayList<> ();
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
        this.date=DateTime.now();
    }

    public double getPrice() {
        return price;
    }
    public void setPrice(Double price) {
        this.price = price;
        this.date=DateTime.now();
    }

    public String getImg() {
        return img;
    }
    public void setImg(String img) {
        this.img = img;
    }

    public double getChange() {
        return change;
    }
    public void setChange(double change) {
        this.change = change;
    }

    public double getSentiment() {
        return sentiment;
    }
    public void setSentiment(double sentiment) {
        this.sentiment = sentiment;
    }

    public int getRank() {
        return rank;
    }
    public void setRank(int rank) {
        this.rank = rank;
    }

    public ArrayList<String> getTweets() {
        return tweets;
    }
    public void setTweets(ArrayList<String> tweets){
        this.tweets=tweets;
    }
    public void addTweet(String tweet){
        tweets.add(tweet);
    }

    public String getId() {
        return id;
    }
    public void setId(String id){
        this.id=id;
    }

    public void setDate(DateTime date){
        this.date=date;
    }
    public DateTime getDate() {
        return date;
    }
}
