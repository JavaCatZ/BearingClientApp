package mage_package;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
import javax.jms.JMSException;
import base_lib.CoordMessage;
import base_lib.FullMessage;
import base_lib.MsgMessage;
import base_lib.Photos;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;

/**
 * @author CatDevil
 *
 */

public class ConnectionThread
{
	private Thread thread = null;
	private boolean threadActive = false;
	private URL url = null;
	private URLConnection connection = null;
	
	public static ImageView infoImg;
	public static Label infoLabel;
	public static TextArea consoleLabel;
	
	Task<Void> task = new Task<Void>() 
	 {
        @Override
        protected Void call() throws Exception 
        {
           while(threadActive) //если поток выполняется
     	   {
     		   if(netIsAvailable())
     		   {
     			  if(Service.lbConnection.conn.isClosed()) //если соединение с базой данных потеряно, то восстанавливаем его
    			   {
     				 Service.lbConnection.getConnection();
    			   }
     			   
     			   System.out.println("Connection is work");
     			   infoImg.setImage(Service.onImg);
     			   Platform.runLater(()-> infoLabel.setText("Online"));
     			   
     			   if(Service.pack_count > 0) //если количество пакетов в локальной базе данных больше нуля, то начинаем их вытаскивать и пересылать брокеру сообщений
     			   {
     				   walkBase();
     			   }
     		   }
     		   		else
     		   		{
     		   			System.out.println("No connection with internet");
     		   			infoImg.setImage(Service.offImg);
     		   			infoLabel.setText("Offline");
     		   		}
     		   
     		   try 
     		   {
     			   if(!Thread.interrupted()) //если поток прерван, то закрываем его
     			   {
     				  Thread.sleep(5000);   
     			   }
     		   } 
     		   	   catch (InterruptedException e) 
     		   	   {
     		   		   e.printStackTrace();
     		   	   };   
     	   }
           
           return null;  
        }
    };
    
    public void conThreadStart()
    {
    	threadActive = true;
    	
    	if(!thread.isAlive())
    	{
    		thread.start();
    	}
    }
    
    public void conThreadStop()
    {
    	threadActive = false;
    	
    	if(thread.isAlive())
    	{
    		thread.interrupt();
    	}
    }
	
	public ConnectionThread(Label refLabel, ImageView refImg, TextArea consoleRefLabel)
	{
		infoImg = refImg;
		infoLabel = refLabel;
		consoleLabel = consoleRefLabel;
		
		try 
		{
			url = new URL("http://mage.ru");
		} 
			catch (MalformedURLException e) 
			{
				e.printStackTrace();
			}
		
		thread = new Thread(task);
	}
	
	private boolean netIsAvailable() 
	{
	    try 
	    {
	        connection = url.openConnection();
	        connection.connect();
	        connection.getInputStream().close();
	        return true;
	    } 
	    	catch (MalformedURLException e) 
	    	{
	    		throw new RuntimeException(e);
	    	}
	    
	    	catch (IOException e) 
	    	{
	    		return false;
	    	}
	}
	
	private void walkBase()
	{
		String getCM = "SELECT * FROM Messages.CoordPacket;";
		String getMM = "SELECT * FROM Messages.MessagePacket;";
		String getFM = "SELECT * FROM Messages.FullPacket;";
		try 
		{
		   Service.lbConnection.resSet = Service.lbConnection.statmt.executeQuery(getCM);
		   
		   while(Service.lbConnection.resSet.next())
		   {    
			  try 
			  {
				  Service.sender.sendPackage(new CoordMessage(Integer.valueOf(Service.lbConnection.resSet.getString(2)), Service.lbConnection.resSet.getString(3),
						Integer.valueOf(Service.lbConnection.resSet.getString(4)), Service.lbConnection.resSet.getString(5), Service.lbConnection.resSet.getString(6),
						Service.lbConnection.resSet.getString(7), Service.lbConnection.resSet.getString(8)));
				  Service.lbConnection.statmt.executeQuery("DELETE FROM Messages.CoordPacket WHERE Messages.CoordPacket.id_packet= " + Service.lbConnection.resSet.getString(4) + ";");
			  } 
			  	catch (NumberFormatException | JMSException e) 
			  	{
			  		Platform.runLater(()-> {
						try 
						{
							consoleLabel.setText(consoleLabel.getText().concat("\n-> Пакет #" + Service.lbConnection.resSet.getString(1) + " не был передан из локальной БД и возвращён обратно..."));
						} 
							catch (SQLException e1) 
							{
								e1.printStackTrace();
							}
					});
			  		
			  		e.printStackTrace();
			  	} 
		   }
		   
		   Service.lbConnection.resSet = Service.lbConnection.statmt.executeQuery(getMM);
		   
		   while(Service.lbConnection.resSet.next())
		   {   
			  try 
			  {
				  Service.sender.sendPackage(new MsgMessage(Integer.valueOf(Service.lbConnection.resSet.getString(2)), Service.lbConnection.resSet.getString(3),
						Integer.valueOf(Service.lbConnection.resSet.getString(4)), Service.lbConnection.resSet.getString(5), Service.lbConnection.resSet.getString(6),
						Service.lbConnection.resSet.getString(7), Service.lbConnection.resSet.getString(8)));
				  Service.lbConnection.statmt.executeQuery("DELETE FROM Messages.MessagePacket WHERE Messages.MessagePacket.id_packet = " + Service.lbConnection.resSet.getString(4) + ";");
			  } 
			  	catch (NumberFormatException | JMSException e) 
			  	{
			  		Platform.runLater(()-> {
						try 
						{
							consoleLabel.setText(consoleLabel.getText().concat("\n-> Пакет #" + Service.lbConnection.resSet.getString(1) + " не был передан из локальной БД и возвращён обратно..."));
						} 
							catch (SQLException e1) 
							{
								e1.printStackTrace();
							}
					});
			  		
			  		e.printStackTrace();
			  	} 
		   }
		   
		   Service.lbConnection.resSet = Service.lbConnection.statmt.executeQuery(getFM);
		   
		   while(Service.lbConnection.resSet.next())
		   {   
			  try 
			  {
				  Service.sender.sendPackage(new FullMessage(Integer.valueOf(Service.lbConnection.resSet.getString(2)), Service.lbConnection.resSet.getString(3),
						Integer.valueOf(Service.lbConnection.resSet.getString(4)), Service.lbConnection.resSet.getString(5), Service.lbConnection.resSet.getString(6),
						Service.lbConnection.resSet.getString(7), Service.lbConnection.resSet.getString(8),  Service.lbConnection.resSet.getString(9),  Service.lbConnection.resSet.getString(10),
						(Photos) Service.lbConnection.resSet.getBlob(11), Service.lbConnection.resSet.getString(12)));
				  Service.lbConnection.statmt.executeQuery("DELETE FROM Messages.FullPacket WHERE Messages.FullPacket.id_packet = " + Service.lbConnection.resSet.getString(4) + ";");
			  } 
			  	catch (NumberFormatException | JMSException e) 
			  	{
			  		Platform.runLater(()-> {
						try 
						{
							consoleLabel.setText(consoleLabel.getText().concat("\n-> Пакет #" + Service.lbConnection.resSet.getString(1) + " не был передан из локальной БД и возвращён обратно..."));
						} 
							catch (SQLException e1) 
							{
								e1.printStackTrace();
							}
					});
			  		
			  		e.printStackTrace();
			  	} 
		   }
		} 
		   catch (SQLException e) 
		   {
			  e.printStackTrace();
		   }    
	}
}