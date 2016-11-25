### Auction system

I have developed an auction system using Java 8 and RMI as part of the assessed exercise for one of my courses, Distributed Algorithms and Systems 4 (H). My implementation covers all of the requirements that were specified in the exercise document.

There is a server that lets the users create auction items, view auctions and make bids.
It also notifies bidders when they are outbid or the auction closes, maintains historical records, has the ability save and load its state from file storage.

There are two client implementations - one that is to be used by actual users and one that acts as a \textit{Runnable} for multithreaded testing. The clientside also contains a failure detector that can detect and correct connection failures as well as measure performance.The auction system is thread safe to the best of my understanding.

More information in **report.pdf**
