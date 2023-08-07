buy = {}
sell = {}

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

Order1 = {"ISIN": "Infosys", "DPID": "1672", "Quantity": 10, "Type": "Sell"}
Order2 = {"ISIN": "Infosys", "DPID": "2365", "Quantity": 5, "Type": "Sell"}
Order3 = {"ISIN": "Tcs", "DPID": "2365", "Quantity": 7, "Type": "Buy"}
Order4 = {"ISIN": "Infosys", "DPID": "1672", "Quantity": 3, "Type": "Sell"}
Order5 = {"ISIN": "ICICI", "DPID": "1672", "Quantity": 3, "Type": "Buy"}

print("After Order-1")
fillQueue(Order1)
print("\n\nAfter Order-2")
fillQueue(Order2)
print("\n\nAfter Order-3")
fillQueue(Order3)
print("\n\nAfter Order-4")
fillQueue(Order4)
print("\n\nAfter Order-5")
fillQueue(Order5)