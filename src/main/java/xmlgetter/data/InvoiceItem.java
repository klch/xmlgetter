package xmlgetter.data;

public class InvoiceItem {
	private String id;
    private double price;
    private int quantity;
    public InvoiceItem(String id, double price, int quantity)
    {
        this(id);
        setPrice(price);
        setQuantity(quantity);
    }
    public InvoiceItem(String id)
    {
        this.id = id;
    }
    protected void setPrice(double price)
    {
        this.price = price;
    }
    protected void setQuantity(int quantity)
    {
        this.quantity = quantity;
    }
    public double getPrice()
    {
        return price;
    }
    public int getQuantity()
    {
        return quantity;
    }
    public double getSumm()
    {
        return price*quantity;
    }
    public String getId()
    {
    	return id;
    }
}
