package application;
	
import java.net.URL;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.stage.Window;

public class Main extends Application 
{
	static private Stage primaryStage;
	static private LoginWindowController loginWindowController;
	static private PatientWindowController patientWindowController;
	static private DoctorWindowController doctorWindowContorller;
	
	@Override
	public void start(Stage _primaryStage) 
	{
		primaryStage = _primaryStage;
		showLoginWindow();
		Helper.connectDatabase();
	}
	
	public static void main(String[] args) 
	{
		launch(args);
	}
	
	public void showLoginWindow() 
	{
		try {
			URL location = getClass().getResource("loginWindow.fxml");
			FXMLLoader fxmlLoader = new FXMLLoader(location);
			fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
			
			Parent root = fxmlLoader.load();
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			
			loginWindowController = fxmlLoader.getController();
			loginWindowController.btn_login.setOnAction(new LoginSystem());
			loginWindowController.btn_exit.setOnAction(new ExitSystem());
			
			primaryStage.setScene(scene);
			primaryStage.setTitle("����ҽԺ�Һ�ϵͳ");
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void showPatientWindow() 
	{
		try {
			URL location = getClass().getResource("patientWindow.fxml");
			FXMLLoader fxmlLoader = new FXMLLoader(location);
			fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
			
			Parent root = fxmlLoader.load();
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			
			patientWindowController = fxmlLoader.getController();
			patientWindowController.init();
			
			primaryStage.setMinHeight(450);
			primaryStage.setMinWidth(600);
			primaryStage.setScene(scene);
			primaryStage.setTitle(String.format("�û����: %s �û�����: %s" , PatientUser.BRBH,PatientUser.BRMC));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void showDoctorWindow() 
	{
		try {
			URL location = getClass().getResource("doctorWindow.fxml");
			FXMLLoader fxmlLoader = new FXMLLoader(location);
			fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
			
			Parent root = fxmlLoader.load();
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			
			doctorWindowContorller = fxmlLoader.getController();
			doctorWindowContorller.init();
			
			primaryStage.setScene(scene);
			primaryStage.setTitle(String.format("�û����: %s �û�����: %s" , DoctorUser.YSBH,DoctorUser.YSMC));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	//��¼
	public void loginSystem() 
	{
		String userId=getUserId();
		String userPasswd=getUserPasswd();
		boolean isPatient=loginWindowController.rb_patient.isSelected()? true : false;
		
		if(isPatient) {
			PatientUser.BRBH=userId;
			PatientUser.DLKL=userPasswd;
			if(PatientUser.login()) {
				showPatientWindow();
			}
			else {
				showAlert(Alert.AlertType.ERROR,primaryStage,"��¼ʧ��","�û������������!");
			}
		}	
		else {
			DoctorUser.YSBH=userId;
			DoctorUser.DLKL=userPasswd;
			if(DoctorUser.login()) {
				showDoctorWindow();
			}
			else {
				showAlert(Alert.AlertType.ERROR,primaryStage,"��¼ʧ��","�û������������!");
			}
		}
	}
	
	//�˳�ϵͳ
	public void exitSystem() 
	{
		System.out.println("�˳�ϵͳ");
		primaryStage.close();
	}
	
	//�����ʾ��Ϣ
    public static void showAlert(Alert.AlertType alertType, Window owner, String title, String message) 
    {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(owner);
        alert.show();
    }
    
    static private String getUserId()
    {
    	return loginWindowController.tf_loginID.getText();
    }
    
    static private String getUserPasswd() 
    {
    	return loginWindowController.tf_passwd.getText();
    }
    
    //��¼�¼�������
  	class LoginSystem implements EventHandler<ActionEvent>
  	{
  		@Override
  		public void handle(ActionEvent e) {
  			loginSystem();
  		}
  	}
  	
  	//�˳��¼�������
  	class ExitSystem implements EventHandler<ActionEvent>
  	{
  		@Override
  		public void handle(ActionEvent e) {
  			exitSystem();
  		}
  	}
}



