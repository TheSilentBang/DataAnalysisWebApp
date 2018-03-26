package uic.edu.ids517.s17g310;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import com.mysql.jdbc.DatabaseMetaData;
import com.mysql.jdbc.ResultSetMetaData;


public class LoginBean {

	//inputs
	private String userName;
	private String password;
	private String host;
	private String rdbms;
	private String dbSchema;
	private String message;
	private Boolean printMessage;

	private String jdbcDriver;
	private String url;
	private Connection connection;
	private Statement stmnt;
	private ResultSet resultSet;
	private ResultSetMetaData resultSetMetaData;
	private DatabaseMetaData  databaseMetaData;
	private int rowNum;
	private int colNum;

	public static LoginBean thisBean; 
	public LoginBean() {
		// TODO Auto-generated constructor stub
		userName="";
		password="";
		host="";
		rdbms="";
		dbSchema="";
		message="";
		printMessage = false;

		connection = null;
		stmnt = null;
		resultSet = null;
		resultSetMetaData = null;
		databaseMetaData = null; 
	} 

	public String connect(){

		try{

			if (connection != null)
			{
				if (resultSet != null)
					resultSet.close();
				if(stmnt != null){
					stmnt.close();
				}
				connection.close();
			}

			if(rdbms.equalsIgnoreCase("mysql")){
				jdbcDriver = "com.mysql.jdbc.Driver";
				url = "jdbc:mysql://" +
						host + ":3306/" +
						dbSchema;
			}
			else if(rdbms.equalsIgnoreCase("db2")){
				jdbcDriver = "com.ibm.db2.jcc.DB2Driver";
				url = "jdbc:db2://" +
						host + ":50000/" +
						dbSchema;
			}
			else if(rdbms.equalsIgnoreCase("oracle")){
				jdbcDriver = "oracle.jdbc.driver.OracleDriver";
				url = "jdbc:oracle:thin:@" +
						host + ":1521:" +
						dbSchema;
			}
			else{
				message="Database not supported.";
				printMessage= true;
				return "FAIL";
			}

			Class.forName(jdbcDriver);
			connection = DriverManager.getConnection(url, userName, password);
			stmnt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			databaseMetaData = (DatabaseMetaData)connection.getMetaData();
			printMessage= false;
			return "SUCCESS";


		} catch(ClassNotFoundException e){
			message =  "Database: " + rdbms + " not supported.";
			printMessage = true;
			return "FAIL";

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			message = "Error Code: " + e.getErrorCode() + "\n" +
					"SQL State: " + e.getSQLState() + "\n" +
					"Message :" + e.getMessage();
			printMessage = true;
			return "FAIL";
		} catch (Exception e){
			message = "Message :" + e.getMessage();
			printMessage = true;
			return "FAIL";
		}
		finally{
			thisBean=this;
		}

	}

	public ResultSet processSelect(String sql)
	{
		try {
			resultSet = stmnt.executeQuery(sql);
			if(resultSet != null){
				resultSetMetaData = (ResultSetMetaData) resultSet.getMetaData();
				colNum = resultSetMetaData.getColumnCount();
			}
			return resultSet;
		} catch (SQLException se) {
			message = "Exception Error Code: " + se.getErrorCode() + "\n" +
					"SQL State: " + se.getSQLState() + "\n" +
					"Message :" + se.getMessage() + "\n\n" +
					"SQLException while processing query.";
			return resultSet = null;
		} catch (Exception e) {
			message = "Exception occurred: " + e.getMessage();
			return resultSet = null;
		}
	}


