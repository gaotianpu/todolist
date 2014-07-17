package com.gaotianpu.ftodo.da;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import android.net.ParseException;
import android.util.Log;

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
	
	public static String GetCurrentWeekDay(int count){
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(new Date());
		int today_week_day = calendar.get(Calendar.DAY_OF_WEEK);
		int left_day = count - today_week_day;
		return getDateStr(left_day);
	}
	
	public static String getNextWeekDay2(String remindDate){
		//先算出，星期几
		//今天是星期几，
		//距离设定的星期几，差几天，还是多几天 		
		Date current = str2Date(remindDate);  
		
		Calendar calendar1 = new GregorianCalendar();
		calendar1.setTime(current);
		int remind_week_day = calendar1.get(Calendar.DAY_OF_WEEK);	
		
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(new Date());	
		
		 
		calendar1.add(calendar1.DATE, 7); 
		
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");		 
		String next = fmt.format(calendar1.getTime());
		return next;
	}
	
	public static String getNextWeekDay(String remindDate){
		//先算出，星期几
		//今天是星期几，
		//距离设定的星期几，差几天，还是多几天 
		
		Date current = str2Date(remindDate);  
		 
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(new Date());
		int today_week_day = calendar.get(Calendar.DAY_OF_WEEK);
		
		Calendar calendar1 = new GregorianCalendar();
		calendar1.setTime(current);
		int remind_week_day = calendar1.get(Calendar.DAY_OF_WEEK);
		
		if(remind_week_day>today_week_day){		
			//remind_week_day 大于当月总天数，eg. 8.31, 9.30
			int max_days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
			if(max_days>remind_week_day){
				calendar.add(calendar.DATE, remind_week_day-today_week_day);
			}else{
				calendar.add(calendar.DATE, max_days);
			}
		}else{
			calendar.add(calendar.DATE, 7 - today_week_day + remind_week_day);
		}
		
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");		 
		String next = fmt.format(calendar.getTime());
		return next;
	}
	
	public static String getNextDate2(String remindDate,int sort){
		Date current = str2Date(remindDate);  
		
		switch(sort){
		case 1: //day			
			return getDateStr(1);  //today+1	???		 
		case 2: //week			 
			return getNextWeekDay2(remindDate);	 //ok	 
		case 3: //month			 
			return GetNextMonthDay2(remindDate); //ok 
		case 4: //year
			//有一个例外，闰年2-29，下一年则没有2-29 
			return getDateOfNextYear(remindDate); 
		} 
		return "";
	}
	
	public static String getNextDate(String currentDate,int sort){
		Date current = str2Date(currentDate); 
		
		//如果设置的提醒日期 大于今天，则直接设置 current
		if(current.after(new Date())){			 
			return currentDate;
		}
		
		switch(sort){
		case 1: //day			
			return getDateStr(1);  //today+1	???		 
		case 2: //week			 
			return getNextWeekDay(currentDate);	 //ok	 
		case 3: //month			 
			return GetNextMonthDay(currentDate); //ok 
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
	
	public static String GetDateFromInts(int year,int month,int day){
		Calendar c = Calendar.getInstance();
		c.set(year, month, day);
		
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");		 
		String next = fmt.format(c.getTime());
		return next;
	}
	
	public static String GetNextMonthDay(String remindDate){
		Date current = str2Date(remindDate);  
		 
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(new Date());
		int today_day_of_month = calendar.get(Calendar.DAY_OF_MONTH);
		
		Calendar calendar1 = new GregorianCalendar();
		calendar1.setTime(current);
		int remind_day_of_month = calendar1.get(Calendar.DAY_OF_MONTH);
		
		if(remind_day_of_month > today_day_of_month){
			// (remind_week_day-today_week_day) 
			calendar.add(calendar.DATE, remind_day_of_month - today_day_of_month);
		}else{ 
			calendar.add(calendar.MONTH, 1);
			calendar.set(Calendar.DAY_OF_MONTH, remind_day_of_month);
		}
		
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");		 
		String next = fmt.format(calendar.getTime());
		return next;
	}
	
	public static String GetNextMonthDay2(String remindDate){
		Date current = str2Date(remindDate);  
		Calendar calendar1 = new GregorianCalendar();
		calendar1.setTime(current);
		int remind_day_of_month = calendar1.get(Calendar.DAY_OF_MONTH);  
		
		calendar1.add(calendar1.MONTH, 1);
		calendar1.set(calendar1.DAY_OF_MONTH, remind_day_of_month);  
		
		
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");		 
		String next = fmt.format(calendar1.getTime());
		return next;
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
