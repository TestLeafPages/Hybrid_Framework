package com.leaftaps.testcases;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.framework.testng.api.base.ProjectSpecificMethods;
import com.leaftaps.pages.LoginPage;

public class TC002_CreateLead extends ProjectSpecificMethods{
	@BeforeTest
	public void setValues() {
		testcaseName = "Create Lead";
		testDescription ="Create Lead with mandatory fields";
		authors="Hari";
		category ="Smoke";
		excelFileName="CreateLead";
	}
	
	@Test(dataProvider = "fetchData")
	public void runLogin(String username, String password,String firstName, String lastName, String company) {
		new LoginPage()
		.enterUsername(username)
		.enterPassword(password)
		.clickLogin()
		.clickCrmsfaLink()
		.clickLeadsLink()
		.clickCreateLeadLink()
		.enterFirstName(firstName)
		.enterLastName(lastName)
		.enterCompanyName(company)
		.clickCreateLeadButton()
		.verifyFirstName(firstName);

	}

}
