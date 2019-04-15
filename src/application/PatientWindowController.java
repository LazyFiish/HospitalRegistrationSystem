package application;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Window;
import javafx.collections.*;
import java.util.regex.*;
import java.util.Date;
import java.text.SimpleDateFormat;


class UserPatient extends User{
	public String name;
	public String password;
	public String money;
	public String lastLogin; //mysql datetime存储格式：xxxx-xx-xx xx:xx:xx 例如：2019-04-06 00:00:00
	
	public UserPatient(String userID) {
		super(userID,1);
		getCompleteInfo(userID);
	}
	private void getCompleteInfo(String userID) {
		Vector<String[]> info;
		Helper helper=new Helper();
		info=helper.getBrxx(userID);
		helper.finalize();
		if(!info.isEmpty()) {
			name=info.get(0)[1];
			password=info.get(0)[2];
			money=info.get(0)[3];
			lastLogin=info.get(0)[4];
		}
	}
}

public class PatientWindowController {
	final String exceptNumber="专家号";
	final String nomalNumber="普通号";
	
	private UserPatient patient;
	
	@FXML
	public ComboBox<String> combox_ksmc;
	public ComboBox<String> combox_ysxm;
	public ComboBox<String> combox_hzlb;
	public TextField tf_hzmc;
	public TextField tf_cost;
	public TextField tf_change;
	public TextField tf_pay;
	public TextField tf_money;
	public TextField tf_ghhm;
	
	public void init(String userId) {
		patient=new UserPatient(userId);
		
		combox_hzlb.getItems().addAll(nomalNumber,exceptNumber);
		combox_hzlb.setValue(nomalNumber);
		
		combox_ksmc.setPromptText("xxx.../xxxxxx");
		KsmcFilterHandler handler = new KsmcFilterHandler();
		combox_ksmc.getEditor().setOnKeyReleased(handler);
		handler.handle(null);
		
		combox_ysxm.setPromptText("xxx.../xxxxxx");
		YsxmFilterHandler handler1 = new YsxmFilterHandler();
		combox_ysxm.getEditor().setOnKeyReleased(handler1);
		handler1.handle(null);
		
		tf_money.setText(String.valueOf(patient.money));
		tf_pay.setDisable(true);
	}
	
	public void updateShow() {
		String[] hzxx;
		String ksbh="";
		boolean isExcept=false;
		
		//获取输入的科室编号
		ksbh=getKsbh();
		
		//获取输入的号种类别
		isExcept=isExceptNumber();
		
		//获取号种名称及应缴金额
		Helper helper=new Helper();
		hzxx=helper.getMatchHzxx(ksbh, isExcept);
		helper.finalize();
		tf_hzmc.setText(hzxx[1]+"/"+hzxx[0]);
		tf_cost.setText(hzxx[6]+"(元)");
		tf_money.setText(String.valueOf(patient.money));
		
		//计算费用
		float cost=Float.parseFloat(hzxx[6]);
		float money=Float.parseFloat(patient.money);
		if(money>=cost) {
			//tf_change.setText(String.valueOf(money-cost));
			tf_change.setDisable(true);
			tf_pay.clear();
			tf_pay.setDisable(true);
		}
		else {
			tf_change.setDisable(false);
			tf_change.clear();
			tf_pay.setDisable(false);
		}
	}
	
	//科室名称过滤
	class KsmcFilterHandler implements EventHandler<KeyEvent>{
		@Override
		public void handle(KeyEvent e) {
			Helper helper=new Helper();
			Vector<String[]> ksxx=helper.getKsxx("");
			helper.finalize();
			
			String input=combox_ksmc.getEditor().getText();
			Pattern pattern=Pattern.compile(input, Pattern.CASE_INSENSITIVE);
			
			List<String> ksxxStr=new ArrayList<String>();
			for(int i=0;i<ksxx.size();i++) {
				String[] tmp=ksxx.get(i);
				if(pattern.matcher(tmp[2]).lookingAt())
					ksxxStr.add(tmp[1]+"/"+tmp[0]);
			}
			ObservableList<String> showList=FXCollections.observableArrayList(ksxxStr);
			combox_ksmc.hide();
			combox_ksmc.getItems().setAll(showList);
			combox_ksmc.show();
		}
	}
	
