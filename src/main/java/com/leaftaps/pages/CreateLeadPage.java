package com.leaftaps.pages;

import com.framework.selenium.api.design.Locators;
import com.framework.testng.api.base.ProjectSpecificMethods;

public class CreateLeadPage extends ProjectSpecificMethods{
	
	public CreateLeadPage enterCompanyName(String data) {
		clearAndType(locateElement(Locators.ID, "createLeadForm_companyName"), data);
		reportStep(data+" company name is entered successfully","pass");
		return this;
	}
	public CreateLeadPage enterFirstName(String data) {
		clearAndType(locateElement(Locators.ID, "createLeadForm_firstName"), data);
		reportStep(data+" first name is entered successfully","pass");
		return this;
	}
	
	public CreateLeadPage enterLastName(String data) {
		clearAndType(locateElement(Locators.ID, "createLeadForm_lastName"), data);
		reportStep(data+" last name is entered successfully","pass");
		return this;
	}
	
	public ViewLeadPage clickCreateLeadButton() {
		click(locateElement(Locators.NAME, "submitButton"));
		reportStep("Create Lead button is clicked successfully", "pass");
		return new ViewLeadPage();
	}

}
