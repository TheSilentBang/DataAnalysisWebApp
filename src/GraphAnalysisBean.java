package uic.edu.ids517.s17g310;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;

public class GraphAnalysisBean {

	private String selectedTable;
	private String message;
	private boolean printMessage;
	private boolean printChartType;
	private String chartSelectedCol;
	private boolean printChartCol;
	private boolean printXYGraphCol;
	private String predValue;
	private String respValue;
	private StatsBean statsBean;
	private String chartType;
	private boolean printRegCol;
	private boolean printTables;
	private boolean printGraphButton;
	private String schema;
	private GraphVisData graphVisData;
	private String piePath;
	private LoginBean loginBean;
	private boolean printPie;
	private boolean printBar;
	private boolean xySeriesChart;
	private boolean xySeriesRegChart;
	private String xyPath;
	private String barPath;
	private String xyRegPath;

	public GraphAnalysisBean()
	{
		printGraphButton=false;
		printChartCol=false;
		printTables=false;
		printRegCol=false;
		printXYGraphCol=false;
		printPie=false;
		printBar=false;
		xySeriesChart=false;
		xySeriesRegChart=false;
	}


	public void tableChangeVal(ValueChangeEvent table)
	{
		restore();
		if (table.getNewValue() != null)
		{
			selectedTable = table.getNewValue().toString();
		}
		printPie=false;
		printBar=false;
		xySeriesChart=false;
		xySeriesRegChart=false;
		statsBean.setSelectedTable(selectedTable);
		if(statsBean.onTableChange())
		{
			printGraphButton=true;
			switch(chartType)
			{
			case("1"):
			case("2"):
				printChartCol=true;
			printXYGraphCol=false;
			break;
			case("3"):
			case("4"):
				printChartCol=false;
			printXYGraphCol=true;
			break;
			default:
				message = "Chart type needs to be selected.";
				printMessage = true;
			}
		}
		else
		{
			message=statsBean.getErrorMessage();
			printMessage=true;
		}	}

