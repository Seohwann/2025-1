import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.DriverManager;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.Duration;
import java.sql.Timestamp;
import java.text. *;
import java.util. *;

public class Auction {
	private static Scanner scanner = new Scanner(System.in);
	private static String username;
	private static Connection conn;
	private static Statement stmt;

	enum Category {
		ELECTRONICS, 
		BOOKS,
		HOME,
		CLOTHING,
		SPORTINGGOODS,
		OTHERS
	}
	enum Condition {
		NEW,
		LIKE_NEW,
		GOOD,
		ACCEPTABLE
	}
	enum Status {
		LISTED,
		BIDDING,
		SOLD,
		EXPIRED
	}
	enum Bidstatus {
		ACTIVE,
		OUTBID,
		WON
	}
	enum Paymentstatus {
		PENDING,
		COMPLETED,
		FAILED,
		REFUNDED
	}

	private static boolean LoginMenu() {
		String userpass, isAdmin;

		System.out.print("----< User Login >\n" +
				" ** To go back, enter 'back' in user ID.\n" +
				"     user ID: ");
		try{
			username = scanner.next();
			scanner.nextLine();

			if(username.equalsIgnoreCase("back")){
				return false;
			}

			System.out.print("     password: ");
			userpass = scanner.next();
			scanner.nextLine();
		}catch (java.util.InputMismatchException e) {
			System.out.println("Error: Invalid input is entered. Try again.");
			username = null;
			return false;
		}
		
		/* Your code should come here to check ID and password */ 
		// User_info에 있으면 성공 없으면 false 반환
		String userinfo = "userid = '" + username + "' AND password = '" + userpass + "'";
		try(
			ResultSet rset = stmt.executeQuery("SELECT * FROM User_info WHERE " + userinfo);
		){
			if(rset.next()){
				System.out.println("You are successfully logged in.\n");
				return true;
			}
			else{
				System.out.println("Error: Incorrect user name or password");
				username = null;
				return false;
			}
		} catch (SQLException e){
			System.out.println("Error: Login Menu - Login error" + e);
			username = null;
			return false;
		}
	}

	private static boolean SellMenu() {
		Category category = null;
		Condition condition = null;
		char choice;
		int price;
		boolean flag_catg = true, flag_cond = true;
		int startingprice = 0;
		int currentprice = 0;
		
		do{
			System.out.println(
					"----< Sell Item >\n" +
					"---- Choose a category.\n" +
					"    1. Electronics\n" +
					"    2. Books\n" +
					"    3. Home\n" +
					"    4. Clothing\n" +
					"    5. Sporting Goods\n" +
					"    6. Other Categories\n" +
					"    P. Go Back to Previous Menu"
					);

			try {
				choice = scanner.next().charAt(0);;
			}catch (java.util.InputMismatchException e) {
				System.out.println("Error: Invalid input is entered. Try again.");
				continue;
			}

			flag_catg = true;

			switch ((int) choice){
				case '1':
					category = Category.ELECTRONICS;
					continue;
				case '2':
					category = Category.BOOKS;
					continue;
				case '3':
					category = Category.HOME;
					continue;
				case '4':
					category = Category.CLOTHING;
					continue;
				case '5':
					category = Category.SPORTINGGOODS;
					continue;
				case '6':
					category = Category.OTHERS;
					continue;
				case 'p':
				case 'P':
					return false;
				default:
					System.out.println("Error: Invalid input is entered. Try again.");
					flag_catg = false;
					continue;
			}
		}while(!flag_catg);

		do{
			System.out.println(
					"---- Select the condition of the item to sell.\n" +
					"   1. New\n" +
					"   2. Like_new\n" +
					"   3. Used (Good)\n" +
					"   4. Used (Acceptable)\n" +
					"   P. Go Back to Previous Menu"
					);

			try {
				choice = scanner.next().charAt(0);;
				scanner.nextLine();
			}catch (java.util.InputMismatchException e) {
				System.out.println("Error: Invalid input is entered. Try again.");
				continue;
			}

			flag_cond = true;

			switch (choice) {
				case '1':
					condition = Condition.NEW;
					break;
				case '2':
					condition = Condition.LIKE_NEW;
					break;
				case '3':
					condition = Condition.GOOD;
					break;
				case '4':
					condition = Condition.ACCEPTABLE;
					break;
				case 'p':
				case 'P':
					return false;
				default:
					System.out.println("Error: Invalid input is entered. Try again.");
					flag_cond = false;
					continue;
			}
		}while(!flag_cond);

		try {
			System.out.println("---- ID of the item (one line): ");
			String itemid = scanner.nextLine();
			System.out.println("---- Description of the item (one line): ");
			String description = scanner.nextLine();
			System.out.println("---- Buy-It-Now price: ");
			
			while (!scanner.hasNextInt()) {
				scanner.next();
				System.out.println("Invalid input is entered. Please enter Buy-It-Now price: ");
			}

			price = scanner.nextInt();
			scanner.nextLine();

			System.out.println("---- Starting price: ");
			
			while (!scanner.hasNextInt()) {
				scanner.next();
				System.out.println("Invalid input is entered. Please enter Starting price: ");
			}
			
			startingprice = scanner.nextInt();
			scanner.nextLine();
			System.out.print("---- Bid closing date and time (YYYY-MM-DD HH:MM): ");
			// category, condition, itemid, description, price, startingprice, Bid closing date를 판매자가 입력함

			// you may assume users always enter valid date/time
			String date = scanner.nextLine();  /* "2023-03-04 11:30"; */
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
			LocalDateTime dateTime = LocalDateTime.parse(date, formatter);
			String bidendtime = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")); 
			LocalDateTime current = LocalDateTime.now(); // 현재 시간 불러오기 
			String currenttime;
			if(dateTime.isBefore(current)) {
                System.out.println("Bid closing date is before current time");
                return false;
			}
			else{
				currenttime = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
			}
			/* TODO: Your code should come here to store the user inputs in your database */
			// item과 auction에 동시에 입력
			try{
				String iteminfo = "VALUES ('" + itemid + "', '" + category + "', '" + description + "', '" + condition + "', '" + username + "')";
				stmt.executeUpdate("INSERT INTO Item(itemid, category, description, condition, sellerid) " + iteminfo);
				String auctioninfo = "VALUES ('" + itemid + "', '" + startingprice + "', NULL, '" + currentprice + "', '" + price + "', '" + currenttime + "', '" + bidendtime + "', '" + Status.LISTED + "')";
				stmt.executeUpdate("INSERT INTO Auction(itemid, startingprice, highestbidder, currentprice, buyitnowprice, bidstarttime, bidendtime, status) " + auctioninfo);
				System.out.println("Your item has been successfully listed.\n");
				return true;
			} catch (SQLException e){
				System.out.println("Error: Sell item error" + e);
				return false;
			}
		}catch (Exception e) {
			System.out.println("Error: Invalid input is entered. Going back to the previous menu.");
			return false;
		}

		
		
	}

