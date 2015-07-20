package xmlgetter.db;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.*;
import java.net.*;
public class UserProperties {
    private static final UserProperties INSTANCE = new UserProperties();
    private static final String ENCODING_WIN1251 = "windows-1251";
    private Properties props;
    private URI fileName;
    private UserProperties()
    {
        try
        {
            fileName = UserProperties.class.getResource("properties.properties").toURI();
            setProperties(fileName);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public static UserProperties getInstance()
    {
        return INSTANCE;
    }
    public String getProperty(String propName)
    {
        return props.getProperty(propName);
    }
    private void setProperties(URI fileName) throws RuntimeException
    {
        try
        {
            File file =new File(fileName);
            FileInputStream in = new FileInputStream(file);
            props = new Properties();
            props.load(in);
            in.close();
        }
        catch(Exception e)
        {
            throw new RuntimeException("Не удалось считать файл свойств");
        }
    }
}
