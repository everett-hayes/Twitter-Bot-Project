import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.util.*;


public class TwitterAutoBot {

    public static void main(String[] args) throws Exception {
        //will loop until user stops program
        while(true){
            TwitterAutoBot myBot = new TwitterAutoBot();
            ArrayList<tweetCondensed> listOfTweets = myBot.searchTwitter("SomeGoodNews", 20);
            myBot.analyzeTweets(listOfTweets);
            tweetCondensed toBeRetweeted = myBot.findMaxScore(listOfTweets);
            System.out.println("retweeting this right now: " + toBeRetweeted.toString());
            myBot.retweetAction(toBeRetweeted);
            try {
                System.out.println("Sleeping for 30 minutes...");
                Thread.sleep(1800000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    //Looks through the tweet array to find the one with the most positive sentiment
    public tweetCondensed findMaxScore(ArrayList<tweetCondensed> tweeters){
        //this will only run if your query actually found something, otherwise an exception will probably pop up
        tweetCondensed max = tweeters.get(0);
        for(tweetCondensed curr: tweeters){
            if(curr.getScore() > max.getScore()){
                max = curr;
            }
        }
        return max;
    }

    //assigns each tweet a sentiment score using the Standford NLP library
    public void analyzeTweets(ArrayList<tweetCondensed> tweeters){
        for(tweetCondensed curr: tweeters){
            Properties props = new Properties();
            props.setProperty("annotators", "tokenize, ssplit, pos, parse, sentiment");
            StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
            Annotation annotation = pipeline.process(curr.getText());
            for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
                Tree tree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
                curr.setScore(RNNCoreAnnotations.getPredictedClass(tree));
            }
        }
    }

    //retweets using ID number
    public void retweetAction(tweetCondensed tweeter){
        try{
            //configure the import using my twitter API credentials (Enter yours below)
            ConfigurationBuilder cb = new ConfigurationBuilder();
            cb.setDebugEnabled(true)
                    .setOAuthConsumerKey("<consumerKey>")
                    .setOAuthConsumerSecret("<consumerSecret>")
                    .setOAuthAccessToken("<accessToken>")
                    .setOAuthAccessTokenSecret("<tokenSecret>");
            TwitterFactory tf = new TwitterFactory(cb.build());
            Twitter twitter = tf.getInstance();
            //retweet param
            twitter.retweetStatus(tweeter.getId());
        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }

    //searchs for "limit" number of tweets related to "word"
    //returns a List of tweet objects
    public ArrayList<tweetCondensed> searchTwitter(String word, int limit){
        //the list to be returned
        ArrayList<tweetCondensed> toBeAnalyzed = new ArrayList<>();
        try{
            //configure the import using my twitter API credentials (Enter yours below)
            ConfigurationBuilder cb = new ConfigurationBuilder();
            cb.setDebugEnabled(true)
                    .setOAuthConsumerKey("<consumerKey>")
                    .setOAuthConsumerSecret("<consumerSecret>")
                    .setOAuthAccessToken("<accessToken>")
                    .setOAuthAccessTokenSecret("<tokenSecret>");
            TwitterFactory tf = new TwitterFactory(cb.build());
            Twitter twitter = tf.getInstance();
            //set up our search
            Query query = new Query(word);
            query.setCount(limit);
            query.setLang("en");
            //do the search
            QueryResult result = twitter.search(query);
            //add the tweets to our list
            for(Status status: result.getTweets()){
                toBeAnalyzed.add(new tweetCondensed(status.getText(), status.getId()));
            }
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        return toBeAnalyzed;
    }

    //send an individual tweet
    private static void sendTweet(String line) {
        Twitter twitter = TwitterFactory.getSingleton();
        Status status;
        try {
            status = twitter.updateStatus(line);
            System.out.println(status);
        } catch (TwitterException e) {;
            e.printStackTrace();
        }
    }
}
