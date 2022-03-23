package com.mach.core.util;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static com.mach.core.util.UtilValidate.getLastTwoCharacter;

public class DateUtil {

	private static final String LANGUAGE = "ES";
	private static final String TIME_PATTERN = "HH:mm";
	private static final String TIME_ZONE = "America/Bogota";

	private DateUtil() {
		throw new IllegalStateException("Utility class");
	}

	public static String plusMinutes(String time, int count) {
		return DateTimeFormatter.ofPattern(TIME_PATTERN).format(LocalTime.parse(time).plusMinutes(count));
	}

	public static String minusMinutes(String time, int count) {
		return DateTimeFormatter.ofPattern(TIME_PATTERN).format(LocalTime.parse(time).minusMinutes(count));
	}

	public static String plusHours(String time, int count) {
		return DateTimeFormatter.ofPattern(TIME_PATTERN).format(LocalTime.parse(time).plusHours(count));
	}

	public static String minusHours(String time, int count) {
		return DateTimeFormatter.ofPattern(TIME_PATTERN).format(LocalTime.parse(time).minusHours(count));
	}

	public static List<String> altTimes(String time) {
        List<Integer> hourList = Arrays.asList(0, 1, 2, 3, 4, 5, 7);
        List<Integer> minList = Arrays.asList(0, 1);
        List<String> timeList = new ArrayList<>();
        for (Integer hour : hourList) {
        	for (Integer min : minList) {
	    		timeList.add(plusHours(plusMinutes(time, min), hour));
	    		timeList.add(minusHours(plusMinutes(time, min), hour));
        	}
        }
        return timeList.stream().distinct().collect(Collectors.toList());
    }

	public static List<String> altTimesMinutes(String time) {
		List<Integer> minList = Arrays.asList(0, 1, 2);
		List<String> timeList = new ArrayList<>();
			for (Integer min : minList) {
				timeList.add(getLastTwoCharacter(plusMinutes(time, min)));
				timeList.add(getLastTwoCharacter(minusMinutes(time, min)));
			}
        return timeList.stream().distinct().collect(Collectors.toList());
	}

	public static List<String> altTimesBalance(String time) {
		List<Integer> minList = Arrays.asList(0, 1, 2);
		List<String> timeList = new ArrayList<>();
		for (Integer min : minList) {
			timeList.add(plusMinutes(time, min));
			timeList.add(minusMinutes(time, min));
		}
		return timeList.stream().distinct().collect(Collectors.toList());
	}

	public static String getDeviceTime(AppiumDriver<MobileElement> driver) {
		return driver.getDeviceTime().substring(11, 16);
	}

	public static String getFirstDayNextMonth() {
		return new SimpleDateFormat(EnumTime.DDMMMMYYYY.getFormat(), new Locale(LANGUAGE)).format(Date
				.from(LocalDate.now().atStartOfDay(ZoneId.of(TIME_ZONE)).plusMonths(1).withDayOfMonth(1).toInstant()));
	}

	public static String getTimeByFormat(String format) {
		return new SimpleDateFormat(format, Locale.ENGLISH).format(new Date());
	}

	public static String getHourByZoneId(String zoneId, String time) {
		return DateTimeFormatter
				.ofPattern(
						EnumTime.HM.getFormat())
				.format(LocalDateTime
						.ofInstant(
								LocalDateTime
										.of(LocalDate.now(),
												LocalTime.parse(time,
														DateTimeFormatter.ofPattern(EnumTime.HM.getFormat())))
										.atZone(ZoneId.of(TIME_ZONE)).toInstant(),
								ZoneId.of(zoneId)));
	}
}