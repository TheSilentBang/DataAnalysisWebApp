package uic.edu.ids517.s17g310;


import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.jsp.jstl.sql.Result;

import org.apache.commons.math3.distribution.FDistribution;
import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.jfree.data.function.LineFunction2D;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.statistics.Regression;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


import com.mysql.jdbc.DatabaseMetaData;
import com.mysql.jdbc.ResultSetMetaData;

public class StatsBean {

	private int columnNum;
	private String message;
	private String selectedTable;
	private String predictorValue;
	private String responseValue;
	private boolean printColumn;	
	private boolean printMessage;
	private boolean printReport;
	private boolean printTabledata;
	private boolean printTablename;
	private boolean printRegressionColumn;
	private boolean printRegressionButton;
	private boolean printColumnListbutton;
	private boolean printRegressionResult;
	private ResultSetMetaData resultSetMetaData;
	private DescrStatsBean descrStatsBean;
	private RegressionAnalysisBean regressionAnalysisBean;
	private String errorMessage;
	private List<String> numData;
	private List<String> nominalData;
	private List<String> selectedColumn;
	private List<String> columnList;
	private List<String> tableList;
	private List<String> columns;
	private List<String> list;
	private List<DescrStatsBean> statisticList;
	private Result result;
	private ResultSet resultSet;
	private LoginBean loginBean;
	private DatabaseMetaData metaData;
	private XYSeriesCollection xySeriesVariable;
	private XYSeries xySeries;
	private XYSeries predictorSeries;
	private XYSeries responseSeries;
	private XYDataset xyDataset;

	public StatsBean()
	{
		selectedColumn = new ArrayList<String>();
		columnList = new ArrayList<String>();
		columns= new ArrayList<String>();	
		printTabledata=false;
		statisticList= new ArrayList<DescrStatsBean>();
		nominalData= new ArrayList<String>();
		numData= new ArrayList<String>();
		printRegressionButton=true;
		printReport=false;
		tableList=new ArrayList<String>();
		list= new ArrayList<String>();
		printTablename=false;
		xySeries= new XYSeries("Random");
		xySeriesVariable= new XYSeriesCollection();
		predictorSeries= new XYSeries("Predictor");
		responseSeries = new XYSeries("Response");
		//descrStatsBean = new DescrStatsBean();
		getTables();
	}

	public void printTableList()
	{
		restore();
		if(tableList.isEmpty())
		{
			message = "Schema has no tables";
			printColumn=false;
			printRegressionResult=false;
			printRegressionColumn=false;
			printTabledata = false;
			printMessage = true;
			printTablename = false;
		}
		else
			printTablename = true;
	}

	public String getRegressionColNames()
	{
		restore();
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
		if(printRegressionColumns())
		{
			return "SUCCESS";
		}
		else
		{
			printMessage=true;
			return "FAIL";
		}
	}

