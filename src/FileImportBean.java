package uic.edu.ids517.s17g310;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.apache.myfaces.util.FilenameUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.servlet.jsp.jstl.sql.Result;
import javax.servlet.jsp.jstl.sql.ResultSupport;

@ManagedBean(name = "fileImportBean")
@SessionScoped
public class FileImportBean {

	private UploadedFile uploadedFile;
	private static UploadedFile uploadedFileForAfterRollBack;

	private String fileLabel_s17g310;
	private String fileName;
	private long fileSize;
	private String fileContentType310;
	private int numberOfRows;
	private int numberOfColumns;
	private String uploadedFileContents;
	private boolean fileImport;
	private boolean fileImportErrorCheck;

	private boolean fileImportWithoutRollBacks = false;

	private String filePath_s17g310;
	private String tempFileName;
	private FacesContext facesContext;
	private String datasetLabel_s17g310;
	private String fileType;
	private String fileFormat;
	private String fileheaderRowFormat;
	private String relativeURL;
	private LoginBean loginBean;
	private List<String> tableList;
	private List<String> tableList1;
	private ResultSet resultSet;
	private String selectedTable;
	private int importCount = 0;
	private Result result;

	public boolean isFileImportWithoutRollBacks() {
		return fileImportWithoutRollBacks;
	}

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

	public void setFileImportWithoutRollBacks(boolean fileImportWithoutRollBacks) {
		this.fileImportWithoutRollBacks = fileImportWithoutRollBacks;
	}

	public boolean isFileImportInfoMsgFlag() {
		return fileImportInfoMsgFlag;
	}

	public void setFileImportInfoMsgFlag(boolean fileImportInfoMsgFlag) {
		this.fileImportInfoMsgFlag = fileImportInfoMsgFlag;
	}

	public boolean isFileImportErrorMsgFlag() {
		return fileImportErrorMsgFlag;
	}

	public void setFileImportErrorMsgFlag(boolean fileImportErrorMsgFlag) {
		this.fileImportErrorMsgFlag = fileImportErrorMsgFlag;
	}

	public String getFileImportInfoMsg() {
		return fileImportInfoMsg;
	}

	public void setFileImportInfoMsg(String fileImportInfoMsg) {
		this.fileImportInfoMsg = fileImportInfoMsg;
	}

	public String getFileImportErrorMsg() {
		return fileImportErrorMsg;
	}

	public void setFileImportErrorMsg(String fileImportErrorMsg) {
		this.fileImportErrorMsg = fileImportErrorMsg;
	}

	private boolean fileImportInfoMsgFlag;
	private boolean fileImportErrorMsgFlag;
	private String fileImportInfoMsg;
	private String fileImportErrorMsg;

	public String getSelectedTable() {
		return selectedTable;
	}

	public void setSelectedTable(String selectedTable) {
		this.selectedTable = selectedTable;
	}

