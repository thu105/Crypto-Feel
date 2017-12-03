# final-project-thu105

## Created by: Hein Moe Thu
## Date: 11/25/2017
## App Name: Crypto Feel

### Features:
  - Shows top 25 cryptocurrencies (according to CoinMarketCap)
    - General Information(Name, Symbol, Icon, Price, and Price Change in 1 Hour)
  - Simple Sentiment Value of Tweets (using the word lists)
  - Related Tweets (shows max 50 tweets)
### Issues:
  - Slow... (takes about 10 seconds when starting up -> has to compile dictionary, load all cryptocurrency data, get all tweets, and perform analysis)
  - Refresh is also slow (everything except compliling dictionary)
### Things tried:
  - JSON parsing
  - HTTPS connection
  - Async task
  - Twitter4j
  - SwipeRefreshLayout
  - Stanford CoreNLP (failed to import package)
  - OpenNLP (failed because too much resources used to train a model)
### REFERENCES:
  - Words List
    - Negative words: http://ptrckprry.com/course/ssd/data/negative-words.txt 
    - Postitve words: http://ptrckprry.com/course/ssd/data/positive-words.txt
  - APIs Used
    - Tweet Search: Twitter4j http://twitter4j.org/en/
    - Cryptocurrency Data: CoinMarketCap https://www.cryptocompare.com/api/data/coinlist/
    - Cryptocurrency Icons: CryptoCompare https://api.coinmarketcap.com/v1/ticker/?limit=25
