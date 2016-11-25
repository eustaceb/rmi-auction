find ./src -name "*.java" | xargs javac
java -cp ./src server.AuctionServlet