	private static boolean SignupMenu() {
		/* 2. Sign Up */
		String new_username, userpass, isAdmin;
		System.out.print("----< Sign Up >\n" + 
				" ** To go back, enter 'back' in user ID.\n" +
				"---- user name: ");
		try {
			new_username = scanner.next();
			scanner.nextLine();
			if(new_username.equalsIgnoreCase("back")){
				return false;
			}
			System.out.print("---- password: ");
			userpass = scanner.next();
			scanner.nextLine();
			System.out.print("---- In this user an administrator? (Y/N): "); // admin인지 체크
			isAdmin = scanner.next();
			scanner.nextLine();
		} catch (java.util.InputMismatchException e) {
			System.out.println("Error: Invalid input is entered. Please select again.");
			return false;
		}

		/* TODO: Your code should come here to create a user account in your database */
		String userid = "userid = '" + username + "'";
		try(			
			ResultSet rset = stmt.executeQuery("SELECT * FROM User_info WHERE " + userid);
		){
			if(rset.next()){
				System.out.println("You are already created.\n");
				return false;
			}
			else{
				String userinfo = "'" + new_username + "', '" + userpass + "', '" + isAdmin;
				stmt.executeUpdate("INSERT INTO User_info(userid, password, isadmin) VALUES (" + userinfo + "')");
				System.out.println("Your account has been successfully created.\n");
				return true;
			}
		} catch (SQLException e){
			System.out.println("Error: Login Menu - Sign up error" + e);
			return false;
		}
	}

