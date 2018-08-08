# Big Brother
I am always watching.

# Setup
It's mostly just a plug and play, aside from the config file. You can load it up, and then restart the server after editing the config OR you can just initially create the config.
To do so, head on over to your `config/` directory, make a folder called `bigbrother` and inside that folder make a file called `core.cfg`.
Inside this file, you will store your mysql details. A template will look something like this:
```
mysql {
  dbName=opalcraft
  host="localhost"
  password="skynet"
  port=3306
  username=opalbot
}
```
Then you can save the file, and start up the server. It *should* work right out of the box after that.

# MYSQL table setup
You also need to create a table for the logs to be stored, it does not do this by itself.
This is the table type it 'works' with, may be able to change it up a little. If you need something different, or want it, lmk.
```
 create table block_logs ( id INT(4) UNSIGNED NOT NULL AUTO_INCREMENT, uuid CHAR(36) NOT NULL, location TEXT, event varchar(50), block TEXT, PRIMARY KEY (id), KEY (uuid));
```
