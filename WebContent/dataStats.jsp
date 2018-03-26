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
<title>Database Statistics</title>
<link rel="stylesheet" href="css/styleSheet.css" />
</head>
<body>
	<div class="report">
		<f:view>
			<h:form>
				<hr />
				<h:commandButton value="Logout" action="#{actionLoginBean.logout}" />
				<h2>Database Access</h2>
				<p>IDS 517 &#160;&#160; S17G310</p>
				<hr />
				<h:commandButton value="Home" action="main" />
				<hr />
				<br />
				<div align="center">
					<h:panelGrid columns="3">
						<h:commandButton value="Table list"
							action="#{statsBean.getTables}" styleClass="button" />
						<h:commandButton value="Column list"
							action="#{statsBean.getColumnNames}" styleClass="button"
							disabled="#{statsBean.printColumnListbutton}" />
						<h:commandButton value="Descriptive Statistics"
							action="#{statsBean.createReport}" styleClass="button"
							disabled="#{statsBean.printReport}" />
						<h:commandButton value="Select Variables for Regression"
							action="#{statsBean.showColRegression}" styleClass="button" />
						<h:commandButton value="Print Regression Report"
							action="#{statsBean.printRegressionReport}" styleClass="button"
							disabled="#{statsBean.printRegressionButton}" />
						<h:commandButton value="Restore"
							action="#{statsBean.restoreButton}" styleClass="button" />

					</h:panelGrid>

					<pre>
						<h:outputText value="#{statsBean.message}"
							rendered="#{statsBean.printMessage}" style="color:red" />
					</pre>
					<h:panelGrid columns="4">
						<h:selectOneListbox id="selectOneCb"
							style="width:150px; height:100px"
							value="#{statsBean.selectedTable}"
							rendered="#{statsBean.printTablename}" size="5">
							<f:selectItems value="#{statsBean.tableList}" />
						</h:selectOneListbox>
						<h:selectManyListbox id="Choosecolumns"
							style="width:150px; height:100px"
							value="#{statsBean.selectedColumn}"
							rendered="#{statsBean.printColumn}" size="5">
							<f:selectItems value="#{statsBean.columnList}" />
						</h:selectManyListbox>
						<h:selectOneListbox id="predictor"
							value="#{statsBean.predictorValue}"
							rendered="#{statsBean.printRegressionColumn}" size="5">
							<f:selectItem itemValue="0" itemLabel="Select Predictor Value" />
							<f:selectItems value="#{statsBean.numData}" />
						</h:selectOneListbox>
						<h:selectOneListbox id="response"
							value="#{statsBean.responseValue}"
							rendered="#{statsBean.printRegressionColumn}" size="5">
							<f:selectItem itemValue="0" itemLabel="Select Response Value" />
							<f:selectItems value="#{statsBean.numData}" />
						</h:selectOneListbox>

					</h:panelGrid>
					
					<br />
					<div
						style="background-attachment: scroll; overflow: auto; background-repeat: repeat; padding:5px"
						align="center">
						<t:dataTable value="#{statsBean.statisticList}" var="rowNumber"
							rendered="#{statsBean.printTabledata}" border="1" cellspacing="0"
							cellpadding="1" headerClass="headerWidth">
							<h:column>
								<f:facet name="header">
									<h:outputText value="Column Selected" />
								</f:facet>
								<h:outputText value="#{rowNumber.selectedColumn}" />
							</h:column>
							<h:column>
								<f:facet name="header">
									<h:outputText value="No. of Obs" />
								</f:facet>
								<h:outputText value="#{rowNumber.numObs}" />
							</h:column>
							<h:column>
								<f:facet name="header">
									<h:outputText value="Minimum Value" />
								</f:facet>
								<h:outputText value="#{rowNumber.minValue}" />
							</h:column>
							<h:column>
								<f:facet name="header">
									<h:outputText value="Maximum Value" />
								</f:facet>
								<h:outputText value="#{rowNumber.maxValue}" />
							</h:column>
							<h:column>
								<f:facet name="header">
									<h:outputText value="Mean" />
								</f:facet>
								<h:outputText value="#{rowNumber.mean}" />
							</h:column>
							<h:column>
								<f:facet name="header">
									<h:outputText value="Variance" />
								</f:facet>
								<h:outputText value="#{rowNumber.variance}" />
							</h:column>
							<h:column>
								<f:facet name="header">
									<h:outputText value="Standard Deviation" />
								</f:facet>
								<h:outputText value="#{rowNumber.std}" />
							</h:column>
							<h:column>
								<f:facet name="header">
									<h:outputText value="Q1" />
								</f:facet>
								<h:outputText value="#{rowNumber.q1}" />
							</h:column>
							<h:column>
								<f:facet name="header">
									<h:outputText value="Q3" />
								</f:facet>
								<h:outputText value="#{rowNumber.q3}" />
							</h:column>
							<h:column>
								<f:facet name="header">
									<h:outputText value="Range" />
								</f:facet>
								<h:outputText value="#{rowNumber.range}" />
							</h:column>
							<h:column>
								<f:facet name="header">
									<h:outputText value="IQR" />
								</f:facet>
								<h:outputText value="#{rowNumber.iqr}" />
							</h:column>
						</t:dataTable>
					</div>
					<br />
					<h:outputText value="Regression Equation: "
						rendered="#{statsBean.printRegressionResult}">
					</h:outputText>
					&#160;
					<h:outputText value="#{regressionAnalysisBean.regressionEq}"
						rendered="#{statsBean.printRegressionResult}">
					</h:outputText>
					<br /> <br />
					<h:outputText value="Regression Model"
						rendered="#{statsBean.printRegressionResult}"></h:outputText>
					<h:panelGrid columns="5"
						rendered="#{statsBean.printRegressionResult}" border="1">
						<h:outputText value="Predictor" />
						<h:outputText value="Co-efficient" />
						<h:outputText value="Standard Error Co-efficient" />
						<h:outputText value="T-Statistic" />
						<h:outputText value="P-Value" />
						<h:outputText value="Constant" />
						<h:outputText value="#{regressionAnalysisBean.intercept}" />
						<h:outputText value="#{regressionAnalysisBean.interceptStdErr}" />
						<h:outputText value="#{regressionAnalysisBean.tStat }" />
						<h:outputText value="#{regressionAnalysisBean.interceptPVal }" />
						<h:outputText value="#{statsBean.predictorValue}" />
						<h:outputText value="#{regressionAnalysisBean.slope}" />
						<h:outputText value="#{regressionAnalysisBean.slopeStdErr}" />
						<h:outputText value="#{regressionAnalysisBean.tStatPred }" />
						<h:outputText value="#{regressionAnalysisBean.pValPred }" />
					</h:panelGrid>
					<br /> <br />
					<h:panelGrid columns="2"
						rendered="#{statsBean.printRegressionResult}" border="1">
						<h:outputText value="Model Standard Error:" />
						<h:outputText value="#{regressionAnalysisBean.stdErrModel}" />
						<h:outputText value="R Square(Co-efficient of Determination)" />
						<h:outputText value="#{regressionAnalysisBean.rSq}" />
						<h:outputText
							value="R Square Adjusted(Co-efficient of Determination)" />
						<h:outputText value="#{regressionAnalysisBean.adjustedRsq}" />
					</h:panelGrid>
					<br /> <br />
					<h:outputText value="Analysis of Variance"
						rendered="#{statsBean.printRegressionResult}" />
					<br />
					<h:panelGrid columns="6"
						rendered="#{statsBean.printRegressionResult}" border="1">
						<h:outputText value="Source" />
						<h:outputText value="Degrees of Freedom(DF)" />
						<h:outputText value="Sum of Squares" />
						<h:outputText value="Mean of Squares" />
						<h:outputText value="F-Statistic" />
						<h:outputText value="P-Value" />
						<h:outputText value="Regression" />
						<h:outputText value="#{regressionAnalysisBean.predDF}" />
						<h:outputText value="#{regressionAnalysisBean.regressionSumSq}" />
						<h:outputText value="#{regressionAnalysisBean.meanSq }" />
						<h:outputText value="#{regressionAnalysisBean.fVal }" />
						<h:outputText value="#{regressionAnalysisBean.pVal}" />
						<h:outputText value="Residual Error" />
						<h:outputText value="#{regressionAnalysisBean.residualErrDF}" />
						<h:outputText value="#{regressionAnalysisBean.sumSqErr }" />
						<h:outputText value="#{regressionAnalysisBean.meanSqErr }" />
						<h:outputText value="" />
						<h:outputText value="" />
						<h:outputText value="Total" />
						<h:outputText value="#{regressionAnalysisBean.totalDF}" />
						<h:outputText value="#{regressionAnalysisBean.totalSumSq}" />
					</h:panelGrid>
				</div>
			</h:form>
		</f:view>
	</div>
</body>
	</html>
</jsp:root>