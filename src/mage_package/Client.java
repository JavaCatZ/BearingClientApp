package mage_package;

import java.io.IOException;

import controllers.ClientController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * @author CatDevil
 *
 */

public class Client 
{
	public static Client client;
	public ClientController controller;
	public Scene clientScene;
	private AnchorPane pane;
	private Group group;  
	
	private Client(Stage stage) throws IOException
	{
	  group = new Group();
	  FXMLLoader fxmlLoader = new FXMLLoader();
	  fxmlLoader.setLocation(getClass().getResource("/templates/mainTemplate.fxml"));
	  controller = new ClientController(stage);
	  fxmlLoader.setController(controller);
	  fxmlLoader.load();
	  pane = fxmlLoader.getRoot();
	  pane.getStylesheets().addAll(this.getClass().getResource("/mage_package/application.css").toString());
	  group.getChildren().addAll(Service.fone, pane);
	  clientScene = new Scene(group, 1082, 620);
	}
	  
	public static synchronized Client getInstance(Stage stage) throws IOException
	{
	   if(client == null)
	   {
	      client = new Client(stage);
	   }
		 
	   return client;
	}
}