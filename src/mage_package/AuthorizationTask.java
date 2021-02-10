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
		Map<Boolean, String> answerMap = new HashMap<>(); //�������� ����� � ���������� ������������ � ����������� �� �������� ����� ������� �����
		
		Platform.runLater(() -> consoleLabel.setText("��������� ���������� � ����� ������..."));
		
		try 
		{
			connection.getConnection(); //������� ���������� � ����� ������
		} 
			catch (ClassNotFoundException cnfe) 
			{
				cnfe.printStackTrace();
				answerMap.put(false, "��� �������� ���������� � ����� ������ �������� ������: " + cnfe.getMessage());
				Platform.runLater(() -> consoleLabel.setText("������ �����������..."));
				return answerMap;
			}
		
			catch (SQLException sqle) 
			{
				sqle.printStackTrace();
				answerMap.put(false, "��� �������� ���������� � ����� ������ �������� ������:" + sqle.getMessage());
				Platform.runLater(() -> consoleLabel.setText("������ �����������..."));
				return answerMap;
			}
		
		if(connection.conn == null)
		{
			answerMap.put(false, "��� �������� ���������� � ����� ������ �������� ������");
			Platform.runLater(() -> consoleLabel.setText("������ �����������..."));
			return answerMap;
		}
		
		String getId = "SELECT * FROM h136894_mage_base.employees" +
    			" WHERE h136894_mage_base.employees.username = '"+ login +"';";
    	connection.resSet = connection.statmt.executeQuery(getId); //���������� ������� �� ��������� ���������� � ������������
    	
    	if(!connection.resSet.next())
    	{
    		try 
    		{
    			connection.closeConn();
    		} 
    			catch (Throwable e) 
    			{
    				e.printStackTrace();
    				answerMap.put(false, "��� �������� ���������� � ����� ������ �������� ������: " + e.getMessage());
    				Platform.runLater(() -> consoleLabel.setText("������ �����������..."));
    				return answerMap;
    			}
    		
    		connection = null;
    		answerMap.put(false, "������������ � ����� ������� �� ����������");
    		Platform.runLater(() -> consoleLabel.setText("������ �����������..."));
    		return answerMap;
    	}
    			
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(8); //�������� ����������� ������ ��� ��������� ���������� ������ � ��� ������������ ��������� � ���� ������
		
		if(!passwordEncoder.matches(pass, connection.resSet.getString(3))) //��������� �������
		{
			try 
			{
				connection.closeConn();
			} 
				catch (Throwable e) 
				{
					e.printStackTrace();
					answerMap.put(false, "��� �������� ���������� � ����� ������ �������� ������: " + e.getMessage());
					Platform.runLater(() -> consoleLabel.setText("������ �����������..."));
					return answerMap;
				}
			
			passwordEncoder = null;

			connection = null;
    		answerMap.put(false, "������ ������������ ����� �������");
    		Platform.runLater(() -> consoleLabel.setText("������ �����������..."));
    		return answerMap;	
		}
		
		passwordEncoder = null;
    	
    	if(connection.resSet.getString(5).equals("1")) //�������� �� �� ����������� �� ������������
    	{
    		try 
    		{
				connection.closeConn();
			} 
    			catch (Throwable e) 
    			{
    				e.printStackTrace();
    				answerMap.put(false, "��� �������� ���������� � ����� ������ �������� ������: " + e.getMessage());
    				Platform.runLater(() -> consoleLabel.setText("������ �����������..."));
    				return answerMap;
    			}
    		
    		connection = null;
    		answerMap.put(false, "������ ������������ ��� �����������");
			return answerMap;
    	}
    	
    	if(connection.resSet.getString(4).equals("0")) //�������� �� ��, ������������ �� ������������
    	{
    		try 
    		{
				connection.closeConn();
			} 
    			catch (Throwable e) 
    			{
    				e.printStackTrace();
    				answerMap.put(false, "��� �������� ���������� � ����� ������ �������� ������: "  + e.getMessage());
    				Platform.runLater(() -> consoleLabel.setText("������ �����������..."));
    				return answerMap;
    			}
    		
    		connection = null;
    		answerMap.put(false, "������ � ������� ������, ���������� � ��������������");
    		Platform.runLater(() -> consoleLabel.setText("������ �����������..."));
			return answerMap;
    	}
    	
    	Service.client_login = connection.resSet.getString(2); //������ �������� ������ ������������ � ���������� ��� �������� ������
    	Service.client_id = Integer.parseInt(connection.resSet.getString(8));
    	Service.client_role = connection.resSet.getString(9);
   
    	String setActive = "UPDATE h136894_mage_base.employees SET h136894_mage_base.employees.desc_active = 1"
    			+ " WHERE h136894_mage_base.employees.username = '"+ login +"';";
    	connection.statmt.executeUpdate(setActive);
    	
    	Platform.runLater(() -> consoleLabel.setText("��������� ���������� � ActiveMQ..."));
    	Service.sender = new Sender();
    	
    	try 
    	{
    	   Service.sender.getBrockerConnection();	//��������� ���������� � �������� ���������
    	}
    	   catch (JMSException jmse)
    	   {
    		   jmse.printStackTrace();
			   answerMap.put(false, "��� ���������� � ActiveMQ �������� ��������: " + jmse.getMessage());
			   Platform.runLater(() -> consoleLabel.setText("������ �����������..."));
			   return answerMap;
    	   }
    	
    	Platform.runLater(() -> consoleLabel.setText("��������� ���������� � ��������� ����� ������..."));
    	//Service.lbConnection = new LocalBaseConnection();
    	//Service.lbConnection.getConnection(); //��������� ���������� � ��������� ����� ������
    	
    	Platform.runLater(() -> consoleLabel.setText("������������� ����� �������..."));
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
    		answerMap.put(false, "������ �������������� ����� �������");
    		Platform.runLater(() -> consoleLabel.setText("������ �����������..."));
    		return answerMap;
    	}
    	
    	answerMap.put(true, "�� ������� ������������");
		return answerMap;
	}
}