	public LoginBean getLoginBean() {
		return loginBean;
	}

	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}

	@PostConstruct
	public void init() {
		Map<String, Object> map2 = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();

		loginBean = (LoginBean) map2.get("loginBean");
		loginBean = LoginBean.thisBean;

		System.out.println("loginBean.getTables(): " + loginBean);

		tableList = new ArrayList<String>();
		tableList1 = new ArrayList<String>();
	}

	public String processFileUploadWithRollBack() {

		uploadedFile = uploadedFileForAfterRollBack;
		fileImportWithoutRollBacks = true;
		// changed here
		String ret=processFileUpload();
		fileImportErrorMsgFlag = false;

		return ret;

	}

	public String processFileUpload() {

		if (uploadedFile == null) {
			return "FAIL";
		}
		uploadedFileContents = null;
		// messageBean.setErrorMessage("");
		facesContext = FacesContext.getCurrentInstance();
		filePath_s17g310 = facesContext.getExternalContext().getRealPath("/temp");
		File tempFile = null;
		FileOutputStream streamObj = null;
		int n = 0;
		fileImport = false;
		fileImportErrorCheck = true;
		try {
			fileName = uploadedFile.getName();
			fileSize = uploadedFile.getSize();
			relativeURL = FilenameUtils.getBaseName(fileName);

			fileContentType310 = uploadedFile.getContentType();
			tempFileName = filePath_s17g310 + "/" + relativeURL;
			// tempFileName = filePath + "/" + fileName;
			tempFile = new File(tempFileName);
			streamObj = new FileOutputStream(tempFile);
			streamObj.write(uploadedFile.getBytes());
			streamObj.close();
			Scanner scanRows = new Scanner(tempFile);
			String[] storeSplitLine = new String[50];
			String input = "";
			ArrayList<String[]> arrayOfColumns = new ArrayList<String[]>();
			/*
			 * int count=0; if(count!=0)
			 */
			while (scanRows.hasNext()) {
				input = scanRows.nextLine();
				if (fileFormat.equals("ExcelTab") || fileFormat.equals("Text")) {
					if (fileType.equals("data")) {
						while (input.contains("\t\t")) {
							input = input.replaceAll("\t\t", "\tnull\t");
						}
						if(input.charAt(input.length()-1)=='\t')
							input+="null";
					}
					storeSplitLine = input.split("\t");


				} else if (fileFormat.equals("ExcelCSV")) {

					if (fileType.equals("data")) {
						while (input.contains(",,")) {
							input = input.replaceAll(",,", ",null,");
							input = input.replaceAll(",\\)", ",null\\)");
						}
						if(input.charAt(input.length()-1)==',')
							input+="null";
					}

					storeSplitLine = input.split(",");
				}
				arrayOfColumns.add(storeSplitLine);
				n++;
			}
			fileImportInfoMsgFlag = true;
			/*
			 * } else if (fileType.equals("data")) {
			 * insertIntoTable(datasetLabel_s17g310, arrayOfColumns);
			 */
			if (fileType.equals("metadata")) {
				addTabletoSchema(arrayOfColumns);
				// createTable(datasetLabel_s17g310, arrayOfColumns);
				fileImportInfoMsgFlag = false;

			} else if (fileType.equals("data")) {
				List<Integer> errorList = addValuestoTable(arrayOfColumns);
				// insertIntoTable(datasetLabel_s17g310, arrayOfColumns);
				fileImportErrorMsg = "";
				if (!errorList.isEmpty()) {
					fileImportErrorMsg = "Following are the rows, for which insert failed.  ";
					for (int i = 0; i < errorList.size(); i++) {
						fileImportErrorMsg += errorList.get(i);
						if (i != errorList.size() - 1)
							fileImportErrorMsg += " ,  ";
					}
					fileImportErrorMsgFlag = true;
					fileImportInfoMsgFlag = false;

				}
				fileImportInfoMsg = "Number of Columns Updated " + importCount;
			}

			{
				numberOfRows = n;
				fileImport = true;
				scanRows.close();
			}
			uploadedFileForAfterRollBack = uploadedFile;
		} catch (IOException e) {
			e.getMessage();
			return "FAIL";
		}
		return "SUCCESS";

	}


	@SuppressWarnings("unchecked")
	public List<Integer> addValuestoTable(ArrayList<String[]> rowWiseValues) {
		// TODO Auto-generated method stub
		ArrayList<Integer> errorList = new ArrayList<Integer>();
		ArrayList<Object> returnList = new ArrayList<Object>();

		try {

			loginBean.connect();
			returnList = (ArrayList<Object>) loginBean.addValuestoTable(rowWiseValues, datasetLabel_s17g310,
					fileImportWithoutRollBacks, fileheaderRowFormat);
			// Integer.parseInt(...get(0).toString());
			importCount = Integer.parseInt(returnList.get(0).toString());
			errorList = (ArrayList<Integer>) returnList.get(1);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return errorList;
	}

	public void addTabletoSchema(ArrayList<String[]> rowWiseStrings) {
		// TODO Auto-generated method stub
		StringBuilder sqlQueryPart = new StringBuilder();
		int fileHeaderRowCount = fileheaderRowFormat.equals("No") ? 0 : 1;

		for (int i = fileHeaderRowCount; i < rowWiseStrings.size(); i++) {
			String[] readStrings310 = rowWiseStrings.get(i);
			String dataType = readStrings310[1];

			if (dataType.toLowerCase().contains("int"))
				dataType = "INT";
			else if (dataType.toLowerCase().contains("double"))
				dataType = "DOUBLE";
			else if (dataType.toLowerCase().contains("float"))
				dataType = "FLOAT";
			else
				dataType = "VARCHAR(300)";

			sqlQueryPart.append(readStrings310[0]).append(" ").append(dataType);
			if (i < (rowWiseStrings.size() - 1)) {
				sqlQueryPart.append(",");
			}
		}

		String createTablefromMeta = "CREATE TABLE " + "S17G310_" + datasetLabel_s17g310 + "(" + sqlQueryPart.toString()
		+ ")";
		try {
			loginBean.connect();
			int status = loginBean.processUpdate(createTablefromMeta);

			if (status != -1)
				System.out.println("Table added");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	// ******************************************For export - 04/18
	public String getTablesExport()
	{
		try {
			tableList1.clear();
			String tablenames;
			resultSet = loginBean.getTables();
			if(resultSet != null)
			{
				while(resultSet.next())
				{
					tablenames = resultSet.getString("TABLE_NAME");
					tableList1.add(tablenames);
				}
			}
			else
			{
				return "FAIL";
			}
			return "SUCCESS";
		} catch (Exception e) {
			e.printStackTrace();
			return "FAIL";
		}
	}

	public String exportToXML() {

		Date date;
		FacesContext fContext;
		ExternalContext externalContext;
		SimpleDateFormat dateFormat;
		DocumentBuilderFactory factory;
		DocumentBuilder builder;
		Document doc;
		Element results;
		java.sql.ResultSetMetaData rsmd;
		try {
			date = new Date();
			fContext = FacesContext.getCurrentInstance();
			externalContext = fContext.getExternalContext();
			String path = fContext.getExternalContext().getRealPath("/temp");
			externalContext.setResponseCharacterEncoding("UTF-8");
			dateFormat = new SimpleDateFormat("MMM_dd_YYYY_HH_mm_ss");

			String fileNameBase = selectedTable + "_" + dateFormat.format(date) + ".xml";
			String fileName = path + "/" + fileNameBase;
			String sqlQuery = "select * from " + loginBean.getDbSchema() + "." + selectedTable;
			resultSet = loginBean.processSelect(sqlQuery);
			if (resultSet != null) {
				factory = DocumentBuilderFactory.newInstance();
				builder = factory.newDocumentBuilder();
				doc = builder.newDocument();
				results = doc.createElement(selectedTable);
				doc.appendChild(results);
				rsmd = resultSet.getMetaData();
				int colCount = rsmd.getColumnCount();
				while (resultSet.next()) {
					Element row = doc.createElement("Row");
					results.appendChild(row);
					String column = "";
					for (int i = 1; i <= colCount; i++) {
						String columnName = rsmd.getColumnName(i);
						Element node = doc.createElement(columnName);
						column = resultSet.getString(i);
						node.appendChild(doc.createTextNode(column));
						row.appendChild(node);
					}
				}
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				DOMSource source = new DOMSource(doc);
				StreamResult file = new StreamResult(new File(fileName));
				transformer.transform(source, file);
				HttpServletResponse response = (HttpServletResponse) fContext.getExternalContext().getResponse();
				response.setHeader("Content-Disposition", "attachment;filename=\"" + fileNameBase + "\"");
				response.setContentLength((int) fileName.length());
				FileInputStream input = null;
				try {
					int i = 0;
					input = new FileInputStream(fileName);
					byte[] buffer = new byte[1024];
					while ((i = input.read(buffer)) != -1) {
						response.getOutputStream().write(buffer, 0, i);
						response.getOutputStream().flush();
					}
					fContext.responseComplete();
					fContext.renderResponse();
					return "SUCCESS";
				} catch (IOException e) {
					e.printStackTrace();

					return "FAIL";
				} finally {
					try {
						if (input != null) {
							input.close();
						}
					} catch (IOException e) {
						e.printStackTrace();

						return "FAIL";
					}
				}
			} else {
				return "FAIL";
			}
		} catch (ParserConfigurationException pe) {
			pe.printStackTrace();

			return "FAIL";
		} catch (SQLException se) {
			se.printStackTrace();

			return "FAIL";
		} catch (Exception e) {
			e.printStackTrace();
			return "FAIL";
		}

	}

	public String exportCSV() {
		FacesContext fContext = FacesContext.getCurrentInstance();
		ExternalContext eContext = fContext.getExternalContext();
		FileOutputStream fOutputStream = null;
		String path = fContext.getExternalContext().getRealPath("/temp");

		File dir = new File(path);
		if (!dir.exists())
			new File(path).mkdirs();
		eContext.setResponseCharacterEncoding("UTF-8");
		Date date = new Date();
		SimpleDateFormat dFormat = new SimpleDateFormat("MMM_dd_YYYY_HH_mm_ss");
		String fBase = selectedTable + "_" + dFormat.format(date) + ".csv";
		String fName = path + "/" + fBase;
		File f = new File(fName);
		resultSet = null;
		String sqlQuery = "select * from " + loginBean.getDbSchema() + "." + selectedTable;
		resultSet = loginBean.processSelect(sqlQuery);
		if (resultSet != null) {
			result = ResultSupport.toResult(resultSet);
			Object[][] sData = result.getRowsByIndex();
			String cNames[] = result.getColumnNames();
			StringBuffer sBuffer = new StringBuffer();
			try {
				fOutputStream = new FileOutputStream(fName);
				for (int i = 0; i < cNames.length; i++) {
					sBuffer.append(cNames[i].toString() + ",");
				}
				sBuffer.append("\n");
				fOutputStream.write(sBuffer.toString().getBytes());
				for (int i = 0; i < sData.length; i++) {
					sBuffer = new StringBuffer();
					for (int j = 0; j < sData[0].length; j++) {
						if (sData[i][j] == null) {
							String sValue = " ";
							sValue = sValue.replaceAll("[^A-Za-z0-9.-]", "  ");
							if (sValue.isEmpty()) {
								sValue = " ";
							}
							sBuffer.append(sValue + ",");
						} else {
							String value = sData[i][j].toString();
							if (value.contains(",")) {
								int index = value.indexOf(",");
								String newValue = value.substring(0, index - 1);
								value = newValue + value.substring(index + 1, value.length());
							}
							value = value.replaceAll("[^A-Za-z0-9,.-]", " ");
							if (value.isEmpty()) {
								value = " ";
							}
							sBuffer.append(value + ",");
						}
					}
					sBuffer.append("\n");
					fOutputStream.write(sBuffer.toString().getBytes());
				}
				fOutputStream.flush();
				fOutputStream.close();
			} catch (FileNotFoundException e) {

			} catch (IOException io) {

			}
			String mType = eContext.getMimeType(fName);
			FileInputStream in = null;
			byte b;
			eContext.responseReset();
			eContext.setResponseContentType(mType);
			eContext.setResponseContentLength((int) f.length());
			eContext.setResponseHeader("Content-Disposition", "attachment; filename=\"" + fBase + "\"");
			try {
				in = new FileInputStream(f);
				OutputStream output = eContext.getResponseOutputStream();
				while (true) {
					b = (byte) in.read();
					if (b < 0)
						break;
					output.write(b);
				}
			} catch (IOException e) {

			} finally {
				try {
					in.close();
				} catch (IOException e) {

				}
			}
			fContext.responseComplete();
		}
		return "SUCCESS";
	}

	public UploadedFile getUploadedFile() {
		return uploadedFile;
	}

	public void setUploadedFile(UploadedFile uploadedFile) {
		this.uploadedFile = uploadedFile;
	}

	public String getFileLabel() {
		return fileLabel_s17g310;
	}

	public void setFileLabel(String fileLabel) {
		this.fileLabel_s17g310 = fileLabel;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public long getFileSize() {
		return fileSize;
	}

	public String getRelativeURL() {
		return relativeURL;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public String getFileContentType() {
		return fileContentType310;
	}

	public void setFileContentType(String fileContentType) {
		this.fileContentType310 = fileContentType;
	}

	public int getNumberRows() {
		return numberOfRows;
	}

	public void setNumberRows(int numberRows) {
		this.numberOfRows = numberRows;
	}

	public int getNumberColumns() {
		return numberOfColumns;
	}

	public void setNumberColumns(int numberColumns) {
		this.numberOfColumns = numberColumns;
	}

	public String getUploadedFileContents() {
		return uploadedFileContents;
	}

	public void setUploadedFileContents(String uploadedFileContents) {
		this.uploadedFileContents = uploadedFileContents;
	}

	public boolean isFileImport() {
		return fileImport;
	}

	public void setFileImport(boolean fileImport) {
		this.fileImport = fileImport;
	}

	public void setRelativeURL(String relativeURL) {
		this.relativeURL = relativeURL;
	}

	public boolean isFileImportError() {
		return fileImportErrorCheck;
	}

	public void setFileImportError(boolean fileImportError) {
		this.fileImportErrorCheck = fileImportError;
	}

	public String getFilePath() {
		return filePath_s17g310;
	}

	public void setFilePath(String filePath) {
		this.filePath_s17g310 = filePath;
	}

	public String getTempFileName() {
		return tempFileName;
	}

	public void setTempFileName(String tempFileName) {
		this.tempFileName = tempFileName;
	}

	public FacesContext getFacesContext() {
		return facesContext;
	}

	public void setFacesContext(FacesContext facesContext) {
		this.facesContext = facesContext;
	}

	public String getDatasetLabel() {
		return datasetLabel_s17g310;
	}

	public void setDatasetLabel(String datasetLabel) {
		this.datasetLabel_s17g310 = datasetLabel;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getFileFormat() {
		return fileFormat;
	}

	public void setFileFormat(String fileFormat) {
		this.fileFormat = fileFormat;
	}

	public String getFileheaderRowFormat() {
		return fileheaderRowFormat;
	}

	public void setFileheaderRowFormat(String fileheaderRowFormat) {
		this.fileheaderRowFormat = fileheaderRowFormat;
	}

	public List<String> getTableList() {
		return tableList;
	}

	public void setTableList(List<String> tableList) {
		this.tableList = tableList;
	}

	public ResultSet getResultSet() {
		return resultSet;
	}

	public List<String> getTableList1() {
		return tableList1;
	}

	public void setTableList1(List<String> tableList1) {
		this.tableList1 = tableList1;
	}

}
