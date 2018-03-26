<?xml version="1.0" encoding="UTF-8" ?>
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
<title>Database Reports</title>
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
				<center>
					<h:panelGrid columns="7">
						<h:commandButton type="submit" value="Table List"
							action="#{dbFunc.getTables}" />
						<h:commandButton type="submit" value="Column List"
							action="#{dbFunc.getColNames}" />
						<h:commandButton value="Display table"
							action="#{dbFunc.getTablesData}" />
						<h:commandButton value="Display selected columns"
							action="#{dbFunc.getColData}" />
						<h:commandButton value="Process SQL query"
							action="#{dbFunc.processQuery}" />
						<h:commandButton type="submit" value="Create Tables"
							action="#{dbFunc.quickCreate}" />
						<h:commandButton type="submit" value="Drop Table"
							action="#{dbFunc.dropTable}" />
					</h:panelGrid>
				</center>
				<pre>
						<h:outputText value="#{dbFunc.message}"
						rendered="#{dbFunc.renderMessage}" style="color:red" />
					</pre>
				<center>
					<h:panelGrid columns="3">
						<h:selectOneListbox id="selectOneTab"
							style="width:150px; height:100px" value="#{dbFunc.selectedTable}"
							rendered="#{dbFunc.printTablename}" size="5">
							<f:selectItems value="#{dbFunc.tableList}" />
						</h:selectOneListbox>
						<h:selectManyListbox id="selectColumn"
							style="width:150px; height:100px" value="#{dbFunc.selectedCol}"
							rendered="#{dbFunc.printColumn}" size="5">
							<f:selectItems value="#{dbFunc.columnList}" />
						</h:selectManyListbox>
						<h:inputTextarea rows="6" cols="40" style="height:100px"
							value="#{dbFunc.usersql}" />
					</h:panelGrid>
					<br />
					<hr />
					<h:outputText value="Rows: " />
					<h:outputText value="#{dbFunc.rownum}" />
					&#160;&#160;
					<h:outputText value="Columns: " />
					<h:outputText value="#{dbFunc.columnnum}" />
					<hr />
					<div
						style="background-attachment: scroll; overflow: auto; height: 400px; background-repeat: repeat"
						align="center">
						<t:dataTable value="#{dbFunc.result}" var="row"
							rendered="#{dbFunc.printTabledata}" border="1" cellspacing="0"
							cellpadding="1" columnClasses="columnClass1 border"
							headerClass="headerClass" footerClass="footerClass"
							rowClasses="rowClass2" styleClass="dataTableEx" width="900px">
							<t:columns var="column" value="#{dbFunc.selectedCol}">
								<f:facet name="header">
									<t:outputText styleClass="outputHeader" value="#{column}" />
								</f:facet>
								<t:outputText styleClass="outputText" value="#{row[column]}" />
							</t:columns>
						</t:dataTable>
					</div>
					<hr />
				</center>
			</h:form>
		</f:view>
	</div>
</body>
	</html>
</jsp:root>