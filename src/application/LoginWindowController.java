package application;

import javafx.fxml.FXML;

import javafx.scene.control.Button;

import javafx.scene.control.TextField;

import javafx.scene.control.ToggleGroup;

import javafx.scene.control.RadioButton;

import javafx.scene.control.PasswordField;


public class LoginWindowController {
	@FXML
	public TextField tf_loginID;
	@FXML
	public PasswordField tf_passwd;
	@FXML
	public RadioButton rb_patient;
	@FXML
	private ToggleGroup g;
	@FXML
	public RadioButton rt_doctors;
	@FXML
	public Button btn_login;
	@FXML
	public Button btn_exit;
}
