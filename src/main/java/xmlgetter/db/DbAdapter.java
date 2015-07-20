/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



package xmlgetter.db;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import xmlgetter.data.Invoice;
import xmlgetter.data.InvoiceItem;
import xmlgetter.data.InvoiceStatus;

public class DbAdapter 
{
    private static final  DbAdapter INSTANCE = new DbAdapter();
    private UserProperties  props;
    private final String mssqlDriver;
    private final String mssqlUrl;
    private Connection mssqlConn;
    //private PreparedStatement mssqlPrepStat;
    //private Statement mssqlStat;

    private DbAdapter()
    {
        props = UserProperties.getInstance();
        mssqlDriver = this.props.getProperty("dbAdapter.mssqldriver");
        mssqlUrl = this.props.getProperty("dbAdapter.mssqlurl");  
        try
        {
            Class.forName(mssqlDriver).newInstance();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    public static DbAdapter getInstance()
    {
        return INSTANCE;
    }
    public List<Invoice> setOrderList(List<Invoice> invoiceList) throws SQLException
    {
    	System.out.println("Size of invoice list " + invoiceList.size());
        if(mssqlConn == null||mssqlConn.isClosed()) 
        {
            establishConnection();
        }
        for(Invoice invoice: invoiceList)
        {
            
            String num = null;
            try
            {
                if(!isFirmaOk(invoice.getShop())) throw new IllegalArgumentException("Error: There is no such FIRMA in the database");
                num = isOrderLoadedToBase(invoice.getId());
                num = updateInvoice(invoice, num, InvoiceStatus.PREPARING);
                System.out.println("num " + num);
                //устанавливаем в 0, потом обновляем статус и вызываем расчет НДС
                invoice.setBaseNum(num);
                deleteInvoiceBody(num);
                insertInvoiceBody(invoice, num);
                setInvoiceStatus(num, invoice.getStatus());
                calculateNDS(num);
                invoice.setExp(null);
            }
            catch(SQLException e)
            {
            	e.printStackTrace();
            	invoice.setExp(e);
            }
        }
        closeConnection();
        return Collections.unmodifiableList(invoiceList);
    }
    private void calculateNDS(String num) throws SQLException
    {
        CallableStatement mssqlCstmt = null;
        try
        {
            String resolveNDS = props.getProperty("dbAdapter.mssql.sql.resolveNDS");
            mssqlCstmt = mssqlConn.prepareCall(resolveNDS);
            mssqlCstmt.setString(1, num);
            mssqlCstmt.execute();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw new SQLException("не удалось расчитать НДС "  +  e.getMessage());
        }
        finally
        {
            try
            {
                mssqlCstmt.close();
            }
            catch(SQLException e){}
        }
    }
    private void setInvoiceStatus(String num, InvoiceStatus status) throws SQLException
    {
        if(num == null)   throw new IllegalArgumentException("num= " + num + " status = " + status);
        if(mssqlConn == null||mssqlConn.isClosed())  throw new SQLException("Ошибка подключения к базе данных в setInvoiceStatus");
        String updateInvoiceStatus = props.getProperty("dbAdapter.mssql.sql.updateInvoiceStatus");
        PreparedStatement mssqlPrepStat = null;
        try
        {
        	mssqlPrepStat = mssqlConn.prepareStatement(updateInvoiceStatus);
            mssqlPrepStat.setInt(1, status.getStat());
            mssqlPrepStat.setString(2, num);
            mssqlPrepStat.executeUpdate();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw new SQLException("не удалось изменить статус "  +  e.getMessage());
        }
        finally
        {
            try
            {
                mssqlPrepStat.close();
            }
            catch(SQLException e){}
        }
    }
    private boolean isItemOK(String itemId) throws SQLException
    {
        if(mssqlConn == null||mssqlConn.isClosed())  throw new SQLException("Ошибка подключения к базе данных isItemOk");
        String selectTovar = props.getProperty("dbAdapter.mssql.sql.selectTovar");
        boolean isTovarOk = false;
        PreparedStatement mssqlPrepStat = null;
        try
        {
            mssqlPrepStat = mssqlConn.prepareStatement(selectTovar);
            mssqlPrepStat.setString(1, itemId.trim() + "%");
            ResultSet rs = mssqlPrepStat.executeQuery();
            isTovarOk = rs.next();
        }
        catch(SQLException e)
        {
            throw new SQLException("не удалось определить правильность поля tovar " + e.getMessage());
        }
        finally
        {
        	mssqlPrepStat.close();
        }
        return isTovarOk;
    }
    private void insertInvoiceBody(Invoice invoice, String num) throws SQLException
    {
        if(mssqlConn == null||mssqlConn.isClosed())  throw new SQLException("Ошибка подключения к базе данных в insertInvoiceBody");
        String insInvoiceBodySQL = props.getProperty("dbAdapter.mssql.sql.insInvoiceBody");
        PreparedStatement mssqlPrepStat =null;
        try
        {
            double kurs = Double.parseDouble(props.getProperty("dbAdapter.mssql.Invoice.KURS")); 
            String shopLetter = props.getProperty("dbAdapter.shopLetter");
            mssqlPrepStat = mssqlConn.prepareStatement(insInvoiceBodySQL);
            int i = 0;
            String card_no = invoice.getDiscountNum();
            for(InvoiceItem oi : invoice.getTablePart())
            {
                if(!isItemOK(oi.getId())) throw new IllegalArgumentException(" такого товара в базе не существует");
                mssqlPrepStat.setInt(1, oi.getQuantity());
                mssqlPrepStat.setString(2, oi.getId());
                System.out.println(oi.getId() + "| " + insInvoiceBodySQL + " " + oi.getQuantity());
                mssqlPrepStat.setDouble(3,oi.getPrice()/kurs);
                mssqlPrepStat.setString(4, num);
                mssqlPrepStat.setString(5, resolveKodeNum(shopLetter, i, 5));
                mssqlPrepStat.setDouble(6,oi.getPrice());
                if(invoice.getDiscount() != 0) 
                {
                    mssqlPrepStat.setString(7, card_no);
                    double summDisc = (-1)*((int)invoice.getDiscount())*oi.getPrice()*oi.getQuantity() /100;
                    mssqlPrepStat.setDouble(8, summDisc);
                }
                else 
                {
                    mssqlPrepStat.setString(7, "");
                    mssqlPrepStat.setDouble(8, 0);
                }
                if(invoice.getNumCheck() != 0 && invoice.getDateCheckL() != 0)
                {
                    mssqlPrepStat.setString(9, String.valueOf(invoice.getNumCheck()));
                    mssqlPrepStat.setDate(10, new java.sql.Date(invoice.getDateCheckL()));
                }
                else
                {
                    mssqlPrepStat.setString(9, "");
                    mssqlPrepStat.setDate(10, null);
                }
                mssqlPrepStat.addBatch();
                i++;
            }
            mssqlPrepStat.executeBatch();
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            throw new SQLException("не удалось выгрузить табличную часть "  +  e.getMessage());
        }
        finally
        {
           mssqlPrepStat.close();
        }
    }
    private String resolveKodeNum(String aLetter, int aNumInt, int aNumLength)
    {
        String num = Integer.toString(aNumInt);
        StringBuilder fNumber = new StringBuilder(num);
        fNumber.reverse();
        for(int i = 0; i < aNumLength - num.length(); i++ )
        {
            fNumber.append("0");
        }
        fNumber.append(aLetter);
        fNumber.reverse();
        return fNumber.toString();
    }
    private void deleteInvoiceBody(String num) throws SQLException
    {
        if(mssqlConn == null||mssqlConn.isClosed())  throw new SQLException("Ошибка подключения к базе данных ");
        String delInvoiceBodySQL = props.getProperty("dbAdapter.mssql.sql.delInvoiceBody");
        PreparedStatement mssqlPrepStat = null;
        try
        {
            //mssqlConn = DriverManager.getConnection(mssqlUrl);
            mssqlPrepStat = mssqlConn.prepareStatement(delInvoiceBodySQL);
            mssqlPrepStat.setString(1, num);
            mssqlPrepStat.execute();
        }
        catch(SQLException e)
        {
            throw new SQLException("не удалось очистить табличную часть "  +  e.getMessage());
        }
        finally
        {
            try
            {
                mssqlPrepStat.close();
            }
            catch(SQLException e){}
        }
    }
    private boolean isFirmaOk(String firma) throws SQLException
    {
        if(mssqlConn == null||mssqlConn.isClosed())  throw new SQLException("Ошибка подключения к базе данных isFirmaOk");
        String selectFirma = props.getProperty("dbAdapter.mssql.sql.selectFirma");
        boolean isFirmaOk = false;
        PreparedStatement mssqlPrepStat = null;
        try
        {
            mssqlPrepStat = mssqlConn.prepareStatement(selectFirma);
            mssqlPrepStat.setString(1, firma);
            ResultSet rs = mssqlPrepStat.executeQuery();
            isFirmaOk = rs.next();
        }
        catch(SQLException e)
        {
            throw new SQLException("не удалось определить правильность поля firma " + e.getMessage());
        }
        return isFirmaOk;
    }
    private String updateInvoice(Invoice invoice, String num, InvoiceStatus iv) throws SQLException
    {
    	if(mssqlConn == null||mssqlConn.isClosed())  throw new SQLException("Ошибка подключения к базе данных ");
        String setInvoiceSQL = null;
        if (num != null) setInvoiceSQL = props.getProperty("dbAdapter.mssql.sql.updateInvoice");
        else setInvoiceSQL = props.getProperty("dbAdapter.mssql.sql.insertInvoice");
        String getKontrNumSQL = props.getProperty("dbAdapter.mssql.sql.getKontrName");
        String shopLetter = props.getProperty("dbAdapter.shopLetter");
        int kontrType = Integer.parseInt(props.getProperty("dbAdapter.mssql.Invoice.TYPE"));
        PreparedStatement mssqlPrepStat = null;
        try
        {
            //mssqlConn = DriverManager.getConnection(mssqlUrl);
            mssqlPrepStat = mssqlConn.prepareStatement(setInvoiceSQL);
            mssqlPrepStat.setDate(1, new java.sql.Date(invoice.getDateOrderL()));
            mssqlPrepStat.setDouble(2, invoice.getSumm());
            mssqlPrepStat.setInt(3, invoice.getType().getType());
            mssqlPrepStat.setString(4, invoice.getShop());
            double kurs = Double.parseDouble(props.getProperty("dbAdapter.mssql.Invoice.KURS"));
            mssqlPrepStat.setDouble(5, kurs);
            mssqlPrepStat.setInt(6, iv.getStat());
            mssqlPrepStat.setDate(7, new java.sql.Date(invoice.getDateOrderL()));
            mssqlPrepStat.setDate(8, new java.sql.Date(invoice.getDateOrderL()));
            mssqlPrepStat.setDouble(9, invoice.getSumm()/kurs);
            String primTemplate = props.getProperty("dbAdapter.mssql.orderInvoiceTemplate");
            String primOrder = (String) invoice.getComments();
            String prim = primTemplate + " " + invoice.getId() + " " + primOrder + " |" + invoice.getDateOrderL() ;
            mssqlPrepStat.setString(10, prim);
            if(num == null){ 
            	CallableStatement mssqlCstmt;
            	mssqlCstmt = mssqlConn.prepareCall(getKontrNumSQL);
            	mssqlCstmt.setString(1, shopLetter);
            	mssqlCstmt.setInt(2, kontrType);
            	mssqlCstmt.registerOutParameter(3, java.sql.Types.VARCHAR);
            	mssqlCstmt.execute();
            	num = mssqlCstmt.getString(3);
            	mssqlPrepStat.setString(11, num);
            	mssqlPrepStat.execute();
            }
        }
        catch(SQLException e)
        {
            throw new SQLException("не удалось записать заголовок накладной " + e.getMessage());
        }
        finally
        {
        	mssqlPrepStat.close();
        }
        return num;
    }
    private String isOrderLoadedToBase(int invoiceNum) throws SQLException
    {
        String isOrderLoadedToBaseSQL = props.getProperty("dbAdapter.mssql.sql.isOrderLoaded");
        String primTemplate = props.getProperty("dbAdapter.mssql.orderInvoiceTemplate");
        String result = null;
        PreparedStatement mssqlPrepStat = null;
        try
        {
            mssqlPrepStat = mssqlConn.prepareStatement(isOrderLoadedToBaseSQL);
            mssqlPrepStat.setString(1, primTemplate + " " +  invoiceNum + "%");
            ResultSet rs = mssqlPrepStat.executeQuery();
            boolean uni = true;
            if(rs.next())
            {
                if(!uni) throw new SQLException("накладная заказа может быть не уникальна");
                result =  rs.getString("Num");
                uni = false;
            }
            else result = null;
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            throw new SQLException("Не возможно определить выгружен ли заказ в базу " + e.getMessage());
        }
        finally
        {
          	mssqlPrepStat.close();
        }
        return result;
    }
    public List<Map<String, Object>> getKontrByNums(Set<String> nums) throws SQLException
    {
        List <Map<String, Object>> kontrsList = new ArrayList <Map<String, Object>>();
        PreparedStatement mssqlPrepStat = null;
        Statement mssqlStat = null;
        if(mssqlConn == null||mssqlConn.isClosed()) 
        {
            establishConnection();
        }
        String getKontrList = props.getProperty("dbAdapter.mssql.sql.getKontrList");
        StringBuilder getKontrListBuilder = new StringBuilder(getKontrList);
        getKontrListBuilder.append("(");
        Iterator<String> iterator = nums.iterator();
        while(iterator.hasNext())
        {
            String num = iterator.next();
            getKontrListBuilder.append("'").append(num).append("'");
            if(iterator.hasNext()) getKontrListBuilder.append(",");
        }
        getKontrListBuilder.append(")");
        mssqlStat = mssqlConn.createStatement();
        ResultSet rs = mssqlStat.executeQuery(getKontrListBuilder.toString());
        while(rs.next())
        {
            Map<String, Object> kontrMap = new LinkedHashMap<String, Object>();
            kontrMap.put("Num", rs.getString("Num"));
            for(int i = 0;i < rs.getMetaData().getColumnCount();i++)
            {
                kontrMap.put(rs.getMetaData().getColumnName(i+1), rs.getObject(i+1));
            }
            kontrsList.add(kontrMap);
        }
        closeConnection();
        return kontrsList;
    }
    private boolean establishConnection() throws SQLException
    {
        try
        {
            mssqlConn = DriverManager.getConnection(mssqlUrl);
            return true;
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            throw new SQLException("Не удается установить соединение с базой данных " + e.getMessage());
        }
    }
    private boolean closeConnection()
    {
        try
        {
            mssqlConn.close();
            return true;
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            return false;
        }
    }
     
}
