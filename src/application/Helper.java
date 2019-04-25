package application;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;


public class Helper {
	static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";	//JDBC驱动名
	static final String DB_URL = "jdbc:mysql://localhost:3306/hospital?serverTimezone=UTC";//数据库URL
	static final String USER = "root";	//用户名
	static final String PASS = "123456";//密码
	
	static private Connection connection;
	static private Statement statement;
	
	/*
	 * 方法名：connectDatabase
	 * 方法功能：连接数据库
	 * 入口参数：
	 * 出口参数：
	 * 返回值：
	 */
	static public void connectDatabase() 
	{
		try {
			System.out.println("打开数据库");
			Class.forName(JDBC_DRIVER);
			connection = DriverManager.getConnection(DB_URL,USER,PASS);
			statement = connection.createStatement();
		} catch(Exception e) {
			System.out.println("数据库连接失败");
			e.printStackTrace();
		}
	}
	
	/*
	 * 方法名：connectDatabase
	 * 方法功能：断开连接数据库
	 * 入口参数：
	 * 出口参数：
	 * 返回值：
	 */
	static protected void closeDatabase( )
	{
		try {
			System.out.println("关闭数据库");
			statement.close();
			connection.close();
		} catch(SQLException e) {
			System.out.println("关闭数据库连接失败...");
			e.printStackTrace();
		}
	}
	
	/*
	 * 方法名：updatePatientLoginTime
	 * 方法功能：更新病人最近登录时间
	 * 入口参数：brbh——病人编号、time——最近登录日期
	 * 出口参数：
	 * 返回值：
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
	 * 方法名：updateDoctorLoginTime
	 * 方法功能：更新病人最近登录时间
	 * 入口参数：ysbh——医生编号、time——最近登录日期
	 * 出口参数：
	 * 返回值：
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
	 * 	函数功能：获取科室信息
	 * 	入口参数：ksbh——科室编号（为空时获取所有科室信息）
	 * 	出口参数：无
	 * 	返回值：科室信息（包括科室编号、科室名称、科室拼音字首）
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
	 * 方法名称：getTodayGhxx
	 * 方法功能：通过医生Id获取病人挂号信息
	 * 入口参数：userId——医生的ID、beginDate——指定其实日期、endDate——指终止日期，
	 * 	三个字段若为空串则认为取任意值，日期格式：yyyy-MM-dd
	 * 出口参数：无
	 * 返回值：当天病人挂号信息
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
	 * 方法名：getIncomeInfo
	 * 方法功能：获取医生收入信息
	 * 入口参数：beginDate、endDate
	 * 出口参数：无
	 * 返回值：医生收入信息
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
	 * 方法功能：获取医生信息
	 * 入口参数：ysbh——医生编号，空串时返回所有医生信息
	 * 出口参数：无
	 * 返回值：医生信息
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
	 * 方法功能：获取病人信息
	 * 入口参数：brbh——病人编号，空串时返回所有病人信息
	 * 出口参数：无
	 * 返回值：病人信息
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
	 * 方法名称：updatePatientMoney
	 * 方法功能：更新病人余额
	 * 输入参数：brbh——病人编号、newMoney——病人余额
	 * 输出参数：无
	 * 返回值：成功时，返回true；失败时，返回false；
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
	 * 方法名称：getHzxx
	 * 方法功能：获取号种信息
	 * 输入参数：hzbh——号种编号
	 * 输出参数：无
	 * 返回值：号种信息
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
	 * 方法功能：根据科室编号与号种类别获取号种信息
	 * 入口参数：ksbh——科室编号、isExcept——是否为专家号
	 * 出口参数：无
	 * 返回值：号种信息
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
	 * 方法功能：插入挂号信息
	 * 入口参数：ghInfo——挂号信息
	 * 出口参数：无
	 * 返回值：挂号信息插入成功返回挂号编号，失败返回空串
	 */
	static public String insertGhxx(Vector<String> ghInfo) 
	{
		String numberStr=null;
		
		//参数非法
		if(ghInfo.size() != 7)
			return null;
		
		//获取下一可用挂号编号
		numberStr=getNextNumber();
		
		//插入
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
	 * 方法名称：getNextNumber
	 * 方法功能：获取下一可用挂号编号
	 * 输入参数：无
	 * 输出参数：无
	 * 返回值：成功时，返回下一可用的挂号编号；失败时，返回空串。
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
	 * 方法名称：getPeople
	 * 方法功能：获取某种号种当天已挂号人次
	 * 输入参数：hzbh——号种编号
	 * 输出参数：无
	 * 返回值：返回当天该号种已挂号人次
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
	 * 方法名称：addQuo
	 * 方法功能：给指定字串加上引号
	 * 输入参数：待加双引号的字串
	 * 输出参数：无
	 * 返回值：加上双引号的字串
	 */
	static private String addQuo(String str) 
	{
		return "\""+str+"\"";
	}
}