	private static boolean AdminMenu() {
		/* 3. Login as Administrator */
		char choice;
		String adminname, adminpass;
		String keyword, seller;
		System.out.print("----< Login as Administrator >\n" +
				" ** To go back, enter 'back' in user ID.\n" +
				"---- admin ID: ");

		try {
			adminname = scanner.next();
			scanner.nextLine();
			if(adminname.equalsIgnoreCase("back")){
				return false;
			}
			System.out.print("---- password: ");
			adminpass = scanner.nextLine();
			// TODO: check the admin's account and password.
		} catch (java.util.InputMismatchException e) {
			System.out.println("Error: Invalid input is entered. Try again.");
			return false;
		}

		boolean login_success = true;
		String admininfo = "userid = '" + adminname + "' AND password = '" + adminpass + "'";		
		try (
			ResultSet rset = stmt.executeQuery("SELECT * FROM User_info WHERE " + admininfo);
		){
			if(rset.next()){
				if(rset.getString("isadmin").equalsIgnoreCase("N")){
					System.out.println("You are not an administrator. Please Try again\n");
					login_success = false;
				}
				else{
					System.out.println("You are successfully logged as an administrator.\n");
				}
			}
		} catch (SQLException e){
			System.out.println("Error: Login Menu - Login as Administrator error" + e);
			return false;
		}

		if(!login_success){
			// login failed. go back to the previous menu.
			return false;
		}

		do {
			System.out.println(
					"----< Admin menu > \n" +
					"    1. Print Sold Items per Category \n" +
					"    2. Print Account Balance for Seller \n" +
					"    3. Print Seller Ranking \n" +
					"    4. Print Buyer Ranking \n" +
					"    P. Go Back to Previous Menu"
					);

			try {
				choice = scanner.next().charAt(0);;
				scanner.nextLine();
			} catch (java.util.InputMismatchException e) {
				System.out.println("Error: Invalid input is entered. Try again.");
				continue;
			}
			// 카테고리 별로 판매된 아이템 출력
			if (choice == '1') {
				System.out.println("----Enter Category to search : ");
				keyword = scanner.next();
				scanner.nextLine();
				/*TODO: Print Sold Items per Category */
				LocalDateTime current = LocalDateTime.now();
				String currenttime = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
				try{
					stmt.executeUpdate("INSERT INTO Billing(itemid, buyerid, sellerid, finalprice, transactiontime, paymentstatus) "+
					"SELECT Item.itemid, Bid.bidderid, Item.sellerid, Bid.bidprice, Bid.bidtime, 'COMPLETED' " +
					"FROM Bid JOIN Auction USING (auctionid) "+
					"JOIN Item USING (itemid)" +
					"WHERE Auction.status = 'BIDDING' "+ 
					"AND Auction.bidendtime < '" + currenttime+ "' " +
					"AND Bid.bidstatus = 'ACTIVE' "+
					"AND Item.itemid NOT IN (SELECT itemid FROM Billing)");

					stmt.executeUpdate(
					"UPDATE Auction " +
					"SET currentprice = subquery.maxbidprice, " +
					"    highestbidder = subquery.bidderid " +
					"FROM ( " +
					"    SELECT auctionid, MAX(bidprice) AS maxbidprice, bidderid " +
					"    FROM Bid " +
					"    WHERE bidstatus IN ('ACTIVE', 'WON') " +
					"    GROUP BY auctionid, bidderid " +
					"    HAVING MAX(bidprice) = ( " +
					"        SELECT MAX(bidprice) " +
					"        FROM Bid b2 " +
					"        WHERE b2.auctionid = Bid.auctionid " +
					"        AND b2.bidstatus IN ('ACTIVE', 'WON') " +
					"    ) " +
					") AS subquery " +
					"WHERE Auction.auctionid = subquery.auctionid " +
					"AND Auction.status IN ('BIDDING', 'SOLD') ");
			
					stmt.executeUpdate("UPDATE Auction SET status = 'SOLD' "+
					"WHERE bidendtime < '" + currenttime + "' " +
					"AND status = 'BIDDING' " +
					"AND itemid IN (SELECT itemid FROM Billing)");
			
					stmt.executeUpdate("UPDATE Bid SET bidstatus = 'WON' "+
					"FROM Auction " +
					"WHERE Auction.auctionid = Bid.auctionid " + 
					"AND bidendtime < '" + currenttime + "' " +
					"AND Bid.bidstatus = 'ACTIVE' "+
					"AND Auction.itemid IN (SELECT itemid FROM Billing)");
					
					stmt.executeUpdate("UPDATE Auction SET status = 'EXPIRED' "+
					"WHERE bidendtime < '" + currenttime + "' " +
					"AND status = 'LISTED' ");
				} catch(SQLException e){
					System.out.println("Error : AdminMenu - Update query error" + e);
				}
				System.out.println("   sold item    |      sold date       |  seller ID  |  buyer ID  | price | commissions");
				System.out.println("---------------------------------------------------------------------------------------");
				try{
					ResultSet rset = stmt.executeQuery(
					"SELECT Item.description, Billing.transactiontime, Billing.sellerid, Billing.buyerid, Billing.finalprice, FLOOR(Billing.finalprice * 0.05) AS commission " +
					"FROM Billing " +
					"JOIN Item ON Billing.itemid = Item.itemid " +
					"WHERE UPPER(Item.category) = UPPER('" + keyword + "')");
					while (rset.next()){
						String description = rset.getString("description");
						String transactiontime = rset.getString("transactiontime");
						String sellerid = rset.getString("sellerid");
						String buyerid = rset.getString("buyerid");
						int finalprice = rset.getInt("finalprice");
						int commission = rset.getInt("commission");
						if (transactiontime != null && transactiontime.contains(".")) {
							transactiontime = transactiontime.substring(0, transactiontime.indexOf(".")); 
						}
						System.out.printf("%-15s | %-20s | %-11s | %-10s | %-5d | %d%n", description, transactiontime, sellerid, buyerid, finalprice, commission);
					}
					rset.close();
				} catch (SQLException e){
					System.out.println("Error : AdminMenu - Category Sold Item error" + e);
				}
				continue;
			} else if (choice == '2') { // Seller의 ID를 입력받아 해당 Seller의 아이템 출력
				/*TODO: Print Account Balance for Seller */
				System.out.println("---- Enter Seller ID to search : ");
				seller = scanner.next();
				scanner.nextLine();
				LocalDateTime current = LocalDateTime.now();
				String currenttime = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
				try{
					stmt.executeUpdate("INSERT INTO Billing(itemid, buyerid, sellerid, finalprice, transactiontime, paymentstatus) "+
					"SELECT Item.itemid, Bid.bidderid, Item.sellerid, Bid.bidprice, Bid.bidtime, 'COMPLETED' " +
					"FROM Bid JOIN Auction USING (auctionid) "+
					"JOIN Item USING (itemid)" +
					"WHERE Auction.status = 'BIDDING' "+ 
					"AND Auction.bidendtime < '" + currenttime+ "' " +
					"AND Bid.bidstatus = 'ACTIVE' "+
					"AND Item.itemid NOT IN (SELECT itemid FROM Billing)");

					stmt.executeUpdate(
					"UPDATE Auction " +
					"SET currentprice = subquery.maxbidprice, " +
					"    highestbidder = subquery.bidderid " +
					"FROM ( " +
					"    SELECT auctionid, MAX(bidprice) AS maxbidprice, bidderid " +
					"    FROM Bid " +
					"    WHERE bidstatus IN ('ACTIVE', 'WON') " +
					"    GROUP BY auctionid, bidderid " +
					"    HAVING MAX(bidprice) = ( " +
					"        SELECT MAX(bidprice) " +
					"        FROM Bid b2 " +
					"        WHERE b2.auctionid = Bid.auctionid " +
					"        AND b2.bidstatus IN ('ACTIVE', 'WON') " +
					"    ) " +
					") AS subquery " +
					"WHERE Auction.auctionid = subquery.auctionid " +
					"AND Auction.status IN ('BIDDING', 'SOLD') ");
			
					stmt.executeUpdate("UPDATE Auction SET status = 'SOLD' "+
					"WHERE bidendtime < '" + currenttime + "' " +
					"AND status = 'BIDDING' " +
					"AND itemid IN (SELECT itemid FROM Billing)");

					stmt.executeUpdate("UPDATE Bid SET bidstatus = 'WON' "+
					"FROM Auction " +
					"WHERE Auction.auctionid = Bid.auctionid " + 
					"AND bidendtime < '" + currenttime + "' " +
					"AND Bid.bidstatus = 'ACTIVE' "+
					"AND Auction.itemid IN (SELECT itemid FROM Billing)");
			
					stmt.executeUpdate("UPDATE Auction SET status = 'EXPIRED' "+
					"WHERE bidendtime < '" + currenttime + "' " +
					"AND status = 'LISTED' ");
				} catch(SQLException e){
					System.out.println("Error : AdminMenu - Update query error" + e);
				}
				System.out.println("   sold item    |      sold date       |  buyer ID  | price | commissions");
				System.out.println("-------------------------------------------------------------------------");
				try{
					ResultSet rset 
					= stmt.executeQuery("SELECT Item.description, Billing.transactiontime, Billing.buyerid, Billing.finalprice, FLOOR(Billing.finalprice * 0.05) AS commission " +
					"FROM Billing " +
					"JOIN Item ON Billing.itemid = Item.itemid " +
					"WHERE Billing.sellerid = '" + seller + "'");
					while (rset.next()){
						String description = rset.getString("description");
						String transactiontime = rset.getString("transactiontime");
						String buyerid = rset.getString("buyerid");
						int finalprice = rset.getInt("finalprice");
						int commission = rset.getInt("commission");
						if (transactiontime != null && transactiontime.contains(".")) {
							transactiontime = transactiontime.substring(0, transactiontime.indexOf(".")); 
						}
						System.out.printf("%-15s | %-20s | %-10s | %-5d | %d%n", description, transactiontime, buyerid, finalprice, commission);
					}
					rset.close();
				} catch (SQLException e){
					System.out.println("Error : AdminMenu - Seller Sold Item error" + e);
				}
				continue;
			} else if (choice == '3') {
				// Seller의 순위를 판매 총액 순서로 출력
				/*TODO: Print Seller Ranking */
				LocalDateTime current = LocalDateTime.now();
				String currenttime = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
				try{
					stmt.executeUpdate("INSERT INTO Billing(itemid, buyerid, sellerid, finalprice, transactiontime, paymentstatus) "+
					"SELECT Item.itemid, Bid.bidderid, Item.sellerid, Bid.bidprice, Bid.bidtime, 'COMPLETED' " +
					"FROM Bid JOIN Auction USING (auctionid) "+
					"JOIN Item USING (itemid)" +
					"WHERE Auction.status = 'BIDDING' "+ 
					"AND Auction.bidendtime < '" + currenttime+ "' " +
					"AND Bid.bidstatus = 'ACTIVE' "+
					"AND Item.itemid NOT IN (SELECT itemid FROM Billing)");

					stmt.executeUpdate(
					"UPDATE Auction " +
					"SET currentprice = subquery.maxbidprice, " +
					"    highestbidder = subquery.bidderid " +
					"FROM ( " +
					"    SELECT auctionid, MAX(bidprice) AS maxbidprice, bidderid " +
					"    FROM Bid " +
					"    WHERE bidstatus IN ('ACTIVE', 'WON') " +
					"    GROUP BY auctionid, bidderid " +
					"    HAVING MAX(bidprice) = ( " +
					"        SELECT MAX(bidprice) " +
					"        FROM Bid b2 " +
					"        WHERE b2.auctionid = Bid.auctionid " +
					"        AND b2.bidstatus IN ('ACTIVE', 'WON') " +
					"    ) " +
					") AS subquery " +
					"WHERE Auction.auctionid = subquery.auctionid " +
					"AND Auction.status IN ('BIDDING', 'SOLD') ");
			
					stmt.executeUpdate("UPDATE Auction SET status = 'SOLD' "+
					"WHERE bidendtime < '" + currenttime + "' " +
					"AND status = 'BIDDING' " +
					"AND itemid IN (SELECT itemid FROM Billing)");

					stmt.executeUpdate("UPDATE Bid SET bidstatus = 'WON' "+
					"FROM Auction " +
					"WHERE Auction.auctionid = Bid.auctionid " + 
					"AND bidendtime < '" + currenttime + "' " +
					"AND Bid.bidstatus = 'ACTIVE' "+
					"AND Auction.itemid IN (SELECT itemid FROM Billing)");
			
					stmt.executeUpdate("UPDATE Auction SET status = 'EXPIRED' "+
					"WHERE bidendtime < '" + currenttime + "' " +
					"AND status = 'LISTED' ");
				} catch(SQLException e){
					System.out.println("Error : AdminMenu - Update query error" + e);
				}

				System.out.println("  seller ID  | # of items sold | Total Profit (excluding commissions)");
				System.out.println("---------------------------------------------------------------------");
				try{
					ResultSet rset 
					= stmt.executeQuery("SELECT sellerid, COUNT(itemid) AS count, SUM(finalprice - FLOOR(finalprice * 0.05)) AS totalprofit "+
                    "FROM Billing GROUP BY sellerid ORDER BY totalprofit DESC"); 
					while (rset.next()){
						String sellerid = rset.getString("sellerid");
						int count = rset.getInt("count");
						int totalprofit = rset.getInt("totalprofit");

						System.out.printf("%-12s | %-15d | %d%n", sellerid, count, totalprofit);
					}
					rset.close();
				} catch (SQLException e){
					System.out.println("Error : AdminMenu - Seller Ranking Serror" + e);
				}
				continue;
			} else if (choice == '4') {
				// Buyer의 순위를 구매 총액 순서로 출력
				/*TODO: Print Buyer Ranking */
				LocalDateTime current = LocalDateTime.now();
				String currenttime = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
				try{
					stmt.executeUpdate("INSERT INTO Billing(itemid, buyerid, sellerid, finalprice, transactiontime, paymentstatus) "+
					"SELECT Item.itemid, Bid.bidderid, Item.sellerid, Bid.bidprice, Bid.bidtime, 'COMPLETED' " +
					"FROM Bid JOIN Auction USING (auctionid) "+
					"JOIN Item USING (itemid)" +
					"WHERE Auction.status = 'BIDDING' "+ 
					"AND Auction.bidendtime < '" + currenttime+ "' " +
					"AND Bid.bidstatus = 'ACTIVE' "+
					"AND Item.itemid NOT IN (SELECT itemid FROM Billing)");

					stmt.executeUpdate(
					"UPDATE Auction " +
					"SET currentprice = subquery.maxbidprice, " +
					"    highestbidder = subquery.bidderid " +
					"FROM ( " +
					"    SELECT auctionid, MAX(bidprice) AS maxbidprice, bidderid " +
					"    FROM Bid " +
					"    WHERE bidstatus IN ('ACTIVE', 'WON') " +
					"    GROUP BY auctionid, bidderid " +
					"    HAVING MAX(bidprice) = ( " +
					"        SELECT MAX(bidprice) " +
					"        FROM Bid b2 " +
					"        WHERE b2.auctionid = Bid.auctionid " +
					"        AND b2.bidstatus IN ('ACTIVE', 'WON') " +
					"    ) " +
					") AS subquery " +
					"WHERE Auction.auctionid = subquery.auctionid " +
					"AND Auction.status IN ('BIDDING', 'SOLD') ");
			
					stmt.executeUpdate("UPDATE Auction SET status = 'SOLD' "+
					"WHERE bidendtime < '" + currenttime + "' " +
					"AND status = 'BIDDING' " +
					"AND itemid IN (SELECT itemid FROM Billing)");

					stmt.executeUpdate("UPDATE Bid SET bidstatus = 'WON' "+
					"FROM Auction " +
					"WHERE Auction.auctionid = Bid.auctionid " + 
					"AND bidendtime < '" + currenttime + "' " +
					"AND Bid.bidstatus = 'ACTIVE' "+
					"AND Auction.itemid IN (SELECT itemid FROM Billing)");
			
					stmt.executeUpdate("UPDATE Auction SET status = 'EXPIRED' "+
					"WHERE bidendtime < '" + currenttime + "' " +
					"AND status = 'LISTED' ");
				} catch(SQLException e){
					System.out.println("Error : AdminMenu - Update query error" + e);
				}
				System.out.println("  buyer ID  | # of items purchased | Total Money Spent ");
				System.out.println("-------------------------------------------------------");
				try{
					ResultSet rset 
					= stmt.executeQuery("SELECT buyerid, COUNT(itemid) AS count, SUM(finalprice) AS totalmoneyspent "+
                    "FROM Billing GROUP BY buyerid ORDER BY totalmoneyspent DESC"); 
					while (rset.next()){
						String buyerid = rset.getString("buyerid");
						int count = rset.getInt("count");
						int totalmoneyspent = rset.getInt("totalmoneyspent");

						System.out.printf("%-11s | %-20d | %d%n", buyerid, count, totalmoneyspent);
					}
					rset.close();
				} catch (SQLException e){
					System.out.println("Error : AdminMenu - Buyer Ranking Serror" + e);
				}
				continue;
			} else if (choice == 'P' || choice == 'p') {
				return false;
			} else {
				System.out.println("Error: Invalid input is entered. Try again.");
				continue;
			}
		} while(true);
	}

