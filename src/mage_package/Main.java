package mage_package;
	
import javax.jms.JMSException;

import controllers.ClientController;
import controllers.StartController;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.fxml.FXMLLoader;

/**
 * @author CatDevil
 *
 */

public class Main extends Application 
{
	private StartController startController = null;
	private Group group = null;
	public Scene scene = null;
	private AnchorPane pane = null;
	private static Image icon = new Image("images/ico.png");

	@Override
	public void start(Stage primaryStage) 
	{
		try 
		{
			Service.categories = FXCollections.observableArrayList("Научная","Техническая","Исследовательская");
			
			Service.fone.setFitHeight(620);
			Service.fone.setFitWidth(1082);
			
			Service.successStage.setTitle("Авторизация");
			Service.successStage.setHeaderText(null);
			Service.successStage.setResizable(false);
			Service.successStage.initStyle(StageStyle.UNDECORATED);
			
			Service.warningStage.setTitle("Предупреждение");
			Service.warningStage.setHeaderText(null);
			Service.warningStage.setResizable(false);
			Service.warningStage.initStyle(StageStyle.UNDECORATED);
			
			Service.errorStage.setTitle("Ошибка");
			Service.errorStage.setHeaderText(null);
			Service.errorStage.setResizable(false);
			Service.errorStage.initStyle(StageStyle.UNDECORATED);
			
			startController = new StartController(primaryStage);
			group = new Group();
			FXMLLoader fxmlLoader = new FXMLLoader();
			fxmlLoader.setLocation(getClass().getResource("/templates/startTemplate.fxml"));
			fxmlLoader.setController(startController);
			fxmlLoader.load();
			pane = fxmlLoader.getRoot();
			pane.getStylesheets().addAll(this.getClass().getResource("/mage_package/application.css").toString());
			
			ImageView logo = new ImageView(new Image("/images/logo.png", true));
			logo.setFitHeight(250.0);
			logo.setFitWidth(250.0);
			logo.setLayoutX((Service.fone.getFitWidth() / 2) - (logo.getFitWidth() / 2));
			logo.setLayoutY(30);
			
			group.getChildren().addAll(Service.fone, logo, pane);
			scene = new Scene(group);
			primaryStage.setScene(scene);
			primaryStage.setTitle(Service.TITLE);
			primaryStage.getIcons().addAll(icon);
			primaryStage.setResizable(false);	
			primaryStage.sizeToScene();
			primaryStage.show();
			
			primaryStage.setOnCloseRequest((WindowEvent windowEvent) -> {
				System.out.println("Close session...");
				
				if(Service.sender != null)
				{
					try 
					{
						Service.sender.connClose();
						System.out.println("Connection with ActiveMQ was closed...");
					} 
						catch (JMSException jmse) 
						{
							jmse.printStackTrace();
							System.out.println("Connection with ActiveMQ was closed with errors...");
						}
				}
				
				startController.lbConClose();
				
				if(ClientController.conThread != null)
		    	{
		    		System.out.println("Connection thread was stoped...");
		    		ClientController.conThread.conThreadStop();
		    	}
		    });
			
		} 
		   catch(Exception e) 
		   {
			  e.printStackTrace();
		   }
	}
	
	public static void main(String[] args) 
	{
	  launch(args);
	}
}