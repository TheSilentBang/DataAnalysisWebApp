package uic.edu.ids517.s17g310;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


import javax.servlet.jsp.jstl.sql.Result;

import com.mysql.jdbc.DatabaseMetaData;
import com.mysql.jdbc.ResultSetMetaData;


public class ComputeBean {

	private int columnNum;
	private String message;
	private String selectedTable;
	private String sourceValue;
	private String destinationValue;
	private boolean printColumn;	
	private boolean printMessage;
	private boolean printTabledata;
	private boolean printTablename;
	private boolean printComputeColumn;
	private boolean printComputeButton;
	private List<String> numData;
	private List<String> nominalData;
	private ResultSetMetaData resultSetMetaData;
	private List<String> selectedColumn;
	private List<String> columnList;
	private List<String> tableList;
	private List<String> columns;
	private List<String> list;
	private Result result;
	private ResultSet resultSet;
	private LoginBean loginBean;
	private DatabaseMetaData metaData;
	
	public ComputeBean() {
		// TODO Auto-generated constructor stub
		selectedColumn = new ArrayList<String>();
		columnList = new ArrayList<String>();
		columns= new ArrayList<String>();	
		printTabledata=false;
		tableList=new ArrayList<String>();
		list= new ArrayList<String>();
		printTablename=false;
		nominalData= new ArrayList<String>();
		numData= new ArrayList<String>();
		getTables();
	}

	public void restore() {
		printMessage = false;
		printTabledata=false;
	}

	public String getTables()
	{
		try {
			restore();
			tableList = new ArrayList<String>();
			String tablenames;
			resultSet = loginBean.getTables();
			if(resultSet != null)
			{
				while(resultSet.next())
				{
					tablenames = resultSet.getString("TABLE_NAME");
					tableList.add(tablenames);
				}
				printTableList();
			}
			else
			{
				message = loginBean.getMessage();
				printMessage = true;
			}
			return "SUCCESS";
		} catch (Exception e) {
			message = e.getMessage();
			printMessage = true;
			return "FAIL";
		}
	}

	public String getColumnNames ()
	{
		try {
			restore();
			getTables();
			if(tableList.isEmpty())
			{
				message = "Schema has no tables";
				printMessage = true;
				return "FAIL";
			}
			if(selectedTable.isEmpty())
			{
				message = "Table needs to be selected";
				printMessage = true;
				return "FAIL";
			}
			else
			{
				String sqlQuery = "select * from " + loginBean.getDbSchema() +
						"." + selectedTable;		
				resultSet = loginBean.getColNames(sqlQuery);
				if(resultSet != null)
				{
					columnList.clear();
					nominalData.clear();
					numData.clear();
					ResultSetMetaData  resultSetmd=(ResultSetMetaData) resultSet.getMetaData();
					int columnCount = resultSetmd.getColumnCount();
					for (int i = 1; i <= columnCount; i++ ) {
						String name = resultSetmd.getColumnName(i);
						String datatype = resultSetmd.getColumnTypeName(i);
						if(datatype.equalsIgnoreCase("char")||datatype.equalsIgnoreCase("varchar"))
						{
							nominalData.add(name);
						}
						else
							numData.add(name);
					}
					printColumn = true;
				}
				else
				{
					message = loginBean.getMessage();
					printMessage = true;
					return "FAIL";
				}
				return "SUCCESS";
			}
		} catch(Exception e) {
			message = e.getMessage();
			printMessage = true;
			return "FAIL";
		}
	}

	public boolean computeValues(){
		try{
			String computeQuery = "Select TRANS_DATE, " + sourceValue + " from " + selectedTable;
			System.out.println(computeQuery);
			List<String> sqlListQuery = loginBean.multipleQueryList(computeQuery);
			if(sqlListQuery.isEmpty())
			{
				return false;
			}
			String[] idList = sqlListQuery.get(0).split(" ");
			double old = Double.parseDouble(idList[1]);
			String updateQuery1 = "Update "+ selectedTable+" set "+ destinationValue+" = "+ 0 + 
					" WHERE " + "TRANS_DATE = '" + idList[0] + "'"; 

			loginBean.processUpdate(updateQuery1);
			for (int i =1;i<sqlListQuery.size();i++){
				String[] splitstr = sqlListQuery.get(i).split(" ");
				double computedValue = Double.parseDouble(splitstr[1]);
				double newValue = (Math.log(computedValue) - Math.log(old));
				String updateQuery2 = "Update "+ selectedTable+" set "+ destinationValue+" = "+ newValue + 
						 " WHERE " + "TRANS_DATE = '" + splitstr[0] + "'"; 
				loginBean.processUpdate(updateQuery2);
				old = computedValue;
			}
			return true;
		}catch(Exception e) {
			message = e.getMessage();
			printMessage = true;
			return false;
		}
	}

