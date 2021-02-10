package mage_package;

import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import base_lib.BaseStatus.Role;

/**
 * @author CatDevil
 *
 */

public class Service 
{
	public static Sender sender = null;
	public static LocalBaseConnection lbConnection = null;
	
	public static final String TITLE = "MAGE Client v1.0";
	public static volatile String client_login;
	public static volatile int client_id;
	public static volatile String client_role;
	public static boolean admin_flag = false;
	
	public static final String GREEN_COLOR = "-fx-border-color: #009900;"; 
	public static final String RED_COLOR = "-fx-border-color: #FF3300;";
	public static final String BLUE_COLOR = "-fx-border-color: #0250a3;";
	
	public static String Thread_Title = "Connection_Thread";
	
	public static final int  L_A_P_LENGTH = 20;
	public static final int  THEME_LENGTH = 50;
	public static final int MSG_LENGTH = 255;
	public static final int MIN_ANGLE = -180;
	public static final int MAX_ANGLE = 180;
	
	public static int pack_count = 0;
	
	public static ObservableList<String> categories;
	
	public static Alert successStage = new Alert(Alert.AlertType.INFORMATION);
	public static Alert warningStage = new Alert(Alert.AlertType.WARNING);
	public static Alert errorStage = new Alert(Alert.AlertType.ERROR);
	
	public static Image onImg = new Image("/images/online.png", true);
	public static Image offImg = new Image("/images/offline.png", true);
	
	public static ImageView fone = new ImageView(new Image("/images/fone.jpg", false));
	
	public static Task<Timeline> task = null; 
	public static Timeline rotation = null;
	
	public static Role getRole(String id_sender)
	{
		int suf = Integer.parseInt(id_sender.substring(4));
		
		if(suf == 50)
		{
			return Role.ADMIN;	
		}
		
		return Role.USER;
	}
	
	public static int getPackNum()
	{
		return (int) ((Math.random() * 29990) + 10);
	}
	
	public static boolean comboBoxHandler(ComboBox<String> comboObj, String message)
    {
    	if(comboObj.getSelectionModel().getSelectedIndex() == -1)
    	{
    	  comboObj.setStyle(RED_COLOR);	
    	  showWarningStage(message);
       	  return false;
    	}
    		
    	else
    	{
    		return true;
    	}
    }
    
	public static boolean textCheckHandler(TextInputControl textObj, boolean textObjType, String message)
    {
    	if(textObjType)
    	{
    		if(textObj.getText().trim().length() == 0 || textObj.getText().compareTo("-") == 0)
    		{
    		  textObj.setStyle(RED_COLOR);
    		  showWarningStage(message);
    		  return false;
    		}
    		  else 
    		  {
    			return true;
    		  }
    	}
    		else
    		{
    			if(textObj.getText().trim().length() == 0)
            	{
            	  textObj.setStyle(RED_COLOR);
            	  showWarningStage(message);
               	  return false;
            	}
    			  else
    			  {
    				return true;
    			  }
    		}
    }
    
	public static void textHandler(TextInputControl textObj, int length)
    {
    	if(textObj.getText().trim().length() != 0)
        {
    		textObj.setStyle(GREEN_COLOR);
        }
    	  else
   	  	  {
    		  textObj.setStyle(BLUE_COLOR);
   	      }
    	
    	if (textObj.getText().length() >= length)
        {
           String s = textObj.getText().substring(0, length - 1);
           textObj.setText(s);	
        }
    }
    
	public static void textAngleHandler(TextField textObj)
    {
    	if(textObj.getText().trim().length() != 0)
        {
    	   if(textObj.getText().compareTo("-") != 0)
    	   {
    	       if(Float.parseFloat(textObj.getText()) < MIN_ANGLE || Float.parseFloat(textObj.getText()) > MAX_ANGLE)
               {
    	    	  textObj.setStyle(RED_COLOR);
               }
                 else
                 {
                	textObj.setStyle(GREEN_COLOR);	
                 }  

               if((textObj.getText().length() > 9))
               {
                 String s = textObj.getText().substring(0, 9);
                 textObj.setText(s);	
               }
           }
        }
    	  else
    	  {
    		 textObj.setStyle(BLUE_COLOR);
    	  }
    }
	
	public static void showSuccessStage(String message)
	{
		successStage.setContentText(message);
      	successStage.showAndWait();
	}
	
	public static void showWarningStage(String message)
	{
		warningStage.setContentText(message);
      	warningStage.showAndWait();
	}
	
	public static void showErrorStage(String message)
	{
		errorStage.setContentText(message);
      	errorStage.showAndWait();
	}
}