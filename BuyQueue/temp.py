buy = {}
sell = {}
executedTrades = []

def fillQueue(Order):
    if Order["Type"] == "Sell":
        if Order["ISIN"] not in sell:
            sell[Order["ISIN"]] = [{"DPID": Order["DPID"], "Qty": Order["Quantity"]}]
        else:
            sell[Order["ISIN"]].append({"DPID": Order["DPID"], "Qty": Order["Quantity"]})
            
    elif Order["Type"] == "Buy":
        if Order["ISIN"] not in buy:
            buy[Order["ISIN"]] = [{"DPID": Order["DPID"], "Qty": Order["Quantity"]}]
        else:
            buy[Order["ISIN"]].append({"DPID": Order["DPID"], "Qty": Order["Quantity"]})
            
    print("Buy Queue: ",buy)
    print("Sell Queue: ",sell)

#Order1 = {"ISIN": "Infosys", "DPID": "1672", "Quantity": 10, "Type": "Sell"}
#Order2 = {"ISIN": "Infosys", "DPID": "2365", "Quantity": 25, "Type": "Buy"}
#Order3 = {"ISIN": "Tcs", "DPID": "2365", "Quantity": 7, "Type": "Buy"}
#Order4 = {"ISIN": "Infosys", "DPID": "6432", "Quantity": 23, "Type": "Sell"}
#Order5 = {"ISIN": "ICICI", "DPID": "1672", "Quantity": 3, "Type": "Buy"}
#Order6 = {"ISIN": "Infosys", "DPID": "1672", "Quantity": 3, "Type": "Sell"}

def matchmaking():
    for stock in buy:
        if stock in sell:
            while len(buy[stock]) and len(sell[stock]):
                buyerDPID = buy[stock][0]['DPID']
                sellerDPID = sell[stock][0]['DPID']
                
                exec_order = min(buy[stock][0]['Qty'], sell[stock][0]['Qty'])
                
                buy[stock][0]['Qty']-=exec_order
                sell[stock][0]['Qty']-=exec_order
                
                if buy[stock][0]['Qty'] == 0:
                    buy[stock].pop(0)
                if sell[stock][0]['Qty'] == 0:
                    sell[stock].pop(0)
                print(f'\n\nTrade Executed\nStock --> {stock}\nBuy Order --> {buyerDPID, exec_order}\nSell Order --> {sellerDPID, exec_order}')
                print("\nBuy Queue: ",buy)
                print("Sell Queue: ",sell)
                print("\n\n")
while 1:
    input("Press Enter to add new order")
    dpid = input("Enter you DPID: ")
    _type = input("Enter Order type: ")
    isin = input("Enter the stock name: ")
    qty = int(input("Enter quantity: "))
    Order = {"ISIN": isin, "DPID": dpid, "Quantity": qty, "Type": _type}
    fillQueue(Order)
    print("-----------------Added to Queue---------------\n")
    matchmaking()   