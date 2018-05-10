## Created by: Hein Moe Thu
## Date: 11/25/2017
## App Name: Crypto Feel

### Features:
  - Shows top 25 cryptocurrencies (according to CoinMarketCap)
    - General Information(Name, Symbol, Icon, Price, and Price Change in 1 Hour)
  - Updates and perform sentiment analysis on multiple threads
  - Simple Sentiment Value of Tweets (using the word lists)
  - Related Tweets (shows max 50 tweets)
### REFERENCES:
  - Words List
    - Negative words: http://ptrckprry.com/course/ssd/data/negative-words.txt 
    - Postitve words: http://ptrckprry.com/course/ssd/data/positive-words.txt
  - APIs Used
    - Tweet Search: Twitter4j http://twitter4j.org/en/
    - Cryptocurrency Data: CoinMarketCap https://www.cryptocompare.com/api/data/coinlist/
    - Cryptocurrency Icons: CryptoCompare https://api.coinmarketcap.com/v1/ticker/?limit=25
