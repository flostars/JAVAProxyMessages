DROP DATABASE proxymessages;
CREATE DATABASE IF NOT EXISTS proxymessages;
use proxymessages;
CREATE TABLE IF NOT EXISTS message (id int(11) NOT NULL AUTO_INCREMENT, receivedDate  DATE NOT NULL,platformCode varchar(20), targetPlatformListingId varchar(200), senderEmailAddress varchar(100), senderEmailSubject varchar(100), senderEmailBody Text, webhookUrl varchar(300), askedToExtractEmail boolean NOT NULL default '0', propagationEmail varchar(100), propagated boolean NOT NULL default '0', hasResponse boolean NOT NULL default '0', identifierToken varchar(100), mailboxId int(11), PRIMARY KEY (id));
CREATE TABLE IF NOT EXISTS mailbox (id int(11) NOT NULL AUTO_INCREMENT, emailAddress varchar(100), password varchar(100), createdDate   DATE NOT NULL, proxyId int(11), PRIMARY KEY (id));  
CREATE TABLE IF NOT EXISTS emailproxy (id int(11) NOT NULL AUTO_INCREMENT, host varchar(100), port int(5), username varchar(100), password varchar(100), source varchar(100), createdDate DATE NOT NULL, PRIMARY KEY (id));
CREATE TABLE IF NOT EXISTS response (id int(11) NOT NULL AUTO_INCREMENT, messageID int(11) NOT NULL,responseEmailAddress varchar(100), responseEmailSubject varchar(100),responseEmailBody Text, webhookNotified boolean NOT NULL default '0', createdDate DATE NOT NULL, PRIMARY KEY (id));

