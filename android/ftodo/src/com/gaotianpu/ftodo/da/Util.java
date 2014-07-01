package com.gaotianpu.ftodo.da;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import android.net.ParseException;

public class Util {
	public static String getDateStr(int days) {
		Date date = new Date();// 取时间
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(calendar.DATE, days);
		date = calendar.getTime();
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
		// Date date = fmt.parse(szDate);
		String tommorrow = fmt.format(calendar.getTime());
		return tommorrow;
	}
	
	public static List  getPickDates(){
		List list=new ArrayList(); 
		list.add( "今天 "  + getDateStr(0)  );
		list.add( "明天 " + getDateStr(1)  );
		list.add("后天 "  + getDateStr(2)  );
		list.add("10天后 " + getDateStr(10)   );
		return list;
		 
		//list.add(“王利虎”);
//		String[] array = (String[])list.toArray(new String[list.size()]);
//		return array;
		
	}
	
	public static Date str2Date(String dateString) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd ");
		try {
			Date date = sdf.parse(dateString);
			return date;
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;	 
	}
}
