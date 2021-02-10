package controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import mage_package.ConnectionThread;
import mage_package.Regulars;
import mage_package.SendTask;
import mage_package.Service;
import base_lib.CoordMessage;
import base_lib.FullMessage;
import base_lib.MageConnection;
import base_lib.Message;
import base_lib.MsgMessage;
import base_lib.Photos;

/**
 * @author CatDevil
 *
 */

public class ClientController 
{	
	private Stage stage;
	
	public ClientController(Stage stage)
	{
	   this.stage = stage;
	}
	
/*******************************Main details**********************************/
	
	@FXML
    private ProgressIndicator indicate;
	
	@FXML
    private AnchorPane mainAnchor;
	
	@FXML
    private Label vesselLabel;
	@FXML
    private ComboBox<String> vesselCmb;
	@FXML
    private Label shipLabel;
	
	@FXML
    private TabPane messagePane;
	@FXML
    private Tab fullPane;
	@FXML
    private Tab msgPane;
	@FXML
    private Tab coordPane;
	
    @FXML
    private Button sendBtn;
    
    private FileChooser fileChooser = null;
    private List<File> list = null;
    
    private String lat_str; 
    private String lon_str;
    
    @FXML
    private TextArea consoleText;
	
    @FXML
    private ProgressIndicator sendProg;
    
	public static ConnectionThread conThread = null;
    
/**********************************Users panel*********************************/	
	
	@FXML
    private Pane usersPane;
	@FXML
    private Label userLabel;
	@FXML
    private Label idLabel;
	@FXML
    private Label userLabelT;
	@FXML
    private Label idLabelT;
	@FXML
    private ImageView userImg;
	@FXML
    private ImageView conImg;
	@FXML
    private Label infoLabel;
	
/************************Full message tab****************************************/
	
	@FXML
    private TextField themeTextF;
	
	@FXML
    private ComboBox<String> categCmb;
	
	@FXML
    private TextField latTextF;
	@FXML
    private Pane latPaneF;
	@FXML
    private RadioButton northRadF;
	@FXML
    private RadioButton southRadF;
	
	@FXML
    private TextField longTextF;
	@FXML
    private Pane longPaneF;
	@FXML
    private RadioButton eastRadF;
	@FXML
    private RadioButton westRadF;
	
	@FXML
    private TextArea descArea;
	
	@FXML
	private Button choiceBtn;
	@FXML
    private TextArea fileArea;
	
/************************Message tab****************************************/
	
	@FXML
    private TextField themeTextM;
	
	@FXML
    private TextArea msgArea;
	 
/************************Coordinate message tab****************************************/
	
	@FXML
    private TextField latTextC;
	@FXML
    private Pane latPaneC;
	@FXML
    private RadioButton northRadC;
	@FXML
    private RadioButton southRadC;
	
	@FXML
    private TextField longTextC;
	@FXML
    private Pane longPaneC;
	@FXML
    private RadioButton eastRadC;
	@FXML
    private RadioButton westRadC;
	
/********************************************************************************/ 
    
	private Message message = null;
	Map<String,String> vessels = null;
	
