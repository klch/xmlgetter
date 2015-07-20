package xmlgetter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import javax.management.modelmbean.XMLParseException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import xmlgetter.data.Invoice;
import xmlgetter.db.DbAdapter;
import xmlgetter.db.MdbUnloader;
import xmlgetter.xml.Validator;

@WebServlet(name = "UploadIShopOrder", urlPatterns = {"/upload"})
@MultipartConfig
public class UploadIShopOrder extends HttpServlet implements MyAction {
    private HttpServletRequest request;
    private HttpServletResponse response;
    public final UserActions ua = UserActions.UPLOAD_ISHOP_ORDER;
    private static final String XML_VALIDATOR =  "/IShopOrder.xsd";
    public UploadIShopOrder() {
        super();
    }
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException  {
		this.request = request;
		this.response = response;
		HttpSession session = this.request.getSession(true);
		Login login = (Login) session.getAttribute("LoginS");
		if(login == null)
		{
			request.getRequestDispatcher(UserActions.NULL.getPage()).forward(request, response);
		}
		String action = request.getParameter("Action");
		UserActions ua = UserActions.valueOf(action);
		if(ua == UserActions.UPLOAD_ISHOP_ORDER&&login.getActionSet().contains(ua))
		{
			
		}
	    Part filePart = request.getPart("xmlFile"); // Retrieves <input type="file" name="file">
	    String fileName = filePart.getSubmittedFileName();
	    InputStream fileContent = filePart.getInputStream();

	    InputStreamReader fileContentReader = new InputStreamReader(fileContent);
	    BufferedReader fileContentBuffer = new BufferedReader(fileContentReader);
	    StringBuilder sb = new StringBuilder();
	    String line;
	    while ((line = fileContentBuffer.readLine())!= null)
	    {
	    	sb.append(line);
	    }
	    byte[] bytes = sb.toString().getBytes("utf-8");
	    InputStream inputStream = new ByteArrayInputStream(bytes);
	    Validator validator = new Validator(XML_VALIDATOR);
	    
	    try {
			if(validator.validate(inputStream)) {
				inputStream = new ByteArrayInputStream(bytes);
				List<Invoice> invoiceList = Invoice.invoiceListParcer(inputStream);
				DbAdapter dbAdapter = DbAdapter.getInstance();
				Map<String, Invoice> invoiceForMdb = null;
				try
				{
					List<Invoice> results = dbAdapter.setOrderList(invoiceList);
					invoiceForMdb = evaluate(results, new Predicate<Invoice>() 
							{
								public boolean test(Invoice inv)
								{
									return inv.getExp() == null;
								}
							});
					//request.setAttribute("invoiceList", invoiceForMdb);
					session.setAttribute("invoiceList", invoiceForMdb);
					MdbUnloader mdbUnloader = new MdbUnloader();
					File mdbFile = mdbUnloader.exportKontrs(invoiceForMdb.keySet());
					session.setAttribute("mdbFile", mdbFile.getAbsolutePath());
					request.getRequestDispatcher(ua.getPage()).forward(request, response);
				} catch (SQLException e) {
					e.printStackTrace();
					response.getWriter().write(e.getMessage());
				} 
			}
		} catch (XMLParseException e1) {
			e1.printStackTrace();
		} catch (SAXException e1) {
			response.getWriter().write("Ошибка в формате файла XML: " + e1.getMessage());
			e1.printStackTrace();
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
	    }
	}
	private static<T> Map<String, T> evaluate(List<T> list, Predicate<T> predicate)
	{
		Map<java.lang.String, T> newMap = new HashMap<java.lang.String, T>();
		for(T listElement: list)
		{
			if(predicate.test(listElement))
			{
				newMap.put(listElement.toString(), listElement);
			}
		}
		return newMap;
	}
}