	public int processUpdate(String sql){
		try{
			int count = stmnt.executeUpdate(sql);
			return count;
		}catch(SQLException e){
			e.printStackTrace();

			message = "Error Code: " + e.getErrorCode() + "\n" +
					"SQL State: " + e.getSQLState() + "\n" +
					"Message :" + e.getMessage() + "\n\n" +
					"SQLException while processing query.";
			return -1;
		} catch (Exception e) {
			e.printStackTrace();
			message = "Exception occurred: " + e.getMessage();
			return -1;
		}
	}
	public List<Object> addValuestoTable(ArrayList<String[]> rowWiseValues, String datasetLabel_s17g310,
			Boolean isImportWithoutRollback,String fileheaderRowFormat) throws SQLException {
		int importCount = 0;
		ArrayList<Integer> errorList = new ArrayList<Integer>();
		ArrayList<Object> returnList = new ArrayList<Object>();

		if (!isImportWithoutRollback)
			connection.setAutoCommit(false);

		System.out.println(isImportWithoutRollback);
		
		boolean isImportError = false;
		String stringForQuery = "";
		int fileHeaderCount =fileheaderRowFormat.equals("Yes")?1:0;
			for (int i = fileHeaderCount; i < rowWiseValues.size(); i++) {
				stringForQuery = "(";
				String[] readStrings310 = rowWiseValues.get(i);
				
				
				for (int j = 0; j < readStrings310.length; j++) {
					stringForQuery += readStrings310[j];
					if (j != readStrings310.length - 1)
						stringForQuery += ",";
				}

				String insertQuery = "INSERT INTO " + "S17G310_" + datasetLabel_s17g310 + " VALUES " + stringForQuery + ")";
				System.out.println("Insert Query"+insertQuery);
				try {
					int k = stmnt.executeUpdate(insertQuery);
					if (k  == -1) {
						errorList.add(i + 1);
					} else {
						importCount += 1;
					}
				} catch (SQLException e) {
					e.printStackTrace();
					errorList.add(i + 1);
					isImportError = true;

					message = "Error Code: " + e.getErrorCode() + "\n" + "SQL State: " + e.getSQLState() + "\n"
							+ "Message :" + e.getMessage() + "\n\n" + "SQLException while processing query.";

				} catch (Exception e) {
					e.printStackTrace();
					errorList.add(i + 1);
					isImportError = true;
					message = "Exception occurred: " + e.getMessage();

				}
			}
		if (!isImportWithoutRollback) {
			if(!errorList.isEmpty())
			connection.rollback();
			else connection.commit();
			connection.setAutoCommit(true);
			connection = null;
		}
		
		returnList.add(importCount);
		returnList.add(errorList);

		return returnList;

	}

	public List<String> multipleQueryList(String query) {

		List<String> sqlQueryList = new ArrayList<String>();
		try {
			resultSet = stmnt.executeQuery(query);
			if (resultSet != null) {
				resultSetMetaData = (ResultSetMetaData) resultSet.getMetaData();
				int numCols = resultSetMetaData.getColumnCount();
				resultSet.last();
				rowNum = resultSet.getRow();
				resultSet.beforeFirst();
				int k=0;
				while (resultSet.next()) {
					String res = new String();
					for (int i = 0; i < numCols; i++) {
						res+= resultSet.getString(i + 1)+" ";
					}
					sqlQueryList.add(res);
					k++;
				}
			}
		} catch (SQLException se) {
			message = "Error Code: " + se.getErrorCode() + "\n" +
					"SQL State: " + se.getSQLState() + "\n" +
					"Message :" + se.getMessage() + "\n\n" +
					"SQLException while getting table columns.";
			closeConn();
		}
		return sqlQueryList;
	}

	
	public ResultSet getTables()
	{
		try {
			connect();
			DatabaseMetaData dbmeta = (DatabaseMetaData) connection.getMetaData();
			resultSet = dbmeta.getTables(null, null, "%", null);
			return resultSet;
		} catch (SQLException se) {
			message = "Error Code: " + se.getErrorCode() + "\n" +
					"SQL State: " + se.getSQLState() + "\n" +
					"Message :" + se.getMessage();
			return resultSet = null;
		} catch (Exception e) {
			message = "Exception occurred: " + e.getMessage();
			return resultSet = null;
		}
	}

