import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
    private Socket socket;
    ObjectOutputStream out;
    ObjectInputStream in;

    public Client(Socket socket){
        this.socket = socket;
        try{
            out = new ObjectOutputStream(this.socket.getOutputStream());
            in = new ObjectInputStream(this.socket.getInputStream());
        }
        catch (IOException e) {
            closeConnection(socket);
        }
    }
//----------------------------------------------------------------------------------------------------------------------------------------------
    public boolean authenticate(String email, String password){
        Credentials creds = new Credentials(email, password);
        Requesting client_request = new Requesting("LOGIN", creds, null, null);
        try {
            out.writeObject(client_request);
            Response r = (Response) in.readObject();
            return r.response;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
//----------------------------------------------------------------------------------------------------------------------------------------------
    public void tradeStock(){
        try{
            Requesting client_request = new Requesting("GET_STOCKS", null, null, null);
            this.out.writeObject(client_request);
            HashMap<String, Integer> stocks = (HashMap<String, Integer>) in.readObject();
            System.out.println("The available stocks to trade are\n");
            System.out.println("Stock name --> Price\n");
            System.out.println("--------------------------");
            for(HashMap.Entry<String, Integer> entry : stocks.entrySet()){
                System.out.println(entry.getKey()+" --> "+entry.getValue());
            }

            Scanner scn = new Scanner(System.in);
            System.out.print("Enter the stock name: "); String isin = scn.nextLine();
            System.out.print("Enter the quantity: "); int qty = Integer.parseInt(scn.nextLine());
            System.out.print("Enter the trade type: "); String type = scn.nextLine();
            
            OrderRequest or = new OrderRequest(isin, qty, type);
            this.out.writeObject(or);
            String report = (String) in.readObject();
            System.out.println("\n"+report);
            System.out.println("Check your portfolio for more updates");

        }catch (Exception e) {e.printStackTrace();}
    }
//----------------------------------------------------------------------------------------------------------------------------------------------
    public void account_creation(UserAccount accountForm){
        Requesting client_request = new Requesting("SIGN_UP", null, accountForm, null);
        try {
            out.writeObject(client_request);
            Response r = (Response) in.readObject();
            System.out.println(r.message);
        }
        catch (Exception e){ e.printStackTrace();}
    }
//----------------------------------------------------------------------------------------------------------------------------------------------
    public void getPortfolio(){
        Requesting client_request = new Requesting("PORTFOLIO", null, null, null);
        try{
            out.writeObject(client_request);
            System.out.println("Your holdings are");
            HashMap<String, Integer> holdings = (HashMap<String, Integer>) in.readObject();
            System.out.println("Stocks --> Quantity");
            for(HashMap.Entry<String, Integer> entry : holdings.entrySet()){
                System.out.println(entry.getKey()+" --> "+entry.getValue());
            }
        }catch (Exception e){ e.printStackTrace(); }
    }
//----------------------------------------------------------------------------------------------------------------------------------------------
    public void getBalance(){
        Requesting client_request = new Requesting("GET_BALANCE", null, null, null);
        try{
            out.writeObject(client_request);
            int balance = (int) in.readObject();
            System.out.println("Your available balance to trade is Rs."+balance);
        }catch (Exception e){ e.printStackTrace(); }
    }
//----------------------------------------------------------------------------------------------------------------------------------------------
    public void addBalance(int amount){
        Requesting client_request = new Requesting("ADD_BALANCE", null, null, null);
        try{
            out.writeObject(client_request);
            out.writeObject(amount);
            Response r = (Response) in.readObject();
            System.out.println(r.message);
        }catch (Exception e){ e.printStackTrace(); }
    }
//----------------------------------------------------------------------------------------------------------------------------------------------
    public void sendMessage() {
        Scanner scn = new Scanner(System.in);
        while(this.socket.isConnected()){
            System.out.println("\n1. Trade stock\n2. View Balance\n3. Add Balance\n4. See portfolio");
            System.out.print("Enter your choice: "); int choice = Integer.parseInt(scn.nextLine());
            if (choice == 1)
                tradeStock();
            else if (choice == 2)
                getBalance();
            else if (choice == 3){
                System.out.print("Enter the amount to be added: ");
                addBalance(Integer.parseInt(scn.nextLine()));
            }
            else if(choice == 4)
                getPortfolio();
        }
    }
//----------------------------------------------------------------------------------------------------------------------------------------------
    public void closeConnection(Socket socket){
        try{
            if(socket != null){
                socket.close();
                System.out.println("You have been disconnected rom the server");
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
//----------------------------------------------------------------------------------------------------------------------------------------------
    public static void main(String[] args) throws IOException{
        Scanner scn = new Scanner(System.in);

        System.out.print("Enter the IP Address of the server: "); String IPAddress = scn.nextLine();
        System.out.print("Enter the port number of the server: "); int port = Integer.parseInt(scn.nextLine());

        //System.out.println(IPAddress);
        System.out.println(port);

        Socket socket = new Socket(IPAddress, port);
        Client client = new Client(socket);

        System.out.println("You are connected to the server.");

        int choice = 1;
        String email;
        String password;

        while (choice!=0){

            System.out.println("1. Login");
            System.out.println("2. SignUp");
            System.out.print("\nEnter your choice: "); choice = Integer.parseInt(scn.nextLine());
            
            if(choice == 1){

                int try_again;
                do{
                    System.out.print("Email Id: "); email = scn.nextLine();
                    System.out.print("Password: "); password = scn.nextLine();

                    if(client.authenticate(email, password)){
                        System.out.println("Login Successful");
                        client.sendMessage();
                        try_again = 0;
                    }
                    else{
                        System.out.println("Login Failed");
                        System.out.print("Press 1 to try again: "); try_again = Integer.parseInt(scn.nextLine());
                        choice = try_again == 0 ? 0 : 1; 
                    }
                } while(try_again == 1);
            }

            else if(choice == 2){
                System.out.print("Enter your name: "); String name  = scn.nextLine();
                System.out.print("Enter you email: "); email = scn.nextLine();
                System.out.print("Enter your PAN number: "); String pan  = scn.nextLine();
                System.out.print("Enter your password: "); password  = scn.nextLine();

                UserAccount new_account_form = new UserAccount(name, pan, email, password);
                client.account_creation(new_account_form);
            }
            
        }
        scn.close();
    }
}
