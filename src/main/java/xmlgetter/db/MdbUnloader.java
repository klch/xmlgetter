package xmlgetter.db;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.healthmarketscience.jackcess.ColumnBuilder;
import com.healthmarketscience.jackcess.DataType;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Table;
import com.healthmarketscience.jackcess.TableBuilder;
/**
 *
 * @author Nickolay
 */
public class MdbUnloader {
    private Database db;
    private UserProperties props;
    private static final TableBuilder KONTR_TB = new TableBuilder("KONTR");
    private static final TableBuilder SPEZ_TB = new TableBuilder("SPEZ");
    static //Таблица Data_Kontr
    {
        try
        {
            KONTR_TB.addColumn(new ColumnBuilder("Num", DataType.fromSQLType(Types.VARCHAR, 10)));
            KONTR_TB.addColumn(new ColumnBuilder("Datap", DataType.fromSQLType(Types.DATE)));
            KONTR_TB.addColumn(new ColumnBuilder("Summa", DataType.fromSQLType(Types.FLOAT)));
            KONTR_TB.addColumn(new ColumnBuilder("Valita", DataType.fromSQLType(Types.INTEGER)));
            KONTR_TB.addColumn(new ColumnBuilder("Okrug", DataType.fromSQLType(Types.INTEGER)));
            KONTR_TB.addColumn(new ColumnBuilder("Type", DataType.fromSQLType(Types.INTEGER)));
            KONTR_TB.addColumn(new ColumnBuilder("Firma", DataType.fromSQLType(Types.VARCHAR, 6)));
            KONTR_TB.addColumn(new ColumnBuilder("Kurs", DataType.fromSQLType(Types.FLOAT)));
            KONTR_TB.addColumn(new ColumnBuilder("Kdost", DataType.fromSQLType(Types.FLOAT)));
            KONTR_TB.addColumn(new ColumnBuilder("Skidka", DataType.fromSQLType(Types.FLOAT)));
            KONTR_TB.addColumn(new ColumnBuilder("Sost", DataType.fromSQLType(Types.INTEGER)));
            KONTR_TB.addColumn(new ColumnBuilder("Dataz", DataType.fromSQLType(Types.DATE)));
            KONTR_TB.addColumn(new ColumnBuilder("DatTim", DataType.fromSQLType(Types.DATE)));
            KONTR_TB.addColumn(new ColumnBuilder("Master", DataType.fromSQLType(Types.VARCHAR, 10)));
            KONTR_TB.addColumn(new ColumnBuilder("Kto", DataType.fromSQLType(Types.VARCHAR, 10)));
            KONTR_TB.addColumn(new ColumnBuilder("Otdano", DataType.fromSQLType(Types.INTEGER)));
            KONTR_TB.addColumn(new ColumnBuilder("Summaot", DataType.fromSQLType(Types.FLOAT)));
            KONTR_TB.addColumn(new ColumnBuilder("Prim", DataType.fromSQLType(Types.VARCHAR, 511)));
            KONTR_TB.addColumn(new ColumnBuilder("Sumkor", DataType.fromSQLType(Types.FLOAT)));
            KONTR_TB.addColumn(new ColumnBuilder("Sklad", DataType.fromSQLType(Types.VARCHAR, 3)));
            KONTR_TB.addColumn(new ColumnBuilder("Svaz", DataType.fromSQLType(Types.VARCHAR, 10)));
            KONTR_TB.addColumn(new ColumnBuilder("Typzena", DataType.fromSQLType(Types.INTEGER)));
            KONTR_TB.addColumn(new ColumnBuilder("Samv", DataType.fromSQLType(Types.INTEGER)));
            KONTR_TB.addColumn(new ColumnBuilder("Openedit", DataType.fromSQLType(Types.INTEGER)));
            KONTR_TB.addColumn(new ColumnBuilder("Sk_otpr", DataType.fromSQLType(Types.INTEGER)));
            KONTR_TB.addColumn(new ColumnBuilder("Oldsumma", DataType.fromSQLType(Types.FLOAT)));
            KONTR_TB.addColumn(new ColumnBuilder("Oldsummaot", DataType.fromSQLType(Types.FLOAT)));
            KONTR_TB.addColumn(new ColumnBuilder("Ves", DataType.fromSQLType(Types.FLOAT)));
            KONTR_TB.addColumn(new ColumnBuilder("Ob", DataType.fromSQLType(Types.FLOAT)));
            KONTR_TB.addColumn(new ColumnBuilder("Numcash", DataType.fromSQLType(Types.INTEGER)));
            KONTR_TB.addColumn(new ColumnBuilder("Cashrep", DataType.fromSQLType(Types.CHAR, 10)));
            KONTR_TB.addColumn(new ColumnBuilder("Beznal", DataType.fromSQLType(Types.INTEGER)));
            KONTR_TB.addColumn(new ColumnBuilder("Sumnanal", DataType.fromSQLType(Types.FLOAT)));
            KONTR_TB.addColumn(new ColumnBuilder("Sumkred", DataType.fromSQLType(Types.FLOAT)));
            KONTR_TB.addColumn(new ColumnBuilder("Section", DataType.fromSQLType(Types.INTEGER)));
            KONTR_TB.addColumn(new ColumnBuilder("Num1c", DataType.fromSQLType(Types.CHAR, 10)));
            KONTR_TB.addColumn(new ColumnBuilder("Numbux", DataType.fromSQLType(Types.CHAR, 10)));
            KONTR_TB.addColumn(new ColumnBuilder("NumBasesCreate", DataType.fromSQLType(Types.VARCHAR, 6)));
            KONTR_TB.addColumn(new ColumnBuilder("IsPrintExec", DataType.fromSQLType(Types.BIT)));
            KONTR_TB.addColumn(new ColumnBuilder("AdditionalExpenseType", DataType.fromSQLType(Types.INTEGER)));
        }
        catch(SQLException e) 
        {
            e.printStackTrace();
        }
    }
    static 
    {
        try
        {
            SPEZ_TB.addColumn(new ColumnBuilder("Num", DataType.fromSQLType(Types.VARCHAR, 6)));
            SPEZ_TB.addColumn(new ColumnBuilder("Kolkor", DataType.fromSQLType(Types.INTEGER)));
            SPEZ_TB.addColumn(new ColumnBuilder("Kolinkor", DataType.fromSQLType(Types.INTEGER)));
            SPEZ_TB.addColumn(new ColumnBuilder("Zenaed", DataType.fromSQLType(Types.FLOAT)));
            SPEZ_TB.addColumn(new ColumnBuilder("Skidka", DataType.fromSQLType(Types.FLOAT)));
            SPEZ_TB.addColumn(new ColumnBuilder("Zenaold", DataType.fromSQLType(Types.FLOAT)));
            SPEZ_TB.addColumn(new ColumnBuilder("DatTim", DataType.fromSQLType(Types.DATE)));
            SPEZ_TB.addColumn(new ColumnBuilder("Master", DataType.fromSQLType(Types.VARCHAR, 10)));
            SPEZ_TB.addColumn(new ColumnBuilder("Numkont", DataType.fromSQLType(Types.VARCHAR, 10)));
            SPEZ_TB.addColumn(new ColumnBuilder("Kodenum", DataType.fromSQLType(Types.VARCHAR, 6)));
            SPEZ_TB.addColumn(new ColumnBuilder("Numchk", DataType.fromSQLType(Types.VARCHAR, 6)));
            SPEZ_TB.addColumn(new ColumnBuilder("Datechk", DataType.fromSQLType(Types.DATE)));
            SPEZ_TB.addColumn(new ColumnBuilder("Zenarub", DataType.fromSQLType(Types.FLOAT)));
            SPEZ_TB.addColumn(new ColumnBuilder("Nameskid", DataType.fromSQLType(Types.VARCHAR, 20)));
            SPEZ_TB.addColumn(new ColumnBuilder("Sumskid", DataType.fromSQLType(Types.FLOAT)));
            SPEZ_TB.addColumn(new ColumnBuilder("Kodeprod", DataType.fromSQLType(Types.VARCHAR, 10)));
            SPEZ_TB.addColumn(new ColumnBuilder("Zena_bnds", DataType.fromSQLType(Types.FLOAT)));
            SPEZ_TB.addColumn(new ColumnBuilder("Sum_bnds", DataType.fromSQLType(Types.FLOAT)));
            SPEZ_TB.addColumn(new ColumnBuilder("Sum_nds", DataType.fromSQLType(Types.FLOAT)));
            SPEZ_TB.addColumn(new ColumnBuilder("NumGtd", DataType.fromSQLType(Types.VARCHAR, 6)));
            SPEZ_TB.addColumn(new ColumnBuilder("BonPlus", DataType.fromSQLType(Types.INTEGER)));
            SPEZ_TB.addColumn(new ColumnBuilder("BonMinus", DataType.fromSQLType(Types.INTEGER)));
            SPEZ_TB.addColumn(new ColumnBuilder("ActionNumber", DataType.fromSQLType(Types.INTEGER)));
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
    }
    
//    public static void main(String[] args) throws Exception
//    {
//        try
//        {
//        	MdbUnloader mdb = new MdbUnloader();
//            List<String> list = new ArrayList<String>();
//            list.add("R100124234");
//            list.add("Q810002916");
//            mdb.exportKontrs(list);
//            
//        }
//        catch(IOException e)
//        {
//            e.printStackTrace();
//        }
//    }
    public MdbUnloader(File file) throws IOException
    {
        try
        {
            props = UserProperties.getInstance();
            if(file.exists()) db = DatabaseBuilder.open(file);
            else db = DatabaseBuilder.create(Database.FileFormat.V2003, file);
        }
        catch(IOException e)
        {
            e.printStackTrace();
            throw new IOException("Не удалось открыть базу данных " + e.getMessage());
        }
    }
    public MdbUnloader() throws IOException
    {
        this(new File(".\\nws.mdb"));
    }
    public File exportKontrs(Set<String> nums) throws IOException, SQLException
    {
        Table kontrTable = db.getTable("KONTR");
        List<Map<String,Object>> kontrList = new ArrayList<Map<String,Object>>();
        if(kontrTable == null)
        {
            kontrTable = KONTR_TB.toTable(db);
        }
        DbAdapter dbAdapter = DbAdapter.getInstance();
        try
        {
            kontrList = dbAdapter.getKontrByNums(nums);
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            throw new SQLException("Не удалось считать из базы накладные " + e.getMessage());
        }
        System.out.println("Длина контр Лист " + kontrList.size());
        kontrTable.addRowsFromMaps(kontrList);
        db.flush();
        db.close();
        return db.getFile();
    }
}