    @FXML
    public void initialize() throws IOException
    {
    	vessLoad();
        conThread = new ConnectionThread(infoLabel, conImg, consoleText);
        conThread.conThreadStart();
    	
    	userLabel.setId("usersLabel");
		idLabel.setId("usersLabel");
		userLabelT.setId("usersLabel");
		idLabelT.setId("usersLabel");
		infoLabel.setId("usersLabel");
		
    	switch(Service.client_role)
    	{
    		case "USER":
    			userImg.setImage(new Image("/images/user.png", true));
    			Service.admin_flag = false;
    		break;
    		
    		case "ADMIN":
    			userImg.setImage(new Image("/images/admin.png", true));
    			Service.admin_flag = true;
    		break;
    	}
    	
    	userLabelT.setText(Service.client_login);
    	idLabelT.setText(String.valueOf(Service.client_id));
    	
    	lat_str = " N.L.";
    	lon_str = " E.L.";
    	
    	categCmb.setItems(Service.categories);
    	
/*********************************Full message settings********************************************/
    	
    	fileChooser = new FileChooser();
    	
    	categCmb.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
     		if(!categCmb.getSelectionModel().isEmpty())
     		{
     			categCmb.setStyle(Service.GREEN_COLOR);
     		}
     	});
        
        themeTextF.textProperty().addListener((observable, oldValue, newValue) -> {
        	Service.textHandler(themeTextF, Service.THEME_LENGTH);
        });
        
        descArea.textProperty().addListener((observable, oldValue, newValue) -> {
        	Service.textHandler(descArea, Service.MSG_LENGTH);
        });
    	
    	latTextF.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!Regulars.coordPattern.matcher(newValue).matches()) latTextF.setText(oldValue);
            Service.textAngleHandler(latTextF);           
     	});
         
        longTextF.textProperty().addListener((observable, oldValue, newValue) -> {   
         	if (!Regulars.coordPattern.matcher(newValue).matches()) longTextF.setText(oldValue);
         	Service.textAngleHandler(longTextF);    
         });
     
    	 choiceBtn.setOnMouseClicked((MouseEvent event) -> {
   
    		fileArea.clear();
    		
    		list = fileChooser.showOpenMultipleDialog(stage);
    		if(list.size() < 1)
    		{
    			Service.errorStage.setContentText("Выберите не менее 1 фото");
    			Service.errorStage.showAndWait();
    			return;
    		}
     			if (list != null) 
     			{
     				long size = 0;
     				for (File file : list) 
     				{
     				   String ext = file.getName().toString().substring(file.getName().toString().lastIndexOf(".") + 1);
						
     				   if(!ext.equals("jpg") && !ext.equals("png"))
     				   {
     					  Service.errorStage.setContentText("Файлы фото должны быть формата .jpg или .png");
     		    		  Service.errorStage.showAndWait();
     		    		  return;
     				   }
     				   
     				   size += file.length();
     				   fileArea.appendText(file.getAbsolutePath() + "\n");
     				}
     				
     				if(size > 10485760)
     				{
     					Service.errorStage.setContentText("Общий размер файлов не должен превышать 10мб.");
    		    		Service.errorStage.showAndWait();
    		    		fileArea.clear();
    		    		return;
     				}
     			}  
    	});

/*********************************Message settings*****************************************************/ 
    	 
    	 themeTextM.textProperty().addListener((observable, oldValue, newValue) -> {
    		 Service.textHandler(themeTextM, Service.THEME_LENGTH);
         });
         
         
         msgArea.textProperty().addListener((observable, oldValue, newValue) -> {
        	 Service.textHandler(msgArea, Service.MSG_LENGTH);
         });
         
/*************************************Coordinate message settings****************************************************/  
         
         latTextC.textProperty().addListener((observable, oldValue, newValue) -> {
             if (!Regulars.coordPattern.matcher(newValue).matches()) latTextC.setText(oldValue);
             Service.textAngleHandler(latTextC);
      	 });
          
         longTextC.textProperty().addListener((observable, oldValue, newValue) -> {   
          	if (!Regulars.coordPattern.matcher(newValue).matches()) longTextC.setText(oldValue);
          	Service.textAngleHandler(longTextC);
          });
   
