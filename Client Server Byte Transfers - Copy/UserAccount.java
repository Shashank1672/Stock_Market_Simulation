import java.io.Serializable;
import java.util.*;

public class UserAccount implements Serializable{
    String name;
    String pan;
    String email;
    String password;
    int balance;
    HashMap <String, Integer> holdings = new HashMap<>();

    UserAccount(String name, String pan, String email, String password){
        this.name = name;
        this.pan = pan;
        this.email= email;
        this.password = password;
        holdings.put("INFY", 3);
        holdings.put("ITC", 5);
        holdings.put("SBI", 7);
        holdings.put("INFY", 10);
        holdings.put("ICICI", 2);
        holdings.put("BOB", 15);
    }
}