package com.redhat.sso.solutiontracker.analytics;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

public class Metrics {
  private Integer total;
  
  private Map<String, Integer> byMonth; // yyyy-mmm -> count
  private Map<String, Integer> byUser;  // username -> count
  private Map<String, Integer> byDept;  // dept -> count
  private Map<String, Integer> byGeo;   // geo -> count
  
  // TODO: add by week?
  // TODO: add by country?
  
  private Map<String, Map<String, Integer>> byMonthUser;
  private Map<String, Map<String, Integer>> byMonthDept;
  private Map<String, Map<String, Integer>> byMonthGeo;
  
  public Integer getTotal(){
    if (total==null) total=0;
    return total;
  }
  public void setTotal(int i) {
    this.total=i;    
  }
  
  @JsonIgnore
  public void setThisMonthTotal(Integer value){}
  @JsonProperty
  public Integer getThisMonthTotal(){
    String yearMonth=new SimpleDateFormat("yyyy-MMM").format(new Date(System.currentTimeMillis()));
    Integer result=byMonth.get(yearMonth);
    return null!=result?result:0;
  }
  @JsonIgnore
  public void setLastMonthTotal(Integer value){}
  @JsonProperty
  public Integer getLastMonthTotal(){
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.MONTH, -1);
    Date lastMonth = cal.getTime();
    String yearMonth=new SimpleDateFormat("yyyy-MMM").format(lastMonth);
    Integer result=byMonth.get(yearMonth);
    return null!=result?result:0;
  }
  public Map<String, Integer> getByMonth(){
    if (null==byMonth) byMonth=new LinkedHashMap<String, Integer>(); return byMonth;
  }
  public Map<String, Integer> getByUser(){
    if (null==byUser) byUser=new LinkedHashMap<String, Integer>(); return byUser;
  }
  public Map<String, Integer> getByDept(){
    if (null==byDept) byDept=new LinkedHashMap<String, Integer>(); return byDept;
  }
  public Map<String, Integer> getByGeo(){
    if (null==byGeo) byGeo=new LinkedHashMap<String, Integer>(); return byGeo;
  }
  public Map<String, Map<String, Integer>> getByMonthUser(){
    if (null==byMonthUser) byMonthUser=new LinkedHashMap<String, Map<String,Integer>>(); return byMonthUser;
  }
  public Map<String, Map<String, Integer>> getByMonthDept(){
    if (null==byMonthDept) byMonthDept=new LinkedHashMap<String, Map<String,Integer>>(); return byMonthDept;
  }
  public Map<String, Map<String, Integer>> getByMonthGeo(){
    if (null==byMonthGeo) byMonthGeo=new LinkedHashMap<String, Map<String,Integer>>(); return byMonthGeo;
  }

  public void increment(String timestamp, String user, String role, String dept, String geo, String count) {
    
    if (dept.contains(" Sales Global Services Practice Development")){
      System.out.println("NOT incrementing status because user ["+user+"] is part of SSO ["+dept+"]");
      return;
    }
    
    System.out.println("Incrementing [user="+user+"; role="+role+"; dept="+dept+"; geo="+geo+"]");
    try{
      Date ts=new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss").parse(timestamp);
      String yearMonth=new SimpleDateFormat("yyyy-MMM").format(ts);
      Integer c=Integer.parseInt(count);
      
      total=total+c;
      inc(getByMonth(), yearMonth, c);
      inc(getByUser(), user, c);
      inc(getByDept(), dept, c);
      inc(getByGeo(), geo, c);
      
      inc2(getByMonthUser(), yearMonth, user, c);
      inc2(getByMonthDept(), yearMonth, dept, c);
      inc2(getByMonthGeo(), yearMonth, geo, c);
      
    }catch(Exception e){
      e.printStackTrace();
    }
  }
  
  private void inc(Map<String, Integer> map, String key, Integer count){
    Integer total=map.get(key);
    if (total==null)
      total=0;
    map.put(key, total+count);
  }
  
  private void inc2(Map<String, Map<String,Integer>> map, String key1, String key2, Integer count){
    Map<String, Integer> m1=map.get(key1);
    if (m1==null) m1=new LinkedHashMap<String, Integer>();
    
    Integer total=m1.get(key2);
    if (total==null)
      total=0;
    m1.put(key2, total+count);
    map.put(key1, m1);
  }
}
