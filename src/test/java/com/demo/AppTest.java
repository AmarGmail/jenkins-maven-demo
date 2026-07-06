package com.demo;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class AppTest {

    @Test
    public void testAdd() {
        int result = App.add( 10, 20);
        assertEquals(30, result);
    }
}