	public static void CheckSellStatus(){
		/* TODO: Check the status of the item the current user is selling */
		// 판매 상황을 출력하는 함수
		LocalDateTime current = LocalDateTime.now();
		String currenttime = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")); // 현재 시간을 yyyy-MM-dd HH:mm:ss 형식으로 가져옴
		try{ // 3종류의 Check함수를 call할때 시간이 지나면 경매를 끝내고, 최종 경매 입찰자를 선정하고, Billing(최종 구매 내역)에 삽입함
            stmt.executeUpdate("INSERT INTO Billing(itemid, buyerid, sellerid, finalprice, transactiontime, paymentstatus) "+
            "SELECT Item.itemid, Bid.bidderid, Item.sellerid, Bid.bidprice, Bid.bidtime, 'COMPLETED' " +
            "FROM Bid JOIN Auction USING (auctionid) "+
            "JOIN Item USING (itemid)" +
            "WHERE Auction.status = 'BIDDING' "+ 
            "AND Auction.bidendtime < '" + currenttime+ "' " +
            "AND Bid.bidstatus = 'ACTIVE' "+
            "AND Auction.itemid NOT IN (SELECT itemid FROM Billing)");
			
			stmt.executeUpdate(
			"UPDATE Auction " +
			"SET currentprice = subquery.maxbidprice, " +
			"    highestbidder = subquery.bidderid " +
			"FROM ( " +
			"    SELECT auctionid, MAX(bidprice) AS maxbidprice, bidderid " +
			"    FROM Bid " +
			"    WHERE bidstatus IN ('ACTIVE', 'WON') " +
			"    GROUP BY auctionid, bidderid " +
			"    HAVING MAX(bidprice) = ( " +
			"        SELECT MAX(bidprice) " +
			"        FROM Bid b2 " +
			"        WHERE b2.auctionid = Bid.auctionid " +
			"        AND b2.bidstatus IN ('ACTIVE', 'WON') " +
			"    ) " +
			") AS subquery " +
			"WHERE Auction.auctionid = subquery.auctionid " +
			"AND Auction.status IN ('BIDDING', 'SOLD') ");

            stmt.executeUpdate("UPDATE Auction SET status = 'SOLD' "+
            "WHERE bidendtime < '" + currenttime + "' " +
            "AND status = 'BIDDING' " +
            "AND itemid IN (SELECT itemid FROM Billing)");

			stmt.executeUpdate("UPDATE Bid SET bidstatus = 'WON' "+
			"FROM Auction " +
            "WHERE Auction.auctionid = Bid.auctionid " + 
			"AND bidendtime < '" + currenttime + "' " +
			"AND Bid.bidstatus = 'ACTIVE' "+
            "AND Auction.itemid IN (SELECT itemid FROM Billing)");
    
            stmt.executeUpdate("UPDATE Auction SET status = 'EXPIRED' "+
            "WHERE bidendtime < '" + currenttime + "' " +
            "AND status = 'LISTED' ");
        } catch(SQLException e){
            System.out.println("Error : CheckSellStatus - Update query error" + e);
        }
		
		System.out.println("item listed in Auction | bidder (buyer ID) | bidding price | Listed time ");
		System.out.println("-------------------------------------------------------------------------");
		
		try{
			ResultSet rset = stmt.executeQuery("SELECT * FROM Item JOIN Auction USING (itemid) WHERE Item.sellerid = '" + username + "'");
			while (rset.next()){
				String description = rset.getString("description");
				String highestbidder = rset.getString("highestbidder");
				int currentprice = rset.getInt("currentprice");
				String bidstart = rset.getString("bidstarttime");
				if (bidstart != null && bidstart.contains(".")) {
            		bidstart = bidstart.substring(0, bidstart.indexOf(".")); 
        		}
				System.out.printf("%-22s | %-17s | %-13d | %s%n", description, highestbidder, currentprice, bidstart);
            	}
			rset.close();
		} catch (SQLException e){
			System.out.println("Error: CheckSellStatus error" + e);
		}
	}

