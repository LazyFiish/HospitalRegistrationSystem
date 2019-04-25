package application;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.collections.*;
import java.util.regex.*;
import java.util.Date;
import java.text.SimpleDateFormat;

public class PatientWindowController {
	final String exceptNumber="ר�Һ�";
	final String nomalNumber="��ͨ��";
	
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
	
	//��ʼ��
	public void init()
	{	
		combox_hzlb.getItems().addAll(nomalNumber,exceptNumber);
		combox_hzlb.setValue(nomalNumber);
		
		combox_ksmc.setPromptText("xxx.../xxxxxx");
		combox_ksmc.getEditor().textProperty().addListener(new FilterKsmcListener());
		
		combox_ysxm.setPromptText("xxx.../xxxxxx");
		combox_ysxm.getEditor().textProperty().addListener(new FilterYsmcListener());
		
		tf_pay.textProperty().addListener(new MyChangeListener());
		
		tf_money.setText(String.valueOf(PatientUser.YCJE));
		tf_pay.setDisable(true);
		tf_change.setDisable(true);
	}
	
	//���º�����Ϣ
	public void updateShow() 
	{
		String[] hzxx;
		String ksbh=null;
		boolean isExcept=false;
		float cost,money;
		
		ksbh=getKsbh();
		isExcept=isExceptNumber();
		hzxx=Helper.getMatchHzxx(ksbh, isExcept);
		if(hzxx!=null){
			cost=Float.parseFloat(hzxx[6]);
			money=Float.parseFloat(PatientUser.YCJE);
			
			tf_hzmc.setText(hzxx[1]+"/"+hzxx[0]);
			tf_cost.setText(hzxx[6]+"(Ԫ)");
			tf_money.setText(String.valueOf(PatientUser.YCJE));
			
			if(money>=cost) {
				tf_change.setDisable(true);
				tf_pay.setDisable(true);
			}
			else {
				tf_change.setDisable(false);
				tf_pay.setDisable(false);
			}
		}
	}
	
	//�Һ�
	public void registered() 
	{
		String numberStr="";
		String hzbh=getHzbh();
		String ksbh=getKsbh();
		String ysbh=getYsbh();
		String cost=getCost();
		String pay=getPay();
		float costF=Float.parseFloat(cost);
		float payF=pay.compareTo("")==0? 0: Float.parseFloat(pay);
		float moneyF=Float.parseFloat(PatientUser.YCJE);
		Date curDate=new Date();
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Vector<String[]> hzxx,ksxx,ysxx;
		Vector<String> rgstInfo=new Vector<String>();
		
		hzxx=Helper.getHzxx(hzbh);
		ksxx=Helper.getKsxx(ksbh);
		ysxx=Helper.getYsxx(ysbh);
		int people=Helper.getPeople(hzbh);
		
		if(hzbh.compareTo("")==0 || ksbh.compareTo("")==0 || ysbh.compareTo("")==0){
			showAlert(Alert.AlertType.ERROR,null,"�Һ�ʧ��","���һ������Ϣ����ȷ");
		}
		else if(hzxx.isEmpty() || ksxx.isEmpty() || ysxx.isEmpty()) {
			showAlert(Alert.AlertType.ERROR,null,"�Һ�ʧ��","���һ������Ϣ����ȷ");
		}
		else if(ysxx.get(0)[5].compareTo("0")==0 && isExceptNumber()) {
			showAlert(Alert.AlertType.ERROR,null,"�Һ�ʧ��","��ר��ҽ�����ɹ�ר�Һ�");
		}
		else if(people==Integer.valueOf(hzxx.get(0)[5])) {
			showAlert(Alert.AlertType.ERROR,null,"�Һ�ʧ��","�Һ������ﵽ����");
		}
		else {
			rgstInfo.add(hzbh);
			rgstInfo.add(ysbh);
			rgstInfo.add(PatientUser.BRBH);
			rgstInfo.add(String.valueOf(people+1));
			rgstInfo.add("0");
			rgstInfo.add(cost);
			rgstInfo.add(dateFormat.format(curDate));
				
			if(moneyF>=costF) {			//Ԥ���֧��
				numberStr=Helper.insertGhxx(rgstInfo);
				showAlert(Alert.AlertType.INFORMATION,null,"�Һųɹ�","�Һź��룺"+(numberStr==null? "": numberStr));
				Helper.updatePatientMoney(PatientUser.BRBH, moneyF-costF);
				PatientUser.YCJE=String.valueOf(moneyF-costF);
			}
			else if(payF>=costF) {		//�ֽ�֧��
				numberStr=Helper.insertGhxx(rgstInfo);
				showAlert(Alert.AlertType.INFORMATION,null,"�Һųɹ�","�Һź��룺"+(numberStr==null? "": numberStr));
			}
			else {						//֧��ʧ��
				showAlert(Alert.AlertType.ERROR,null,"�Һ�ʧ��","֧��ʧ��");
			}
		}
		
		tf_ghhm.setText(numberStr==null? "": numberStr);
		updateShow();
	}
	
