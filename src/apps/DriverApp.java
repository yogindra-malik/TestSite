package apps;


import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.server.RemoteControlConfiguration;
import org.openqa.selenium.server.SeleniumServer;
import org.openqa.selenium.support.events.EventFiringWebDriver;

import com.thoughtworks.selenium.DefaultSelenium;

import testReports.TestReports;
import util.DbManager;
import util.TestConfig;
import util.TestUtil;
import util.monitoringMail;
import datatable.Xlfile_Reader;


public class DriverApp{
	
	public static Properties CONFIG;
	public static Properties Objects;
	public static Properties APPTEXT;
	public static Xlfile_Reader Core;
	public static Xlfile_Reader testData=null;
//	public static Xlfile_Reader DBresults=null;
	public static Random randomGenerator = new Random(); // Random Port Number generation 
	public static String currentTest;
	public static String keyword;
	//public static SeleniumServer server;
	//public static DefaultSelenium selenium=null;
	public static WebDriver dr=null;
	public static EventFiringWebDriver driver=null;
	public static String object;
	public static String currentTSID;
	public static String stepDescription;
	public static String proceedOnFail;
	public static String testStatus;
	public static String data_column_name;
	public static int  testRepeat;
	public static int nSelPort;
	public static String sSelPort;
	public static Calendar cal = new GregorianCalendar();
	public static  int month = cal.get(Calendar.MONTH);
	public static int year = cal.get(Calendar.YEAR);
	public static  int sec =cal.get(Calendar.SECOND);
	public static  int min =cal.get(Calendar.MINUTE);
	public static  int date = cal.get(Calendar.DATE);
	public static  int day =cal.get(Calendar.HOUR_OF_DAY);
	public static String strDate;
	public static String result;
	public static String mailresult=" - Script successfully executed - no errors found";
	public static String mailscreenshotpath;
	public static final Logger SELENIUM_LOGS = Logger.getRootLogger();
	public static final Logger APPLICATION_LOGS = Logger.getLogger("devpinoyLogger");
	

	//Get the current system time - used for generated unique file ids (ex: Screenshots, Reports etc on every test run)
	public static String getCurrentTimeStamp()
    { 
          SimpleDateFormat CurrentDate = new SimpleDateFormat("MM-dd-yyyy"+"_"+"HH-mm-ss");
          Date now = new Date(); 
         String CDate = CurrentDate.format(now); 
          return CDate; 
    }

	
	
	//Loaded the Selenium and Application log files
	
	
		

	
	
   	@BeforeSuite
	public void startTesting() throws Exception{
   		
   		// Code to Generate random numbers
   		
		 nSelPort = randomGenerator.nextInt(40000);
		 strDate=getCurrentTimeStamp();
     	System.out.println("date time stamp :"+strDate);
		 
		 // Start testing method will start generating the Test Reports from the beginning       
		TestReports.startTesting("C:\\AutomationSelennium\\Tomcat 8.0\\webapps\\ROOT\\htmlpages\\index"+strDate+".html",
		TestUtil.now("dd.MMMMM.yyyy hh.mm.ss aaa"), 
        "Monitor Prod",
        "3.1");
		
		
       //Loading Config File
		CONFIG = new Properties();
		FileInputStream fs = new FileInputStream(System.getProperty("user.dir")+"\\src\\core\\config.properties");
		CONFIG.load(fs);
		
		
		// LOAD Objects properties File
		Objects = new Properties();
		fs = new FileInputStream(System.getProperty("user.dir")+"\\src\\core\\object.properties");
		Objects.load(fs);
		
	
		//Load datatable
		Core= new Xlfile_Reader(System.getProperty("user.dir")+"\\src\\core\\Core.xlsx");
		testData  =  new Xlfile_Reader(System.getProperty("user.dir")+"\\src\\core\\TestData.xlsx");
		//DBresults = new Xlfile_Reader(System.getProperty("user.dir")+"\\src\\config\\db_data.xlsx");
		
		
		//Intialize selenium
		//selenium = new DefaultSelenium("localhost",nSelPort,CONFIG.getProperty("testBrowser"),CONFIG.getProperty("testSiteURL"));
	//	selenium.start();
	//	selenium.windowFocus();
	//	selenium.windowMaximize();
		//Initializing Webdriver
		 System.setProperty("webdriver.chrome.driver","C:\\Sanjana\\drivers\\chromedriver.exe");
				dr = new ChromeDriver();
				driver = new EventFiringWebDriver(dr);	
				
				//maximize window
				driver.manage().window().maximize();
				
				
				//wait for 30 seconds and then fail
				driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);	
				
		 
		//Create db connection
		//DbManager.setDbConnection();
	//	DbManager.setMysqlDbConnection();
		
