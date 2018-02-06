package com.redhat.sso.solutiontracker.analytics;

import java.util.LinkedHashMap;
import java.util.Map;


public class Analytics3 {
  
  private Map<String, Document> documents;
  
  public Map<String, Document> getDocuments(){
    if (documents==null) documents=new LinkedHashMap<String, Document>();
    return documents;
  }
  
}