	public void clear()
	{
		combox_ksmc.getEditor().clear();
		combox_ysxm.getEditor().clear();
		tf_hzmc.clear();
		tf_cost.clear();
		tf_change.clear();
		tf_pay.clear();
		tf_ghhm.clear();
	}
	
	public void exit()
	{
		Stage stage=(Stage)combox_ksmc.getScene().getWindow();
		stage.close();
	}
	
	public void filterKsmc()
	{
		List<String> ksxxStr=new ArrayList<String>();
		String input=combox_ksmc.getEditor().getText();
		Vector<String[]> ksxx=Helper.getKsxx("");

		if(!input.contains("/")) {
			Pattern pattern=Pattern.compile(input, Pattern.CASE_INSENSITIVE);
			for(int i=0;i<ksxx.size();i++) {
				String[] tmp=ksxx.get(i);
				if(pattern.matcher(tmp[2]).lookingAt())
					ksxxStr.add(tmp[1]+"/"+tmp[0]);
			}
			combox_ksmc.getItems().setAll(FXCollections.observableArrayList(ksxxStr));
		}		
	}
	
	public void filterYsmc()
	{
		Vector<String[]> ysxx=new Vector<String[]>();
		List<String> listYsxm=new ArrayList<String>();
		String ksbh=getKsbh();
		String ysxm=getYsxm();
		String input=ksbh+ysxm;
		
		if(!input.contains("/")){
			Pattern pattern=Pattern.compile(input,Pattern.CASE_INSENSITIVE);
			ysxx=Helper.getYsxx("");
			for(int i=0;i<ysxx.size();i++) {
				String[] tmp=ysxx.get(i);
				if(pattern.matcher(tmp[1]+tmp[3]).lookingAt()) {
					listYsxm.add(tmp[2]+(tmp[5].compareTo("1")==0? "(ר)" : "(��)")+"/"+tmp[0]);
				}
			}
			combox_ysxm.getItems().setAll(FXCollections.observableArrayList(listYsxm));
		}
	}
	
	class FilterKsmcListener implements ChangeListener<String> {
		@Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
			filterKsmc();
			combox_ksmc.hide();
			combox_ksmc.show();
			
        }
	}
	
	class FilterYsmcListener implements ChangeListener<String> {
		@Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
			filterYsmc();
			combox_ysxm.hide();
			combox_ysxm.show();
        }
	}
	
	class MyChangeListener implements InvalidationListener
	{
		@Override
		public void invalidated(Observable ov)
		{
			float pay=getPay().compareTo("")==0? 0: Float.parseFloat(getPay());
			float cost=Float.parseFloat(getCost());
			tf_change.setText(String.valueOf(pay-cost));
		}
	}
	
	private String getKsbh() 
	{
		String ksbh="";
		String inputKsxx=combox_ksmc.getEditor().getText();
		String[] strArray=inputKsxx.split("/");
		if(strArray.length>=2)
			ksbh=strArray[strArray.length-1];
		return ksbh;
	}
	
	private String getYsbh() 
	{
		String ysbh="";
		String inputYsxx=combox_ysxm.getEditor().getText();
		String[] strArray=inputYsxx.split("/");
		if(strArray.length>=2)
			ysbh=strArray[strArray.length-1];
		return ysbh;
	}
	
	private String getHzbh() 
	{
		String hzbh="";
		String inputYsxx=tf_hzmc.getText();
		String[] strArray=inputYsxx.split("/");
		if(strArray.length>=2)
			hzbh=strArray[strArray.length-1];
		return hzbh;
	}
	
	private String getCost() 
	{
		String cost="";
		String inputCost=tf_cost.getText();
		int pos=0;
		while(pos<inputCost.length()&&inputCost.getBytes()[pos]>='0'&&inputCost.getBytes()[pos]<='9') pos++;
		cost=inputCost.substring(0,pos);
		return cost;
	}
	
	private String getPay() 
	{
		String pay="";
		String inputPay=tf_pay.getText();
		int pos=0;
		while(pos<inputPay.length()&&inputPay.getBytes()[pos]>='0'&&inputPay.getBytes()[pos]<='9') pos++;
		pay=inputPay.substring(0,pos);
		return pay;
	}
	
	private boolean isExceptNumber() 
	{
		return combox_hzlb.getValue().toString().compareTo(exceptNumber)==0;
	}
	
	private String getYsxm() 
	{
		return combox_ysxm.getEditor().getText();
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
}
