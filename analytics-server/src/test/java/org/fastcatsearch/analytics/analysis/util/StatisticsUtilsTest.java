package org.fastcatsearch.analytics.analysis.util;

import org.fastcatsearch.analytics.analysis.StatisticsUtils;
import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;

/**
 * Created by swsong on 2016. 3. 12..
 */
public class StatisticsUtilsTest {
    @Test
    public void testGetTimeId() {
        assertEqualsYearWeek("2015.12.27", 2015, 52);
        assertEqualsYearWeek("2015.12.28", 2015, 53);
        assertEqualsYearWeek("2015.12.29", 2015, 53);
        assertEqualsYearWeek("2015.12.30", 2015, 53);
        assertEqualsYearWeek("2015.12.31", 2015, 53);
        assertEqualsYearWeek("2016.01.01", 2015, 53);
        assertEqualsYearWeek("2016.01.02", 2015, 53);
        assertEqualsYearWeek("2016.01.03", 2015, 53);
        assertEqualsYearWeek("2016.01.04", 2016, 1);
    }

    private void assertEqualsYearWeek(String timeText, int year, int week) {
        String yearString = String.valueOf(year);
        String weekString = week > 9 ? String.valueOf(week) : "0" + String.valueOf(week);
        Calendar calendar = StatisticsUtils.parseDatetimeString(timeText, true);
        String[] timeComponent = StatisticsUtils.getTimeComponent(calendar, Calendar.WEEK_OF_YEAR);
        System.out.println(timeText + " > " + year + ", " + week + " :: " + timeComponent[0] + ", " + timeComponent[4]);
        assertEquals(yearString, timeComponent[0]);
        assertEquals(weekString, timeComponent[4]);
    }

    @Test
    public void testCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2015);
        calendar.set(Calendar.MONTH, 11);
        calendar.set(Calendar.DATE, 27);
        String[] timeComponent = StatisticsUtils.getTimeComponent(calendar, Calendar.WEEK_OF_YEAR);
        System.out.println(calendar.getTime());
        System.out.println(timeComponent[0] + ", " + timeComponent[4]);
    }

    @Test
    public void testParseDatetimeString() {
        String timeText = "2016.01.01";
        Calendar calendar = StatisticsUtils.parseDatetimeString(timeText, true);
        System.out.println(calendar.getTime());
    }
}
