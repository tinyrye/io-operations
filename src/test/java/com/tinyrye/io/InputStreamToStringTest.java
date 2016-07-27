package com.tinyrye.io;

import org.junit.Assert;
import org.junit.Test;

public class InputStreamToStringTest
{
    @Test
    public void testRunToString() {
        Assert.assertEquals("Na einai kalytera anthropo apo ton patera toy!",
            new InputStreamToString(getClass().getResourceAsStream("peterbishop.txt")).runToString());
    }
}