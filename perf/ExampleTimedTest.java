import junit.framework.Test;

import com.clarkware.junitperf.ExampleTestCase;
import com.clarkware.junitperf.TimedTest;

public class ExampleTimedTest {

    public static Test suite() {
        
        long maxElapsedTime = 1000;
        
        Test testCase = new ExampleTestCase("testOneSecondResponse");
        Test timedTest = new TimedTest(testCase, maxElapsedTime);
        
        return timedTest;
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }
}