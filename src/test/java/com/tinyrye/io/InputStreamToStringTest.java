package com.tinyrye.io;

import org.junit.Assert;
import org.junit.Test;

public class InputStreamToStringTest
{
    @Test
    public void testRunToString() {
        Assert.assertEquals("Na einai kalyteros anthropo apo ton patera tou!",
            new InputStreamToString(getClass().getResourceAsStream("peterbishop.txt")).runToString());
    }
}