	public String printComputeValues(){
		try{
			restore();
			if (tableList.isEmpty())
			{
				message = "Schema has no tables";
				printMessage = true;
				return "FAIL";
			}
			if (selectedTable == null)
			{
				message = "Table needs to be selected";
				printMessage=true;
				return "FAIL";
			}
			if (sourceValue == null || destinationValue == null)
			{
				message = "Source and destination values needs to be selected";
				printMessage = true;
				return "FAIL";
			}
			if(sourceValue.equals("0") || destinationValue.equals("0"))
			{
				message="Source and destination variables needs to be selected";
				printMessage=true;
				return "FAIL";
			}
			if (computeValues())
			{
				message="Computed values inserted into the table.";
				printMessage=true;
				return "SUCCESS";
			}
			else
				return "FAIL";
		} 
		catch(Exception e) {
			message = e.getMessage();
			printMessage = true;
			return "FAIL";
		}
	}

	public void printTableList()
	{
		restore();
		if(tableList.isEmpty())
		{
			message = "Schema has no tables";
			printTabledata = false;
			printMessage = true;
			printTablename = false;
			printColumn = false;
		}
		else
			printTablename = true;
	}




	public int getColumnNum() {
		return columnNum;
	}

	public void setColumnNum(int columnNum) {
		this.columnNum = columnNum;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getSelectedTable() {
		return selectedTable;
	}

	public void setSelectedTable(String selectedTable) {
		this.selectedTable = selectedTable;
	}

	public String getSourceValue() {
		return sourceValue;
	}

	public void setSourceValue(String sourceValue) {
		this.sourceValue = sourceValue;
	}

	public String getDestinationValue() {
		return destinationValue;
	}

	public void setDestinationValue(String destinationValue) {
		this.destinationValue = destinationValue;
	}

	public boolean isPrintColumn() {
		return printColumn;
	}

	public void setPrintColumn(boolean printColumn) {
		this.printColumn = printColumn;
	}

	public boolean isPrintMessage() {
		return printMessage;
	}

	public void setPrintMessage(boolean printMessage) {
		this.printMessage = printMessage;
	}

	public boolean isPrintTabledata() {
		return printTabledata;
	}

	public void setPrintTabledata(boolean printTabledata) {
		this.printTabledata = printTabledata;
	}

	public boolean isPrintTablename() {
		return printTablename;
	}

	public void setPrintTablename(boolean printTablename) {
		this.printTablename = printTablename;
	}

	public boolean isPrintComputeColumn() {
		return printComputeColumn;
	}

	public void setPrintComputeColumn(boolean printComputeColumn) {
		this.printComputeColumn = printComputeColumn;
	}

	public boolean isPrintComputeButton() {
		return printComputeButton;
	}

	public void setPrintComputeButton(boolean printComputeButton) {
		this.printComputeButton = printComputeButton;
	}

	public ResultSetMetaData getResultSetMetaData() {
		return resultSetMetaData;
	}

	public void setResultSetMetaData(ResultSetMetaData resultSetMetaData) {
		this.resultSetMetaData = resultSetMetaData;
	}

	public List<String> getSelectedColumn() {
		return selectedColumn;
	}

	public void setSelectedColumn(List<String> selectedColumn) {
		this.selectedColumn = selectedColumn;
	}

	public List<String> getColumnList() {
		return columnList;
	}

	public void setColumnList(List<String> columnList) {
		this.columnList = columnList;
	}

	public List<String> getTableList() {
		return tableList;
	}

	public void setTableList(List<String> tableList) {
		this.tableList = tableList;
	}

	public List<String> getColumns() {
		return columns;
	}

	public void setColumns(List<String> columns) {
		this.columns = columns;
	}

	public List<String> getList() {
		return list;
	}

	public void setList(List<String> list) {
		this.list = list;
	}

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

	public ResultSet getResultSet() {
		return resultSet;
	}

	public void setResultSet(ResultSet resultSet) {
		this.resultSet = resultSet;
	}

	public LoginBean getLoginBean() {
		return loginBean;
	}

	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}

	public DatabaseMetaData getMetaData() {
		return metaData;
	}

	public void setMetaData(DatabaseMetaData metaData) {
		this.metaData = metaData;
	}

	public List<String> getNumData() {
		return numData;
	}

	public void setNumData(List<String> numData) {
		this.numData = numData;
	}

	public List<String> getNominalData() {
		return nominalData;
	}

	public void setNominalData(List<String> nominalData) {
		this.nominalData = nominalData;
	}

}
