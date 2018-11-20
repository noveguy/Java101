package examples;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternMatching {
	public static String input = "Writing data: 900k/8466k (10%)";
	static String EbOutput = "Erasing blocks: 2/133 (1%)";
	static String VdOutput = "Verifying data: 1680k/8466k (19%)";
	
	static String smpmpOutput = "Programing FW file: 87552/120064 (72%)";
	static String smpmrOutput = "Reading from EEPROM: 18688/120064 (15%)";
	
	static String csrfMWTokenLine = "<input type='hidden' name='csrfmiddlewaretoken' value='CfZW5oZhzMax0eM5uzePO7IjwkC0s4UhQBVWTWqoRN5Ws2kNCHVWTydFc4QP41rU' />";
	
	static String loginCookie = "[sessionid=sw2ohui0v3ud5z4uwx3wzoz15739poa5; expires=Thu, 15-Nov-2018 03:17:44 GMT; httponly; Max-Age=1209600; Path=/, "
			+ "csrftoken=fY2d6OV4uEp8y0rB6WShIZWNNetCrWDCxd1gU2F0RDMbvDK4KPt943VbEY3CY8Pl; expires=Thu, 31-Oct-2019 03:17:44 GMT; Max-Age=31449600; Path=/]";
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println(csrfMWTokenLine);
		Pattern pattern = Pattern.compile("(?m)(Writing data:.*?)(\\d{1,})(.*?)($)");
		Pattern ebtbPattern = Pattern.compile("(?m)(Erasing blocks:.*?)(/)(\\d{1,})(.*?)($)");
//		Pattern ebPattern = Pattern.compile("(?m)(Erasing blocks: )(\\d{1,})(.*?)(\\()(\\d{1,})(\\%)(\\))($)");
		Pattern ebPattern = Pattern.compile("(?m)(Erasing blocks:.*?)(\\d{1,})(.*?)($)");
		Pattern vdPattern = Pattern.compile("(?m)(Verifying data:.*?)(\\d{1,})(.*?)($)");
		
		Pattern csrfMWTokenPattern = Pattern.compile("<(input type='hidden' )(name='csrfmiddlewaretoken' value=')(.*?)(' /)>");
		Matcher csrfTokenMatcher = csrfMWTokenPattern.matcher(csrfMWTokenLine);
		if (csrfTokenMatcher.find()) {
			System.out.println(csrfTokenMatcher.group(3));
		}
		
//		Pattern loginCookiePattern = Pattern.compile("(^\\[sessionid=)(.*?)(;.*?)(csrftoken=)(.*?)(;)(.*?\\])");
		Pattern loginCookiePattern = Pattern.compile("(^\\[sessionid=)([a-zA-Z0-9]*?)(;.*?)(csrftoken=)(.*?)(;)(.*?\\])");
		Matcher loginCookieMatcher = loginCookiePattern.matcher(loginCookie);
		if (loginCookieMatcher.find()) {
			System.out.println(loginCookieMatcher.group(2));
			System.out.println(loginCookieMatcher.group(5));
		}
		
		Matcher matcher = pattern.matcher(input);
		Matcher TotalBlock = ebtbPattern.matcher(EbOutput);
		Matcher BlockNumErase = ebPattern.matcher(EbOutput);
		Matcher DataVerified = vdPattern.matcher(VdOutput);
		if (matcher.find() /*&& (matcher.groupCount() >= 3)*/) {
			System.out.println(matcher.group(2));
		}
		if (TotalBlock.find() /*&& (matcher.groupCount() >= 3)*/) {
			System.out.println(TotalBlock.group(3));
		} else {
			System.out.println("Failed to locate total block");
		}
		if (BlockNumErase.find() /*&& (matcher.groupCount() >= 3)*/) {
			System.out.println(BlockNumErase.group(2));
		} else {
			System.out.println("Failed to locate erased block");
		}
		if (DataVerified.find() /*&& (matcher.groupCount() >= 3)*/) {
			System.out.println(DataVerified.group(2));
		} else {
			System.out.println("Failed to locate data verified");
		}
		
		Pattern smpmPrograming = Pattern.compile("(?m)(Programing FW file:.*?)(\\d{1,})(.*?)($)");
		Pattern smpmTotalSize = Pattern.compile("(?m)(Programing FW file:.*?)(/)(\\d{1,})(.*?)($)");
		Matcher dataProgramed = smpmPrograming.matcher(smpmpOutput);
		Matcher totalSize = smpmTotalSize.matcher(smpmpOutput);
		if (dataProgramed.find()) {
			System.out.println(dataProgramed.group(2));
//			System.out.println(dataProgramed.group(4));
		} else {
			System.out.println("Failed to locate total data programed");
		}
		
		if (totalSize.find()) {
			System.out.println(totalSize.group(3));
		} else {
			System.out.println("Failed to locate total data size");
		}
		
//		Pattern smpmReading = Pattern.compile("(?m)(Reading from EEPROM:.*?)(\\d{1,})(.*?)(\\()(\\d{1,})(\\%)(\\))($)");
		Pattern smpmReading = Pattern.compile("(?m)(Reading from EEPROM:.*?)(\\d{1,})(.*?)($)");
		Matcher dataRead = smpmReading.matcher(smpmrOutput);
		if (dataRead.find()) {
			System.out.println(dataRead.group(2));
		} else {
			System.out.println("Failed to locate total data read");
		}
//		if (dataProgramed.find() /*&& (matcher.groupCount() >= 3)*/) {
//			System.out.println(dataProgramed.group(1));
//		} else {
//			System.out.println("Failed to locate data programed");
//		}
	}

}
