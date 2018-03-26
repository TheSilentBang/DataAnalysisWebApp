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
<title>Database Access</title>
<link rel="stylesheet" href="css/styleSheet.css" />
</head>
<body>
	<div class="main">
		<h2>Database Access</h2>
		<p>IDS 517 &#160;&#160; S17G310</p>
		<hr />
		<br /> <br />
		<div class="box" align="center">
			<f:view>
				<h:form>
					<p>
						<b>Database Login</b><br />
					<p style="color: red; font-size: 10px">* Required Fields</p>
					</p>

					<h:panelGrid columns="3">
						<h:outputText value="Username*:" />
						<h:inputText id="userName" value="#{loginBean.userName}"
							style="width:100%" required="true"
							requiredMessage="Field required" />
						<h:message for="userName" style="color:red; padding:5px" />
						<h:outputText value="Password*:" />
						<h:inputSecret id="password" value="#{loginBean.password}"
							style="width:100%" required="true"
							requiredMessage="Field required" />
						<h:message for="password" style="color:red; padding:5px" />
						<h:outputText value="Host*:" />
						<h:selectOneMenu id="host" value="#{loginBean.host}"
							required="true">
							<f:selectItem itemValue="131.193.209.54"
								itemLabel="131.193.209.54" />
							<f:selectItem itemValue="131.193.209.57"
								itemLabel="131.193.209.57" />
							<f:selectItem itemValue="localhost" itemLabel="Localhost" />
						</h:selectOneMenu>
						<h:message for="host" style="color:red; padding:5px" />
						<h:outputText value="RDBMS*:" />
						<h:selectOneMenu id="rdbms" value="#{loginBean.rdbms}"
							required="true">
							<f:selectItem itemValue="mysql" itemLabel="MySQL" />
							<f:selectItem itemValue="db2" itemLabel="Db2" />
							<f:selectItem itemValue="oracle" itemLabel="Oracle" />
						</h:selectOneMenu>
						<h:message for="rdbms" style="color:red; padding:5px" />
						<h:outputText value="Database Schema*:" />
						<h:inputText id="dbSchema" value="#{loginBean.dbSchema}"
							required="true" requiredMessage="Field required" />
						<h:message for="dbSchema" style="color:red; padding:5px" />
					</h:panelGrid>
					<br />
					<div class="buttons">
						<h:commandButton type="submit" value="Login"
							action="#{actionLoginBean.login}" />
					</div>
					<br />
					<pre>
						<h:outputText value="#{loginBean.message}"
							rendered="#{loginBean.printMessage}"
							style="color:red; font-weight:bold" />
						</pre>
				</h:form>
			</f:view>
		</div>
		<br /> <br /> <br /> <a href="guides/UserGuide.pdf">User's Guide</a> <br />
		<a href="guides/ProgrammerGuide.pdf">Programmer's Guide</a>
	</div>
</body>
	</html>
</jsp:root>