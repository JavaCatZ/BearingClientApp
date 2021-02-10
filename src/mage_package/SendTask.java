package mage_package;

import base_lib.Message;
import javafx.concurrent.Task;

public class SendTask extends Task<Boolean>
{
	private Message message;
	
	public SendTask(Message message)
	{
		this.message = message;
	}

	@Override
	protected Boolean call() throws Exception 
	{
		try
		{
			Service.sender.sendPackage(message);
			return true;
		}
			catch(Exception ex)
			{
				return false;
			}
	}
}
