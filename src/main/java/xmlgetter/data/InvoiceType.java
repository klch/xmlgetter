package xmlgetter.data;

public enum InvoiceType {
	REALYZE_REPORT(15);
	private final int type;
	InvoiceType(int type)
	{
		this.type = type;
	}
	public int getType()
	{
		return type;
	}
	

}
