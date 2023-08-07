import java.io.Serializable;

public class Requesting implements Serializable{
    String request;
    Credentials login;
    UserAccount signUp;
    OrderRequest orderRequest;
    
    Requesting(String request, Credentials login, UserAccount signUp, OrderRequest orderRequest){
        this.request = request;
        this.login = login;
        this.signUp = signUp;
        this.orderRequest = orderRequest;
    }
}
