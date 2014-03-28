package test;


import android.test.InstrumentationTestCase;

/**
 * Created by thrawn on 28/03/14.
 */
public class ExampleTest extends InstrumentationTestCase {
    public void test() throws Exception {
        final int expected = 1;
        final int reality = 5;
        assertEquals(expected, reality);
    }
}

