package application;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class DoctorWindowController {
	@FXML
	public TextField tf_beginTime;
	public TextField tf_endTime;
	public Tab tab_patient;
	public Tab tab_income;
	public TableView<Ghxx> tableView_patient;
	public TableView<IncomeInfo> tableView_income;
	public TableColumn<Ghxx,String> pait_ghbh;
	public TableColumn<Ghxx,String> pait_brmc;
	public TableColumn<Ghxx,String> pait_ghrq;
	public TableColumn<Ghxx,String> pait_hzlb;
	public TableColumn<IncomeInfo,String> doct_ksmc;
	public TableColumn<IncomeInfo,String> doct_ysbh;
	public TableColumn<IncomeInfo,String> doct_ysmc;
	public TableColumn<IncomeInfo,String> doct_hzlb;
	public TableColumn<IncomeInfo,String> doct_ghrc;
	public TableColumn<IncomeInfo,String> doct_srhj;
	
	final ObservableList<Ghxx> dataGhxx = FXCollections.observableArrayList();
	final ObservableList<IncomeInfo> dataIncomeInfo = FXCollections.observableArrayList();
	
	public void init()
	{	
		pait_ghbh.setCellValueFactory(new PropertyValueFactory<Ghxx,String>("ghbh"));
		pait_brmc.setCellValueFactory(new PropertyValueFactory<Ghxx,String>("brmc"));
		pait_ghrq.setCellValueFactory(new PropertyValueFactory<Ghxx,String>("ghrq"));
		pait_hzlb.setCellValueFactory(new PropertyValueFactory<Ghxx,String>("hzlb"));
		
		doct_ksmc.setCellValueFactory(new PropertyValueFactory<IncomeInfo,String>("ksmc"));
		doct_ysbh.setCellValueFactory(new PropertyValueFactory<IncomeInfo,String>("ysbh"));
		doct_ysmc.setCellValueFactory(new PropertyValueFactory<IncomeInfo,String>("ysxm"));
		doct_hzlb.setCellValueFactory(new PropertyValueFactory<IncomeInfo,String>("hzlb"));
		doct_ghrc.setCellValueFactory(new PropertyValueFactory<IncomeInfo,String>("ghrc"));
		doct_srhj.setCellValueFactory(new PropertyValueFactory<IncomeInfo,String>("srhj"));
		
		tableView_patient.setEditable(false);
		tableView_income.setEditable(false);
		
		tableView_patient.setItems(dataGhxx);
		tableView_income.setItems(dataIncomeInfo);
		
		tf_beginTime.textProperty().addListener(new ChangeListener<String>(){
			@Override
	        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				showIncomeInfo();
	        }
		});
		tf_endTime.textProperty().addListener(new ChangeListener<String>(){
			@Override
	        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				showIncomeInfo();
	        }
		});
		
		showGhxx();
		showIncomeInfo();
	}
	
	public void exit()
	{
		((Stage)tableView_patient.getScene().getWindow()).close();
	}
	
	public void showGhxx()
	{
		Vector<String[]> ghxx;
		Date date=new Date();
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String beginTime,endTime;
		
		beginTime=String.format("%s 00:00:00",format.format(date).split(" ")[0]);
		endTime=format.format(date);
		ghxx=Helper.getTodayGhxx(DoctorUser.YSBH, beginTime, endTime);
		
		dataGhxx.clear();
		for(int i=0;i<ghxx.size();i++) {
			dataGhxx.add(new Ghxx(ghxx.get(i)[0],ghxx.get(i)[1],ghxx.get(i)[2],ghxx.get(i)[3].compareTo("0")==0? "普通号": "专家号"));
		}
	}
	
	public void showIncomeInfo()
	{
		Vector<String[]> incomeInfo;
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
		String beginTime=tf_beginTime.getText();
		String endTime=tf_endTime.getText();
		dateFormat.setLenient(false);
		
		try {
			dateFormat.parse(beginTime);
		}catch(ParseException e){
			System.out.println("beginTime:日期格式不合法");
			beginTime="";
		}
		try {
			dateFormat.parse(endTime);
		}
		catch(ParseException e){
			System.out.println("endTime:日期格式不合法");
			endTime="";
		}
		
		System.out.println("beginTime:"+beginTime+" endTime:"+endTime);
		incomeInfo=Helper.getIncomeInfo(beginTime,endTime);
		
		dataIncomeInfo.clear();
		for(int i=0;i<incomeInfo.size();i++) {
			dataIncomeInfo.add(new IncomeInfo(incomeInfo.get(i)[0],incomeInfo.get(i)[1],incomeInfo.get(i)[2],
					(incomeInfo.get(i)[3].compareTo("0")==0? "普通号": "专家号"),incomeInfo.get(i)[4],incomeInfo.get(i)[5]));
		}
	}
	
	public class Ghxx {
	    private final SimpleStringProperty ghbh;
	    private final SimpleStringProperty brmc;
	    private final SimpleStringProperty ghrq;
	    private final SimpleStringProperty hzlb;
	 
	    private Ghxx(String ghbh,String brmc,String ghrq,String hzlb) {
	        this.ghbh = new SimpleStringProperty(ghbh);
	        this.brmc = new SimpleStringProperty(brmc);
	        this.ghrq = new SimpleStringProperty(ghrq);
	        this.hzlb = new SimpleStringProperty(hzlb);
	    }
	 
	    public String getGhbh() {
	        return this.ghbh.get();
	    }
	    public void setGhbh(String ghbh) {
	    	this.ghbh.set(ghbh);
	    }
	        
	    public String getBrmc() {
	    	return this.brmc.get();
	    }
	    public void setBrmc(String brmc) {
	    	this.brmc.set(brmc);
	    }
	    
	    public String getGhrq() {
	    	return this.ghrq.get();
	    }
	    public void setGhrq(String ghrq) {
	    	this.ghrq.set(ghrq);
	    }
	    
	    public String getHzlb() {
	    	return this.hzlb.get();
	    }
	    public void setHzlb(String hzlb) {
	    	this.hzlb.set(hzlb);
	    }   
	}
	
	public class IncomeInfo
	{
		private final SimpleStringProperty ksmc;
		private final SimpleStringProperty ysbh;
		private final SimpleStringProperty ysxm;
		private final SimpleStringProperty hzlb;
		private final SimpleStringProperty ghrc;
		private final SimpleStringProperty srhj;
		
		private IncomeInfo(String ksmc,String ysbh,String ysxm,String hzlb,String ghrc,String srhj)
		{
			this.ksmc=new SimpleStringProperty(ksmc);
			this.ysbh=new SimpleStringProperty(ysbh);
			this.ysxm=new SimpleStringProperty(ysxm);
			this.hzlb=new SimpleStringProperty(hzlb);
			this.ghrc=new SimpleStringProperty(ghrc);
			this.srhj=new SimpleStringProperty(srhj);
		}
		
		public String getKsmc()
		{
			return this.ksmc.get();
		}
		public void setKsmc(String ksmc)
		{
			this.ksmc.set(ksmc);
		}
		
		public String getYsbh()
		{
			return this.ysbh.get();
		}
		public void setYsbh(String ysbh)
		{
			this.ysbh.set(ysbh);
		}
		
		public String getYsxm()
		{
			return this.ysxm.get();
		}
		public void setYsxm(String ysxm)
		{
			this.ysxm.set(ysxm);
		}
		
		public String getHzlb()
		{
			return this.hzlb.get();
		}
		public void setHzlb(String hzlb)
		{
			this.hzlb.set(hzlb);
		}
		
		public String getGhrc()
		{
			return this.ghrc.get();
		}
		public void setGhrc(String ghrc)
		{
			this.ghrc.set(ghrc);
		}
		
		public String getSrhj()
		{
			return this.srhj.get();
		}
		public void setSrhj(String srhj)
		{
			this.srhj.set(srhj);
		}
	}
}


