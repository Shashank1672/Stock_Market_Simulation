public class RequestOrder {
    String isin;
    String dpid;
    int qty;
    String type;

    public RequestOrder(String isin, String dpid, int qty, String type){
        this.isin = isin;
        this.dpid = dpid;
        this.qty = qty;
        this.type = type;
    }
}
