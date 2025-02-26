package com.xmlToExcel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



public class ReadXmlFile {

	static int count = 2;
	//static String path = "C:\\Users\\agurbax\\Desktop\\SOAP_UI_WSDL\\JAVA_Program\\XmlToExcel\\Registration Response";

	public static Logger DBLog = Logger.getLogger(ReadXmlFile.class.getName());
	
	public static void main(String[] args) {
		
		
		// log file

				PropertyConfigurator.configure("XmlToExcel_log.properties");
				DBLog.info("************************************************************\n\t\t\t\t\t\t\t\t\t XmlToExcel log");
				DBLog.info("************************************************************");

		
		// Read data from property file
		
		InputStream inputStream = null;

		Properties prop = new Properties();
		String propFileName = "XmlToExcelparameters.properties";

		try {
			inputStream = new FileInputStream(System.getProperty("user.dir") + "\\" + propFileName);
		} catch (FileNotFoundException var83) {
			var83.printStackTrace();
		}

		if (inputStream != null) {
			try {
				prop.load(inputStream);
				DBLog.info("Found Parameter file");
				System.out.println("Found Parameter file");
			} catch (IOException var82) {
				DBLog.info("Unable to find Parameter file");
				System.out.println("Unable to find Parameter file");
				var82.printStackTrace();
			}
		}
		
		
		String path = prop.getProperty("XmlFilePath");
		System.out.println("Xml file path :" + path);
		DBLog.info("Xml file path :" + path);
		
		String Excelpath = prop.getProperty("ExcelFilePath");
		System.out.println("Excel file path :" + Excelpath);
		DBLog.info("Excel file path :" + Excelpath);
		
		
		
		//End

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		String Reg = "";
		String Key = "";
		String Password = "";

		XSSFWorkbook workbook = new XSSFWorkbook();
		// change
		XSSFSheet spreadsheet = workbook.createSheet(" Registration_Key_Password ");
		XSSFRow row;
		// change
		Map<String, Object[]> Data = new TreeMap<String, Object[]>();

		try {

			DocumentBuilder builder = factory.newDocumentBuilder();

			File FilePath = new File(path);

			File[] listFiles = FilePath.listFiles();

			for (File file : listFiles) {

				System.out.println(file);

				String fileName = file.getName();
				System.out.println("File name : " + fileName);

				Document document = builder.parse(file);

				document.getDocumentElement().normalize();

				// Reading Register number
				NodeList RegisterNumber = document.getElementsByTagName("v7:TransactionDetail");
				Node item = RegisterNumber.item(0);

				System.out.println("Tag Name is : " + item.getNodeName());

				if (item.getNodeType() == Node.ELEMENT_NODE) {

					Element nn = (Element) item;
					Reg = nn.getElementsByTagName("v7:CustomerTransactionId").item(0).getTextContent();

					System.out.println("Register number : " + Reg);

				}

				// Reading User Credential
				NodeList UserCredential = document.getElementsByTagName("v7:UserCredential");
				Node item2 = UserCredential.item(0);

				System.out.println("key/Password tag Name is : " + item2);

				if (item2.getNodeType() == Node.ELEMENT_NODE) {
					Element n = (Element) item2;
					Key = n.getElementsByTagName("v7:Key").item(0).getTextContent();
					Password = n.getElementsByTagName("v7:Password").item(0).getTextContent();

					System.out.println("Key : " + Key);
					System.out.println("Password : " + Password);

					System.out.println("*********************");

					String excelrow = String.valueOf(count);

					Data.put("1", new Object[] { "Register Number", "Key", "Password" });
					Data.put(excelrow, new Object[] { Reg, Key, Password });

					Set<String> keyid = Data.keySet();

					int rowid = 0;

					for (String key : keyid) {

						row = spreadsheet.createRow(rowid++);
						Object[] objectArr = Data.get(key);
						int cellid = 0;

						for (Object obj : objectArr) {
							Cell cell = row.createCell(cellid++);
							cell.setCellValue((String) obj);
						}
					}

				}

				FileOutputStream out = new FileOutputStream(new File(
						Excelpath +"Registration_Key_Password.xlsx"));
				workbook.write(out);

				out.close();
				workbook.close();

				count++;
				System.out.println("Written Excel File");
			}

			System.out.println("Total Number of files : " + count);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
