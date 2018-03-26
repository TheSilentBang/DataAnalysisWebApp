<?xml version="1.0" encoding="UTF-8" ?>
<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page"
	xmlns:f="http://java.sun.com/jsf/core"
	xmlns:h="http://java.sun.com/jsf/html" version="2.0">
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
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title>Main Menu</title>
<link rel="stylesheet" href="css/styleSheet.css" />
</head>
<body>
	<div class="main">
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
				<p>
					<a href="dbReports.jsp" title="Database Reports">Database
						Reports</a> <br /> <br /> <a href="dbImport.xhtml"
						title="Import and Export Data">Import and Export Data</a> <br />
					<br /> <a href="dbCompute.jsp" title="Database Compute Values">Database
						Compute Values</a> <br /> <br /> <a href="dataStats.jsp"
						title="Database Statistics">Database Statistics</a> <br /> <br />
					<a href="dbVisual.jsp" title="Data Visualization">Data
						Visualization</a> <br /> <br /> <a href="transactionLog.jsp"
						title="Access Log">Access Log</a> <br />
				</p>
			</h:form>
		</f:view>
	</div>
</body>
	</html>
</jsp:root>