	public String createChart() 
	{
		restore();
		try
		{
			if(selectedTable==null)
			{
				message="Table needs to be selected.";
				printMessage=true;
				return "FAIL";
			}
			if(chartType==null)
			{
				message="A chart type needs to be selected";
				printMessage=true;
				return "FAIL";
			}
			FacesContext context = FacesContext.getCurrentInstance();
			String path = context.getExternalContext().getRealPath("/ChartImages");
			File dir = new File(path);
			if(!dir.exists())
			{
				new File(path).mkdirs();
			}
			if(statsBean.getTableList().isEmpty())
			{
				message = "The schema does not have any tables.";
				printMessage = true;
				printTables=false;
				printXYGraphCol=false;
				printChartCol = false;
				return "FAIL";
			}
			if(selectedTable == null)
			{
				message = "Table needs to be selected.";
				printMessage=true;
				return "FAIL";
			}
			switch(chartType)
			{
			case("1"):
				if(chartSelectedCol.isEmpty())
				{
					message="To print a chart column needs to be selected";
					printMessage=true;
					return "FAIL";
				}
			List<String> columnList= new ArrayList<String>();
			columnList.add(chartSelectedCol);
			statsBean.getList().clear();
			statsBean.setList(columnList);
			if(statsBean.createResultsforGraph())
			{
				if(graphVisData.createChart(chartSelectedCol,selectedTable))
				{
					JFreeChart chart = ChartFactory.createPieChart
							(chartSelectedCol, graphVisData.getPieModel(), true, true, false);
					File outPie = new File(path+"/"+loginBean.getUserName()+"_piechart.png");
					ChartUtilities.saveChartAsPNG(outPie, chart, 600, 450);
					piePath = "/ChartImages/"+loginBean.getUserName()+"_piechart.png";
					printPie=true;
					printBar=false;
					xySeriesChart=false;
					xySeriesRegChart=false;
					statsBean.getList().clear();
					return "SUCCESS";
				}
				else
				{
					message=graphVisData.getErrorMessage();
					printMessage=true;
					statsBean.getList().clear();
					return "FAIL";
				}
			}
			else
			{
				message=statsBean.getErrorMessage();
				printMessage=true;
				statsBean.getList().clear();
				return "FAIL";
			}
			case("2"):
				if(chartSelectedCol.isEmpty())
				{
					message = "Column needs to be selected";
					printMessage=true;
					return "FAIL";
				}
			columnList= new ArrayList<String>();
			columnList.add(chartSelectedCol);
			statsBean.getList().clear();
			statsBean.setList(columnList);
			if(statsBean.createResultsforGraph())
			{
				if(graphVisData.createChart(chartSelectedCol,selectedTable))
				{
					JFreeChart chart = ChartFactory.createBarChart
							(chartSelectedCol, "Category","Value",
									graphVisData.getDataset(),PlotOrientation.VERTICAL,
									true, true, false);
					File outBar = new File(path+"/"+loginBean.getUserName()+"_barGraph.png");
					ChartUtilities.saveChartAsPNG(outBar, chart, 600, 450);
					barPath = "/ChartImages/"+loginBean.getUserName()+"_barGraph.png";
					printPie=false;
					printBar=true;
					xySeriesChart=false;
					xySeriesRegChart=false;
					statsBean.getList().clear();
					return "SUCCESS";
				}
				else
				{
					message=graphVisData.getErrorMessage();
					printMessage=true;
					statsBean.getList().clear();
					return "FAIL";
				}
			}
			else
			{
				message=statsBean.getErrorMessage();
				printMessage=true;
				statsBean.getList().clear();
				return "FAIL";
			}
			case("3"):
				if(respValue == null || predValue == null)
				{
					message="To print chart, select predictor and response values";
					printMessage=true;
					return "FAIL";
				}
			if(respValue.equals("0") || predValue.equals("0"))
			{
				message="To print chart, select predictor and response values";
				printMessage=true;
				return "FAIL";
			}
			statsBean.setPredictorValue(predValue);
			statsBean.setResponseValue(respValue);
			if(statsBean.printRegressionResults())
			{
				JFreeChart chart = ChartFactory.createScatterPlot(
						"Scatter Plot", predValue, respValue,
						statsBean.getXySeriesVariable(), PlotOrientation.VERTICAL,
						true, true, false);
				File xy = new File(path+"/"+loginBean.getUserName()+"_scatterplot.png");
				ChartUtilities.saveChartAsPNG(xy, chart, 600, 450);
				xyPath = "/ChartImages/"+loginBean.getUserName()+"_scatterplot.png";
				printPie=false;
				printBar=false;
				xySeriesChart=true;
				xySeriesRegChart=false;
				return "SUCCESS";
			}
			case("4"):
				if(respValue == null || predValue == null)
				{
					message="To print chart, select predictor and response values";
					printMessage=true;
					return "FAIL";
				}
			if(respValue.equals("0") || predValue.equals("0"))
			{
				message="To print chart, select predictor and response values";
				printMessage=true;
				return "FAIL";
			}
			statsBean.setPredictorValue(predValue);
			statsBean.setResponseValue(respValue);
			if(statsBean.printRegressionResults())
			{
				JFreeChart chart = ChartFactory.createScatterPlot(
						"Scatter Plot with Regression Line", predValue, respValue,
						statsBean.getXySeriesVariable(), PlotOrientation.VERTICAL,
						true, true, false);
				XYPlot xyPlot = chart.getXYPlot();
				xyPlot.setDataset(1, statsBean.getXyDataset());
				XYLineAndShapeRenderer xyLineandShapeRenderer = new XYLineAndShapeRenderer(true, false);
				xyLineandShapeRenderer.setSeriesPaint(0, Color.YELLOW);
				xyPlot.setRenderer(1, xyLineandShapeRenderer);
				File xyReg = new File(path+"/"+loginBean.getUserName()+"_scatterplotwRline.png");
				ChartUtilities.saveChartAsPNG(xyReg, chart, 600, 450);
				xyRegPath = "/ChartImages/"+loginBean.getUserName()+"_scatterplotwRline.png";
				printPie=false;
				printBar=false;
				xySeriesChart=false;
				xySeriesRegChart = true;
				return "SUCCESS";
			}
			else
			{
				message=statsBean.getErrorMessage();
				printMessage=true;
				return "fail";
			}		
			}
		} catch(IOException io) {
			message=io.getMessage();
			printMessage=true;
			return "fail";
		} catch(Exception e) {
			message=e.getMessage();
			printMessage=false;
			return "FAIL";
		}
		return "SUCCESS";
	}



