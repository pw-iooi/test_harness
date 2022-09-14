/**
 * @author Patricia Wong (20121668)
 * ARTHarnessTest - TDD approach for ARTHarness
 *
 */
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Random;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

public class TestHarnessTest {

    private TestHarness t;
    private ArrayList array;
    private Random rand = new Random(7);

    @Before
    public void setUp() throws Exception {
        t = new TestHarness();
        this.array = new ArrayList<String>();
        array.add("0.00");
        array.add("0.5");
        array.add("0.00");
        array.add("0.5");
    }

//    @Test
//    public void createOneTestCase() throws Exception {
//        float rand1 = -0.269301f;
//        float rand2 = -0.36146235f;
//        float output1 = 0.5f - rand1;
//        float output2 = 0.5f - rand2;
//        String[] expectedOutput = {Float.toString(output1),Float.toString(output2)};
//        assertArrayEquals(expectedOutput, t.createTestCase(2,array, rand));
//    }

    @Test
    public void executeTestCaseInOracle() throws Exception {
        String[] input = {"0.0", "0.1", "0.0", "0.4"};
        assertEquals(0.5, t.executeTestCase(input, "./src/Oracle"), 0.001);
    }

    @Test
    public void executeTestCaseInPUTFailureRegion() throws Exception {
        String[] input = {"0.0", "0.1", "0.0", "0.4"};
        // 0.5 is in the failure region, return 0.0
        assertEquals(0.0, t.executeTestCase(input, "./src/PUT"), 0.001);
    }

    @Test
    public void executeTestCaseInPUT() throws Exception {
        String[] input = {"0.0", "0.1", "0.0", "0.3"};
        // 0.5 is in the failure region, return 0.0
        assertEquals(0.4, t.executeTestCase(input, "./src/PUT"), 0.001);
    }

    @Test
    public void compareTwoFloats() throws Exception {
        assertTrue(t.compareFloatNum(0.1f, 0.099f));
        assertTrue(t.compareFloatNum(0.01f, 0.0099f));
        assertTrue(t.compareFloatNum(0.001f, 0.00099f));
        assertTrue(t.compareFloatNum(3.1428f, 3.1415f));
        assertTrue(t.compareFloatNum(1.0f, 0.99f));
        assertFalse(t.compareFloatNum(0.1f, 0.01f));
        assertFalse(t.compareFloatNum(0.1f, 0.12f));
    }

    @Test
    public void getEuclideanDist() throws Exception {
        // 2-d dist calculation
        String[] input1 = {"3.0", "4.0"};
        String[] input2 = {"7.0", "1.0"};
        assertEquals(5.0f, t.getEuclideanDist(input1, input2), 0.001);
        // 3-d dist calculation
        String[] input3 = {"7.0", "4.0", "3.0"};
        String[] input4 = {"17.0", "6.0", "2.0"};
        assertEquals(10.246951f, t.getEuclideanDist(input3, input4), 0.001);
        // 4-d dist calculation
        String[] input5 = {"1.0", "2.0", "3.0","5.0"};
        String[] input6 = {"3.0", "5.0", "6.0", "1.0"};
        assertEquals(6.164f, t.getEuclideanDist(input5, input6), 0.001);
    }




} // end ARTHarnessTest
