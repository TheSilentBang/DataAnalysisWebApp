<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page"
	xmlns:f="http://java.sun.com/jsf/core"
	xmlns:h="http://java.sun.com/jsf/html"
	xmlns:t="http://myfaces.apache.org.tomahawk" version="2.0">
	<jsp:directive.page language="java"
		contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" />
	<jsp:text>
		<![CDATA[ <?xml version="1.0" encoding="UTF-8" ?> ]]>
	</jsp:text>
	<jsp:text>
		<![CDATA[ <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"> ]]>
	</jsp:text>
	<html xmlns="http://www.w3.org/1999/xhtml"
		xmlns:f="http://java.sun.com/jsf/core"
		xmlns:h="http://java.sun.com/jsf/html"
		xmlns:t="http://myfaces.apache.org/tomahawk">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Database Visualizations</title>
<link rel="stylesheet" href="css/styleSheet.css" />
</head>
<body>
	<f:view>
		<div class="report">
			<h:form>
				<hr />
				<h:commandButton value="Logout" action="#{actionLoginBean.logout}" />
				<h2>Database Access</h2>
				<p>IDS 517 &#160;&#160; S17G310</p>
				<hr />
				<h:commandButton value="Home" action="main" />
				<hr />
				<br />
				<h:commandButton value="Generate Graph"
					action="#{graphAnalysisBean.createChart}" styleClass="button"
					rendered="#{graphAnalysisBean.printGraphButton}" />
				<pre>
						<h:outputText value="#{graphAnalysisBean.message}"
						rendered="#{graphAnalysisBean.printMessage}" style="color:red" />
					</pre>
				<div align="center">
					<h:panelGrid columns="4">
						<h:selectOneListbox id="displayCharts"
							value="#{graphAnalysisBean.chartType}" onchange="submit()"
							valueChangeListener="#{graphAnalysisBean.changeChartVal}"
							size="5">
							<f:selectItem itemValue="0" itemLabel="Select Chart Type" />
							<f:selectItem itemValue="1" itemLabel="Pie Chart" />
							<f:selectItem itemValue="2" itemLabel="Bar Graph" />
							<f:selectItem itemValue="3" itemLabel="Scatterplot" />
							<f:selectItem itemValue="4"
								itemLabel="Scatterplot w Regression Line" />
						</h:selectOneListbox>
						<h:selectOneListbox id="selectOneCb"
							style="width:150px; height:100px"
							value="#{graphAnalysisBean.selectedTable}"
							rendered="#{graphAnalysisBean.printTables}" size="5"
							onchange="submit()"
							valueChangeListener="#{graphAnalysisBean.tableChangeVal}">
							<f:selectItems value="#{statsBean.tableList}" />
						</h:selectOneListbox>
						<h:selectOneListbox id="Graphs"
							value="#{graphAnalysisBean.chartSelectedCol}"
							rendered="#{graphAnalysisBean.printChartCol}" size="5">
							<f:selectItems value="#{statsBean.numData}" />
						</h:selectOneListbox>
						<h:selectOneListbox id="predictor1"
							value="#{graphAnalysisBean.predValue}" size="5"
							rendered="#{graphAnalysisBean.printXYGraphCol}">
							<f:selectItem itemValue="0" itemLabel="Select Predictor Value" />
							<f:selectItems value="#{statsBean.numData}" />
						</h:selectOneListbox>
						<h:selectOneListbox id="response2"
							value="#{graphAnalysisBean.respValue}" size="5"
							rendered="#{graphAnalysisBean.printXYGraphCol}">
							<f:selectItem itemValue="0" itemLabel="Select Response Value" />
							<f:selectItems value="#{statsBean.numData}" />
						</h:selectOneListbox>
					</h:panelGrid>
					<hr />
					<div
						style="background-attachment: scroll; overflow: auto; height: 600px; background-repeat: repeat"
						align="center">
						<h:graphicImage value="#{graphAnalysisBean.piePath}" width="600"
							height="600" rendered="#{graphAnalysisBean.printPie}" />
						<h:graphicImage value="#{graphAnalysisBean.barPath}" width="600"
							height="600" rendered="#{graphAnalysisBean.printBar}" />
						<h:graphicImage value="#{graphAnalysisBean.xyPath}" width="600"
							height="600" rendered="#{graphAnalysisBean.xySeriesChart}" />
						<h:graphicImage value="#{graphAnalysisBean.xyRegPath}" width="600"
							height="600" rendered="#{graphAnalysisBean.xySeriesRegChart}" />
						<br />
					</div>
				</div>
			</h:form>
		</div>
	</f:view>
</body>
	</html>
</jsp:root>