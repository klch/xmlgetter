package xmlgetter;

public enum UserActions {
	NULL(null, "/index.jsp"),
	LOGIN("/list.jsp", "/list.jsp"),
	UPLOAD_ISHOP_ORDER("/UploadIShopOrder", "/uploadIShopOrder.jsp");
	private final String aClass;
	private final String page;
	UserActions(String cl, String page)
	{
		this.aClass = cl;
		this.page = page;
	}
	public String getAClass()
	{
		return aClass;
	}
	public String getPage()
	{
		return page;
	}
}