	public boolean printRegressionColumns()
	{
		try
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
				return false;
			}
			return true;
		} catch(Exception e) {
			message = e.getMessage();
			printMessage = true;
			return false;
		}
	}
	public String processLogout()
	{
		try
		{
			restore();
			loginBean.closeConn();
			ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
			ec.invalidateSession();
			return "LOGOUT";
		} catch(Exception e)
		{
			message = e.getMessage();
			printMessage = true;
			return "FAIL";
		}
	}
	public String splitCol()
	{
		try {
			restore();
			if(selectedTable != null && selectedColumn != null)
			{
				List<String> columnSeperated = new ArrayList<String>();
				for (int i = 0; i < selectedColumn.size(); i++) 
				{
					String data = selectedColumn.get(i);
					int index = data.indexOf(" ");
					String column = data.substring(0, index);
					String datatype = data.substring((index + 1), data.length());
					if(datatype.equalsIgnoreCase("CHAR")|| datatype.equalsIgnoreCase("VARCHAR"))
					{
						message = "Nominal variables are not allowed";
						return "FAIL";
					}
					else{
						columnSeperated.add(column);
					}
				}
				selectedColumn = new ArrayList<String>();
				selectedColumn = columnSeperated;
				list.clear();
				list = selectedColumn;
				columnSeperated = null;
				return "SUCCESS";
			}
			else
			{
				message = "Table and column need to be selected";
				return "FAIL";
			}
		} catch (Exception e) {
			message = e.getMessage();
			printMessage = true;
			return "FAIL";
		}
	}

	public boolean onGraphChange()
	{
		if(getTables().equals("SUCCESS"))
		{
			printRegressionColumn=false;
			printTablename=false;
			return true;
		}
		else
		{
			errorMessage=message;
			return false;
		}
	}

	public boolean createResultsforGraph()
	{
		if(calVariables().equals("SUCCESS"))
		{
			printTabledata=false;
			return true;
		}
		printTabledata=false;
		errorMessage = message;
		return false;
	}

	public String createReport()
	{
		restore();
		if(tableList.isEmpty())
		{
			message = "Schema has no tables";
			printMessage = true;
			return "FAIL";
		}			
		if(selectedTable.isEmpty())
		{
			message = "Table and Column need to be selected";
			printMessage = true;
			return "FAIL";
		}
		if (selectedColumn.isEmpty())
		{
			message = "Column need to be selected ";
			printMessage = true;
			return "FAIL";
		}
		if(splitCol().equalsIgnoreCase("FAIL"))
		{
			printMessage = true;
			return "FAIL";
		}
		else
		{
			if(calVariables().equals("FAIL"))
			{
				printMessage=true;
				return "FAIL";
			}
			else
			{
				return "SUCCESS";
			}
		}
	}
	public String showColRegression()
	{
		restore();
		if(tableList.isEmpty())
		{
			message = "Schema has no tables";
			printMessage = true;
			printColumnListbutton = true;
			printReport = true;
			printRegressionColumn = false;
			return "FAIL";
		}
		if(selectedTable == null)
		{
			message = "Table needs to be selected";
			printMessage = true;
			printColumnListbutton = true;
			printReport=true;
			return "FAIL";
		}
		String status = getRegressionColNames();
		if(status.equalsIgnoreCase("SUCCESS"))
		{
			printColumn = false;
			printRegressionButton = false;
			printRegressionColumn = true;
			printColumnListbutton = true;
			printReport = true;

			return "SUCCESS";
		}
		else {
			printMessage=true;
			return "FAIL";
		}
	}

	public String calVariables()
	{
		try
		{
			for(int a=0;a<list.size();a++)
			{
				String sqlQuery = "select " + list.get(a) + " from " + loginBean.getDbSchema() +
						"." + selectedTable;
				resultSet = loginBean.processSelect(sqlQuery);
				if (resultSet == null)
				{
					message = loginBean.getMessage();
					printMessage = true;
					return "FAIL";
				}
				resultSetMetaData = (ResultSetMetaData) resultSet.getMetaData();
				columnNum = resultSetMetaData.getColumnCount();
				String columnName;
				for(int b=1;b<columnNum+1;b++)
				{
					List<Double> values = new ArrayList<Double>();
					columnName = resultSetMetaData.getColumnName(b);
					String columnType=resultSetMetaData.getColumnTypeName(b);
					while(resultSet.next())
					{
						switch(columnType.toLowerCase())
						{
						case "int":
							values.add((double) resultSet.getInt(columnName));
							break;
						case "smallint":
							values.add((double) resultSet.getInt(columnName));
							break;
						case "float":
							values.add((double) resultSet.getFloat(columnName));
							break;
						case "double":
							values.add((double) resultSet.getDouble(columnName));
							break;
						case "long":
							values.add((double) resultSet.getLong(columnName));
							break;
						default:
							values.add((double) resultSet.getDouble(columnName));
							break;
						}
					}
					double[] valuesArray= new double[values.size()];
					for(int c=0;c<values.size();c++)
					{
						valuesArray[c]= (double)values.get(c);
					}
					double minValue = Math.round(StatUtils.min(valuesArray) * 10000.0) / 10000.0; 
					double maxValue =  Math.round(StatUtils.max(valuesArray) * 10000.0) / 10000.0; 
					double mean =  Math.round(StatUtils.mean(valuesArray) * 10000.0) / 10000.0;
					double variance =  Math.round(StatUtils.variance(valuesArray, mean) * 10000.0) / 10000.0;
					double std =  Math.round(Math.sqrt(variance) * 10000.0) / 10000.0;
					double median =  Math.round(StatUtils.percentile(valuesArray, 50.0) * 10000.0) / 10000.0;
					double q1 =  Math.round(StatUtils.percentile(valuesArray, 25.0) * 10000.0) / 10000.0;
					double q3 =  Math.round(StatUtils.percentile(valuesArray, 75.0) * 10000.0) / 10000.0;
					double iqr = q3 - q1;
					double range = maxValue - minValue;
					int numObs = values.size();
					descrStatsBean.setGraphVar(q1, q3, median);
					statisticList.add(new DescrStatsBean(columnName, minValue, maxValue,
							mean, variance, std, median, q1, q3, iqr, range, numObs));
				}
				printTabledata = true;
			}
			return "SUCCESS";
		} catch(Exception e) {
			message = e.getMessage();
			printMessage = true;
			return "FAIL";
		}
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
				columnList.clear();
				String sqlQuery = "select * from " + loginBean.getDbSchema() +
						"." + selectedTable;
				ResultSet resultSet = loginBean.getColNames(sqlQuery);
				if(resultSet!=null)
				{

					ResultSetMetaData  resultSetmd=(ResultSetMetaData) resultSet.getMetaData();
					int columnCount = resultSetmd.getColumnCount();
					for (int i = 1; i <= columnCount; i++ ) {
						String name = resultSetmd.getColumnName(i);
						String datatype = resultSetmd.getColumnTypeName(i);
						columns.add(name);
						columnList.add(name + " " + datatype);
					}
					printColumn= true;
				}
				else
				{
					message = loginBean.getMessage();
					printMessage = true;
				}
			}
			return "SUCCESS";
		} catch(Exception e) {
			message = e.getMessage();
			printMessage = true;
			return "FAIL";
		}
	}



	public String printRegressionReport()
	{
		restore();
		if (tableList.isEmpty())
		{
			message = "Table needs to be selected";
			printMessage = true;
			printColumnListbutton = true;
			printReport = true;
			printRegressionColumn = false;
			return "FAIL";
		}
		if (selectedTable == null)
		{
			message = "Table needs to be selected";
			printMessage=true;
			return "FAIL";
		}
		if (predictorValue == null || responseValue == null)
		{
			message = "Predictor and response values needs to be selected";
			printMessage = true;
			return "FAIL";
		}
		if(responseValue.equals("0") || predictorValue.equals("0"))
		{
			message="Predictor and response variables needs to be selected";
			printMessage=true;
			return "FAIL";
		}
		if(responseValue.equalsIgnoreCase(predictorValue))
		{
			message="Predictor and response variables needs to be different";
			printMessage=true;
			return "FAIL";
		}
		if (calRegressionVariables())
		{
			return "SUCCESS";
		}
		else
			return "FAIL";
	}

	public boolean calRegressionVariables()
	{
		try {			
			responseSeries.clear();
			predictorSeries.clear();
			xySeries.clear();
			xySeriesVariable.removeAllSeries();
			String sqlQuery = "select " + predictorValue + ", " + responseValue + 
					" from "+ loginBean.getDbSchema() + "." + selectedTable;
			resultSet = loginBean.processSelect(sqlQuery);
			if(resultSet!= null)
			{
				resultSetMetaData = (ResultSetMetaData) resultSet.getMetaData();
				String predictorName = resultSetMetaData.getColumnTypeName(1);
				String responseName = resultSetMetaData.getColumnTypeName(2);
				List<Double> predictorList = new ArrayList<Double>();
				List<Double> responseList = new ArrayList<Double>();
				while(resultSet.next())
				{
					switch(predictorName.toLowerCase())
					{
					case "int":
						predictorList.add((double) resultSet.getInt(1));
						break;
					case "smallint":
						predictorList.add((double) resultSet.getInt(1));
						break;
					case "float":
						predictorList.add((double)resultSet.getFloat(1));
						break;
					case "double":
						predictorList.add((double) resultSet.getDouble(1));
						break;
					case "long":
						predictorList.add((double) resultSet.getLong(1));
						break;
					default:
						predictorList.add((double) resultSet.getDouble(1));
						break;
					}
					switch(responseName.toLowerCase())
					{
					case "int":
						responseList.add((double) resultSet.getInt(2));
						break;
					case "smallint":
						responseList.add((double) resultSet.getInt(2));
						break;
					case "float":
						responseList.add((double)resultSet.getFloat(2));
						break;
					case "double":
						responseList.add((double) resultSet.getDouble(2));
						break;
					case "long":
						responseList.add((double) resultSet.getLong(2));
						break;
					default:
						responseList.add((double) resultSet.getDouble(2));
						break;
					}
				}
				double[] predictorArray = new double[predictorList.size()];
				for(int i=0;i<predictorList.size();i++)
				{
					predictorArray[i]= (double)predictorList.get(i);
					predictorSeries.add(i+1, (double)predictorList.get(i));
				}
				double[] responseArray= new double[responseList.size()];
				for(int i=0;i<responseList.size();i++)
				{
					responseArray[i]= (double)responseList.get(i);
					responseSeries.add(i+1, (double)responseList.get(i));
				}
				SimpleRegression sr = new SimpleRegression();
				if(responseArray.length > predictorArray.length)
				{
					for(int i=0;i<predictorArray.length;i++)
					{
						sr.addData(predictorArray[i], responseArray[i]);
						xySeries.add(predictorArray[i], responseArray[i]);
					}
				}
				else
				{
					for(int i=0;i<responseArray.length;i++)
					{
						sr.addData(predictorArray[i], responseArray[i]);
						xySeries.add(predictorArray[i], responseArray[i]);
					}
				}
				xySeriesVariable.addSeries(xySeries);
				double regressionParam[] = Regression.getOLSRegression(xySeriesVariable,0);
				LineFunction2D lineFunc2d = new LineFunction2D(regressionParam[0], regressionParam[1]);
				xyDataset = DatasetUtilities.sampleFunction2D(lineFunc2d,xySeriesVariable.getDomainLowerBound(true),xySeriesVariable.getDomainUpperBound(true), 100, "Fitted Regression Line");
				int totalDF = responseArray.length-1;
				TDistribution tDistribution = new TDistribution(totalDF);
				double intercept = sr.getIntercept();
				double interceptStandardError = sr.getInterceptStdErr();
				double tStatistic = 0;
				int predictorDF = 1;
				int residualErrorDF = totalDF - predictorDF;
				double rSquare = sr.getRSquare();
				double rSquareAdjusted = rSquare - (1 - rSquare)/(totalDF - predictorDF - 1);
				if(interceptStandardError!=0){
					tStatistic = (double)intercept/interceptStandardError;
				}
				double interceptPValue =
						(double)2*tDistribution.cumulativeProbability(-Math.abs(tStatistic));
				double slope = sr.getSlope();
				double slopeStandardError = sr.getSlopeStdErr();
				double tStatisticpredict = 0;
				if(slopeStandardError != 0) {
					tStatisticpredict = (double)slope/slopeStandardError;
				}
				double pValuePredictor =
						(double)2*tDistribution.cumulativeProbability(-Math.abs(tStatisticpredict));
				double standardErrorModel = Math.sqrt(StatUtils.variance(responseArray))*(Math.sqrt(1-rSquareAdjusted));
				double regressionSumSquares = sr.getRegressionSumSquares();
				double sumSquaredErrors = sr.getSumSquaredErrors();
				double totalSumSquares = sr.getTotalSumSquares();
				double meanSquare = 0;
				if(predictorDF!=0) {
					meanSquare = regressionSumSquares/predictorDF;
				}
				double meanSquareError = 0;
				if(residualErrorDF != 0) {
					meanSquareError = (double)(sumSquaredErrors/residualErrorDF);
				}
				double fValue = 0;
				if(meanSquareError != 0) {
					fValue = meanSquare/meanSquareError;
				}
				String regressionEquation = responseValue +
						" = " + intercept + " + (" + slope + ") " +
						predictorValue;
				FDistribution fDistribution = new FDistribution(predictorDF, residualErrorDF);
				double pValue = (double)(1-fDistribution.cumulativeProbability(fValue));
				boolean regressionResultsStatus =
						regressionAnalysisBean.setRegressionAnalysisVariables(
								regressionEquation, intercept, interceptStandardError,
								tStatistic, interceptPValue, slope, slopeStandardError,
								tStatisticpredict, pValuePredictor, standardErrorModel,
								rSquare, rSquareAdjusted, predictorDF, residualErrorDF,
								totalDF, regressionSumSquares, sumSquaredErrors, totalSumSquares,
								meanSquare, meanSquareError, fValue, pValue);
				if(regressionResultsStatus)
				{
					printRegressionResult = true;
					return true;
				}
				else
				{
					message = regressionAnalysisBean.getMessage();
					printMessage = true;
					return false;
				}
			}
			else
			{
				message=loginBean.getMessage();
				printMessage=true;
				return false;
			}
		} catch(Exception e) {
			message = e.getMessage();
			printMessage = true;
			return false;
		}
	}



	public boolean onTableChange()
	{
		if(printRegressionColumns())
		{
			printRegressionColumn=false;
			return true;
		}
		else
		{
			errorMessage = message;
			return false;
		}
	}

	public boolean printRegressionResults()
	{
		if(calRegressionVariables())
		{
			printRegressionResult=false;
			return true;
		}
		else
		{
			errorMessage = message;
			return false;
		}
	}

	public String restoreButton()
	{
		printColumn = false;
		printRegressionButton = true;
		printColumnListbutton = false;
		printRegressionColumn = false;
		printRegressionResult = false;
		printReport = false;
		printMessage = false;
		selectedColumn.clear();
		return "SUCCESS";
	}
	public boolean createRegressionResults()
	{
		xySeries.clear();
		xySeriesVariable.removeAllSeries();
		if(calRegressionVariables())
		{
			printRegressionResult=false;
			return true;
		}
		else
		{
			errorMessage = message;
			return false;
		}
	}


	public void restore() {
		printMessage = false;
		printTabledata=false;
		printRegressionResult=false;
	}

	public String getSelectedTable() {
		return selectedTable;
	}

	public void setSelectedTable(String selectedTable) {
		this.selectedTable = selectedTable;
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

	public XYSeriesCollection getXySeriesVariable() {
		return xySeriesVariable;
	}

	public void setXySeriesVariable(XYSeriesCollection xySeriesVariable) {
		this.xySeriesVariable = xySeriesVariable;
	}
	public boolean isPrintColumn() {
		return printColumn;
	}

	public void setPrintColumn(boolean printColumn) {
		this.printColumn = printColumn;
	}

	public ResultSet getResultSet() {
		return resultSet;
	}

	public void setResultSet(ResultSet resultSet) {
		this.resultSet = resultSet;
	}

	public boolean isPrintTablename() {
		return printTablename;
	}

	public void setPrintTablename(boolean printTablename) {
		this.printTablename = printTablename;
	}

	public ResultSetMetaData getResultSetMetaData() {
		return resultSetMetaData;
	}

	public void setResultSetMetaData(ResultSetMetaData resultSetMetaData) {
		this.resultSetMetaData = resultSetMetaData;
	}

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

	public List<String> getTableList() {
		return tableList;
	}

	public void setTableList(List<String> tableList) {
		this.tableList = tableList;
	}

	public DatabaseMetaData getMetaData() {
		return metaData;
	}

	public void setMetaData(DatabaseMetaData metaData) {
		this.metaData = metaData;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isPrintMessage() {
		return printMessage;
	}

	public void setPrintMessage(boolean printMessage) {
		this.printMessage = printMessage;
	}

	public LoginBean getLoginBean() {
		return loginBean;
	}

	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}

	public List<String> getColumns() {
		return columns;
	}

	public void setColumns(List<String> columns) {
		this.columns = columns;
	}

	public int getColumnNum() {
		return columnNum;
	}

	public void setColumnNum(int columnNum) {
		this.columnNum = columnNum;
	}

	public List<DescrStatsBean> getStatisticList() {
		return statisticList;
	}

	public void setStatisticList(List<DescrStatsBean> statisticList) {
		this.statisticList = statisticList;
	}

	public boolean isPrintTabledata() {
		return printTabledata;
	}

	public void setPrintTabledata(boolean printTabledata) {
		this.printTabledata = printTabledata;
	}

	public DescrStatsBean getDescrStatsBean() {
		return descrStatsBean;
	}

	public void setDescrStatsBean(DescrStatsBean descrStatsBean) {
		this.descrStatsBean = descrStatsBean;
	}

	public boolean isPrintRegressionColumn() {
		return printRegressionColumn;
	}

	public void setRenderRegressionColumn(boolean printRegressionColumn) {
		this.printRegressionColumn = printRegressionColumn;
	}

	public boolean isPrintColumnListbutton() {
		return printColumnListbutton;
	}

	public void setPrintColumnListbutton(boolean printColumnListbutton) {
		this.printColumnListbutton = printColumnListbutton;
	}

	public boolean isPrintRegressionButton() {
		return printRegressionButton;
	}

	public void setPrintRegressionButton(boolean printRegressionButton) {
		this.printRegressionButton = printRegressionButton;
	}

	public List<String> getNominalData() {
		return nominalData;
	}

	public void setNominalData(List<String> nominalData) {
		this.nominalData = nominalData;
	}

	public List<String> getNumData() {
		return numData;
	}

	public void setNumData(List<String> numData) {
		this.numData = numData;
	}

	public String getPredictorValue() {
		return predictorValue;
	}

	public void setPredictorValue(String predictorValue) {
		this.predictorValue = predictorValue;
	}

	public String getResponseValue() {
		return responseValue;
	}

	public void setResponseValue(String responseValue) {
		this.responseValue = responseValue;
	}

	public boolean isPrintReport() {
		return printReport;
	}

	public void setPrintReport(boolean printReport) {
		this.printReport = printReport;
	}

	public boolean isPrintRegressionResult() {
		return printRegressionResult;
	}

	public void setPrintRegressionResult(boolean printRegressionResult) {
		this.printRegressionResult = printRegressionResult;
	}

	public RegressionAnalysisBean getRegressionAnalysisBean() {
		return regressionAnalysisBean;
	}

	public void setRegressionAnalysisBean(RegressionAnalysisBean regressionAnalysisBean) {
		this.regressionAnalysisBean = regressionAnalysisBean;
	}
	public List<String> getList() {
		return list;
	}

	public void setList(List<String> list) {
		this.list = list;
	}
	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public XYDataset getXyDataset() {
		return xyDataset;
	}

	public void setXyDataset(XYDataset xyDataset) {
		this.xyDataset = xyDataset;
	}

}