	public static boolean BuyItem(){
		Category category = null;
		Condition condition = null;
		char choice;
		int price;
		String keyword, seller, datePosted;
		boolean flag_catg = true, flag_cond = true;
		
		do {

			System.out.println( "----< Select category > : \n" +
					"    1. Electronics\n"+
					"    2. Books\n" + 
					"    3. Home\n" + 
					"    4. Clothing\n" + 
					"    5. Sporting Goods\n" +
					"    6. Other categories\n" +
					"    7. Any category\n" +
					"    P. Go Back to Previous Menu"
					);

			try {
				choice = scanner.next().charAt(0);;
				scanner.nextLine();
			} catch (java.util.InputMismatchException e) {
				System.out.println("Error: Invalid input is entered. Try again.");
				return false;
			}

			flag_catg = true;

			switch (choice) {
				case '1':
					category = Category.ELECTRONICS;
					break;
				case '2':
					category = Category.BOOKS;
					break;
				case '3':
					category = Category.HOME;
					break;
				case '4':
					category = Category.CLOTHING;
					break;
				case '5':
					category = Category.SPORTINGGOODS;
					break;
				case '6':
					category = Category.OTHERS;
					break;
				case '7':
					break;
				case 'p':
				case 'P':
					return false;
				default:
					System.out.println("Error: Invalid input is entered. Try again.");
					flag_catg = false;
					continue;
			}
		} while(!flag_catg);

		do {

			System.out.println(
					"----< Select the condition > \n" +
					"   1. New\n" +
					"   2. Like-new\n" +
					"   3. Used (Good)\n" +
					"   4. Used (Acceptable)\n" +
					"   P. Go Back to Previous Menu"
					);
			try {
				choice = scanner.next().charAt(0);;
				scanner.nextLine();
			} catch (java.util.InputMismatchException e) {
				System.out.println("Error: Invalid input is entered. Try again.");
				return false;
			}

			flag_cond = true;

			switch (choice) {
				case '1':
					condition = Condition.NEW;
					break;
				case '2':
					condition = Condition.LIKE_NEW;
					break;
				case '3':
					condition = Condition.GOOD;
					break;
				case '4':
					condition = Condition.ACCEPTABLE;
					break;
				case 'p':
				case 'P':
					return false;
				default:
					System.out.println("Error: Invalid input is entered. Try again.");
					flag_cond = false;
					continue;
				}
		} while(!flag_cond);

		try {
			System.out.println("---- Enter keyword to search the description : ");
			keyword = scanner.next();
			scanner.nextLine();

			System.out.println("---- Enter Seller ID to search : ");
			System.out.println(" ** Enter 'any' if you want to see items from any seller. ");
			seller = scanner.next();
			scanner.nextLine();

			System.out.println("---- Enter date posted (YYYY-MM-DD): ");
			System.out.println(" ** This will search items that have been posted after the designated date.");
			datePosted = scanner.next();
			scanner.nextLine();
		} catch (java.util.InputMismatchException e) {
			System.out.println("Error: Invalid input is entered. Try again.");
			return false;
		}

		LocalDateTime current = LocalDateTime.now();
		String currenttime = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		try{ // BuyItem 함수를 call할때 시간이 지나면 경매를 끝내고, 최종 경매 입찰자를 선정하고, Billing(최종 구매 내역)에 삽입함
            stmt.executeUpdate("INSERT INTO Billing(itemid, buyerid, sellerid, finalprice, transactiontime, paymentstatus) "+
            "SELECT Item.itemid, Bid.bidderid, Item.sellerid, Bid.bidprice, Bid.bidtime,  'COMPLETED' " +
            "FROM Bid JOIN Auction USING (auctionid) "+
            "JOIN Item USING (itemid)" +
            "WHERE Auction.status = 'BIDDING' "+ 
            "AND Auction.bidendtime < '" + currenttime+ "' " +
            "AND Bid.bidstatus = 'ACTIVE' "+
            "AND Item.itemid NOT IN (SELECT itemid FROM Billing)");
    
			stmt.executeUpdate(
			"UPDATE Auction " +
			"SET currentprice = subquery.maxbidprice, " +
			"    highestbidder = subquery.bidderid " +
			"FROM ( " +
			"    SELECT auctionid, MAX(bidprice) AS maxbidprice, bidderid " +
			"    FROM Bid " +
			"    WHERE bidstatus IN ('ACTIVE', 'WON') " +
			"    GROUP BY auctionid, bidderid " +
			"    HAVING MAX(bidprice) = ( " +
			"        SELECT MAX(bidprice) " +
			"        FROM Bid b2 " +
			"        WHERE b2.auctionid = Bid.auctionid " +
			"        AND b2.bidstatus IN ('ACTIVE', 'WON') " +
			"    ) " +
			") AS subquery " +
			"WHERE Auction.auctionid = subquery.auctionid " +
			"AND Auction.status IN ('BIDDING', 'SOLD') ");

            stmt.executeUpdate("UPDATE Auction SET status = 'SOLD' "+
            "WHERE bidendtime < '" + currenttime + "' " +
            "AND status = 'BIDDING' " +
            "AND itemid IN (SELECT itemid FROM Billing)");

			stmt.executeUpdate("UPDATE Bid SET bidstatus = 'WON' "+
			"FROM Auction " +
            "WHERE Auction.auctionid = Bid.auctionid " + 
			"AND bidendtime < '" + currenttime + "' " +
			"AND Bid.bidstatus = 'ACTIVE' "+
            "AND Auction.itemid IN (SELECT itemid FROM Billing)");

            stmt.executeUpdate("UPDATE Auction SET status = 'EXPIRED' "+
            "WHERE bidendtime < '" + currenttime + "' " +
            "AND status = 'LISTED' ");

        } catch(SQLException e){
            System.out.println("Error : Buy Item - Update query error" + e);
        }
		/* TODO: Query condition: item category */
		/* TODO: Query condition: item condition */
		/* TODO: Query condition: items whose description match the keyword (use LIKE operator) */
		/* TODO: Query condition: items from a particular seller */
		/* TODO: Query condition: posted date of item */

		/* TODO: List all items that match the query condition */
		System.out.println("Item ID | Item description |  Condition  | Seller | Buy-It-Now | Current Bid | highest bidder |   Time left   | bid close");
		System.out.println("-------------------------------------------------------------------------------------------------------------------------");
		try{
			String query = 
			"SELECT Item.itemid, Item.description, Item.condition, Item.sellerid, " +
			"Auction.buyitnowprice, COALESCE(Auction.currentprice, 0) AS currentprice, " +
			"COALESCE(Auction.highestbidder, 'No bids') AS highestbidder, Auction.bidendtime " +
			"FROM Auction JOIN Item ON Auction.itemid = Item.itemid " +
        	"WHERE Auction.status IN ('LISTED', 'BIDDING') ";

			// 동적 조건 추가
			if (category != null) {
				query += "AND Item.category = '" + category + "' ";
			}
			if (condition != null) {
				query += "AND Item.condition = '" + condition + "' ";
			}
			if (keyword != null) {
				query += "AND Item.description LIKE '%" + keyword + "%' ";
			}
			if (!seller.equalsIgnoreCase("any")) {
				query += "AND Item.sellerid = '" + seller + "' ";
			}
			if (datePosted != null) {
				query += "AND Auction.bidstarttime >= '" + datePosted + "' ";
			}

			Statement stmt1 = conn.createStatement();
			ResultSet rset1 = stmt1.executeQuery(query);
			if (!rset1.isBeforeFirst() && !rset1.next()) { // 만일 Item이 하나도 없다면 false 반환
				rset1.close();
				stmt1.close();
				System.out.println("There is No Item!");
				return false;
			}

			while(rset1.next()){
				String itemid = rset1.getString("itemid");
				String description = rset1.getString("description");
				String itemcondition = rset1.getString("condition");
				String itemseller = rset1.getString("sellerid");
				int buyitnowprice = rset1.getInt("buyitnowprice");
				int currentbid = rset1.getInt("currentprice");
				String highestbidder = rset1.getString("highestbidder");
				String bidclose = rset1.getString("bidendtime");
				String timeleft;
				if (bidclose != null && bidclose.contains(".")) {
            		bidclose = bidclose.substring(0, bidclose.indexOf(".")); 
        		}
				LocalDateTime currenttime1 = LocalDateTime.now();
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
				LocalDateTime bidendtime = LocalDateTime.parse(bidclose, formatter);
				if (bidendtime.isBefore(currenttime1)) {
					timeleft = "0 day 0 hrs";
				} else {
					Duration duration = Duration.between(currenttime1, bidendtime);
					long totalSeconds = duration.getSeconds();
					long days = totalSeconds / (60 * 60 * 24);
					long hours = (totalSeconds % (60 * 60 * 24)) / (60 * 60);
					timeleft = days + " day " + hours + " hrs";
				}
				System.out.printf("%-7s | %-16s | %-11s | %-6s | %-10d | %-11d | %-14s | %-13s | %s%n", 
					itemid, description, itemcondition, itemseller, buyitnowprice, currentbid, highestbidder, timeleft, bidclose);
				}
				rset1.close();
				stmt1.close();
		} catch (SQLException e){
			System.out.println("Error : Buy Item - print query error" + e);
		}

		System.out.println("---- Select Item ID to buy or bid: ");

		String selecteditemid;
		try {
			selecteditemid = scanner.next();
			scanner.nextLine();
			System.out.println("---- Price: ");
			price = scanner.nextInt();
			scanner.nextLine();
		} catch (java.util.InputMismatchException e) {
			System.out.println("Error: Invalid input is entered. Try again.");
			return false;
		}
		

		/* TODO: Buy-it-now or bid: If the entered price is higher or equal to Buy-It-Now price, the bid ends and the following needs to be printed. */
		/* Even if the bid price is higher than the Buy-It-Now price, the buyer pays the B-I-N price. */
		int buyitnowprice = 0;
		int selectedauctionid = -1;
		Timestamp ts;
		int maxbid = 0;
		try{
			Statement stmt2 = conn.createStatement();
            ResultSet rset2 = stmt2.executeQuery("SELECT auctionid, buyitnowprice, bidendtime FROM Auction WHERE itemid = '" + selecteditemid + "'");
			if(rset2.next()){
				selectedauctionid = rset2.getInt("auctionid");
				buyitnowprice = rset2.getInt("buyitnowprice");
				ts = rset2.getTimestamp("bidendtime");
				LocalDateTime endtime = ts.toLocalDateTime();
				LocalDateTime currenttime2 = LocalDateTime.now();
				if (endtime.isBefore(currenttime2)) { // BuyItem 중에도 현재시간이 경매 종료 시간을 지난다면 입찰 종료.
					System.out.println("Bid Ended.");
					rset2.close();
					stmt2.close();
					return false;
				}
			}
			rset2.close();
			stmt2.close();
			
			Statement stmt3 = conn.createStatement();
            ResultSet rset3 = stmt3.executeQuery("SELECT currentprice, startingprice FROM Auction WHERE itemid = '" + selecteditemid + "'");
			if(rset3.next()){
				maxbid = rset3.getInt("currentprice");
				int startprice = rset3.getInt("startingprice");
				if(price < startprice){
					System.out.println("Price is smaller than Bid starting price");
					rset3.close();
					stmt3.close();
					return false;
				}
			}

			LocalDateTime current1 = LocalDateTime.now();
			
			String currenttime3 = current1.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
			if (price >= buyitnowprice){ // 입찰 가격이 즉시구매가보다 크거나 같은 경우 즉시 구매 가능
				if (price > buyitnowprice){
					System.out.println("You must bid higher than the current price. \n"); 
				}
				System.out.println("Thank you for the purchase.\n"); 
                stmt.executeUpdate("INSERT INTO Bid(bidderid, auctionid, bidprice, bidtime, bidstatus) VALUES ('" + username + "' ," + selectedauctionid + "," + buyitnowprice + ", '" + currenttime3 + "' , '" + Bidstatus.WON + "')");
                stmt.executeUpdate("UPDATE Auction SET status = 'SOLD' WHERE itemid = '" + selecteditemid + "'");
                stmt.executeUpdate("UPDATE Bid SET bidstatus = 'OUTBID' WHERE auctionid = '" + selectedauctionid + "' AND bidderid <> '"+ username + "'");
				stmt.executeUpdate("INSERT INTO Billing(itemid, buyerid, sellerid, finalprice, transactiontime, paymentstatus) "+
                "SELECT Item.itemid, '" + username + "', Item.sellerid," + buyitnowprice + ", '" + currenttime3 + "' , 'COMPLETED' " +
				"FROM Auction JOIN Item USING (itemid) " +
				"WHERE Item.itemid = '" + selecteditemid + "'");
			}
			else if (price > maxbid){ // 입찰 가격이 즉시구매가보다 작고, 현재 최고 입찰가보다 큰 경우 입찰 가능
				System.out.println("Congratulations, you are the highest bidder.\n"); 
				stmt.executeUpdate("INSERT INTO Bid(bidderid, auctionid, bidprice, bidtime, bidstatus) VALUES ('" + username + "' ," + selectedauctionid + "," + price + ", '" + currenttime3 + "' , '" + Bidstatus.ACTIVE + "')");
                stmt.executeUpdate("UPDATE Auction SET status = 'BIDDING', currentprice = " + price + 
				", highestbidder = '" + username + "' WHERE itemid = '" + selecteditemid + "'");
				stmt.executeUpdate("UPDATE Bid SET bidstatus = 'OUTBID' WHERE auctionid = '" + selectedauctionid + "' AND bidderid <> '"+ username + "' AND bidprice < " + price);
			}
			else { // 입찰 가격이 현재 최고 입찰가보다 작은 경우 입찰 불가능
				System.out.println("You must bid higher than the current price. \n");
				return false;
			}
			rset3.close();
			stmt3.close();
		} catch (SQLException e){
			System.out.println("Error : BuyItem - Bid process error" + e);
			return false;
		}
	
		return true;
	}

