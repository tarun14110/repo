package teammates.test.cases.ui.browsertests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.test.pageobjects.AdminEmailPage;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;

public class AdminEmailPageUiTest extends BaseUiTestCase {
    private static Browser browser;
    private static AdminEmailPage emailPage;
    
    public static final int ADMIN_EMAIL_TABLE_NUM_COLUMNS = 5;

    @BeforeClass
    public static void classSetup() {
        printTestClassHeader();
        browser = BrowserPool.getBrowser();
    }
    
    @Test 
    public void allTests() {    
        testCompose();
        testSent();
        testDraft();
        testTrash();
    }
    
    private void testCompose() {
        ______TS("email compose page");
        
        emailPage = loginAdminToPageForAdminUiTests(
                        browser, createUrl(Const.ActionURIs.ADMIN_EMAIL_COMPOSE_PAGE), AdminEmailPage.class);
        assertTrue(isEmailComposeElementsPresent());
        
        ______TS("send email - no recipient");
        
        emailPage.clickSendButton();
        emailPage.verifyStatus("Error : No reciver address or file given");
        
        ______TS("send email - recipient email format error");
        
        emailPage.inputRecipient("recipient");
        emailPage.inputSubject("Email Subject");
        emailPage.inputContent("Email Content");
        emailPage.clickSendButton();
        assertTrue(hasStatusMessageRecipientEmailFormatError("recipient"));
        
        ______TS("send email - no subject");
        
        emailPage.inputRecipient("recipient@email.tmt");
        emailPage.clearSubjectBox();
        emailPage.clickSendButton();
        assertTrue(hasStatusMessageNoSubject());
        
        ______TS("send email - success");
        
        emailPage.inputSubject("Email Subject");
        emailPage.clickSendButton();
        assertFalse(hasErrorMessage());
        assertTrue(isEmailComposeElementsPresent());
        
        ______TS("save email - success");
        
        emailPage.inputRecipient("recipient@email.tmt");
        emailPage.inputSubject("Email Subject");
        emailPage.inputContent("Email to save");
        emailPage.clickSaveButton();
        emailPage.verifyStatus("Email draft has been saved");
    }

    private void testSent() {
        emailPage.clickSentTab();
        assertTrue(isEmailSentDataDisplayCorrect());
    }

    private void testDraft() {
        emailPage.clickDraftTab();
        assertTrue(isEmailDraftDataDisplayCorrect());
    }

    private void testTrash() {
        emailPage.clickTrashTab();
        assertTrue(isEmailTrashDataDisplayCorrect());
    }
    
    private boolean isEmailComposeElementsPresent() {
        return emailPage.isElementPresent(By.id("addressReceiverEmails"))
            && emailPage.isElementPresent(By.name("emailsubject"))
            && emailPage.isElementPresent(By.className("mce-tinymce"))
            && emailPage.isElementPresent(By.id("adminEmailBox"))
            && emailPage.isElementPresent(By.id("composeSubmitButton"))
            && emailPage.isElementPresent(By.id("composeSaveButton"));
    }
    
    private boolean hasStatusMessageRecipientEmailFormatError(String recipientName) {
        return emailPage.getStatus().contains(
                String.format(FieldValidator.EMAIL_ERROR_MESSAGE, recipientName, FieldValidator.REASON_INCORRECT_FORMAT));
    }
    
    private boolean hasStatusMessageNoSubject() {
        return emailPage.getStatus().equals(
                String.format(FieldValidator.EMAIL_SUBJECT_ERROR_MESSAGE, "", FieldValidator.REASON_EMPTY));
    }
    
    private boolean hasErrorMessage() {
        return emailPage.isElementPresent(By.className("alert-danger"));
    }
    
    /**
     * This method only checks if the email sent data table is displayed correctly
     * i.e, table headers are correct
     * It does not test for the table content
     */
    private boolean isEmailSentDataDisplayCorrect() {
        return emailPage.isElementPresent(By.className("table")) && isEmailTableHeaderCorrect();
    }
    
    /**
     * This method only checks if the email draft data table is displayed correctly
     * i.e, table headers are correct
     * It does not test for the table content
     */
    private boolean isEmailDraftDataDisplayCorrect() {
        return emailPage.isElementPresent(By.className("table")) && isEmailTableHeaderCorrect();
    }
    
    /**
     * This method only checks if the email trash data table is displayed correctly
     * i.e, table headers are correct
     * It does not test for the table content
     */
    private boolean isEmailTrashDataDisplayCorrect() {
        return emailPage.isElementPresent(By.className("table")) 
            && isEmptyTrashButtonPresent() 
            && isEmailTableHeaderCorrect();
    }

    private boolean isEmailTableHeaderCorrect() {
        int numColumns = emailPage.getNumberOfColumnsFromDataTable(0); // 1 table
        
        if (numColumns != ADMIN_EMAIL_TABLE_NUM_COLUMNS) {
            return false;
        }
        
        List<String> expectedSessionTableHeaders = Arrays.asList("Action",
                                                                 "Address Receiver",
                                                                 "Group Receiver",
                                                                 "Subject",
                                                                 "Date");
        List<String> actualSessionTableHeaders = new ArrayList<String>();
        
        for (int i = 0; i < numColumns; i++) {
            actualSessionTableHeaders.add(emailPage.getHeaderValueFromDataTable(0, 0, i));
        }
        
        return actualSessionTableHeaders.equals(expectedSessionTableHeaders);
    }
    
    private boolean isEmptyTrashButtonPresent() {
        if (!emailPage.isElementPresent(By.className("btn-danger"))) {
            return false;
        }
        
        WebElement trashButton = browser.driver.findElement(By.className("btn-danger"));
        
        return trashButton.getText().contains("Empty Trash");
    }
    
    @AfterClass
    public static void classTearDown() {
        BrowserPool.release(browser);
    }
}
