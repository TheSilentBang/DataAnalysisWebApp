package uic.edu.ids517.s17g310;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

public class ActionLoginBean {

	private LoginBean loginBean;
	private DbFunc dbFunc;
	private AccessLogBean accessLogBean;
	private String message;

	public ActionLoginBean() {
		// TODO Auto-generated constructor stub

	}

	public String login()
	{
		String status = loginBean.connect();
		if(status.equalsIgnoreCase("SUCCESS")){
			accessLogBean.createLog();
			dbFunc.restore();
			return "SUCCESS";
		}else{
			return "FAIL";
		}
	}
	
	public String logout()
	{
		try
		{
			accessLogBean.logout();
			ExternalContext extContext = FacesContext.getCurrentInstance().getExternalContext();
			extContext.invalidateSession();
			return "LOGOUT";
		} catch(Exception e) {
			message = e.getMessage();
			return "FAIL";
		}
	}

	public LoginBean getLoginBean() {
		return loginBean;
	}

	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}

	public DbFunc getDbFunc() {
		return dbFunc;
	}

	public void setDbFunc(DbFunc dbFunc) {
		this.dbFunc = dbFunc;
	}

	public AccessLogBean getAccessLogBean() {
		return accessLogBean;
	}

	public void setAccessLogBean(AccessLogBean accessLogBean) {
		this.accessLogBean = accessLogBean;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
