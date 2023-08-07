import java.util.*;

public class Main{
    static HashMap<String,  <StockOrder>> buyQueue = new HashMap<String, Queue<StockOrder>>();
    static HashMap<String, Queue<StockOrder>> sellQueue = new HashMap<String, Queue<StockOrder>>();
    public static void main(String[] args){
        RequestOrder order1 = new RequestOrder("Infosys", "1672", 9, "Sell");
        RequestOrder order6 = new RequestOrder("Infosys", "76543", 12, "Sell");
        RequestOrder order2 = new RequestOrder("ITC", "2365", 5, "Sell");
        RequestOrder order3 = new RequestOrder("TCS", "2465", 7, "Buy");
        RequestOrder order4 = new RequestOrder("Infosys", "1672", 3, "Buy");
        RequestOrder order5 = new RequestOrder("ICICI", "1672", 3, "Buy");
        
        fillQueue(order1);
        fillQueue(order6);
        fillQueue(order2);
        fillQueue(order3);
        fillQueue(order4);
        fillQueue(order5);


    }
    public static void fillQueue(RequestOrder requestOrder) {
        if(requestOrder.type.equals("Sell")){

            String isin = requestOrder.isin;
            String dpid = requestOrder.dpid;
            int qty = requestOrder.qty;

            StockOrder stockOrder = new StockOrder(dpid, qty);

            if(sellQueue.containsKey(isin)){
                Queue<StockOrder> temp = sellQueue.get(isin);
                temp.add(stockOrder);
            }
            else{
                Queue<StockOrder> temp = new LinkedList<>();
                temp.add(stockOrder);
                sellQueue.put(isin, temp);
            }
        }

        else if(requestOrder.type.equals("Buy")){

            String isin = requestOrder.isin;
            String dpid = requestOrder.dpid;
            int qty = requestOrder.qty;

            StockOrder stockOrder = new StockOrder(dpid, qty);

            if(buyQueue.containsKey(isin)){
                Queue<StockOrder> temp = buyQueue.get(isin);
                temp.add(stockOrder);
            }
            else{
                Queue<StockOrder> temp = new LinkedList<>();
                temp.add(stockOrder);
                buyQueue.put(isin, temp);
            }
        }

        System.out.print("BUY QUEUE --> ");
        for (HashMap.Entry<String, Queue<StockOrder>> entry : buyQueue.entrySet()) {
            String isin = entry.getKey();
            Queue<StockOrder> queue = entry.getValue();
            System.out.print(isin + ": ");
            for (StockOrder element : queue) {
                System.out.print("{DPID: " + element.dpid + ", Qty: " + element.qty + "},  ");
            }
            System.out.print(" || ");
        }
        System.out.println();

        System.out.print("SELL QUEUE --> ");
        for (HashMap.Entry<String, Queue<StockOrder>> entry : sellQueue.entrySet()) {
            String isin = entry.getKey();
            Queue<StockOrder> queue = entry.getValue();
            System.out.print(isin + ": ");
            for (StockOrder element : queue) {
                System.out.print("{DPID: " + element.dpid + ", Qty: " + element.qty + "},  ");
            }
            System.out.print(" || ");
        }
        System.out.println("\n\n");
    }
}