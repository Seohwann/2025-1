CREATE TABLE User_info(
	userid VARCHAR(100) PRIMARY KEY,
	password VARCHAR(100) not null,
	isadmin VARCHAR(5) not null
);

CREATE TABLE Item(
	itemid VARCHAR(100) PRIMARY KEY,
	category VARCHAR(100) NOT NULL,
	description	VARCHAR(200) NOT NULL,
	condition VARCHAR(100) NOT NULL,
    sellerid VARCHAR(100) NOT NULL,
	FOREIGN KEY (sellerid) REFERENCES User_info(userid),
    CHECK (category IN  ('ELECTRONICS', 'BOOKS', 'HOME', 'CLOTHING', 'SPORTINGGOODS', 'OTHERS')),
    CHECK (condition IN ('NEW', 'LIKE_NEW', 'GOOD', 'ACCEPTABLE'))
);

CREATE TABLE Auction(
    auctionid SERIAL PRIMARY KEY,
    itemid VARCHAR(100) NOT NULL,
    startingprice INT NOT NULL DEFAULT 0,
    currentprice INT NOT NULL,
    highestbidder VARCHAR(100),
    buyitnowprice INT NOT NULL,
    bidstarttime TIMESTAMP DEFAULT NOW(),
    bidendtime TIMESTAMP,
    status VARCHAR(100) NOT NULL,
    FOREIGN KEY (itemid) REFERENCES Item(itemid),
    FOREIGN KEY (highestbidder) REFERENCES User_info(userid),
    CHECK (status IN ('LISTED', 'BIDDING', 'SOLD', 'EXPIRED'))
);

CREATE TABLE Bid(
    bidid SERIAL PRIMARY KEY,
	bidderid VARCHAR(100) NOT NULL,
    auctionid INT NOT NULL,
	bidprice INT NOT NULL,
	bidtime TIMESTAMP DEFAULT NOW(),
	bidstatus VARCHAR(100) NOT NULL,
	FOREIGN KEY (bidderid) REFERENCES User_info(userid),
    FOREIGN KEY (auctionid) REFERENCES Auction(auctionid),
    CHECK (bidstatus IN ('ACTIVE', 'OUTBID', 'WON'))
);

CREATE TABLE Billing(
	billingid SERIAL PRIMARY KEY,
    itemid VARCHAR(100) NOT NULL,
	buyerid VARCHAR(100) NOT NULL,
	sellerid VARCHAR(100) NOT NULL,
    finalprice INT NOT NULL,
	transactiontime TIMESTAMP,
	paymentstatus VARCHAR(100) DEFAULT 'PENDING',
	FOREIGN KEY (itemid) REFERENCES Item(itemid),
	FOREIGN KEY (buyerid) REFERENCES User_info(userid),
	FOREIGN KEY (sellerid) REFERENCES User_info(userid),
    CHECK (paymentstatus IN ('PENDING', 'COMPLETED', 'FAILED', 'REFUNDED'))
);