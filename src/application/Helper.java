package application;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;


public class Helper {
	static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";	//JDBC������
	static final String DB_URL = "jdbc:mysql://localhost:3306/hospital?serverTimezone=UTC";//���ݿ�URL
	static final String USER = "root";	//�û���
	static final String PASS = "123456";//����
	
	static private Connection connection;
	static private Statement statement;
	
	/*
	 * ��������connectDatabase
	 * �������ܣ��������ݿ�
	 * ��ڲ�����
	 * ���ڲ�����
	 * ����ֵ��
	 */
	static public void connectDatabase() 
	{
		try {
			System.out.println("�����ݿ�");
			Class.forName(JDBC_DRIVER);
			connection = DriverManager.getConnection(DB_URL,USER,PASS);
			statement = connection.createStatement();
		} catch(Exception e) {
			System.out.println("���ݿ�����ʧ��");
			e.printStackTrace();
		}
	}
	
	/*
	 * ��������connectDatabase
	 * �������ܣ��Ͽ��������ݿ�
	 * ��ڲ�����
	 * ���ڲ�����
	 * ����ֵ��
	 */
	static protected void closeDatabase( )
	{
		try {
			System.out.println("�ر����ݿ�");
			statement.close();
			connection.close();
		} catch(SQLException e) {
			System.out.println("�ر����ݿ�����ʧ��...");
			e.printStackTrace();
		}
	}
	