/************************************************************************************************************************/ 
    	
        vesselCmb.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
        	if(!vesselCmb.getSelectionModel().isEmpty())
       		{
       			vesselCmb.setStyle(Service.GREEN_COLOR);
       		}
        	
        	shipLabel.setText(vessels.get(vesselCmb.getSelectionModel().getSelectedItem()));
       	});
          
        sendBtn.setOnMouseClicked((MouseEvent event) -> {
        	
        	String currentDateTime = new SimpleDateFormat("dd-MM-yy HH:mm").format(Calendar.getInstance().getTime());
        	
        	if(!Service.comboBoxHandler(vesselCmb, "Выберите судно")) return;
        	
        	int package_id = Service.getPackNum();
        	
        	switch(messagePane.getSelectionModel().getSelectedItem().getId())
      		{
      		  case "fullTab":
      			
      			if(!Service.textCheckHandler(themeTextF, false, "Введите тему сообщения")) return; 
      			if(!Service.comboBoxHandler(categCmb, "Выберите категорию сообщения")) return;           
            	if(!Service.textCheckHandler(latTextF, true, "Введите значение широты")) return;
            	if(!Service.textCheckHandler(longTextF, true, "Введите значение долготы")) return;
            	if(!Service.textCheckHandler(descArea, false, "Введите описание проблемы в сообщении")) return; 
            	
            	if (list == null) 
         		{
            	  Service.warningStage.setContentText("Выберите фото для отправки формы");
            	  Service.warningStage.showAndWait(); 
            	  return;
         		}
            	
            	File[] images = new File[list.size()];
    			
    			int num = 0;
			    for(File file : list)
			    {
			       images[num] = file;
			       num++;
			    }
            	
            	message = new FullMessage(Service.client_id, Service.client_role, package_id, shipLabel.getText(), latTextF.getText() + lat_str,
            			longTextF.getText() + lon_str, categCmb.getValue().toString(), themeTextF.getText(), descArea.getText(), new Photos(images), currentDateTime);
            	        	
      		  break;
      			  
      		  case "msgTab":
      			  
      			if(!Service.textCheckHandler(themeTextM, false, "Введите тему сообщения")) return; 		
            	if(!Service.textCheckHandler(msgArea, false, "Введите описание проблемы в сообщении")) return;   	
            	
            	message = new MsgMessage(Service.client_id, Service.client_role, package_id, shipLabel.getText(), themeTextM.getText(), msgArea.getText(), currentDateTime);  
            	
      		  break;
      		  
      		  case "coordTab":
      			  
      			if(!Service.textCheckHandler(latTextC, true, "Введите значение широты")) return;
            	if(!Service.textCheckHandler(longTextC, true, "Введите значение долготы")) return;
            	
            	message = new CoordMessage(Service.client_id, Service.client_role, package_id, shipLabel.getText(),
            			latTextC.getText() + lat_str, longTextC.getText() + lon_str, currentDateTime);  
                        	
      		  break;
      		}
        	
        	consoleText.appendText("\n-> Отправка пакета #" + package_id + "...");
        	
        	Task<Boolean> task = new SendTask(message);
        	sendProg.progressProperty().bind(task.progressProperty());
    		task.runningProperty().addListener(new ChangeListener<Boolean>() 
    		{
    			@Override
    			public void changed(ObservableValue<? extends Boolean> arg0, Boolean wasRunning, Boolean isRunning) 
    			{
    				if(!isRunning)
    				{
    					sendProg.setVisible(false);
    				} else sendProg.setVisible(true);
    			}   
    		});
    			 
    		task.setOnSucceeded((Event) -> {
    			Boolean answer = task.getValue();
    				
    			if(answer)
    			{
    				consoleText.appendText("\n-> Пакет #" + package_id + " был успешно отправлен...");
    				
    			}
    				else 
    				{			
    					consoleText.appendText("\n-> Ошибка отправки пакета #" + package_id + "...");
    					
    					try 
    					{
    						if(message != null)
    						{
    							savePacket(message);
    							Service.pack_count++;
        						Service.showWarningStage("Пакет #" + package_id + "был успешно сохранён в базе данных клиента");	
    						}
						} 
    						catch (FileNotFoundException fnfe) 
    						{
								fnfe.printStackTrace();
								Service.pack_count++;
	    						Service.showWarningStage("Ошибка сохранения пакета #" + package_id + " в базе данных клиента");
							}
    				}
    			});
    			
    			new Thread(task).start();	
        });
    }
    
    @FXML
    public void onRadClickHandler(MouseEvent event)
    {
		String curObj = ((RadioButton) event.getSource()).getId();
		
		switch(curObj)
		{
			case "id_northRadF":
				northRadF.setSelected(true);
	      		southRadF.setSelected(false);
	      		lat_str = " N.L.";
			break;
			
			case "id_southRadF":
				southRadF.setSelected(true);
	      		northRadF.setSelected(false);
	      		lat_str = " S.L.";
			break;
				
			case "id_eastRadF":
				eastRadF.setSelected(true);
	      		westRadF.setSelected(false);
	      		lon_str = " E.L.";
			break;
				
			case "id_westRadF":
				westRadF.setSelected(true);
	      		eastRadF.setSelected(false);
	      		lon_str = " W.L.";
			break;
			
			case "id_northRadC":
				northRadC.setSelected(true);
	      		southRadC.setSelected(false);
	      		lat_str = " N.L.";
			break;
			
			case "id_southRadC":
				southRadC.setSelected(true);
	      		northRadC.setSelected(false);
	      		lat_str = " S.L.";
			break;
				
			case "id_eastRadC":
				eastRadC.setSelected(true);
	      		westRadC.setSelected(false);
	      		lon_str = " E.L.";
			break;
				
			case "id_westRadC":
				westRadC.setSelected(true);
	      		eastRadC.setSelected(false);
	      		lon_str = " W.L.";
			break;
		}
    }
    
    private void vessLoad()
    {
    	ObservableList<String> data = FXCollections.observableArrayList();
    	vessels = new HashMap<String, String>();
    	data.clear();
    	vesselCmb.getItems().clear();
    	String getVessels = "SELECT h136894_mage_base.ships.name,h136894_mage_base.ships.id_num FROM h136894_mage_base.ships";
    	
    	MageConnection connection = new MageConnection();
    	
    	try 
        {
    		try {
				connection.getConnection();
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    		connection.resSet = connection.statmt.executeQuery(getVessels);
    		while(connection.resSet.next())
  		  	{
    			vessels.put(connection.resSet.getString(1), connection.resSet.getString(2));  
  		  	}
  		
    		data.addAll(vessels.keySet());
    		vesselCmb.setItems(data);
    		try {
				connection.closeConn();
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    		catch (SQLException e) 
        	{
    			System.out.println(e.getMessage());
        	}
    }
    
    private void savePacket(Message message) throws FileNotFoundException
    {
    	PreparedStatement prepSt = null;
    	Message current = null;
    	
    	try 
    	{
			Service.lbConnection.conn.setAutoCommit(false);
			
			switch(message.getMsgType())
	    	{
	    		case 20:
	    			current = message;
	    			
	    			prepSt = (PreparedStatement) Service.lbConnection.conn.prepareStatement("INSERT INTO Messages.CoordPacket (id_sender, sender_role, id_packet, vessel_info, latitude, longitude, date_time)"
							+ " VALUES(?, ?, ?, ?, ?, ?, ?)");
	    			prepSt.setInt(1, ((CoordMessage) current).getIdSender());
	    			prepSt.setString(2, ((CoordMessage) current).getSenderRole());
	    			prepSt.setInt(3, ((CoordMessage) current).getIdPacket());
	    			prepSt.setString(4, ((CoordMessage) current).getVessel());
	    			prepSt.setString(5, ((CoordMessage) current).getLatitude());
	    			prepSt.setString(6, ((CoordMessage) current).getLongitude());
	    			prepSt.setString(7, ((CoordMessage) current).getDateTime());
	    		break;
	    		
	    		case 40:
	    			current = message;
	    			
	    			prepSt = (PreparedStatement) Service.lbConnection.conn.prepareStatement("INSERT INTO Messages.MessagePacket (id_sender, sender_role, id_packet, vessel_info, packet_theme, packet_desc, date_time)"
							+ " VALUES(?, ?, ?, ?, ?, ?, ?)");
	    			prepSt.setInt(1, ((MsgMessage) current).getIdSender());
	    			prepSt.setString(2, ((MsgMessage) current).getSenderRole());
	    			prepSt.setInt(3, ((MsgMessage) current).getIdPacket());
	    			prepSt.setString(4, ((MsgMessage) current).getVessel());
	    			prepSt.setString(5, ((MsgMessage) current).getPacketTheme());
	    			prepSt.setString(6, ((MsgMessage) current).getPacketDesc());
	    			prepSt.setString(7, ((MsgMessage) current).getDateTime());
	        	break;
	        		
	    		case 60:
	    			current = message;
	    			
	    			prepSt = (PreparedStatement) Service.lbConnection.conn.prepareStatement("INSERT INTO Messages.FullPacket (id_sender, sender_role, id_packet, vessel_info, latitude,"
	    					+ " longitude, packet_cat, packet_theme, packet_desc, packet_images, date_time)"
							+ " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
	    			prepSt.setInt(1, ((FullMessage) current).getIdSender());
	    			prepSt.setString(2, ((FullMessage) current).getSenderRole());
	    			prepSt.setInt(3, ((FullMessage) current).getIdPacket());
	    			prepSt.setString(4, ((FullMessage) current).getVessel());
	    			prepSt.setString(5, ((FullMessage) current).getLatitude());
	    			prepSt.setString(6, ((FullMessage) current).getLongitude());
	    			prepSt.setString(7, ((FullMessage) current).getPacketCat());
	    			prepSt.setString(8, ((FullMessage) current).getPacketTheme());
	    			prepSt.setString(9, ((FullMessage) current).getPacketDesc());
					prepSt.setObject(10, ((FullMessage) current).getPacketImages());
					prepSt.setString(11, ((FullMessage) current).getDateTime());
	    		break;
	    	}
			
			prepSt.executeUpdate();
			Service.lbConnection.conn.commit();
			prepSt.close();
		} 
    	
    		catch (SQLException e) 
    		{
    			e.printStackTrace();
    		}
    }
}