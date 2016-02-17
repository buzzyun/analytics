package org.fastcatsearch.analytics.analysis;

import junit.framework.Assert;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by swsong on 2016. 2. 17..
 */
public class CalendarTest {

    /*
     * Week 선택시 날짜를 월요일로 셋팅할 경우,
     * 마지막 주 월요일의 year 는 전년도로 나오고, week는 1로 나오게 되므로 문제발생.
     * 그러므로 Sunday로 잡아야 함.
    * */
    @Test
    public void testCalendarWeek() {
        int year = 2012;
        Calendar calendar = Calendar.getInstance(Locale.GERMAN);
        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, 11);
        calendar.set(Calendar.DATE, 31);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        System.out.println(calendar.getTime());
        System.out.println("year = " + calendar.get(Calendar.YEAR)
                        + ", week = " + calendar.get(Calendar.WEEK_OF_YEAR)
                        + ", dayOfWeek = " + calendar.get(Calendar.DAY_OF_WEEK)
        );
        String timeId = StatisticsUtils.getTimeId(calendar, Calendar.WEEK_OF_YEAR);
        System.out.println("timeid = " + timeId);

        Assert.assertEquals("W201301", timeId);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

        System.out.println(calendar.getTime());
        System.out.println("year = " + calendar.get(Calendar.YEAR)
                        + ", week = " + calendar.get(Calendar.WEEK_OF_YEAR)
                        + ", dayOfWeek = " + calendar.get(Calendar.DAY_OF_WEEK)
        );

        timeId = StatisticsUtils.getTimeId(calendar, Calendar.WEEK_OF_YEAR);
        System.out.println("timeid = " + timeId);
        Assert.assertEquals("W201301", timeId);
    }


    @Test
    public void testChangeYearRange() {

        String timeFrom = "2012.12.30";
        String timeTo = "2013.1.1";

        Calendar startTime = StatisticsUtils.parseDatetimeString(timeFrom, true);
        Calendar endTime = StatisticsUtils.parseDatetimeString(timeTo, false);

        startTime.set(Calendar.DAY_OF_MONTH, 1);
        startTime.add(Calendar.MONTH, -2); //2달간.
        startTime = StatisticsUtils.getFirstDayOfWeek(startTime);
        endTime = StatisticsUtils.getLastDayOfWeek(endTime);

        System.out.println(startTime.getTime());
        System.out.println(endTime.getTime());
    }



}
