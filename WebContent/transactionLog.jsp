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
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Transaction Log</title>
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
				<h:commandButton value="Transaction Log"
					action="#{accessLogBean.getLog}" styleClass="button" />
				<br />
				<pre>
						<h:outputText value="#{accessLogBean.message}"
						rendered="#{accessLogBean.printMessage}" style="color:red" />
					</pre>
				<br />
				<div align="center">
					<table border="1px solid black" cellpadding="5" cellspacing="5">
						<tr>
							<th><h:outputText value="UserName"
									rendered="#{accessLogBean.printLog}" /></th>
							<th><h:outputText value="IP Address"
									rendered="#{accessLogBean.printLog}" /></th>
							<th><h:outputText value="Login Time"
									rendered="#{accessLogBean.printLog}" /></th>
							<th><h:outputText value="Logout Time"
									rendered="#{accessLogBean.printLog}" /></th>
							<th><h:outputText value="Session ID"
									rendered="#{accessLogBean.printLog}" /></th>
						</tr>
						<tr>
							<td><h:outputText value="#{accessLogBean.currUser}"
									rendered="#{accessLogBean.printLog}" /></td>
							<td><h:outputText value="#{accessLogBean.ipAddr}"
									rendered="#{accessLogBean.printLog}" /></td>
							<td><h:outputText value="#{accessLogBean.loginTStr}"
									rendered="#{accessLogBean.printLog}" /></td>
							<td><h:outputText value="#{accessLogBean.logoutTStr}"
									rendered="#{accessLogBean.printLog}" /></td>
							<td><h:outputText value="#{accessLogBean.sessionID}"
									rendered="#{accessLogBean.printLog}" /></td>
						</tr>
					</table>
				</div>
			</h:form>
		</f:view>
	</div>
</body>
	</html>
</jsp:root>