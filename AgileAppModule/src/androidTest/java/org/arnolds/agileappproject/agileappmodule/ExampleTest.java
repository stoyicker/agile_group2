package org.arnolds.agileappproject.agileappmodule;

import android.test.InstrumentationTestCase;

/**
 * Created by Robert on 2014-03-28.
 */
public class ExampleTest extends InstrumentationTestCase {
    public void test() throws Exception {
        final int expected = 1;
        final int reality = 5;
        assertEquals(expected, reality);
    }
}