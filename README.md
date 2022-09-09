# Smail
JavaFx application for sending mails. Contains server which saves data in sqlite database and client side.
Server side saves all data in mail.db sqlite database. Server listen for new connections and open one new thread per connected user.
Every thread listens for user's requests which are made through some kind of simple protocol (sending requests and responds as objects with type field and 
data object field). Depending on type field server than manipulates mail.db database and send respond to client.
Client is JavaFx app and has plenty of functionality as well plenty of unfinished. Firstly user must to login or register and than login. After successfull
login user can send message to more users, save unfinished messages, arrange sending, look in inbox, sent, unfinished and arranged messages...
Database is built so it can support attachment sending in future. Some other functionality that can be added in future are:
* Filtering messages by some criteria, as well as sorting
* Marking read and unread messages
* Sending attachments
* ...
