package org.fastcatsearch.analytics.analysis.web.xss;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by white on 2016-07-25.
 */
public class XSSRequestWrapperTest {

    private static Logger logger = LoggerFactory.getLogger(XSSRequestWrapperTest.class);

    @Test
    public void testReplaceText() {

        String text = "abcabcaaacccbbbasdf";
        logger.debug(text.trim().replace("a", "T"));
    }
}