	public ResultSet getColNames(String sqlQuery)
	{
		try
		{
			ResultSet resultSet = stmnt.executeQuery(sqlQuery);
			return resultSet;
		} catch (SQLException se) {
			message = "Error Code: " + se.getErrorCode() + "\n" +
					"SQL State: " + se.getSQLState() + "\n" +
					"Message :" + se.getMessage() + "\n\n" +
					"SQLException while getting table columns.";
			return resultSet = null;
		} catch (Exception e) {
			message = "Exception occurred: " + e.getMessage();
			return resultSet = null;
		}
	}

	public void closeConn()
	{
		try {
			resultSet.close();
			stmnt.close();
			connection.close();
		} catch (SQLException se) {
			message = "Exception Error Code: " + se.getErrorCode() + "\n" +
					"SQL State: " + se.getSQLState() + "\n" +
					"Message :" + se.getMessage() + "\n\n" +
					"SQLException while closing connections.";
		}
		catch (Exception e)
		{
			message = "Exception occurred: " + e.getMessage();
		}
	}

	public int enterLog(String sqlQuery1, String userNm,
			String ipAddr, String sessionID)
	{
		try
		{
			java.sql.PreparedStatement sqlQuery2 = connection.prepareStatement(sqlQuery1);
			sqlQuery2.setString(1,userNm);
			sqlQuery2.setString(2, ipAddr);
			sqlQuery2.setTimestamp(3, new java.sql.Timestamp(new Date().getTime()));
			sqlQuery2.setString(4, sessionID);
			sqlQuery2.executeUpdate();
			return 0;
		} catch (SQLException e) {
			message = "Error Code: " + e.getErrorCode() + "\n" +
					"SQL State: " + e.getSQLState() + "\n" +
					"Message :" + e.getMessage() + "\n\n" +
					"SQLException in obtaining data";
			return -1;
		} catch (Exception e) {
			message = "Exception: " + e.getMessage();
			return -1;
		}
	}
	
	public int updateLog(String sqlQuery1, String userNm, int id)
	{
		try
		{
			java.sql.PreparedStatement sqlQuery2 = connection.prepareStatement(sqlQuery1);
			sqlQuery2.setTimestamp(1, new java.sql.Timestamp(new Date().getTime()));
			sqlQuery2.setString(2,userNm);
			sqlQuery2.setInt(3,id);
			int rows = sqlQuery2.executeUpdate();
	     	return rows;
		} catch (SQLException se) {
			message = "Error Code: " + se.getErrorCode() + "\n" +
					"SQL State: " + se.getSQLState() + "\n" +
					"Message :" + se.getMessage() + "\n\n" +
					"SQLException while getting column data.";
			return -1;
		} catch (Exception e) {
			message = "Exception occurred: " + e.getMessage();
			return -1;
		}
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getRdbms() {
		return rdbms;
	}

	public void setRdbms(String rdbms) {
		this.rdbms = rdbms;
	}

	public String getDbSchema() {
		return dbSchema;
	}

	public void setDbSchema(String dbSchema) {
		this.dbSchema = dbSchema;
	}

	public String getMessage() {
		return message;
	}

	public Boolean getprintMessage() {
		return printMessage;
	}

	public Statement getStmnt() {
		return stmnt;
	}

	public ResultSet getResultSet() {
		return resultSet;
	}

	public ResultSetMetaData getResultSetMetaData() {
		return resultSetMetaData;
	}

	public DatabaseMetaData getDatabaseMetaData() {
		return databaseMetaData;
	}

	public int getRowNum() {
		return rowNum;
	}

	public void setRowNum(int rowNum) {
		this.rowNum = rowNum;
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public int getColNum() {
		return colNum;
	}

	public void setColNum(int colNum) {
		this.colNum = colNum;
	}

}
