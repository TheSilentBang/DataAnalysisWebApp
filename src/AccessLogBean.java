package uic.edu.ids517.s17g310;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class AccessLogBean {

	private LoginBean loginBean;
	private ResultSet resultSet;

	private String message;
	private String currUser;
	private String password;
	private String ipAddr;
	private String sessionID;
	private Date loginT;
	private String loginTStr;
	private Date logoutT;
	private String logoutTStr;
	private boolean printLog;
	private boolean printMessage;
	private List <String> tableList;
	private int lastRowID;
	private String dbSchema;

	private static final String USERNAME = "f16gxxx";
	private static final String PASSWORD = "f16gxxxR02S";

	public AccessLogBean() {
		// TODO Auto-generated constructor stub
		tableList = new ArrayList < String > ();
		printMessage=false;
		printLog=false; 
	}

	public boolean createLog()
	{
		try
		{
			currUser = loginBean.getUserName();
			password=loginBean.getPassword();
			dbSchema=loginBean.getDbSchema();
			FacesContext facesContext = FacesContext.getCurrentInstance();
			HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(false);
			String sessionId = session.getId(); 
			boolean conn = connectLog();
			HttpServletRequest req =
					(HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
			String ipAddr = req.getHeader("X-FORWARDED-FOR");
			if (ipAddr == null) 
				ipAddr = req.getRemoteAddr();
			String sqlQuery = "Create table if not exists "+dbSchema+".s17g310_AccessLog (ID INT(6)" +
					" NOT NULL AUTO_INCREMENT, Username char(50) not null, IPAddress char(50), " +
					"LoginTime timestamp null, LogoutTime timestamp null, SessionID char(50), PRIMARY KEY (ID)) " +
					"ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;";
			if (conn) 
			{
				resultSet = loginBean.getTables();
				if (resultSet != null) 
				{
					while (resultSet.next()) 
					{
						String tablenm = resultSet.getString("TABLE_NAME");
						tableList.add(tablenm);
					}
					if (tableList.contains("s17g310_AccessLog")) 
					{
						String insertQuery = "Insert into "+dbSchema+".s17g310_AccessLog (Username," +
								"IPAddress,LoginTime,SessionID)" +
								"values ( ?,?,?,?)";
						int insertRow = loginBean.enterLog(insertQuery, currUser,
								ipAddr, sessionId);
						if (insertRow < 0)
						{
							message = loginBean.getMessage();
							connect2();
							return false; 
						}
						else
						{
							lastRowID=getLastRowID();
							connect2();
							return true;
						}
					} 
					else
					{
						int rows = loginBean.processUpdate(sqlQuery);
						if (rows < 0) 
						{
							message = loginBean.getMessage();
							connect2();
							return false;
						}
						else
						{
							String insertQuery = "Insert into "+dbSchema+".s17g310_AccessLog " +
									"(Username,IPAddress,LoginTime,SessionID)" +
									"values ( ?,?,?,?)";
							int rowsInserted = loginBean.enterLog(insertQuery,
									currUser,ipAddr, sessionId);
							if (rowsInserted < 0) 
							{
								message = loginBean.getMessage();
								connect2();
								return false;
							}
							else
							{
								lastRowID=getLastRowID();
								connect2();
								return true;
							}
						}
					}
				} 
				else 
				{
					message = loginBean.getMessage();
					connect2();
					return false;
				}
			}
			else
			{
				connect2();
				return false;
			}
		} catch (Exception e) {
			message = e.getMessage();
			connect2();
			return false;
		}
	}


	public boolean connectLog() {
		loginBean.setUserName(USERNAME);
		loginBean.setPassword(PASSWORD);
		loginBean.setDbSchema("f16gxxx");
		if(loginBean.connect().equalsIgnoreCase("SUCCESS"))
			return true;
		else
			return false;
	}

	public void connect2() 
	{
		loginBean.setUserName(currUser);
		loginBean.setPassword(password);
		loginBean.setDbSchema(dbSchema);
		loginBean.connect();
	}

	public String getLog()
	{
		try {
			if(connectLog())
			{
				int rowNumber = 0;
				String selectQuery="Select count(*)  cnt from s17g310_AccessLog where userName = '" + 
						currUser + "' and ID <> " + lastRowID + " order by logintime desc limit 1";
				resultSet=loginBean.processSelect(selectQuery);
				if(resultSet!=null)
				{
					while(resultSet.next())
					{
						rowNumber=resultSet.getInt("cnt");
					}

					if(rowNumber == 0)
					{
						message = "No data available in the Access Log";
						printMessage = true;
					}
					else
					{
						String sqlQuery = "select Username,ipAddress,LoginTime,LogoutTime, sessionID from "
								+"f16gxxx.s17g310_AccessLog where Username = '" + currUser + "' and ID <> " +
								lastRowID + " order by LoginTime desc limit 1";
						resultSet = loginBean.processSelect(sqlQuery);
						if (resultSet != null)
						{
							while (resultSet.next())
							{
								currUser = resultSet.getString("Username");
								loginTStr = resultSet.getTimestamp("LoginTime").toString();
								if(loginTStr == null)
								{
									message = "No data available in the Access Log";
									printMessage = true;
									printLog = false;
								}
								else
								{
									sessionID = resultSet.getString("sessionID");
									ipAddr = resultSet.getString("ipAddress");
									try
									{
										logoutTStr = resultSet.getTimestamp("LogoutTime").toString();
									} catch (Exception e) {
										logoutTStr = "Previous Logout Unsuccessful";
									}
								}
								printLog=true;
							}
						}
						else
						{
							message=loginBean.getMessage();
							printMessage=true;
						}
					}
				}
				else
				{
					message=loginBean.getMessage();
					printMessage=true;
				}
			}
			else
			{
				message = loginBean.getMessage();
				printMessage = true;
			}
			connect2();
			return "SUCCESS";
		} catch (SQLException e) {
			message=e.getMessage();
			printMessage=true;
			connect2();
			return "FAIL";
		} catch(Exception e) {
			connect2();
			return "FAIL";
		}
	}

    public boolean logout()
    {
    	try
    	{
    		connectLog();
    		int rowNum = getLastRowID();
    		if(rowNum==0)
    		{
    			message = "No data available";
	         	printMessage=true;
	         	connect2();
	         	return false;
    		}
    		else
    		{
    			String updLogoutQuery="Update "+dbSchema+".s17g310_AccessLog set LogoutTime= ? where " +
    					"Username = ? and ID = ? and LoginTime is not null order " +
    					"by LoginTime desc limit 1";
    			loginBean.updateLog(updLogoutQuery, currUser, lastRowID);
    		}
    		return true;
    	}
    	catch(Exception e)
    	{
    		message=e.getMessage();
    		return false;
    	}
    }

	public LoginBean getLoginBean() {
		return loginBean;
	}

	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public ResultSet getResultSet() {
		return resultSet;
	}

	public void setResultSet(ResultSet resultSet) {
		this.resultSet = resultSet;
	}

	public List<String> getTableList() {
		return tableList;
	}

	public void setTableList(List<String> tableList) {
		this.tableList = tableList;
	}

	public String getCurrUser() {
		return currUser;
	}

	public void setCurrUser(String currUser) {
		this.currUser = currUser;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Date getLoginT() {
		return loginT;
	}

	public void setLoginT(Date loginT) {
		this.loginT = loginT;
	}

	public String getIpAddr() {
		return ipAddr;
	}

	public void setIpAddr(String ipAddr) {
		this.ipAddr = ipAddr;
	}

	public String getSessionID() {
		return sessionID;
	}

	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}

	public String getLoginTStr() {
		return loginTStr;
	}

	public void setLoginTStr(String loginTStr) {
		this.loginTStr = loginTStr;
	}

	public boolean isPrintLog() {
		return printLog;
	}

	public void setPrintLog(boolean printLog) {
		this.printLog = printLog;
	}

	public boolean isPrintMessage() {
		return printMessage;
	}

	public void setPrintMessage(boolean printMessage) {
		this.printMessage = printMessage;
	}

	public int getLastRowID() {
		int rowNum = 0;
		try {
			String getRId = "Select ID from "+dbSchema+".s17g310_AccessLog where Username = '" + 
					currUser + "' order by ID desc limit 1";
			if(connectLog())
			{
				resultSet = loginBean.processSelect(getRId);
				if (resultSet != null)
				{
					while (resultSet.next()) 
					{
						rowNum = resultSet.getInt("ID");
					}
				} 
				else 
				{
					message=loginBean.getMessage();
					printMessage=true;
					return rowNum = -1;
				}
				return rowNum;
			}
			else
			{
				message = loginBean.getMessage();
				printMessage = true;
				return -1;
			}
		} catch (Exception e) {
			message = e.getMessage();
			return rowNum = -1;
		}
	}

	public void setLastRowID(int lastRowID) {
		this.lastRowID = lastRowID;
	}

	public String getDbSchema() {
		return dbSchema;
	}

	public void setDbSchema(String dbSchema) {
		this.dbSchema = dbSchema;
	}

	public Date getLogoutT() {
		return logoutT;
	}

	public void setLogoutT(Date logoutT) {
		this.logoutT = logoutT;
	}

	public String getLogoutTStr() {
		return logoutTStr;
	}

	public void setLogoutTStr(String logoutTStr) {
		this.logoutTStr = logoutTStr;
	}
}