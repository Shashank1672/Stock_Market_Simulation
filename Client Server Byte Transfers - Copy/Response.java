import java.io.Serializable;

public class Response implements Serializable{
    String request;
    boolean response;
    String message;

    Response(String request, boolean response, String message){
        this.request = request;
        this.response = response;
        this.message = message;
    }
}