	public static void CheckBuyStatus(){
		/* TODO: Check the status of the item the current buyer is bidding on */
		/* Even if you are outbidded or the bid closing date has passed, all the items this user has bidded on must be displayed */
		LocalDateTime current = LocalDateTime.now();
		String currenttime = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		try{ // 3종류의 Check함수를 call할때 시간이 지나면 경매를 끝내고, 최종 경매 입찰자를 선정하고, Billing(최종 구매 내역)에 삽입함
            stmt.executeUpdate("INSERT INTO Billing(itemid, buyerid, sellerid, finalprice, transactiontime, paymentstatus) "+
            "SELECT Item.itemid, Bid.bidderid, Item.sellerid, Bid.bidprice, Bid.bidtime,  'COMPLETED' "+
            "FROM Bid JOIN Auction USING (auctionid) "+
            "JOIN Item USING (itemid)" +
            "WHERE Auction.status = 'BIDDING' "+ 
            "AND Auction.bidendtime < '" + currenttime+ "' " +
            "AND Bid.bidstatus = 'ACTIVE' "+
            "AND Item.itemid NOT IN (SELECT itemid FROM Billing)");

			stmt.executeUpdate(
			"UPDATE Auction " +
			"SET currentprice = subquery.maxbidprice, " +
			"    highestbidder = subquery.bidderid " +
			"FROM ( " +
			"    SELECT auctionid, MAX(bidprice) AS maxbidprice, bidderid " +
			"    FROM Bid " +
			"    WHERE bidstatus IN ('ACTIVE', 'WON') " +
			"    GROUP BY auctionid, bidderid " +
			"    HAVING MAX(bidprice) = ( " +
			"        SELECT MAX(bidprice) " +
			"        FROM Bid b2 " +
			"        WHERE b2.auctionid = Bid.auctionid " +
			"        AND b2.bidstatus IN ('ACTIVE', 'WON') " +
			"    ) " +
			") AS subquery " +
			"WHERE Auction.auctionid = subquery.auctionid " +
			"AND Auction.status IN ('BIDDING', 'SOLD') ");
    
            stmt.executeUpdate("UPDATE Auction SET status = 'SOLD' "+
            "WHERE bidendtime < '" + currenttime + "' " +
            "AND status = 'BIDDING' " +
            "AND itemid IN (SELECT itemid FROM Billing)");

			stmt.executeUpdate("UPDATE Bid SET bidstatus = 'WON' "+
			"FROM Auction " +
            "WHERE Auction.auctionid = Bid.auctionid " + 
			"AND bidendtime < '" + currenttime + "' " +
			"AND Bid.bidstatus = 'ACTIVE' "+
            "AND Auction.itemid IN (SELECT itemid FROM Billing)");
    
            stmt.executeUpdate("UPDATE Auction SET status = 'EXPIRED' "+
            "WHERE bidendtime < '" + currenttime + "' " +
            "AND status = 'LISTED' ");
        } catch(SQLException e){
            System.out.println("Error : CheckBuyStatus - Update query error" + e);
        }

		System.out.println("item ID   | item description   | highest bidder | highest bidding price | your bidding price | bid closing date/time");
		System.out.println("--------------------------------------------------------------------------------------------------------------------");
		
		try{
			ResultSet rset = stmt.executeQuery("SELECT * FROM Item JOIN Auction USING (itemid) JOIN Bid USING (auctionid) WHERE Bid.bidderid = '" + username + "'");
			while (rset.next()){
				String itemid = rset.getString("itemid");
				String description = rset.getString("description");
				String highestbidder = rset.getString("highestbidder");
				int currentprice = rset.getInt("currentprice");
				int bidprice = rset.getInt("bidprice");
				String bidclose = rset.getString("bidendtime");
				String timeleft;
				if (bidclose != null && bidclose.contains(".")) {
            		bidclose = bidclose.substring(0, bidclose.indexOf(".")); 
        		}
				System.out.printf("%-9s | %-18s | %-14s | %-21d | %-18d | %s%n", itemid, description, highestbidder, currentprice, bidprice, bidclose);
            	}
			rset.close();
		} catch (SQLException e){
			System.out.println("Error: CheckBuyStatus error" + e);
		}
	}

