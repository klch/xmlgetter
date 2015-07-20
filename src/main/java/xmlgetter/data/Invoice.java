package xmlgetter.data;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
public class Invoice implements Serializable {
	private static final int MILLS_PER_DAY = 24*60*60*1000;
	private final int id;
    private InvoiceStatus status;
    private String shop;
    private List<InvoiceItem> tablePart;
    private double summ;
    private int numCheck;
    private java.sql.Date dateCheck;
    private java.sql.Date dateOrder;
    private long dateCheckL; // 
    private long dateOrderL; //
    private double discount;
    private String discountNum;
    private InvoiceType type;
    private Map<String, Object> invoiceFields;
    private String comments;
    private String baseNum;
    private Exception exp; 
    public static List<Invoice> invoiceListParcer(InputStream is) throws ParserConfigurationException, SAXException, IOException
    {
    	List<Invoice> invoiceList = new ArrayList<Invoice>();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(is);
        doc.getDocumentElement().normalize();
        NodeList invoices = doc.getElementsByTagName("order"); // 
        for(int i = 0; i < invoices.getLength(); i++)
        {
        	Map<String, Object> invoiceFields = new HashMap<String, Object>();
        	List<InvoiceItem> aTablePart = new ArrayList<InvoiceItem>();

            Element invoiceElement = (Element) invoices.item(i); //
            int orderNum = Integer.parseInt(invoiceElement.getAttribute("id"));
            invoiceFields.put("id", orderNum);
            NodeList invoiceHeadList = invoiceElement.getElementsByTagName("head"); //
            Node invoiceHeadNode = invoiceHeadList.item(0); //
            NodeList headNodeList = invoiceHeadNode.getChildNodes();
            System.out.println(headNodeList.getLength());
            for(int k = 0; k < headNodeList.getLength(); k++)
            {

                Node headNode = headNodeList.item(k);
            	System.out.println(headNode.getNodeValue());
                invoiceFields.put(headNode.getNodeName(), headNode.getFirstChild().getNodeValue());
            }
            NodeList invoiceBodyList = invoiceElement.getElementsByTagName("body");
            Node invoiceBodyNode = invoiceBodyList.item(0);
            NodeList bodyNodes = invoiceBodyNode.getChildNodes();
            List<Map<String, Object>> tablePartList = new ArrayList<Map<String, Object>>();//
            for(int k = 0; k < bodyNodes.getLength(); k++)
            {
            	Map<String, Object> tablePartItem = new HashMap<String, Object>();
            	Element bodyElement = (Element) bodyNodes.item(k);
                String invoiceItemId = bodyElement.getAttribute("id");
                tablePartItem.put("id",invoiceItemId);
                NodeList itemNodes = bodyElement.getChildNodes();
                for(int m = 0; m < itemNodes.getLength();m++)
                {
                	Node itemNode = itemNodes.item(m);
                	tablePartItem.put(itemNode.getNodeName(), itemNode.getFirstChild().getNodeValue());
                }
                tablePartList.add(tablePartItem);
            }
            Invoice invoice = invoiceBuilder(invoiceFields, tablePartList);
            invoiceList.add(invoice);
        }
    	return invoiceList;
    }
    private static Invoice invoiceBuilder(/*An XML Node*/Map<String, Object> propsMap,List<Map<String, Object>> bodyList) throws IllegalArgumentException
    {
    	if(propsMap == null||bodyList == null) throw new IllegalArgumentException();
    	int id = (Integer)propsMap.get("id");
    	Invoice invoice = new Invoice(id);
    	InvoiceStatus status = InvoiceStatus.valueOf((String)propsMap.get("status"));
    	invoice.setStatus(status);
    	InvoiceType type = InvoiceType.valueOf((String) propsMap.get("type"));
    	invoice.setType(type);
    	String shop = (String) propsMap.get("firma");
    	invoice.setShop(shop);
    	double discount = Double.parseDouble((String)propsMap.get("discount"));
    	invoice.setDiscount(discount);
    	String discountNum = (propsMap.get("card_num") != null ? (String)propsMap.get("card_num") : "");
    	invoice.setDiscountNum(discountNum);
    	LocalDate orderDate = LocalDate.parse((String) propsMap.get("orderdate"), DateTimeFormatter.ofPattern("yyyy-MM-dd"));

    	long dateOrderL = orderDate.toEpochDay()*MILLS_PER_DAY;
    	invoice.setDateOrderL(dateOrderL);
    	if(status ==  InvoiceStatus.DONE)
    	{
            if(propsMap.get("billNum")== null || propsMap.get("billNum").equals("")) throw new IllegalArgumentException("����������� ����� ���� � ��������� ������.");
            int numCheck = Integer.parseInt((String) propsMap.get("billNum"));
            invoice.setNumCheck(numCheck);
            if(propsMap.get("billDate")== null || propsMap.get("billDate").equals("")) throw new IllegalArgumentException("����������� ���� ���� � ��������� ������.");
            LocalDate billDate = LocalDate.parse((String) propsMap.get("billDate"), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            long dateCheckL = billDate.toEpochDay()*MILLS_PER_DAY;
            invoice.setDateCheckL(dateCheckL);
    	}
    	String comments = (String) propsMap.get("prim");
    	invoice.setComments(comments);
    	List<InvoiceItem> tablePart = new ArrayList<InvoiceItem>();
    	for(Map<String, Object> map : bodyList)
    	{
    		String itemId = (String) map.get("id");
    		InvoiceItem item = new InvoiceItem(itemId);
    		int quant = Integer.parseInt((String) map.get("quant"));
    		double price = Double.parseDouble((String) map.get("price"));
    		item.setQuantity(quant);
    		item.setPrice(price);
    		tablePart.add(item);
    	}
    	invoice.setTablePart(tablePart);
    	return invoice;
    }
    private Invoice(int id)
    {
    	this.id = id;
    }
    private void setStatus(InvoiceStatus status)
    {
    	this.status = status;
    }
    private void setType(InvoiceType type)
    {
    	this.type = type;
    }
    private void setShop(String shop)
    {
    	this.shop = shop;
    }
    private void setDiscount(double discount)
    {
    	this.discount = discount;
    }
    private void setDiscountNum(String discountNum)
    {
    	this.discountNum = discountNum;
    }
    private void setDateOrderL(long dateOrderL)
    {
    	this.dateOrderL = dateOrderL;
    }
    private void setNumCheck(int numCheck)
    {
    	this.numCheck = numCheck;
    }
    private void setDateCheckL(long dateCheckL)
    {
    	this.dateCheckL = dateCheckL;
    }
    private void setTablePart(List<InvoiceItem> tablePart)
    {
    	this.tablePart = tablePart;
    }
    private void setComments(String comments)
    {
    	this.comments = comments;
    }
    public void setBaseNum(String baseNum)
    {
    	this.baseNum = baseNum;
    }
    public int getId()
    {
    	return this.id;
    }
    public String getShop()
    {
    	return this.shop;
    }
    public InvoiceStatus getStatus()
    {
    	return this.status;
    }
    public String getBaseNum()
    {
    	return this.baseNum;
    }
    public String getDiscountNum()
    {
    	return this.discountNum;
    }
    public List<InvoiceItem> getTablePart()
    {
    	return Collections.unmodifiableList(this.tablePart);
    }
    public double getDiscount()
    {
    	return this.discount;
    }
    public int getNumCheck()
    {
    	return this.numCheck;
    }
    public long getDateCheckL()
    {
    	return this.dateCheckL;
    }
    public long getDateOrderL()
    {
    	return this.dateOrderL;
    }
    public double getSumm()
    {
        if(tablePart == null) summ = 0;
        else
        {
            summ = 0;
            for(InvoiceItem item: tablePart)
            {
                summ = summ +  item.getSumm();
            }
        }
        return summ;
    }
    public InvoiceType getType()
    {
    	return this.type;
    }
    public String getComments()
    {
    	return this.comments;
    }
    public void setExp(Exception exp)
    {
    	this.exp = exp;
    }
    public Exception getExp()
    {
    	return exp;
    }
    @Override
    public String toString()
    {
    	return baseNum;
    }
    @Override
    public int hashCode()
    {
    	return id *31 + shop.hashCode() * 17 + baseNum.hashCode(); 
    }
}
