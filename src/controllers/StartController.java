package controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import mage_package.AuthorizationTask;
import mage_package.Client;
import mage_package.Service;
import base_lib.MageConnection;

/**
 * @author CatDevil
 *
 */

public class StartController 
{
	private Stage stage = null;
	
	public StartController(Stage stage)
	{
	   this.stage = stage;
	}
	
	@FXML
    private AnchorPane autAnchor;

	@FXML
    private Label loginLabel;
	@FXML
    private Label passLabel;
	
	@FXML
    private TextField loginText;
	@FXML
    private PasswordField passText;
	
	@FXML
    private Button startBtn;
	
	@FXML
    private Label consoleLabel;
	
	@FXML
    private ProgressIndicator progI;
	
	public volatile MageConnection connection = null;
	
	private int focusNumber = 1;
	
	private void authGo()
	{
		try 
    	{
    		if(!Service.textCheckHandler(loginText, false, "Введите логин"))
    		{
    			loginText.requestFocus();
    			return;
    		}
    		
			if(!Service.textCheckHandler(passText, false, "Введите пароль")) 
			{
				passText.requestFocus();
				return;
			}
			
			Task<Map<Boolean, String>> task = new AuthorizationTask(loginText.getText(), passText.getText(), connection, stage, consoleLabel);
			progI.progressProperty().bind(task.progressProperty());
			task.runningProperty().addListener(new ChangeListener<Boolean>() 
			{
				@Override
				public void changed(ObservableValue<? extends Boolean> arg0, Boolean wasRunning, Boolean isRunning) 
				{
					if(!isRunning)
					{
					  progI.setVisible(false);
					} else progI.setVisible(true);
				}   
			});
			 
			task.setOnSucceeded((Event) -> {
				Map<Boolean, String> answer = task.getValue();
				
				if(!answer.keySet().iterator().next())
				{
					loginText.setStyle(Service.RED_COLOR);
	        		passText.setStyle(Service.RED_COLOR);
					Service.showErrorStage(answer.values().iterator().next());
					return;
				}
					else 
					{			
			        	stage.setScene(Client.client.clientScene);
			        	Service.showSuccessStage(answer.values().iterator().next());	
					}
			});
			
			new Thread(task).start();	
		}
    	  
    	catch (Throwable e) 
    	{
    	   try 
    	   {
			  Service.sender.connClose();
		   } 
    		  catch (Throwable e1) 
			  {
    			 consoleLabel.setText(e1.getMessage());
    			 Service.showErrorStage("При закрытии соединения с базой данных возникли ошибки");
   				 e1.printStackTrace();
			  }
    		  
    	    consoleLabel.setText(e.getMessage());
    		Service.showErrorStage("При закрытии соединения с базой данных возникли ошибки");
			e.printStackTrace();
    	  }
	}
	
	@FXML
    public void initialize() throws IOException
    {	
		connection = new MageConnection();
		
		loginLabel.setId("startLabel");
		passLabel.setId("startLabel");
		
		loginText.textProperty().addListener((observable, oldValue, newValue) -> {
			Service.textHandler(loginText, Service.L_A_P_LENGTH);
        });
		
		passText.textProperty().addListener((observable, oldValue, newValue) -> {
        	Service.textHandler(passText, Service.L_A_P_LENGTH);
        });
		
	    startBtn.setOnMouseClicked((MouseEvent event) -> {	
	    	authGo();
	    });
	    
	    stage.addEventFilter(KeyEvent.KEY_PRESSED, (event) -> {
	        if(event.getCode().equals(KeyCode.TAB))
	        {
	            event.consume();
	            switch(focusNumber)
	            {
	            	case 1:
	            		loginText.requestFocus();
	            	break;
	            	
	            	case 2:
	            		passText.requestFocus();
		            break;
	            }
	            
	            focusNumber++;
	            if(focusNumber > 2)
	            {
	              focusNumber = 1;
	            }
	        }
	        
	        if(event.getCode().equals(KeyCode.ENTER))
	        {
	        	event.consume();
	        	authGo();
	        }
	    }); 
     }	
	
	public void lbConClose()
	{
		if(connection != null && Service.client_id != 0)
		{
			try 
			{
				String setDisActive = "UPDATE h136894_mage_base.employees SET h136894_mage_base.employees.desc_active = 0"
        			+ " WHERE h136894_mage_base.employees.id_employee = '"+ Service.client_id +"';";
    		
				connection.statmt.executeUpdate(setDisActive);
	
				connection.closeConn();
			} 
    	
			catch (SQLException e) 
			{
				consoleLabel.setText(e.getMessage());
				Service.showErrorStage("При закрытии сессии пользователя #" + Service.client_id);
				e.printStackTrace();
			} 
    	
			catch (Throwable e) 
			{
				consoleLabel.setText(e.getMessage());
				Service.showErrorStage("При закрытии сессии пользователя #" + Service.client_id);
				e.printStackTrace();
			}
		}
	}
}