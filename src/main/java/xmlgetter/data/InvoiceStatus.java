package xmlgetter.data;

public enum InvoiceStatus {
	PREPARING(0), RESERVED(1), DONE(4), MARKFORDEL(99),DELETED(100);
	private final int stat;
	InvoiceStatus(int stat)
	{
		this.stat = stat;
	}
	public int getStat()
	{
		return stat;
	}
}