	public void changeChartVal(ValueChangeEvent event) 
	{
		printPie = false;
		printBar = false;
		xySeriesChart = false;
		xySeriesRegChart=false;
		chartSelectedCol = null;
		restore();
		if (event.getNewValue() != null)
		{
			chartType = event.getNewValue().toString();
		}
		if(statsBean.onGraphChange())
		{
			printTables=true;
			if(selectedTable!=null)
			{
				if(statsBean.onTableChange())
				{
					printGraphButton=true;
					switch(chartType)
					{
					case("1"):
					case("2"):
						printChartCol=true;
					printXYGraphCol=false;
					break;
					case("3"):
					case("4"):
						printChartCol=false;
					printXYGraphCol=true;
					break;
					default:
						message = "Chart type needs to be selected";
						printMessage = true;
					}
				}
				else
				{
					message=statsBean.getErrorMessage();
					printMessage=true;
				}
			}
			else
			{
				printChartCol=false;
			}
		}
		else
		{
			message=statsBean.getErrorMessage();
			printMessage=true;
			printGraphButton=false;
		}
	}
	public void restore()
	{
		printMessage=false;
	}

	public String getSelectedTable() {
		return selectedTable;
	}

	public void setSelectedTable(String selectedTable) {
		this.selectedTable = selectedTable;
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

	public boolean isPrintChartType() {
		return printChartType;
	}

	public void setPrintChartType(boolean printChartType) {
		this.printChartType = printChartType;
	}

	public String getChartSelectedCol() {
		return chartSelectedCol;
	}

	public void setChartSelectedCol(String chartSelectedCol) {
		this.chartSelectedCol = chartSelectedCol;
	}

	public boolean isPrintChartCol() {
		return printChartCol;
	}

	public void setPrintChartCol(boolean printChartCol) {
		this.printChartCol = printChartCol;
	}

	public boolean isPrintXYGraphCol() {
		return printXYGraphCol;
	}

	public void setPrintXYGraphCol(boolean printXYGraphCol) {
		this.printXYGraphCol = printXYGraphCol;
	}

	public String getPredValue() {
		return predValue;
	}

	public void setPredValue(String predValue) {
		this.predValue = predValue;
	}

	public String getRespValue() {
		return respValue;
	}

	public void setRespValue(String respValue) {
		this.respValue = respValue;
	}

	public StatsBean getStatsBean() {
		return statsBean;
	}

	public void setStatsBean(StatsBean statsBean) {
		this.statsBean = statsBean;
	}

	public String getChartType() {
		return chartType;
	}

	public void setChartType(String chartType) {
		this.chartType = chartType;
	}

	public boolean isPrintRegCol() {
		return printRegCol;
	}

	public void setPrintRegCol(boolean printRegCol) {
		this.printRegCol = printRegCol;
	}

	public boolean isPrintTables() {
		return printTables;
	}

	public void setPrintTables(boolean printTables) {
		this.printTables = printTables;
	}

	public boolean isPrintGraphButton() {
		return printGraphButton;
	}

	public void setPrintGraphButton(boolean printGraphButton) {
		this.printGraphButton = printGraphButton;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public GraphVisData getGraphVisData() {
		return graphVisData;
	}

	public void setGraphVisData(GraphVisData graphVisData) {
		this.graphVisData = graphVisData;
	}

	public String getPiePath() {
		return piePath;
	}

	public void setPiePath(String piePath) {
		this.piePath = piePath;
	}

	public LoginBean getLoginBean() {
		return loginBean;
	}

	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}

	public boolean isPrintPie() {
		return printPie;
	}

	public void setPrintPie(boolean printPie) {
		this.printPie = printPie;
	}

	public boolean isPrintBar() {
		return printBar;
	}

	public void setPrintBar(boolean printBar) {
		this.printBar = printBar;
	}

	public boolean isXySeriesChart() {
		return xySeriesChart;
	}

	public void setXySeriesChart(boolean xySeriesChart) {
		this.xySeriesChart = xySeriesChart;
	}

	public String getXyPath() {
		return xyPath;
	}

	public void setXyPath(String xyPath) {
		this.xyPath = xyPath;
	}

	public String getBarPath() {
		return barPath;
	}

	public void setBarPath(String barPath) {
		this.barPath = barPath;
	}


	public String getXyRegPath() {
		return xyRegPath;
	}


	public void setXyRegPath(String xyRegPath) {
		this.xyRegPath = xyRegPath;
	}


	public boolean isXySeriesRegChart() {
		return xySeriesRegChart;
	}


	public void setXySeriesRegChart(boolean xySeriesRegChart) {
		this.xySeriesRegChart = xySeriesRegChart;
	}

}
