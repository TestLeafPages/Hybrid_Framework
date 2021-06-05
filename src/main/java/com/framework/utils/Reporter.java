package com.framework.utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;

import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.testng.ITestResult;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.internal.IResultListener2;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.MediaEntityModelProvider;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.ChartLocation;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.framework.selenium.api.base.DriverInstance;

public abstract class Reporter extends DriverInstance {

	private static ExtentReports extent;
	private static final ThreadLocal<ExtentTest> parentTest = new ThreadLocal<ExtentTest>();
	private static final ThreadLocal<ExtentTest> test = new ThreadLocal<ExtentTest>();
	private static final ThreadLocal<String> testName = new ThreadLocal<String>();
	private static final HashMap<String, Object> JsonString = new HashMap<String, Object>();
	private String fileName = "result.html";
	private String pattern = "dd-MMM-yyyy HH-mm-ss";

	public String testcaseName, testDescription, authors, category, dataFileName, dataFileType, excelFileName;
	public static String folderName = "";

	@BeforeSuite(alwaysRun = true)
	public synchronized void startReport() {
		String date = new SimpleDateFormat(pattern).format(new Date());
		folderName = "reports/" + date;

		File folder = new File("./" + folderName);
		if (!folder.exists()) {
			folder.mkdir();
		}
		ExtentHtmlReporter htmlReporter = new ExtentHtmlReporter("./" + folderName + "/" + fileName);
		htmlReporter.config().setTestViewChartLocation(ChartLocation.BOTTOM);
		htmlReporter.config().setChartVisibilityOnOpen(!true);
		htmlReporter.config().setTheme(Theme.STANDARD);
		htmlReporter.config().setDocumentTitle("Bid Proc");
		htmlReporter.config().setEncoding("utf-8");
		htmlReporter.config().setReportName("Bid Proc");
		htmlReporter.setAppendExisting(true);
		extent = new ExtentReports();
		extent.attachReporter(htmlReporter);
	}

	@BeforeClass(alwaysRun = true)
	public synchronized void startTestCase() {
		ExtentTest parent = extent.createTest(testcaseName, testDescription);
		parent.assignCategory(category);
		parent.assignAuthor(authors);
		parentTest.set(parent);
		testName.set(testcaseName);
	}

	public synchronized void setNode() {
		ExtentTest child = parentTest.get().createNode(getTestName());
		test.set(child);
	}

	public abstract long takeSnap();

	public void reportStep(String desc, String status, boolean bSnap) {
		synchronized (test) {

			// Start reporting the step and snapshot
			MediaEntityModelProvider img = null;
			if (bSnap && !(status.equalsIgnoreCase("INFO") || status.equalsIgnoreCase("skipped")
					|| status.equalsIgnoreCase("pass"))) {
				long snapNumber = 100000L;
				snapNumber = takeSnap();
				try {
					img = MediaEntityBuilder
							.createScreenCaptureFromPath("./../../" + folderName + "/images/" + snapNumber + ".jpg")
							.build();
				} catch (IOException e) {
				}
			}
			if (status.equalsIgnoreCase("pass")) {
				test.get().pass(desc, img);
			} else if (status.equalsIgnoreCase("fail")) { // additional steps to manage alert pop-up
				test.get().fail(desc, img);
				throw new RuntimeException("See the reporter for details.");

			} else if (status.equalsIgnoreCase("warning")) {
				test.get().warning(desc, img);
			} else if (status.equalsIgnoreCase("skipped")) {
				test.get().skip("The test is skipped due to dependency failure");
			} else if (status.equalsIgnoreCase("INFO")) {
				test.get().info(desc);
			}

			/*
			 * Check if there is logs
			 */
			/*
			 * if (!(status.equalsIgnoreCase("skipped") || status.equalsIgnoreCase("INFO")
			 * || parentTest.get().getModel().getCategoryContext().get(0).getName().
			 * equalsIgnoreCase("REST"))) { LogEntries jserrors =
			 * getDriver().manage().logs().get(LogType.BROWSER); for
			 * (org.openqa.selenium.logging.LogEntry error : jserrors) { if
			 * (error.getLevel() == Level.SEVERE) {
			 * test.get().warning("JavaScript Error Found : " + error.getMessage()); } } }
			 */
		}
	}

	public void reportStep(String desc, String status) {
		reportStep(desc, status, true);
	}

	@AfterSuite(alwaysRun = true)
	public synchronized void endResult() {
		try {
			if (getDriver() != null) {
				getDriver().quit();
			}
		} catch (UnreachableBrowserException e) {
		}
		extent.flush();
	}

	public void setValue(String key, String value) {
		JsonString.put(key, value);
	}

	public void setValue(String key, int value) {
		JsonString.put(key, value);
	}

	public Object getValue(String key) {
		return JsonString.get(key);
	}

	public String getTestName() {
		return testName.get();
	}

	public Status getTestStatus() {
		return parentTest.get().getModel().getStatus();
	}
}