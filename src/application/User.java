package application;

public class User {
	public String userID;//用户id
	public int userClass;//用户类别
	
	protected User(String _userID,int _userClass) {
		userID=_userID;
		userClass=_userClass;
	}
}
