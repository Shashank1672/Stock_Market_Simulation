import java.io.Serializable;

public class OrderRequest implements Serializable{
    String isin;
    int qty;
    String type;

    public OrderRequest(String isin, int qty, String type){
        this.isin = isin;
        this.qty = qty;
        this.type = type;
    }
}
