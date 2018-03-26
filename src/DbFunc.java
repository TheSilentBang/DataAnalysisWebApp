package uic.edu.ids517.s17g310;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.jstl.sql.Result;
import javax.servlet.jsp.jstl.sql.ResultSupport;

public class DbFunc {

	private static final String [] TABLE_TYPES={"TABLE","VIEW"};
	private LoginBean loginBean;

	private List<String> tableList;
	private List<String> selectedCol;
	private List<String> columnList;
	private List<String> column;

	private ResultSetMetaData resultSetMetaData;
	private ResultSet resultSet;
	private String message;
	private String selectedTable;
	private String usersql;

	private Boolean renderMessage;
	private Boolean printTablename;
	private Boolean printTabledata;
	private Boolean printColumn;
	private Result result;

	private int rownum;
	private int columnnum;

	public DbFunc() {
		// TODO Auto-generated constructor stub
		tableList = new ArrayList<String>();
		rownum = 0;
		columnList= new ArrayList<String>();
		column= new ArrayList<String>();
		selectedCol = new ArrayList<String>();
		selectedTable=null;

		printColumn = false;
		printTablename = false;
		printTabledata = false;
		renderMessage = false;
	}

	public void restore()
	{
		message = "";
		renderMessage = false;
		rownum = 0;
		columnnum = 0;
		printTabledata = false;
		printColumn = false;
	}

	public void renderTableList()
	{
		renderMessage=false;
		if(tableList.isEmpty())
		{
			message = "No tables found in the schema.";
			renderMessage = true;
			setPrintTabledata(false);
			renderMessage = true;
			printTablename = false;
			usersql = "";
		}
		else
			printTablename = true;
	}

