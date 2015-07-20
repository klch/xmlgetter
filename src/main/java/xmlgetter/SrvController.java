package xmlgetter;

import java.io.IOException;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

/**
 * Servlet implementation class SrvController
 */
@WebServlet(name = "SrvController", urlPatterns = {"/upload"})
@MultipartConfig
public class SrvController extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private static final String LOGIN_PAGE = "/index.jsp";
    private static final String ACTION_LIST_PAGE = "/list.jsp";
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SrvController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		processRequest(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		processRequest(request, response);
	}
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException  {
		this.request = request;
		this.response = response;
		HttpSession session = this.request.getSession(true);
		Login login = (Login) session.getAttribute("LoginS");
		//String action = getRequestParametr("Action");
		String action = request.getParameter("Action");
		System.out.println("Action " + action);
		UserActions ua = UserActions.valueOf(action);
		if(ua == UserActions.LOGIN)
		{
			String name = request.getParameter("Name");
			String password = request.getParameter("Password");
			login = Login.getLoginInfo(name, password);
			session.setAttribute("LoginS", login);
		}
		if(login == null)
		{
			request.getRequestDispatcher(UserActions.NULL.getPage()).forward(request, response);
		}
		else
		{
			if(login.getActionSet().contains(ua))
			{
				request.getRequestDispatcher(ua.getAClass()).forward(request, response);
			}
		}
		response.getWriter().append("Shit happeneds");
	}

    /*private String getRequestParametr(String name) throws IOException
    {
        boolean isMultipart = ServletFileUpload.isMultipartContent(this.request);
        if(!isMultipart)
        {
            return request.getParameter(name);
        }
        else
        {
            DiskFileItemFactory factory = new DiskFileItemFactory();
            //factory.setSizeThreshold(1024*1024);
            //File tempDir = (File)getServletContext().getAttribute("javax.servlet.context.tempdir");
            //factory.setRepository(tempDir);
            ServletFileUpload upload = new ServletFileUpload(factory);
            //upload.setSizeMax(1024 * 1024 * 10);
            try
            {
                List items = upload.parseRequest(request);
                Iterator iter = items.iterator();
                while (iter.hasNext()) 
                {
                    FileItem item = (FileItem) iter.next();
                    if (item.isFormField()) 
                    {
                        if(item.getFieldName().equals(name))
                        {
                            return item.getString();
                        }
                        //processFormField(item);
                    }
                    else 
                    {
                        //processUploadedFile(item);
                    }
                }	
            }
            catch (Exception e) 
            {
                e.printStackTrace();
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }	
        }
        return "no item";
    }*/


}
