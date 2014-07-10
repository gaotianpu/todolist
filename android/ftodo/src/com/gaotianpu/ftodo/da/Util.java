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
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date date = sdf.parse(dateString);
			return date;
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;	 
	}
	
	public static String getAddDateStr(Date date,int days) {
		//Date date = new Date();// 取时间
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(calendar.DATE, days);
		date = calendar.getTime();
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
		// Date date = fmt.parse(szDate);
		String tommorrow = fmt.format(calendar.getTime());
		return tommorrow;
	}
	
	public static String getNextDate(String currentDate,int sort){
		Date current = str2Date(currentDate);
		
		switch(sort){
		case 1: //day
			//day+1
			return getDateStr(1);			 
		case 2: //week
			//day+7
			//先算出，星期几
			//今天是星期几，
			//距离设定的星期几，差几天，还是多几天
			return getAddDateStr(current,7);			 
		case 3: //month			 
			return getDateOfNextMonth(currentDate);  
		case 4: //year
			//有一个例外，闰年2-29，下一年则没有2-29 
			return getDateOfNextYear(currentDate); 
		}
		
		
		return "";
	}
	
	public static Calendar getDateOfNextYear(Calendar date) {
		Calendar lastDate = (Calendar) date.clone();
		lastDate.add(Calendar.YEAR,  1);
		return lastDate;
	}
	
	public static String getDateOfNextYear(String dateStr) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date;
		try {
			date = sdf.parse(dateStr);
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			//return getDateOfLastMonth(c);
			
			return sdf.format(getDateOfNextYear(c).getTime());
			
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "";
		
	}

	
	public static Calendar getDateOfNextMonth(Calendar date) {
		Calendar lastDate = (Calendar) date.clone();
		lastDate.add(Calendar.MONTH,  1);
		return lastDate;
	}

	public static String getDateOfNextMonth(String dateStr) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date;
		try {
			date = sdf.parse(dateStr);
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			//return getDateOfLastMonth(c);
			
			return sdf.format(getDateOfNextMonth(c).getTime());
			
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "";
		
	}

	 


}