	public static void CheckAccount(){
		/* TODO: Check the balance of the current user.  */
		LocalDateTime current = LocalDateTime.now();
		String currenttime = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		try{ // 3종류의 Check함수를 call할때 시간이 지나면 경매를 끝내고, 최종 경매 입찰자를 선정하고, Billing(최종 구매 내역)에 삽입함
            stmt.executeUpdate("INSERT INTO Billing(itemid, buyerid, sellerid, finalprice, transactiontime, paymentstatus) "+
            "SELECT Item.itemid, Bid.bidderid, Item.sellerid, Bid.bidprice, Bid.bidtime, 'COMPLETED' " +
            "FROM Bid JOIN Auction USING (auctionid) "+
            "JOIN Item USING (itemid)" +
            "WHERE Auction.status = 'BIDDING' "+ 
            "AND Auction.bidendtime < '" + currenttime+ "' " +
            "AND Bid.bidstatus = 'ACTIVE' "+
            "AND Item.itemid NOT IN (SELECT itemid FROM Billing)");

			stmt.executeUpdate(
			"UPDATE Auction " +
			"SET currentprice = subquery.maxbidprice, " +
			"    highestbidder = subquery.bidderid " +
			"FROM ( " +
			"    SELECT auctionid, MAX(bidprice) AS maxbidprice, bidderid " +
			"    FROM Bid " +
			"    WHERE bidstatus IN ('ACTIVE', 'WON') " +
			"    GROUP BY auctionid, bidderid " +
			"    HAVING MAX(bidprice) = ( " +
			"        SELECT MAX(bidprice) " +
			"        FROM Bid b2 " +
			"        WHERE b2.auctionid = Bid.auctionid " +
			"        AND b2.bidstatus IN ('ACTIVE', 'WON') " +
			"    ) " +
			") AS subquery " +
			"WHERE Auction.auctionid = subquery.auctionid " +
			"AND Auction.status IN ('BIDDING', 'SOLD') ");
    
            stmt.executeUpdate("UPDATE Auction SET status = 'SOLD' "+
            "WHERE bidendtime < '" + currenttime + "' " +
            "AND status = 'BIDDING' " +
            "AND itemid IN (SELECT itemid FROM Billing)");

			stmt.executeUpdate("UPDATE Bid SET bidstatus = 'WON' "+
			"FROM Auction " +
            "WHERE Auction.auctionid = Bid.auctionid " + 
			"AND bidendtime < '" + currenttime + "' " +
			"AND Bid.bidstatus = 'ACTIVE' "+
            "AND Auction.itemid IN (SELECT itemid FROM Billing)");

            stmt.executeUpdate("UPDATE Auction SET status = 'EXPIRED' "+
            "WHERE bidendtime < '" + currenttime + "' " +
            "AND status = 'LISTED' ");
        } catch(SQLException e){
            System.out.println("Error : CheckAccount - Update query error" + e);
        }
		// 자신의 판매 Item을 모두 나열함
		System.out.println("\n[Sold Items] \n");
		System.out.println("item category  | item ID   |      sold date       | sold price |  buyer ID   | commission  ");
		System.out.println("-------------------------------------------------------------------------------------------");
		try{
            ResultSet rset 
            = stmt.executeQuery("SELECT solditem.category, solditem.itemid, Billing.transactiontime, Billing.finalprice, Billing.buyerid, FLOOR(Billing.finalprice * 0.05) AS commission " +
            "FROM Billing JOIN (SELECT itemid, category FROM Item) AS solditem " +
            "ON Billing.itemid = solditem.itemid "+
            "WHERE Billing.sellerid = '" + username + "'");
            while (rset.next()){
                String category = rset.getString("category");
                String itemid = rset.getString("itemid");
                String transactiontime = rset.getString("transactiontime");
				int finalprice = rset.getInt("finalprice");
                String buyerid = rset.getString("buyerid");
                int commission = rset.getInt("commission");
				if (transactiontime != null && transactiontime.contains(".")) {
            		transactiontime = transactiontime.substring(0, transactiontime.indexOf(".")); 
        		}
                System.out.printf("%-14s | %-9s | %-20s | %-10d | %-11s | %d%n", category, itemid, transactiontime, finalprice, buyerid, commission);
            }
            rset.close();
        } catch (SQLException e){
            System.out.println("Error : CheckAccount - Sold Item error" + e);
        }
		// 자신의 구매 Item을 모두 나열함
		System.out.println("\n[Purchased Items] \n");
		System.out.println("item category  | item ID   |    purchased date    | purchased price | seller ID ");
		System.out.println("--------------------------------------------------------------------------------");
		try{
            ResultSet rset 
            = stmt.executeQuery("SELECT purchaseditem.category, purchaseditem.itemid, Billing.transactiontime, Billing.finalprice, Billing.sellerid " +
            "FROM Billing JOIN (SELECT itemid, category FROM Item) AS purchaseditem " +
            "ON Billing.itemid = purchaseditem.itemid "+
            "WHERE Billing.buyerid = '" + username + "'");
            while (rset.next()){
                String category = rset.getString("category");
                String itemid = rset.getString("itemid");
                String transactiontime = rset.getString("transactiontime");
				int finalprice = rset.getInt("finalprice");
                String sellerid = rset.getString("sellerid");
				if (transactiontime != null && transactiontime.contains(".")) {
            		transactiontime = transactiontime.substring(0, transactiontime.indexOf(".")); 
        		}
                System.out.printf("%-14s | %-9s | %-20s | %-15d | %s%n", category, itemid, transactiontime, finalprice, sellerid);
            }
            rset.close();
        } catch (SQLException e){
            System.out.println("Error : CheckAccount - Purchased Item error" + e);
        }
	}

