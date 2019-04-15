package application;

import java.sql.*;
import java.util.Vector;


public class Helper {
	static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";	//JDBC驱动名
	static final String DB_URL = "jdbc:mysql://localhost:3306/hospital?serverTimezone=UTC";//数据库URL
	static final String USER = "root";	//用户名
	static final String PASS = "123456";//用户登录密码
	
	private Connection connection;
	private Statement statement;
	
	public Helper() 
	{
		//注册JDBC驱动
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
	
	protected void finalize( )
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
	 * 	函数功能：获取科室信息
	 * 	入口参数：ksbh——科室编号（为空时获取所有科室信息）
	 * 	出口参数：无
	 * 	返回值：科室信息（包括科室编号、科室名称、科室拼音字首）
	 */
	public Vector<String[]> getKsxx(String ksbh) 
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
	 * 方法功能：获取医生信息
	 * 入口参数：ysbh——医生编号，空串时返回所有医生信息
	 * 出口参数：无
	 * 返回值：医生信息
	 */
	public Vector<String[]> getYsxx(String ysbh)
	{
		Vector<String[]> ysxx=new Vector<String[]>();
		
		String query="SELECT * FROM t_ksys"+(ysbh.compareTo("")==0? "": " WHERE YSBH="+addQuo(ysbh));
		try {
			ResultSet set=statement.executeQuery(query);
			if(set.next()) {
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
	public Vector<String[]> getBrxx(String brbh)
	{
		Vector<String[]> brxx=new Vector<String[]>();
		
		String query="SELECT * FROM t_brxx"+(brbh.compareTo("")==0? "": " WHERE BRBH="+addQuo(brbh));
		try {
			ResultSet set=statement.executeQuery(query);
			if(set.next()) {
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
	 * 方法名称：updatePatientInfo
	 * 方法功能：更新病人余额
	 * 输入参数：brbh——病人编号、newMoney——病人余额
	 * 输出参数：无
	 * 返回值：成功时，返回true；失败时，返回false；
	 */
	boolean updatePatientMoney(String brbh,float newMoney)
	{
		boolean isSuccess=false;
		String query="UPDATE t_brxx SET YCJE="+String.valueOf(newMoney)+" WHERE BRBH="+addQuo(brbh);
		System.out.println(query);
		
		try{
			if(statement.executeUpdate(query)==1)
				isSuccess=true;
		}catch(SQLException e) {
			e.printStackTrace();
		}
		
		return isSuccess;
	}
	
	/*
	 * 方法功能：获取号种信息
	 * 输入参数：hzbh——号种编号
	 * 输出参数：无
	 * 返回值：号种信息
	 */
	public Vector<String[]> getHzxx(String hzbh) 
	{
		Vector<String[]> hzxx=new Vector<String[]>();
		
		String query="SELECT * FROM t_hzxx WHERE HZBH="+addQuo(hzbh);
		try {
			ResultSet set=statement.executeQuery(query);
			if(set.next()) {
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
	public String[] getMatchHzxx(String ksbh,boolean isExcept) 
	{
		String[] hzxx=new String[7];
		
		String query="SELECT * FROM t_hzxx WHERE KSBH="+addQuo(ksbh)+" AND SFZJ="+isExcept;
		System.out.println("Query: 0"+query);
		try {
			ResultSet set=statement.executeQuery(query);
			if(set.next()) {
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
	 * 输入参数：挂号信息所有字段
	 * 输出参数：无
	 * 返回值：挂号信息插入成功返回挂号编号，失败返回空串
	 */
	public String insertGhxx(Vector<String> ghInfo) 
	{
		String numberStr;
		
		//参数非法
		if(ghInfo.size() != 8)
			return "";
		
		//获取下一可用挂号编号
		numberStr=getNextNumber();
		
		//插入
		String query="INSERT INTO t_ghxx (GHBH,HZBH,YSBH,BRBH,GHRC,THBZ,GHFY,RQSJ,KBSJ)"+" VALUES("
					+addQuo(numberStr)+","+addQuo(ghInfo.get(0))+","+addQuo(ghInfo.get(1))+","+addQuo(ghInfo.get(2))+","+addQuo(ghInfo.get(3))+","
					+addQuo(ghInfo.get(4))+","+addQuo(ghInfo.get(5))+","+addQuo(ghInfo.get(6))+","+addQuo(ghInfo.get(7))+")";
		System.out.println(query);
		try {
			if(statement.executeUpdate(query)==1) {//成功
				
			}
			else{
				numberStr="";
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
	private String getNextNumber() 
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
	 * 方法名称：
	 * 方法功能：获取某种号种当天已挂号人次
	 * 输入参数：hzbh——号种编号
	 * 输出参数：无
	 * 返回值：成功时，返回指定号种当天已挂号人次；失败时，返回-1
	 */
	int getPeople(String ghbh)
	{
		int num=-1;
		String query="SELECT MAX(GHRC) FROM t_ghxx WHERE DATE(RQSJ)=CurDate() AND GHBH="+addQuo(ghbh);
		System.out.println(query);
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
	private String addQuo(String str) 
	{
		return "\""+str+"\"";
	}
}
