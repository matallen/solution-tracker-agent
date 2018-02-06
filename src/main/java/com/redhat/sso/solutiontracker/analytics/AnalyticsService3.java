package com.redhat.sso.solutiontracker.analytics;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

import com.redhat.sso.solutiontracker.IOUtils2;
import com.redhat.sso.solutiontracker.Json;

public class AnalyticsService3 {

  private static final File DATABASE=new File("analytics3.json");
  private static Analytics3 analytics;
  private static AnalyticsService3 instance;
  public static AnalyticsService3 get(){
    if (null==instance) instance=new AnalyticsService3();
    return instance;
  }
  
  public void reset(){
    analytics=null;
  }
  
  public synchronized void save() throws JsonGenerationException, JsonMappingException, JsonParseException, IOException{
    String x=Json.newObjectMapper(true).writeValueAsString(getAnalytics());
//    System.out.println("SAVE SIZE = "+getAnalytics().getDocuments().size());
    IOUtils2.writeAndClose(x.getBytes(), new FileOutputStream(DATABASE));
  }
  
//  private void patchData(Map<String, Document> map) throws JsonGenerationException, JsonMappingException, JsonParseException, IOException{
//    boolean changed=false;
//    for(Document d:map.values()){
//      if (!d.getTags().contains("SSO")){
//        List<String> oldTags=d.getTags();
//        List<String> newTags=new ArrayList<String>();
//        newTags.add("SSO");
//        for(String t:oldTags)
//          newTags.add(t);
//        d.getTags().clear();
//        d.getTags().addAll(newTags);
//        changed=true;
//      }
//    }
////    if (changed)
////      save();
//  }
  
  
  private synchronized Analytics3 getAnalytics(){
	  try{
	    if (analytics==null){
	      if (DATABASE.exists()){
	        analytics=Json.newObjectMapper(true).readValue(DATABASE, Analytics3.class);
//	        patchData(analytics.getDocuments());
//	        System.out.println("AFTER LOAD SIZE="+analytics.getDocuments().size());
	      }else
	        analytics=new Analytics3();
	    }
	  }catch(Exception e){
		  e.printStackTrace();
	  }
	  return analytics;
  }
  
  public Map<String, Document> getDocuments(){
	  return getAnalytics().getDocuments();
  }
  
  public void track(String timestamp, Document doc, String user, String role, String dept, String geo, String count) throws JsonParseException, JsonMappingException, IOException{
    getAnalytics(); // TODO: do I need this line? test and remove...
    doc.getMetrics().increment(timestamp, user, role, dept, geo, count);
    save();
  }
  
  public Integer getNextId() throws JsonParseException, JsonMappingException, IOException{
	  int newId=1;
      for(Document doc:getAnalytics().getDocuments().values()){
    	  if (newId<=Integer.parseInt(doc.getId())){
              newId=Integer.parseInt(doc.getId())+1;
            }  
	  }
      return newId;
  }
//	public DocumentUI getDocument(String id) throws JsonParseException, JsonMappingException, IOException {
//		return getAnalytics().getDocuments().get(id);
//	}
}