	/*
	 * ��������updatePatientLoginTime
	 * �������ܣ����²��������¼ʱ��
	 * ��ڲ�����brbh�������˱�š�time���������¼����
	 * ���ڲ�����
	 * ����ֵ��
	 */
	static public void updatePatientLoginTime(String brbh,String time)
	{
		String query="update t_brxx set dlrq="+addQuo(time)+" WHERE brbh="+addQuo(brbh);
		try {
			statement.execute(query);
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * ��������updateDoctorLoginTime
	 * �������ܣ����²��������¼ʱ��
	 * ��ڲ�����ysbh����ҽ����š�time���������¼����
	 * ���ڲ�����
	 * ����ֵ��
	 */
	static public void updateDoctorLoginTime(String ysbh,String time)
	{
		String query="update t_ksys set dlrq="+addQuo(time)+" WHERE ysbh="+addQuo(ysbh);
		try {
			statement.execute(query);
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * 	�������ܣ���ȡ������Ϣ
	 * 	��ڲ�����ksbh�������ұ�ţ�Ϊ��ʱ��ȡ���п�����Ϣ��
	 * 	���ڲ�������
	 * 	����ֵ��������Ϣ���������ұ�š��������ơ�����ƴ�����ף�
	 */
	static public Vector<String[]> getKsxx(String ksbh) 
	{
		Vector<String[]> ksxx=new Vector<String[]>();
		
		String query="SELECT * FROM t_ksxx"+(ksbh.compareTo("")==0? "": " WHERE KSBH="+addQuo(ksbh));
		try {
			ResultSet ksxxSet = statement.executeQuery(query);
			while(ksxxSet.next()) {
				String[] tuple =  new String[3];
				tuple[0]=ksxxSet.getString("KSBH");
				tuple[1]=ksxxSet.getString("KSMC");
				tuple[2]=ksxxSet.getString("PYZS");
				ksxx.addElement(tuple);
			}
		} catch(SQLException e) {
			ksxx.clear();
			e.printStackTrace();
		}
		
		return ksxx;
	}
	
	/*
	 * �������ƣ�getTodayGhxx
	 * �������ܣ�ͨ��ҽ��Id��ȡ���˹Һ���Ϣ
	 * ��ڲ�����userId����ҽ����ID��beginDate����ָ����ʵ���ڡ�endDate����ָ��ֹ���ڣ�
	 * 	�����ֶ���Ϊ�մ�����Ϊȡ����ֵ�����ڸ�ʽ��yyyy-MM-dd
	 * ���ڲ�������
	 * ����ֵ�����첡�˹Һ���Ϣ
	 */
	static public Vector<String []> getTodayGhxx(String userId,String beginDate,String endDate)
	{
		Vector<String[]> ghxx=new Vector<String[]>();
		Date curDate=new Date();
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		String query="SELECT ghbh,brmc,rqsj,sfzj FROM t_ghxx,t_brxx,t_hzxx WHERE t_ghxx.brbh=t_brxx.brbh AND t_ghxx.hzbh=t_hzxx.hzbh AND "+
				(userId.compareTo("")==0? "1": "ysbh="+addQuo(userId))+" AND DATE(rqsj) BETWEEN "+
				addQuo(beginDate.compareTo("")==0? "1970-01-01 00:00:00": beginDate)+" AND "+
				addQuo(endDate.compareTo("")==0? dateFormat.format(curDate): endDate);
		try {
			ResultSet set=statement.executeQuery(query);
			while(set.next()) {
				String[] tuple=new String[4];
				tuple[0]=set.getString(1);
				tuple[1]=set.getString(2);
				tuple[2]=set.getString(3);
				tuple[3]=set.getString(4);
				ghxx.addElement(tuple);
			} 
		}catch(SQLException e) {
			ghxx.clear();
			e.printStackTrace();
		}

		return ghxx;
	}
	
	/*
	 * ��������getIncomeInfo
	 * �������ܣ���ȡҽ��������Ϣ
	 * ��ڲ�����beginDate��endDate
	 * ���ڲ�������
	 * ����ֵ��ҽ��������Ϣ
	 */
	static public Vector<String []> getIncomeInfo(String beginDate,String endDate)
	{
		Vector<String []> incomeInfo=new Vector<String[]>();
		Date curDate=new Date();
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
		
		String query="SELECT ksmc,t_ghxx.ysbh,ysmc,t_ksys.sfzj,COUNT(*),SUM(t_ghxx.ghfy) " + 
				"FROM t_ksxx,t_ksys,t_hzxx JOIN t_ghxx " + 
				"WHERE t_ghxx.YSBH=t_ksys.YSBH AND t_ghxx.HZBH=t_hzxx.HZBH AND t_hzxx.KSBH=t_ksxx.KSBH AND  t_ghxx.rqsj BETWEEN "+
				addQuo(beginDate.compareTo("")==0? "1970-01-01 00:00:00" :beginDate)+" AND "+
				addQuo(endDate.compareTo("")==0? dateFormat.format(curDate): endDate)+" GROUP BY t_ghxx.ysbh,sfzj";
		try {
			ResultSet set=statement.executeQuery(query);
			while(set.next()) {
				String[] tuple=new String[6];
				tuple[0]=set.getString(1);
				tuple[1]=set.getString(2);
				tuple[2]=set.getString(3);
				tuple[3]=set.getString(4);
				tuple[4]=set.getString(5);
				tuple[5]=set.getString(6);
				incomeInfo.addElement(tuple);
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		
		return incomeInfo;
	}
	
	
	/*
	 * �������ܣ���ȡҽ����Ϣ
	 * ��ڲ�����ysbh����ҽ����ţ��մ�ʱ��������ҽ����Ϣ
	 * ���ڲ�������
	 * ����ֵ��ҽ����Ϣ
	 */
	static public Vector<String[]> getYsxx(String ysbh)
	{
		Vector<String[]> ysxx=new Vector<String[]>();
		
		String query="SELECT * FROM t_ksys"+(ysbh.compareTo("")==0? "": " WHERE YSBH="+addQuo(ysbh));
		try {
			ResultSet set=statement.executeQuery(query);
			while(set.next()) {
				String[] tuple=new String[7];
				tuple[0]=set.getString("YSBH");
				tuple[1]=set.getString("KSBH");
				tuple[2]=set.getString("YSMC");
				tuple[3]=set.getString("PYZS");
				tuple[4]=set.getString("DLKL");
				tuple[5]=set.getString("SFZJ");
				tuple[6]=set.getString("DLRQ");
				ysxx.addElement(tuple);
			}
		}catch(SQLException e) {
			ysxx.clear();
			e.printStackTrace();
		}
		
		return ysxx;
	}
	
	/*
	 * �������ܣ���ȡ������Ϣ
	 * ��ڲ�����brbh�������˱�ţ��մ�ʱ�������в�����Ϣ
	 * ���ڲ�������
	 * ����ֵ��������Ϣ
	 */
	static public Vector<String[]> getBrxx(String brbh)
	{
		Vector<String[]> brxx=new Vector<String[]>();
		
		String query="SELECT * FROM t_brxx"+(brbh.compareTo("")==0? "": " WHERE BRBH="+addQuo(brbh));
		try {
			ResultSet set=statement.executeQuery(query);
			while(set.next()) {
				String[] tuple=new String[5];
				tuple[0]=set.getString("BRBH");
				tuple[1]=set.getString("BRMC");
				tuple[2]=set.getString("DLKL");
				tuple[3]=set.getString("YCJE");
				tuple[4]=set.getString("DLRQ");
				brxx.addElement(tuple);
			}
		}catch(SQLException e) {
			brxx.clear();
			e.printStackTrace();
		}
		
		return brxx;
	}
	
	/*
	 * �������ƣ�updatePatientMoney
	 * �������ܣ����²������
	 * ���������brbh�������˱�š�newMoney�����������
	 * �����������
	 * ����ֵ���ɹ�ʱ������true��ʧ��ʱ������false��
	 */
	static boolean updatePatientMoney(String brbh,float newMoney)
	{
		boolean isSuccess=false;
		String query="UPDATE t_brxx SET YCJE="+String.valueOf(newMoney)+" WHERE BRBH="+addQuo(brbh);
		try{
			if(statement.executeUpdate(query)==1)
				isSuccess=true;
		}catch(SQLException e) {
			e.printStackTrace();
		}
		
		return isSuccess;
	}
	
	/*
	 * �������ƣ�getHzxx
	 * �������ܣ���ȡ������Ϣ
	 * ���������hzbh�������ֱ��
	 * �����������
	 * ����ֵ��������Ϣ
	 */
	static public Vector<String[]> getHzxx(String hzbh) 
	{
		Vector<String[]> hzxx=new Vector<String[]>();
		
		String query="SELECT * FROM t_hzxx WHERE HZBH="+addQuo(hzbh);
		try {
			ResultSet set=statement.executeQuery(query);
			while(set.next()) {
				String[] tuple=new String[7];
				tuple[0]=set.getString("HZBH");
				tuple[1]=set.getString("HZMC");
				tuple[2]=set.getString("PYZS");
				tuple[3]=set.getString("KSBH");
				tuple[4]=set.getString("SFZJ");
				tuple[5]=set.getString("GHRS");
				tuple[6]=set.getString("GHFY");
				hzxx.addElement(tuple);
			}
		}catch(SQLException e) {
			hzxx.clear();
			e.printStackTrace();
		}
		
		return hzxx;
	}
	
	
	/*
	 * �������ܣ����ݿ��ұ�����������ȡ������Ϣ
	 * ��ڲ�����ksbh�������ұ�š�isExcept�����Ƿ�Ϊר�Һ�
	 * ���ڲ�������
	 * ����ֵ��������Ϣ
	 */
	static public String[] getMatchHzxx(String ksbh,boolean isExcept) 
	{
		String[] hzxx=null;
		
		String query="SELECT * FROM t_hzxx WHERE KSBH="+addQuo(ksbh)+" AND SFZJ="+isExcept;
		try {
			ResultSet set=statement.executeQuery(query);
			if(set.next()) {
				hzxx=new String[7];
				hzxx[0]=set.getString("HZBH");
				hzxx[1]=set.getString("HZMC");
				hzxx[2]=set.getString("PYZS");
				hzxx[3]=set.getString("KSBH");
				hzxx[4]=set.getString("SFZJ");
				hzxx[5]=set.getString("GHRS");
				hzxx[6]=set.getString("GHFY");
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		return hzxx;
	}
	
	/*
	 * �������ܣ�����Һ���Ϣ
	 * ��ڲ�����ghInfo�����Һ���Ϣ
	 * ���ڲ�������
	 * ����ֵ���Һ���Ϣ����ɹ����عҺű�ţ�ʧ�ܷ��ؿմ�
	 */
	static public String insertGhxx(Vector<String> ghInfo) 
	{
		String numberStr=null;
		
		//�����Ƿ�
		if(ghInfo.size() != 7)
			return null;
		
		//��ȡ��һ���ùҺű��
		numberStr=getNextNumber();
		
		//����
		String query="INSERT INTO t_ghxx (GHBH,HZBH,YSBH,BRBH,GHRC,THBZ,GHFY,RQSJ)"+" VALUES("
					+addQuo(numberStr)+","+addQuo(ghInfo.get(0))+","+addQuo(ghInfo.get(1))+","+addQuo(ghInfo.get(2))+","+addQuo(ghInfo.get(3))+","
					+addQuo(ghInfo.get(4))+","+addQuo(ghInfo.get(5))+","+addQuo(ghInfo.get(6))+")";
		try {
			if(statement.executeUpdate(query)!=1) {
				numberStr=null;
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return numberStr;
	}
	
	/*
	 * �������ƣ�getNextNumber
	 * �������ܣ���ȡ��һ���ùҺű��
	 * �����������
	 * �����������
	 * ����ֵ���ɹ�ʱ��������һ���õĹҺű�ţ�ʧ��ʱ�����ؿմ���
	 */
	static private String getNextNumber() 
	{
		String numberStr="";
		String query1="SELECT MAX(GHBH) FROM t_ghxx";
		try {
			ResultSet set=statement.executeQuery(query1);
			if(set.next()) {
				numberStr=set.getString(1);
				Integer number=Integer.valueOf(numberStr==null? "000000": numberStr);
				number++;
				numberStr=String.format("%06d", number);
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		return numberStr;
	}
	
	/*
	 * �������ƣ�getPeople
	 * �������ܣ���ȡĳ�ֺ��ֵ����ѹҺ��˴�
	 * ���������hzbh�������ֱ��
	 * �����������
	 * ����ֵ�����ص���ú����ѹҺ��˴�
	 */
	static int getPeople(String ghbh)
	{
		int num=0;
		String query="SELECT MAX(GHRC) FROM t_ghxx WHERE DATE(RQSJ)=CurDate() AND GHBH="+addQuo(ghbh);
		try {
			ResultSet set=statement.executeQuery(query);
			if(set.next())
				num=set.getInt(1);
			else
				num=0;
		}catch(SQLException e) {
			e.printStackTrace();
		}
		
		return num;
	}
	
	/*
	 * �������ƣ�addQuo
	 * �������ܣ���ָ���ִ���������
	 * �������������˫���ŵ��ִ�
	 * �����������
	 * ����ֵ������˫���ŵ��ִ�
	 */
	static private String addQuo(String str) 
	{
		return "\""+str+"\"";
	}
}
