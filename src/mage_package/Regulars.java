package mage_package;

import java.util.regex.Pattern;

/**
*
* @author CatDevil's
*/

public class Regulars 
{
    private static String coord = "^-?[0-9]{0,3}(?:\\.[0-9]{0,5})?$";
    public static Pattern coordPattern = Pattern.compile(coord);
    
    private static String login = "^[a-zA-Z0-9]{0,20}$";
	public static Pattern loginPattern = Pattern.compile(login);
	
	private static String pass = "^[a-zA-Z0-9]{0,20}$";
	public static Pattern passPattern = Pattern.compile(pass);
	
	private static String id = "^[0-9]{0,4}$";
    public static Pattern idPattern = Pattern.compile(id);
}