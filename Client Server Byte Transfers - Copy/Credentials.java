import java.io.Serializable;

public class Credentials implements Serializable{
    String email;
    String password;

    Credentials (String uname, String pass){
        email = uname;
        password = pass;
    }
}