		//Pass Query - Un Comment the below queries and use them for performing database testing
		
	//	List<String> values =DbManager.getQuery("select top 1* from employee");
	//	APPLICATION_LOGS.debug("Selected from employee, first column and first row value:"+values);
		
	//	List<String> values1 =DbManager.getMysqlQuery("select emp_name from employee limit 1");
	//	APPLICATION_LOGS.debug("Selected from employee name:"+values1);
		
	}
	
	@Test
	public void testApp() {
		
		String startTime=null;
		
		TestReports.startSuite("Suite 1");
		
		for(int tcid=2 ; tcid<=Core.getRowCount("Suite1");tcid++){
			currentTest = Core.getCellData("Suite1", "TCID", tcid);
			
			// initilize start time of test
			if(Core.getCellData("Suite1", "Runmode", tcid).equals("Y")){
				
				// executed the keywords
				
				// loop again - rows in test data
				int totalSets=testData.getRowCount(currentTest+"1");; // holds total rows in test data sheet. IF sheet does not exist then 2 by default
				if(totalSets<=1){
					totalSets=2; // run atleast once
				}
					
				for( testRepeat=2; testRepeat<=totalSets;testRepeat++){	
					startTime=TestUtil.now("dd.MMMMM.yyyy hh.mm.ss aaa");

				APPLICATION_LOGS.debug("Executing the test "+ currentTest);
				
				// implemented keywords file
				try{
				for(int tsid=2;tsid<=Core.getRowCount(currentTest);tsid++){
					
					
					// Get values from xls file
					
					keyword=Core.getCellData(currentTest, "Keyword", tsid);
					object=Core.getCellData(currentTest, "Object", tsid);
					currentTSID=Core.getCellData(currentTest, "TSID", tsid);
					stepDescription=Core.getCellData(currentTest, "Decription", tsid);
					proceedOnFail=Core.getCellData(currentTest, "ProceedOnFail", tsid);
					data_column_name=Core.getCellData(currentTest, "Data_Column_Name", tsid);
			     	System.out.println(keyword);
					Method method= KeywordsApp.class.getMethod(keyword);
					result = (String)method.invoke(method);
					APPLICATION_LOGS.debug("***Result of execution -- "+result);
					
					// take screenshot - every keyword
					String fileName="Suite1_TC"+(tcid-1)+"_TS"+tsid+"_"+keyword+testRepeat+".jpg";
				
					
					if(result.startsWith("Pass")){
						testStatus=result;
						
						//Uncomment this one to capture screenshots in case of Pass, For every test step a screenshot will be captured
						
					TestReports.addKeyword(stepDescription, keyword, result, "http://"+TestUtil.Handeler()+":8282//screenshots//"+TestUtil.imageNameIP+".jpeg");
					
					}
					
					else if(result.startsWith("Fail")){
							testStatus=result;
							// take screenshot - only on error
						TestUtil.captureScreenshot(CONFIG.getProperty("screenshotPath")+TestUtil.imageName+".jpeg");
					    	
							//changed to make the screenshot path generic
						TestReports.addKeyword(stepDescription, keyword, result, "http://"+TestUtil.Handeler()+":8282//screenshots//"+TestUtil.imageNameIP+".jpeg");
					
						//	mailscreenshotpath = "C:/CMAutomation/tomcat-6.0/webapps/ROOT/screenshots/"+currentTest+currentTSID+TestUtil.year+"_"+TestUtil.date+"_"+(TestUtil.month+1)+"_"+TestUtil.day+"_"+TestUtil.min+"_" +TestUtil.sec+".jpeg";
						mailscreenshotpath = TestUtil.imageName+".jpeg";
						System.out.println("your screenshot path :: "+ mailscreenshotpath);
						
						//System.out.println("Your attachemnt path"+ TestConfig.attachmentPath);
							mailresult=" - Script failed ";
							
							if(proceedOnFail.equalsIgnoreCase("N")){
								
								break;
								
								
							}
						break;
						
						}
					
					}
					
					
				}
				catch(Throwable t){
					APPLICATION_LOGS.debug("Error came");
					
				}
				
				// report pass or fail in HTML Report
				
				if(testStatus == null){
					testStatus="Pass";
				}
				APPLICATION_LOGS.debug("######################"+currentTest+" --- " +testStatus);
				TestReports.addTestCase(currentTest, 
										startTime, 
										TestUtil.now("dd.MMMMM.yyyy hh.mm.ss aaa"),
										testStatus );
				
				if(result.startsWith("Fail")){
				
					break; 
	                  }
				
				}// test data

				
				
				
			}else{
				APPLICATION_LOGS.debug("Skipping the test "+ currentTest);
				testStatus="Skip";
				
				// report skipped
				APPLICATION_LOGS.debug("#######################"+currentTest+" --- " +testStatus);
				TestReports.addTestCase(currentTest, 
										TestUtil.now("dd.MMMMM.yyyy hh.mm.ss aaa"), 
										TestUtil.now("dd.MMMMM.yyyy hh.mm.ss aaa"),
										testStatus );
				
			}
			
			testStatus=null;
			
			if(result.startsWith("Fail")){
                break; 
                }

		}
		TestReports.endSuite();
	}
	
	

	
	@AfterSuite
	public static void endScript() throws Exception{
		
		// Once the test is completed update the end time in HTML report
		TestReports.updateEndTime(TestUtil.now("dd.MMMMM.yyyy hh.mm.ss aaa"));
		
	
		
		// Sending Mail when script fails
		if(result.startsWith("Fail")){
			
		
		driver.quit();
		
			//monitoringMail.sendMail(TestConfig.server, TestConfig.from,TestConfig.username, TestConfig.password,TestConfig.port, TestConfig.to, TestConfig.subject+mailresult+" "+"on step "+stepDescription+'-'+" "+result+" "+" "+"Timed out after "+Keywords.globalwait+" seconds", TestConfig.messageBody, mailscreenshotpath, TestConfig.attachmentName);
		monitoringMail.sendMail(TestConfig.server, TestConfig.from, currentTSID, currentTSID, currentTSID, TestConfig.to, TestConfig.subject+mailresult+" "+"on step "+stepDescription+'-'+" "+result+" "+" "+"Timed out after "+KeywordsApp.globalwait+" seconds", TestConfig.messageBody, mailscreenshotpath, TestConfig.attachmentName);
			
		}
		
		// Sending Mail After Execution of All TestCases ON HOLD AND RECOMENDED ONLY FOR COMPLETE BVT
		
		//monitoringMail.sendMail(TestConfig.server, TestConfig.from, TestConfig.to, TestConfig.subject+mailresult, TestConfig.messageBody, TestConfig.attachmentPath, TestConfig.attachmentName);
		
		//or
		
		//	monitoringMail.sendMail(TestConfig.server, TestConfig.from, TestConfig.to, TestConfig.subject, TestConfig.messageBody, TestConfig.attachmentPath, TestConfig.attachmentName);
		
	//driver.quit();
		

	}
	

}
