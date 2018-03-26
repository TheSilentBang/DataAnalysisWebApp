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
<title>Database Compute Input</title>
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
							action="#{computeBean.getTables}" styleClass="button" />
						<h:commandButton value="Select Source and Destination Variables"
							action="#{computeBean.getColumnNames}" styleClass="button" />
						<h:commandButton value="Compute Values"
							action="#{computeBean.printComputeValues}" styleClass="button" />
					</h:panelGrid>
					<pre>
						<h:outputText value="#{computeBean.message}"
							rendered="#{computeBean.printMessage}" style="color:red" />
					</pre>
					<h:panelGrid columns="3">
						<h:selectOneListbox id="selectOneCb"
							style="width:150px; height:100px"
							value="#{computeBean.selectedTable}"
							rendered="#{computeBean.printTablename}" size="5">
							<f:selectItems value="#{computeBean.tableList}" />
						</h:selectOneListbox>
						<h:selectOneListbox id="source" value="#{computeBean.sourceValue}"
							rendered="#{computeBean.printColumn}" size="5">
							<f:selectItem itemValue="0" itemLabel="Select Source Value" />
							<f:selectItems value="#{computeBean.numData}" />
						</h:selectOneListbox>
						<h:selectOneListbox id="destination"
							value="#{computeBean.destinationValue}"
							rendered="#{computeBean.printColumn}" size="5">
							<f:selectItem itemValue="0" itemLabel="Select Destination Value" />
							<f:selectItems value="#{computeBean.numData}" />
						</h:selectOneListbox>
					</h:panelGrid>
				</div>
			</h:form>
		</f:view>
	</div>
</body>
	</html>
</jsp:root>