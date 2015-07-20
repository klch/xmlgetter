package xmlgetter;
import java.util.*;
public class Login {
    private String name;
    private boolean isLogined;
    private Set<UserActions> actionSet;
    public final UserActions ua = UserActions.LOGIN; 
    private Login(String name, String password)
    {
    	this.name = name;
    	checkUser(name, password);
    }
    public static Login getLoginInfo(String name, String password)
    {
    	Login user = new Login(name, password);
    	if(user.getActionSet() == null) return null;
    	else return user;
    }
    public Set<UserActions> getActionSet()
    {
    	if (actionSet != null) 	return Collections.unmodifiableSet(actionSet);
    	return null;
    }
    private Set<UserActions> checkUser(String name, String password) //метод-заглушка. Так то надо пользователя в базе посмотреть
    {
    	if(name.equals("1"))
    	{
    		actionSet = EnumSet.of(UserActions.UPLOAD_ISHOP_ORDER, UserActions.LOGIN);
    	    return actionSet;
    	}
    	else return null; 

    }
    @Override
    public String toString()
    {
    	return name + ":" + actionSet;
    }
    @Override
    public int hashCode()
    {
    	return name.hashCode()*31 + actionSet.hashCode();
    }
}