	//医生名称过滤
	class YsxmFilterHandler implements EventHandler<KeyEvent>{
		@Override
		public void handle(KeyEvent e) {
			Vector<String[]> ysxx=new Vector<String[]>();
			
			String ksbh=getKsbh();
			String input="(?:"+ksbh+")"+"{0,1}"+combox_ysxm.getEditor().getText();
			System.out.println(input);
			Pattern pattern=Pattern.compile(input,Pattern.CASE_INSENSITIVE);
			
			List<String> listYsxm=new ArrayList<String>();
			Helper helper=new Helper();
			ysxx=helper.getYsxx("");
			helper.finalize();
			for(int i=0;i<ysxx.size();i++) {
				String[] tmp=ysxx.get(i);
				if(pattern.matcher(tmp[1]+tmp[3]).lookingAt()) {
					listYsxm.add(tmp[2]+(tmp[5].compareTo("1")==0? "(专)" : "(普)")+"/"+tmp[0]);
				}
			}
			ObservableList<String> showList=FXCollections.observableArrayList(listYsxm);
			combox_ysxm.hide();
			combox_ysxm.getItems().setAll(showList);
			combox_ysxm.show();
		}
	}
	
	//挂号
	public void registered() {
		String numberStr="";
		
		String hzbh=getHzbh();
		String ksbh=getKsbh();
		String ysbh=getYsbh();
		String cost=getCost();
		String pay=getPay();
		float costF=Float.parseFloat(cost);
		float payF=pay.compareTo("")==0? 0: Float.parseFloat(pay);
		float moneyF=Float.parseFloat(patient.money);
		
		if(hzbh.compareTo("")==0 || ksbh.compareTo("")==0 || ysbh.compareTo("")==0){
			showAlert(Alert.AlertType.ERROR,null,"挂号失败","科室或号种信息不正确");
			return;
		}
		
		Helper helper=new Helper();
		
		Vector<String[]> hzxx,ksxx,ysxx;
		hzxx=helper.getHzxx(hzbh);
		ksxx=helper.getKsxx(ksbh);
		ysxx=helper.getYsxx(ysbh);
		
		if(hzxx.isEmpty() || ksxx.isEmpty() || ysxx.isEmpty()) {
			showAlert(Alert.AlertType.ERROR,null,"挂号失败","科室或号种信息不正确");
		}
		else if(ysxx.get(0)[5].compareTo("0")==0 && isExceptNumber()) {
			showAlert(Alert.AlertType.ERROR,null,"挂号失败","非专家医生不可挂专家号");
		}
		else {
			Date curDate=new Date();
			SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			int people=helper.getPeople(hzbh);
			
			Vector<String> vecStr=new Vector<String>();
			vecStr.add(hzbh);
			vecStr.add(ysbh);
			vecStr.add(patient.userID);
			vecStr.add(String.valueOf(people));
			vecStr.add("0");
			vecStr.add(cost);
			vecStr.add(dateFormat.format(curDate));
			vecStr.add(dateFormat.format(curDate));
			
			if(moneyF>=costF) {//使用预存款支付
				numberStr=helper.insertGhxx(vecStr);
				helper.updatePatientMoney(patient.userID, moneyF-costF);
				patient.money=String.valueOf(moneyF-costF);
			}
			else if(payF>=costF) {//使用现金支付
				numberStr=helper.insertGhxx(vecStr);
				tf_change.setText(String.valueOf(payF-costF));
			}
			else {//支付失败
				showAlert(Alert.AlertType.ERROR,null,"挂号失败","支付失败");
			}
		}
		
		tf_ghhm.setText(numberStr);
		updateShow();
		
		helper.finalize();
	}
	
	private String getKsbh() {
		String ksbh="";
		String inputKsxx=combox_ksmc.getEditor().getText();
		String[] strArray=inputKsxx.split("/");
		if(strArray.length>=2)
			ksbh=strArray[strArray.length-1];
		return ksbh;
	}
	
	private String getYsbh() {
		String ysbh="";
		String inputYsxx=combox_ysxm.getEditor().getText();
		String[] strArray=inputYsxx.split("/");
		if(strArray.length>=2)
			ysbh=strArray[strArray.length-1];
		return ysbh;
	}
	
	private String getHzbh() {
		String hzbh="";
		String inputYsxx=tf_hzmc.getText();
		String[] strArray=inputYsxx.split("/");
		if(strArray.length>=2)
			hzbh=strArray[strArray.length-1];
		return hzbh;
	}
	
	private String getCost() {
		String cost="";
		String inputCost=tf_cost.getText();
		int pos=0;
		while(pos<inputCost.length()&&inputCost.getBytes()[pos]>='0'&&inputCost.getBytes()[pos]<='9') pos++;
		cost=inputCost.substring(0,pos);
		return cost;
	}
	
	private String getPay() {
		String pay="";
		String inputPay=tf_pay.getText();
		int pos=0;
		while(pos<inputPay.length()&&inputPay.getBytes()[pos]>='0'&&inputPay.getBytes()[pos]<='9') pos++;
		pay=inputPay.substring(0,pos);
		return pay;
	}
	
	private boolean isExceptNumber() {
		return combox_hzlb.getValue().toString().compareTo(exceptNumber)==0;
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