	public static void main(String[] args) {
		char choice;
		boolean ret;

		if(args.length<2){
			System.out.println("Usage: java Auction postgres_id password");
			System.exit(1);
		}


		try{
			conn = DriverManager.getConnection("jdbc:postgresql://localhost/"+args[0], args[0], args[1]); 
			stmt = conn.createStatement();
		}
		catch(SQLException e){
			System.out.println("SQLException : " + e);	
			System.exit(1);
		}

		do {
			username = null;
			System.out.println(
					"----< Login menu >\n" + 
					"----(1) Login\n" +
					"----(2) Sign up\n" +
					"----(3) Login as Administrator\n" +
					"----(Q) Quit"
					);

			try {
				choice = scanner.next().charAt(0);;
				scanner.nextLine();
			} catch (java.util.InputMismatchException e) {
				System.out.println("Error: Invalid input is entered. Try again.");
				continue;
			}

			try {
				switch ((int) choice) {
					case '1':
						ret = LoginMenu();
						if(!ret) continue;
						break;
					case '2':
						ret = SignupMenu();
						if(!ret) continue;
						break;
					case '3':
						ret = AdminMenu();
						if(!ret) continue;
					case 'q':
					case 'Q':
						System.out.println("Good Bye");
						/* TODO: close the connection and clean up everything here */
						conn.close();
						System.exit(1);
					default:
						System.out.println("Error: Invalid input is entered. Try again.");
				}
			} catch (SQLException e) {
				System.out.println("SQLException : " + e);	
			}
		} while (username==null || username.equalsIgnoreCase("back"));  

		// logged in as a normal user 
		do {
			System.out.println(
					"---< Main menu > :\n" +
					"----(1) Sell Item\n" +
					"----(2) Check Status of Your Listed Item \n" +
					"----(3) Buy Item\n" +
					"----(4) Check Status of your Bid \n" +
					"----(5) Check your Account \n" +
					"----(Q) Quit"
					);

			try {
				choice = scanner.next().charAt(0);;
				scanner.nextLine();
			} catch (java.util.InputMismatchException e) {
				System.out.println("Error: Invalid input is entered. Try again.");
				continue;
			}

			try{
				switch (choice) {
					case '1':
						ret = SellMenu();
						if(!ret) continue;
						break;
					case '2':
						CheckSellStatus();
						break;
					case '3':
						ret = BuyItem();
						if(!ret) continue;
						break;
					case '4':
						CheckBuyStatus();
						break;
					case '5':
						CheckAccount();
						break;
					case 'q':
					case 'Q':
						System.out.println("Good Bye");
						/* TODO: close the connection and clean up everything here */
						stmt.close();
						conn.close();
						System.exit(1);
				}
			} catch (SQLException e) {
				System.out.println("SQLException : " + e);	
				System.exit(1);
			}
		} while(true);
	} // End of main 
} // End of class


