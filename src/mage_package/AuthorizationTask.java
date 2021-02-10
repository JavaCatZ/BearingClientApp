package mage_package;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import base_lib.MageConnection;

public class AuthorizationTask extends Task<Map<Boolean, String>>
{
	private MageConnection connection = null;
	private String login;
	private String pass;
	private Stage stage;
	private Label consoleLabel;
	private boolean packetSceneFlag = true;
	
	public AuthorizationTask(String login, String pass, MageConnection connection, Stage stage, Label consoleLabel)
	{
		this.login = login;
		this.pass = pass;
		this.connection = connection;
		this.stage = stage;
		this.consoleLabel = consoleLabel;
	}

	@Override
	protected Map<Boolean, String> call() throws Exception 
	{
		Map<Boolean, String> answerMap = new HashMap<>(); //создание карты с сообщением пользователю и разрешением на открытие сцены главной формы
		
		Platform.runLater(() -> consoleLabel.setText("Установка соединения с базой данных..."));
		
		try 
		{
			connection.getConnection(); //попытка соединения с базой данных
		} 
			catch (ClassNotFoundException cnfe) 
			{
				cnfe.printStackTrace();
				answerMap.put(false, "При закрытии соединения с базой данных возникли ошибки: " + cnfe.getMessage());
				Platform.runLater(() -> consoleLabel.setText("Ошибка авторизации..."));
				return answerMap;
			}
		
			catch (SQLException sqle) 
			{
				sqle.printStackTrace();
				answerMap.put(false, "При закрытии соединения с базой данных возникли ошибки:" + sqle.getMessage());
				Platform.runLater(() -> consoleLabel.setText("Ошибка авторизации..."));
				return answerMap;
			}
		
		if(connection.conn == null)
		{
			answerMap.put(false, "При закрытии соединения с базой данных возникли ошибки");
			Platform.runLater(() -> consoleLabel.setText("Ошибка авторизации..."));
			return answerMap;
		}
		
		String getId = "SELECT * FROM h136894_mage_base.employees" +
    			" WHERE h136894_mage_base.employees.username = '"+ login +"';";
    	connection.resSet = connection.statmt.executeQuery(getId); //выполнение запроса на получение информации о пользователе
    	
    	if(!connection.resSet.next())
    	{
    		try 
    		{
    			connection.closeConn();
    		} 
    			catch (Throwable e) 
    			{
    				e.printStackTrace();
    				answerMap.put(false, "При закрытии соединения с базой данных возникли ошибки: " + e.getMessage());
    				Platform.runLater(() -> consoleLabel.setText("Ошибка авторизации..."));
    				return answerMap;
    			}
    		
    		connection = null;
    		answerMap.put(false, "Пользователя с таким логином не существует");
    		Platform.runLater(() -> consoleLabel.setText("Ошибка авторизации..."));
    		return answerMap;
    	}
    			
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(8); //создание кодировщика пароля для сравнения введенного пароля с его хешированным значением в базе данных
		
		if(!passwordEncoder.matches(pass, connection.resSet.getString(3))) //сравнение паролей
		{
			try 
			{
				connection.closeConn();
			} 
				catch (Throwable e) 
				{
					e.printStackTrace();
					answerMap.put(false, "При закрытии соединения с базой данных возникли ошибки: " + e.getMessage());
					Platform.runLater(() -> consoleLabel.setText("Ошибка авторизации..."));
					return answerMap;
				}
			
			passwordEncoder = null;

			connection = null;
    		answerMap.put(false, "Пароль пользователя введён неверно");
    		Platform.runLater(() -> consoleLabel.setText("Ошибка авторизации..."));
    		return answerMap;	
		}
		
		passwordEncoder = null;
    	
    	if(connection.resSet.getString(5).equals("1")) //проверка на то авторизован ли пользователь
    	{
    		try 
    		{
				connection.closeConn();
			} 
    			catch (Throwable e) 
    			{
    				e.printStackTrace();
    				answerMap.put(false, "При закрытии соединения с базой данных возникли ошибки: " + e.getMessage());
    				Platform.runLater(() -> consoleLabel.setText("Ошибка авторизации..."));
    				return answerMap;
    			}
    		
    		connection = null;
    		answerMap.put(false, "Данный пользователь уже авторизован");
			return answerMap;
    	}
    	
    	if(connection.resSet.getString(4).equals("0")) //проверка на то, заблокирован ли пользователь
    	{
    		try 
    		{
				connection.closeConn();
			} 
    			catch (Throwable e) 
    			{
    				e.printStackTrace();
    				answerMap.put(false, "При закрытии соединения с базой данных возникли ошибки: "  + e.getMessage());
    				Platform.runLater(() -> consoleLabel.setText("Ошибка авторизации..."));
    				return answerMap;
    			}
    		
    		connection = null;
    		answerMap.put(false, "Доступ к системе закрыт, обратитесь к администратору");
    		Platform.runLater(() -> consoleLabel.setText("Ошибка авторизации..."));
			return answerMap;
    	}
    	
    	Service.client_login = connection.resSet.getString(2); //запись основных данных пользователя в переменные для текущего сеанса
    	Service.client_id = Integer.parseInt(connection.resSet.getString(8));
    	Service.client_role = connection.resSet.getString(9);
   
    	String setActive = "UPDATE h136894_mage_base.employees SET h136894_mage_base.employees.desc_active = 1"
    			+ " WHERE h136894_mage_base.employees.username = '"+ login +"';";
    	connection.statmt.executeUpdate(setActive);
    	
    	Platform.runLater(() -> consoleLabel.setText("Установка соединения с ActiveMQ..."));
    	Service.sender = new Sender();
    	
    	try 
    	{
    	   Service.sender.getBrockerConnection();	//установка соединения с брокером сообщений
    	}
    	   catch (JMSException jmse)
    	   {
    		   jmse.printStackTrace();
			   answerMap.put(false, "При соединении с ActiveMQ возникли проблемы: " + jmse.getMessage());
			   Platform.runLater(() -> consoleLabel.setText("Ошибка авторизации..."));
			   return answerMap;
    	   }
    	
    	Platform.runLater(() -> consoleLabel.setText("Установка соединения с локальной базой данных..."));
    	//Service.lbConnection = new LocalBaseConnection();
    	//Service.lbConnection.getConnection(); //установка соединения с локальной базой данных
    	
    	Platform.runLater(() -> consoleLabel.setText("Инициализация сцены пакетов..."));
    	Platform.runLater(() -> {
			try 
			{
				Client.getInstance(stage);
			} 
				catch (IOException ioe) 
				{
					packetSceneFlag = false;
					ioe.printStackTrace();
				}
		});
    	
    	if(!packetSceneFlag) 
    	{
    		answerMap.put(false, "Ошибка инициализвации сцены пакетов");
    		Platform.runLater(() -> consoleLabel.setText("Ошибка авторизации..."));
    		return answerMap;
    	}
    	
    	answerMap.put(true, "Вы успешно авторизованы");
		return answerMap;
	}
}