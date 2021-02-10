package mage_package;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnectionFactory;

import base_lib.Message;

/**
*
* @author CatDevil's
*/

public class Sender 
{
   //private String url = ActiveMQConnection.DEFAULT_BROKER_URL;
   private String subject = "MAGE_Queue";
   private ActiveMQConnectionFactory connectionFactory = null;
   private Connection mq_connection = null;  
   private Session session = null;
   private Destination destination = null;
   private MessageProducer producer = null;
   
   public Sender()
   {   
        connectionFactory = new ActiveMQConnectionFactory("failover:(tcp://localhost:61616)?maxReconnectAttempts=-1"); 
   }
   
    public void getBrockerConnection() throws JMSException
  	{
    	mq_connection = connectionFactory.createConnection();
		
		try
		{
			mq_connection.start();	
		}
			catch(Exception ex)
			{
				throw new JMSException("No connection with brocker");
			}

	    session = mq_connection.createSession(false, Session.AUTO_ACKNOWLEDGE); 
	    destination = session.createQueue(subject); 
	    producer = session.createProducer(destination); 
  	}
    
    public Connection getConnStatus()
  	{
    	if(mq_connection == null)
    	{
    	   return null;
    	}
    		else
    		{
    			return mq_connection;
    		}
  	}
    
   	public void sendPackage(Message msgPacket) throws JMSException
   	{
	    ObjectMessage msg = session.createObjectMessage();
	    msg.setObject(msgPacket);  
	    producer.send(msg);	
   	}
   	
   	public void connClose() throws JMSException
   	{
   		if(mq_connection != null)
   		{
   			mq_connection.close();
   		}
   	}
}