	public String quickCreate()
	{
		try {
			loginBean.getStmnt().executeUpdate("DROP TABLE IF EXISTS s17g310_recipes");
			loginBean.getStmnt().executeUpdate("CREATE TABLE s17g310_recipes (recipe_id INT NOT NULL,recipe_name VARCHAR(30) NOT NULL,PRIMARY KEY (recipe_id),UNIQUE (recipe_name))");
			loginBean.getStmnt().executeUpdate("INSERT INTO s17g310_recipes (recipe_id, recipe_name) VALUES (1,'Tacos'),(2,'Tomato Soup'),(3,'Grilled Cheese')");
			loginBean.getStmnt().executeUpdate("DROP TABLE IF EXISTS s17g310_ingredients");
			loginBean.getStmnt().executeUpdate("CREATE TABLE s17g310_ingredients (ingredient_id INT NOT NULL, ingredient_name VARCHAR(30) NOT NULL,ingredient_price INT NOT NULL,PRIMARY KEY (ingredient_id),UNIQUE (ingredient_name))");
			loginBean.getStmnt().executeUpdate("INSERT INTO s17g310_ingredients (ingredient_id, ingredient_name, ingredient_price) VALUES (1, 'Beef', 5),(2, 'Lettuce', 1),(3, 'Tomatoes', 2),(4, 'Taco Shell', 2),(5, 'Cheese', 3),(6, 'Milk', 1),(7, 'Bread', 2)");
			getTables();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "SUCCESS";

	}

	public String getTables(){
		try{
			restore();
			tableList.clear();
			String tableName="";
			resultSet = loginBean.getDatabaseMetaData().getTables(null,loginBean.getUserName(),null,TABLE_TYPES);
			if(resultSet!=null) {
				while(resultSet.next()) {
					tableName = resultSet.getString("TABLE_NAME");
					if(!loginBean.getRdbms().equalsIgnoreCase("oracle") || tableName.length()<4)
						tableList.add(tableName);
					else if(!tableName.substring(0,4).equalsIgnoreCase("BIN$"))
						tableList.add(tableName);
				}
				renderTableList();
				return "SUCCESS";
			}else{
				return "FAIL";
			}
		}catch(SQLException e){
			message = "Error Code: " + e.getErrorCode() + "\n" +
					"SQL State: " + e.getSQLState() + "\n" +
					"Message :" + e.getMessage() + "\n\n" +
					"SQLException while getting table List";
			resultSet = null;
			return "FAIL";

		}catch (Exception e) {
			message = e.getMessage();
			renderMessage = true;
			return "FAIL";
		}
	}

	public String getColNames()
	{
		try {
			restore();
			if(tableList.isEmpty())
			{
				message = "There are no Tables.";
				renderMessage = true;
				return "FAIL";
			}
			if(selectedTable.isEmpty())
			{
				message = "Select a Table.";
				renderMessage = true;
			}
			else
			{
				String sql = "select * from " + loginBean.getDbSchema() +"." + selectedTable;
				ResultSet resultSet = loginBean.processSelect(sql);
				usersql = "";

				if(resultSet!=null)
				{
					columnList.clear();
					ResultSetMetaData  resultSetMetaData=(ResultSetMetaData) resultSet.getMetaData();
					int columnCount = resultSetMetaData.getColumnCount();
					for (int i = 1; i <= columnCount; i++ ) {
						String name = resultSetMetaData.getColumnName(i);
						String datatype = resultSetMetaData.getColumnTypeName(i);
						column.add(name);
						columnList.add(name + " " + datatype);
					}
					printColumn = true;
				}
				else
				{
					message = loginBean.getMessage();
					renderMessage = true;
				}
			}
			return "SUCCESS";
		} catch(Exception e) {
			message = e.getMessage();
			renderMessage = true;
			return "FAIL";
		}
	}

	public String getTablesData()
	{
		try {
			restore();
			if (tableList.isEmpty())
			{
				message = "No Tables to Display.";
				renderMessage = true;
				return "FAIL";
			}
			if(selectedTable.isEmpty())
			{
				message = "Select a Table.";
				renderMessage = true;
				return "FAIL";
			}
			else
			{
				String sql="select * from " + loginBean.getDbSchema() + "." + selectedTable;
				resultSet = loginBean.processSelect(sql);
				usersql = sql;
				if(resultSet != null)
				{
					generateMetaData();
					return "SUCCESS";
				}
				else
				{
					message = loginBean.getMessage();	
					renderMessage = true;
					return "FAIL";
				}
			}
		} catch (Exception e) {
			message = e.getMessage();
			renderMessage = true;
			return "FAIL";
		}
	}

	public String getColData()
	{
		restore();
		if (tableList.isEmpty())
		{
			message = "No Tables to Display.";
			renderMessage = true;
			return "FAIL";
		}
		if(selectedTable.isEmpty())
		{
			message = "Select a Table.";
			renderMessage = true;
			return "FAIL";
		}
		if (selectedCol.isEmpty())
		{
			message = "Select a column First.";
			renderMessage = true;
			return "FAIL";
		}
		String data = selectedCol.get(0);
		int index = data.indexOf(" ");
		if (index < 0)
		{
			message = "Please select a column.";
			renderMessage = true;
			return "FAIL";
		}
		else
		{
			try {
				if(selectedTable != null && selectedCol != null )
				{
					List<String> colSeperated = new ArrayList<String>();
					for (int i = 0; i < selectedCol.size(); i++) 
					{
						int index1 = selectedCol.get(i).indexOf(" ");
						String column = selectedCol.get(i).substring(0, index1);
						colSeperated.add(column);
					}
					selectedCol = new ArrayList<String>();
					selectedCol = colSeperated;
					colSeperated = null;
					StringBuilder whole = new StringBuilder();
					for (String each : selectedCol) 
					{
						whole.append(",").append(each);
					}
					String sqlQuery = whole.toString();
					int index1 = sqlQuery.indexOf(",");  
					sqlQuery = sqlQuery.substring(index1+1, sqlQuery.length());
					sqlQuery = "select " + sqlQuery + " from "+ loginBean.getDbSchema() +"." + selectedTable;
					resultSet = loginBean.processSelect(sqlQuery);
					usersql = sqlQuery;
					if(resultSet!=null)
					{
						generateMetaData();
					}
					else
					{
						message = loginBean.getMessage();
						renderMessage = true;
					}
				}
				else
				{
					message = "Please select a table and a column.";
				}
			} catch (Exception e) {
				message = e.getMessage();
				renderMessage = true;
			}

			return "SUCCESS";
		}
	}

	public String processQuery()
	{
		try {
			restore();
			if(usersql.isEmpty())
			{
				message = "There is no Query to run.";
				renderMessage = true;
				return "FAIL";
			}
			else
			{
				usersql=usersql.toLowerCase();
				int index = usersql.indexOf(" ");
				if (index < 0)
				{
					message = "The Query is not Valid.";
					renderMessage = true;
					return "FAIL";
				}
				String keyWord = usersql.substring(0, index);
				if(keyWord.equalsIgnoreCase("select"))
				{
					restore();
					resultSet = loginBean.processSelect(usersql);
					if(resultSet!=null){
						generateMetaData();
					}

				}else if(keyWord.equalsIgnoreCase("drop")){
					restore();
					rownum = loginBean.processUpdate(usersql);
					if(rownum < 0){
						message = loginBean.getMessage();
						renderMessage = true;
					}
					else{
						message = "Table sucessfully dropped.";
						renderMessage = true;
						getTables();
					}
				}else if(keyWord.equalsIgnoreCase("create")){
					restore();
					rownum = loginBean.processUpdate(usersql);
					if(rownum < 0){
						message = loginBean.getMessage();
						renderMessage = true;
					}
					else{
						message = "Table sucessfully created.";
						renderMessage = true;
						getTables();
					}
				}
				else if(keyWord.equalsIgnoreCase("insert")){
					restore();
					rownum = loginBean.processUpdate(usersql);
					if(rownum < 0){
						message = loginBean.getMessage();
						renderMessage = true;
					}
					else{
						message = "Table inserted with "+ rownum +" rows.";
						renderMessage = true;
					}
				}else if(keyWord.equalsIgnoreCase("delete") || keyWord.equalsIgnoreCase("update")){
					message = "This functionality is unavaliable";
					renderMessage = true;
				}
				else {
					message = "Enter a valid Query.";
					renderMessage = true;
				}
			}
			return "SUCCESS";

		} catch(Exception e) {
			message = e.getMessage();
			renderMessage = true;
			return "FAIL";
		}
	}

	public String dropTable()
	{
	try {
		restore();
		if (tableList.isEmpty())
		{
			message = "There are no tables.";
			renderMessage = true;
			return "FAIL";
		}
		if(selectedTable.isEmpty())
		{
			message = "Select a table.";
			renderMessage = true;
			return "FAIL";
		}
		else
		{
			rownum = loginBean.processUpdate("drop table " + loginBean.getDbSchema() +
					"." + selectedTable);
			if(rownum < 0)
			{
				message = loginBean.getMessage();
				renderMessage = true;
			}
			else
				getTables();
		}
		return "SUCCESS";
	} catch(Exception e)
	{
		message = e.getMessage();
		renderMessage = true;
		return "FAIL";
    }
	}

	private void generateMetaData()
	{
		try {
			if(resultSet!=null)
			{
				selectedCol = new ArrayList<String>();
				resultSetMetaData = resultSet.getMetaData();
				result = ResultSupport.toResult(resultSet);
				columnnum = resultSetMetaData.getColumnCount();
				rownum = result.getRowCount();
				String columnNameList [] = result.getColumnNames();
				for(int i=0; i<columnnum; i++) 
				{
					selectedCol.add(columnNameList[i]);
				}
				printTabledata = true;
			}
			else
			{
				message = loginBean.getMessage();
				renderMessage = true;
			}
		} catch(Exception e) {
			message = e.getMessage();
			renderMessage = true;
		}
	}

	public List<String> getTableList() {
		return tableList;
	}

	public void setTableList(List<String> tableList) {
		this.tableList = tableList;
	}

	public List<String> getColumnList() {
		return columnList;
	}

	public void setColumnList(List<String> columnList) {
		this.columnList = columnList;
	}

	public List<String> getColumn() {
		return column;
	}

	public void setColumn(List<String> column) {
		this.column = column;
	}

	public ResultSet getResultSet() {
		return resultSet;
	}

	public void setResultSet(ResultSet resultSet) {
		this.resultSet = resultSet;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Boolean getRenderMessage() {
		return renderMessage;
	}

	public void setRenderMessage(Boolean renderMessage) {
		this.renderMessage = renderMessage;
	}

	public Boolean getprintTablename() {
		return printTablename;
	}

	public void setprintTablename(Boolean printTablename) {
		this.printTablename = printTablename;
	}


	public String getSelectedTable() {
		return selectedTable;
	}

	public void setSelectedTable(String selectedTable) {
		this.selectedTable = selectedTable;
	}

	public List<String> getSelectedCol() {
		return selectedCol;
	}

	public void setSelectedCol(List<String> selectedCol) {
		this.selectedCol = selectedCol;
	}

	public String getUsersql() {
		return usersql;
	}

	public void setUsersql(String usersql) {
		this.usersql = usersql;
	}

	public LoginBean getLoginBean() {
		return loginBean;
	}

	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

	public Boolean getPrintTabledata() {
		return printTabledata;
	}

	public void setPrintTabledata(Boolean printTabledata) {
		this.printTabledata = printTabledata;
	}

	public int getRownum() {
		return rownum;
	}

	public void setRownum(int rownum) {
		this.rownum = rownum;
	}

	public int getColumnnum() {
		return columnnum;
	}

	public void setColumnnum(int columnnum) {
		this.columnnum = columnnum;
	}

	public Boolean getPrintColumn() {
		return printColumn;
	}

	public void setPrintColumn(Boolean printColumn) {
		this.printColumn = printColumn;
	}



}
