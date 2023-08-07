import java.net.*;
import java .io.*;
import java.sql.*;
import java.util.*;

public class ClientHandler implements Runnable{
    
    private Socket socket;
    private Connection db_conn;
    private String dpid;
    static HashMap<String, List<StockOrder>> buyQueue = new HashMap<String, List<StockOrder>>();
    static HashMap<String, List<StockOrder>> sellQueue = new HashMap<String, List<StockOrder>>();
    static HashMap<String, Integer> stocks = new HashMap<>();

    Statement stmt;
    ObjectOutputStream out;
    ObjectInputStream in;

    public ClientHandler(Socket socket, Connection conn){
        try{
            this.socket = socket;
            this.db_conn = conn;
            stmt = this.db_conn.createStatement();
            out = new ObjectOutputStream(this.socket.getOutputStream());
            in = new ObjectInputStream(this.socket.getInputStream());
            stocks.put("ITC", 300);
            stocks.put("ICICI", 754);
            stocks.put("INFY", 1400);
            stocks.put("BOB", 130);
            stocks.put("SBI", 456);
            stocks.put("RELIANCE", 2000);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
//-----------------------------------------------------------------------------------------------------------------------------------
    public void fillQueue(OrderRequest or){
        String query = String.format("SELECT * FROM broker.user_info WHERE dpid = '%s'",this.dpid);
        try {
            ResultSet rs = stmt.executeQuery(query);
            rs.next();

            // Retriving Blob from database
            InputStream binaryStream = rs.getBinaryStream("account_info");
            ObjectInputStream byte_in = new ObjectInputStream(binaryStream);
            UserAccount user_account = (UserAccount) byte_in.readObject();

            if(or.type.equalsIgnoreCase("sell")){
                System.out.println("Sell Order request received from "+this.dpid);
                String isin = or.isin;
                if(user_account.holdings.get(isin) >= or.qty){
                    StockOrder stock_order = new StockOrder(this.dpid, or.qty);
                    if(sellQueue.containsKey(isin)){
                        List<StockOrder> temp = sellQueue.get(isin);
                        temp.add(stock_order);
                    }
                    else{
                        List<StockOrder> temp = new ArrayList<>();
                        temp.add(stock_order);
                        sellQueue.put(isin, temp);
                    }
                    this.out.writeObject("Sell Order placed");
                } 
                else{
                    this.out.writeObject("Insufficient stock to trade");
                }
            }
            else if(or.type.equalsIgnoreCase("buy")){
                if(user_account.balance >= (or.qty * stocks.get(or.isin)))
                {
                    System.out.println("Buy Order request received from "+this.dpid);
                    String isin = or.isin;
                    StockOrder stock_order = new StockOrder(this.dpid, or.qty);
                    if(buyQueue.containsKey(isin)){
                        List<StockOrder> temp = buyQueue.get(isin);
                        temp.add(stock_order);
                    }
                    else{
                        List<StockOrder> temp = new ArrayList<>();
                        temp.add(stock_order);
                        buyQueue.put(isin, temp);
                    }
                    this.out.writeObject("Buy Order placed");
                } 
                else{
                    this.out.writeObject("Insufficient Balance to trade");
                }
            }
        }catch (Exception e){e.printStackTrace();}
        matchMaking();
    }
//--------------------------------------------------------------------------------------------------------------------------------------
    public void matchMaking(){
        for(String stock : buyQueue.keySet()){
            List<StockOrder> q1 = buyQueue.get(stock);
            List<StockOrder> q2 = sellQueue.get(stock);
            if(sellQueue.containsKey(stock)){
                while(q1.size()!=0 && q2.size()!=0){
                    StockOrder buy_order = q1.get(0);
                    StockOrder sell_order = q2.get(0);

                    int exec_order = Math.min(buy_order.qty, sell_order.qty);

                    try{
                        buy_order.qty-=exec_order;
                        q1.set(0, buy_order);
                        String query = String.format("SELECT * FROM broker.user_info WHERE dpid = '%s'",buy_order.dpid);
                        ResultSet rs = stmt.executeQuery(query);
                        rs.next();

                        // Retriving Blob from database
                        InputStream binaryStream = rs.getBinaryStream("account_info");
                        ObjectInputStream byte_in = new ObjectInputStream(binaryStream);
                        UserAccount user_account = (UserAccount) byte_in.readObject();

                        user_account.balance-=(exec_order * stocks.get(stock));
                        int prev_stock = user_account.holdings.get(stock);
                        prev_stock+=exec_order;
                        user_account.holdings.put(stock, prev_stock);

                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        ObjectOutputStream byte_out = new ObjectOutputStream(bos);
                        byte_out.writeObject(user_account);
                        byte[] bytes = bos.toByteArray();
                        
                        PreparedStatement pstmt = db_conn.prepareStatement("UPDATE broker.user_info SET account_info = ? WHERE dpid = ?");
                        pstmt.setBytes(1, bytes);
                        pstmt.setString(2, buy_order.dpid);
                        pstmt.executeUpdate();
                        System.out.println("Buyer's Portfolio Updated. DPID is "+buy_order.dpid);

                    } catch (Exception e) {e.printStackTrace();}

                    try{
                        sell_order.qty-=exec_order;
                        q2.set(0, sell_order);
                        String query = String.format("SELECT * FROM broker.user_info WHERE dpid = '%s'",sell_order.dpid);
                        ResultSet rs = stmt.executeQuery(query);
                        rs.next();

                        // Retriving Blob from database
                        InputStream binaryStream = rs.getBinaryStream("account_info");
                        ObjectInputStream byte_in = new ObjectInputStream(binaryStream);
                        UserAccount user_account = (UserAccount) byte_in.readObject();

                        user_account.balance+=(exec_order * stocks.get(stock));
                        int prev_stock = user_account.holdings.get(stock);
                        prev_stock-=exec_order;
                        user_account.holdings.put(stock, prev_stock);

                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        ObjectOutputStream byte_out = new ObjectOutputStream(bos);
                        byte_out.writeObject(user_account);
                        byte[] bytes = bos.toByteArray();
                        
                        PreparedStatement pstmt = db_conn.prepareStatement("UPDATE broker.user_info SET account_info = ? WHERE dpid = ?");
                        pstmt.setBytes(1, bytes);
                        pstmt.setString(2, sell_order.dpid);
                        pstmt.executeUpdate();
                        System.out.println("Seller's Portfolio Updated. DPID is "+sell_order.dpid);

                    } catch (Exception e) {e.printStackTrace();}
                    
                    if(buy_order.qty == 0)
                        q1.remove(0);
                    if(sell_order.qty == 0)
                        q1.remove(0);
                    
                    System.out.println("\nTrade Executed --> "+stock);
                    System.out.println("Buyer - "+buy_order.dpid+"|  Qty - "+exec_order);
                    System.out.println("Seller - "+sell_order.dpid+"|  Qty - "+exec_order);
                }
            }
        }
    }
//--------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public void run() {
        
        while(socket.isConnected()){
            try{
                Requesting client_request = (Requesting) in.readObject();
                
                // Authenticating the user
                if(client_request.request.equals("LOGIN")){
                    Credentials creds = client_request.login;
                    String query = "SELECT * FROM broker.user_creds";
                    try {
                        ResultSet rs = stmt.executeQuery(query);
                        boolean found = false;
                        while(rs.next()){
                            if(creds.email.equals(rs.getString("email")) && creds.password.equals(rs.getString("pass"))){
                                Response r = new Response("LOGIN", true, "Login Successful");
                                this.dpid = rs.getString("dpid");
                                this.out.writeObject(r);
                                System.out.println(this.dpid+" has logged in");
                                found = true;
                                break;
                            }
                        }
                        if(!found){
                            Response r = new Response("LOGIN", false, "Login Failed");
                            this.out.writeObject(r);
                        }    
                    } catch (Exception e){ e.printStackTrace();}
                }
//------------------------------------------------------------------------------------------------------------------------------------------------------------
                // Creating a new user acoount 
                else if(client_request.request.equals("SIGN_UP")){
                    System.out.println("Client has requested to create a new account");
                    UserAccount new_acc = client_request.signUp;
                    String query = String.format("INSERT INTO broker.user_creds VALUES ('%s', '%s', '%s')", new_acc.email, new_acc.password, new_acc.pan);
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    ObjectOutputStream byte_out = new ObjectOutputStream(bos);
                    byte_out.writeObject(new_acc);
                    byte[] bytes = bos.toByteArray();
                    
                    try{
                        stmt.executeUpdate(query);
                        PreparedStatement pstmt = db_conn.prepareStatement("INSERT INTO broker.user_info VALUES (?, ?)");
                        pstmt.setString(1, new_acc.pan);
                        pstmt.setBytes(2, bytes);
                        pstmt.executeUpdate();
                        Response r = new Response("SIGN_UP", true, "Account creation successful");
                        this.out.writeObject(r);
                    }
                    catch(Exception e){
                        e.printStackTrace();
                        Response r = new Response("SIGN_UP", true, "Unable to create account at this moment");
                        this.out.writeObject(r);
                    }
                }
//------------------------------------------------------------------------------------------------------------------------------------------------------------
                //Retrieve the portfolio
                else if(client_request.request.equals("PORTFOLIO")){
                    String query = String.format("SELECT * FROM broker.user_info WHERE dpid = '%s'",this.dpid);
                    System.out.println(this.dpid+" has sent a request to see the portfolio");
                    try {
                        ResultSet rs = stmt.executeQuery(query);
                        rs.next();
                        InputStream binaryStream = rs.getBinaryStream("account_info");
                        ObjectInputStream byte_in = new ObjectInputStream(binaryStream);
                        UserAccount user_account = (UserAccount) byte_in.readObject();
                        this.out.writeObject(user_account.holdings);
                    } catch (Exception e){ e.printStackTrace();}
                }
//------------------------------------------------------------------------------------------------------------------------------------------------------------
                //Add Balance
                else if(client_request.request.equals("ADD_BALANCE")){
                    System.out.println(this.dpid+" has requested to add balance");
                    String query = String.format("SELECT * FROM broker.user_info WHERE dpid = '%s'",this.dpid);
                    try {
                        ResultSet rs = stmt.executeQuery(query);
                        rs.next();

                        // Retriving Blob from database
                        InputStream binaryStream = rs.getBinaryStream("account_info");
                        ObjectInputStream byte_in = new ObjectInputStream(binaryStream);
                        UserAccount user_account = (UserAccount) byte_in.readObject();

                        // Updated the balance to the user account
                        user_account.balance+= (int) in.readObject();

                        // Write back updated value to database
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        ObjectOutputStream byte_out = new ObjectOutputStream(bos);
                        byte_out.writeObject(user_account);
                        byte[] bytes = bos.toByteArray();
                        
                        PreparedStatement pstmt = db_conn.prepareStatement("UPDATE broker.user_info SET account_info = ? WHERE dpid = ?");
                        pstmt.setBytes(1, bytes);
                        pstmt.setString(2, this.dpid);
                        pstmt.executeUpdate();

                        Response r = new Response("ADD_BALANCE", true, "Your wallet has been successfully recharged.");
                        this.out.writeObject(r);
                    } catch (Exception e){ e.printStackTrace();}
                }
//------------------------------------------------------------------------------------------------------------------------------------------------------------
                //Get Balance
                else if(client_request.request.equals("GET_BALANCE")){
                    System.out.println(this.dpid+" has requested to VIEW balance");
                    String query = String.format("SELECT * FROM broker.user_info WHERE dpid = '%s'",this.dpid);
                    try {
                        ResultSet rs = stmt.executeQuery(query);
                        rs.next();

                        InputStream binaryStream = rs.getBinaryStream("account_info");
                        ObjectInputStream byte_in = new ObjectInputStream(binaryStream);
                        UserAccount user_account = (UserAccount) byte_in.readObject();

                        out.writeObject(user_account.balance);
                    } catch (Exception e){ e.printStackTrace();}
                }
//-------------------------------------------------------------------------------------------------------------------------------------------------------------
                else if(client_request.request.equals("GET_STOCKS")){
                    System.out.println(this.dpid+" has requested to TRADE stocks");
                    this.out.writeObject(stocks);
                    fillQueue((OrderRequest) in.readObject());
                }
            }
            catch (Exception e){
                e.printStackTrace();
                closeEverything(socket);
                break;
            }
        }
    }


    public void closeEverything(Socket socket){
        try{
            if(socket != null)
                socket.close();
                System.out.println("Client left the network");
        }catch(IOException e){ e.printStackTrace();}
    }
}
