package teammates.test.cases.ui;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.util.Const;

public class AdminActivityLogPageActionTest extends BaseActionTest {

    // private final DataBundle dataBundle = getTypicalDataBundle();

    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        uri = Const.ActionURIs.ADMIN_ACTIVITY_LOG_PAGE;
        // removeAndRestoreTypicalDataInDatastore();
    }
    
    @Test
    public void testExecuteAndPostProcess() throws Exception{
        
        //TODO: implement this
    }

}
