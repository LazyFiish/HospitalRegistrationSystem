package application;
	
import java.net.URL;
import java.util.Vector;

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

public class Main extends Application {
	private Stage primaryStage;
	private LoginWindowController loginWindowController;
	private PatientWindowController patientWindowController;
	@Override
	public void start(Stage _primaryStage) {
		primaryStage = _primaryStage;
		showLoginWindow();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public void showLoginWindow() {
		try {
			URL location = getClass().getResource("loginWindow.fxml");
			FXMLLoader fxmlLoader = new FXMLLoader(location);
			fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
			Parent root = fxmlLoader.load();
			loginWindowController = fxmlLoader.getController();
			//设置事件处理
			LoginSystem loginHandler = new LoginSystem();
			loginWindowController.btn_login.setOnAction(loginHandler);
			ExitSystem exitHandler = new ExitSystem();
			loginWindowController.btn_exit.setOnAction(exitHandler);
			
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setTitle("登录界面");
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void showPatientWindow(String userID) {
		try {
			URL location = getClass().getResource("patientWindow.fxml");
			FXMLLoader fxmlLoader = new FXMLLoader(location);
			fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
			Parent root = fxmlLoader.load();
			patientWindowController = fxmlLoader.getController();
			patientWindowController.init(userID);
			
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setMinHeight(450);
			primaryStage.setMinWidth(600);
			primaryStage.setScene(scene);
			primaryStage.setTitle("挂号界面");
			patientWindowController.combox_ksmc.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	//登录事件处理
	class LoginSystem implements EventHandler<ActionEvent>{
		@Override
		public void handle(ActionEvent e) {
			//System.out.println("用户名："+controller.tf_loginID.getText()+" 密码："+controller.pf_passwd.getText());
			loginSystem();
		}
	}
	//退出事件处理
	class ExitSystem implements EventHandler<ActionEvent>{
		@Override
		public void handle(ActionEvent e) {
			exitSystem();
		}
	}
	
	//登录
	public void loginSystem() {
		Vector<String[]> userInfo;
		
		//获取用户登录信息
		int userClass = loginWindowController.rb_patient.isSelected()? 1 : 2;
		String userID = loginWindowController.tf_loginID.getText();
		String password = loginWindowController.pf_passwd.getText();
		
		Helper helper = new Helper();
		if(userClass==1)
			userInfo=helper.getBrxx(userID);
		else
			userInfo=helper.getYsxx(userID);
		helper.finalize();
		if(!userInfo.isEmpty() && (userClass==1 && password.compareTo(userInfo.get(0)[2])==0) 
				|| (userClass==2 && password.compareTo(userInfo.get(0)[4])==0)) {//登录成功
			if(userClass==1)
				showPatientWindow(userID);			//病人用户
			else
				System.out.println("医生登录");		//医生用户
		}
		else {//登录失败
			showAlert(Alert.AlertType.ERROR,primaryStage,"登录失败","用户名或密码错误!");
		}
	}
	
	//退出
	public void exitSystem() {
		System.out.println("退出系统");
		primaryStage.close();
	}
	
	//输出提示信息
    public static void showAlert(Alert.AlertType alertType, Window owner, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(owner);
        alert.show();
    }
}



