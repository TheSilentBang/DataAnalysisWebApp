package uic.edu.ids517.s17g310;

import java.sql.ResultSet;
import javax.servlet.jsp.jstl.sql.Result;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.XYDataset;
import com.mysql.jdbc.ResultSetMetaData;

public class GraphVisData {
   
   private LoginBean loginBean;
   private ResultSet resultSet;
   
   private Result result;
   private DefaultCategoryDataset dataset ;
   private DescrStatsBean descrStatsBean;
   private DefaultPieDataset pieModel;
   private boolean printErrorMessage;
   private boolean printPie;
   private XYDataset data;
   private ResultSetMetaData resultSetMD;
   private String errorMessage;
   private JFreeChart pieGraph;

 
   public GraphVisData()
   {
	   pieModel=new DefaultPieDataset();
	   dataset = new DefaultCategoryDataset();
   }
  
   public boolean createChart(String selectedCol,String selectedTable) 
   {
		try
		{
			pieModel.clear();
			dataset.clear();
			double Q1 =descrStatsBean.getQ1();
			double Q3 = descrStatsBean.getQ3();
			double median = descrStatsBean.getMedian();
			int cMedian = 0;
			int cQ1 =0;
			int cQ3	=0;
			int greaterQ3 = 0;
			String sqlQuery="Select" + " "+ selectedCol + " " +
					"from" + " "+loginBean.getDbSchema()+"."+selectedTable;
			resultSet=loginBean.processSelect(sqlQuery);
			resultSetMD = (ResultSetMetaData) resultSet.getMetaData();
			String datatype =resultSetMD.getColumnTypeName(1);
			int value=0;
			float floatCol=0;
			double doubleCol=0;
			int smallIntCol=0;
			long longCol=0;
			if(resultSet!=null)
			{
				while(resultSet.next())
				{
					switch(datatype.toLowerCase())
					{
						case "int":
							value=resultSet.getInt(1);
							if(value <= Q1)
							{
								cQ1++;
							}
							if(value> Q1 && value<=median)
								cMedian++;
							if(value> median && value<=Q3)
								cQ3++;
							if(value> Q3)
								greaterQ3++;
							break;
						case "smallint":
							smallIntCol=resultSet.getInt(1);
							if(smallIntCol <= Q1)
								cQ1++;
							if(smallIntCol> Q1 && smallIntCol<=median)
								cMedian++;
							if(smallIntCol> median && smallIntCol<=Q3)
								cQ3++;
							if(smallIntCol> Q3)
								greaterQ3++;
							break;
						case "float":
							floatCol=resultSet.getFloat(1);
							if(floatCol <= Q1)
								cQ1++;
							if(floatCol> Q1 && floatCol<=median)
								cMedian++;
							if(floatCol> median && floatCol<=Q3)
								cQ3++;
							if(floatCol> Q3)
								greaterQ3++;
							break;
						case "double":
							doubleCol=resultSet.getDouble(1);
							if(doubleCol <= Q1)
								cQ1++;
							if(doubleCol> Q1 && doubleCol<=median)
								cMedian++;
							if(doubleCol> median && doubleCol<=Q3)
								cQ3++;
							if(doubleCol> Q3)
								greaterQ3++;
							break;
						case "long":
							longCol=resultSet.getLong(1);
							if(longCol <= Q1)
								cQ1++;
							if(longCol> Q1 && longCol<=median)
								cMedian++;
							if(longCol> median && longCol<=Q3)
								cQ3++;
							if(longCol> Q3)
								greaterQ3++;
							break;
					}
				}
				pieModel.setValue("Quartile Q1", cQ1);
				pieModel.setValue("Q1 < Value(s) < Median", cMedian);
				pieModel.setValue("Median < Value(s) < Q3", cQ3);
				pieModel.setValue("Q3 < Value(s)", greaterQ3);
				dataset.addValue(cQ1,"Q1", "Category 1");
				dataset.addValue(cMedian,"Q1 < Value(s) < Median", "Category 2");
				dataset.addValue(cQ3,"Median < Value(s) < Q3","Category 3");
				dataset.addValue(greaterQ3,"Q3 < Value(s)", "Category 4");
				return true;
			}
			else
			{
				errorMessage=loginBean.getMessage();
				return false;
			}
		} catch(Exception e) {
			errorMessage=e.getMessage();
			return false;
		}
	}

	public JFreeChart getPieGraph() {
		return pieGraph;
	}

	public void setPieChart(JFreeChart pieGraph) {
		this.pieGraph = pieGraph;
	}

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

	public LoginBean getLoginBean() {
		return loginBean;
	}

	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}

	public ResultSet getResultSet() {
		return resultSet;
	}

	public void setResultSet(ResultSet resultSet) {
		this.resultSet = resultSet;
	}

	public DefaultPieDataset getPieModel() {
		return pieModel;
	}

	public void setPieModel(DefaultPieDataset pieModel) {
		this.pieModel = pieModel;
	}

	public ResultSetMetaData getResultSetMD() {
		return resultSetMD;
	}

	public void setResultSetMD(ResultSetMetaData resultSetMD) {
		this.resultSetMD = resultSetMD;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public boolean isPrintErrorMessage() {
		return printErrorMessage;
	}

	public void setPrintErrorMessage(boolean printErrorMessage) {
		this.printErrorMessage = printErrorMessage;
	}

	public boolean isPrintPie() {
		return printPie;
	}

	public void setPrintPie(boolean printPie) {
		this.printPie = printPie;
	}

	public XYDataset getData() {
		return data;
	}

	public void setData(XYDataset data) {
		this.data = data;
	}

	public DefaultCategoryDataset getDataset() {
		return dataset;
	}

	public void setDataset(DefaultCategoryDataset dataset) {
		this.dataset = dataset;
	}

	public DescrStatsBean getDescrStatsBean() {
		return descrStatsBean;
	}

	public void setDescrStatsBean(DescrStatsBean descrStatsBean) {
		this.descrStatsBean = descrStatsBean;
	}
}
