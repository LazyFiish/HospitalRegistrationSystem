package application;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

class PatientUser
{
	static public String BRBH;
	static public String BRMC;
	static public String DLKL;
	static public String YCJE;
	
	static public boolean login()
	{
		boolean isSuccess=false;
		Vector<String[]> userInfo;
		
		if(BRBH!=null && DLKL!=null){
			userInfo=Helper.getBrxx(BRBH);
			if(userInfo.size()!=0 && userInfo.firstElement()[2].compareTo(DLKL)==0) {
				isSuccess=true;
				
				BRMC=userInfo.firstElement()[1];
				YCJE=userInfo.firstElement()[3];
				updateLoginInfo();
			}
		}

		return isSuccess;
	}
	
	static private void updateLoginInfo() {
		Date time=new Date();
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Helper.updatePatientLoginTime(BRBH,dateFormat.format(time));
	}
}

class DoctorUser
{
	static public String YSBH;
	static public String KSBH;
	static public String YSMC;
	static public String PYZS;
	static public String DLKL;
	static public String SFZJ;
	
	static public boolean login()
	{
		boolean isSuccess=false;
		Vector<String[]> userInfo;
		
		if(YSBH!=null && DLKL!=null){
			userInfo=Helper.getYsxx(YSBH);
			if(userInfo.size()!=0 && userInfo.firstElement()[4].compareTo(DLKL)==0) {
				isSuccess=true;
				KSBH=userInfo.firstElement()[1];
				YSMC=userInfo.firstElement()[2];
				PYZS=userInfo.firstElement()[3];
				SFZJ=userInfo.firstElement()[5];
				
				updateLoginInfo();
			}
		}

		return isSuccess;
	}
	
	static private void updateLoginInfo() {
		Date time=new Date();
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Helper.updatePatientLoginTime(YSBH,dateFormat.format(time));
	}
}