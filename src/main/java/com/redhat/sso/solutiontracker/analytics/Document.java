package com.redhat.sso.solutiontracker.analytics;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.google.api.client.repackaged.com.google.common.base.Objects;

//@JsonFilter("myFilter")
public class Document {
  private String id;
  private String theme;
  private String solution;
  private String document;
  private String type;
  private String link;
  private String tracker;
  private String version;
  private List<String> downloadLinks;
  private List<String> tags;
  private Metrics metrics;
  private String status;
  
  public String getId()      {return id;}
  public String getTheme()   {return theme;}
  public String getSolution(){return solution;}
  public String getDocument(){return document;}
  public String getType(){return type;}
  public String getLink()    {return link;}
  public String getTracker() {return tracker;}
  public String getVersion() {return version;}
  public String getStatus()  {return status;}
  @JsonIgnore
  public String getColorKey(){return theme+solution;}
  
  public void setId(String value) {this.id=value;}
  public void setTheme(String value){this.theme=value;}
  public void setSolution(String value){this.solution=value;}// public void setOffering(String value){this.offering=value;}
  public void setDocument(String value){this.document=value;}
  public void setType(String value){this.type=value;}
  public void setLink(String value){this.link=value;}// System.out.println("\""+link+"\"");}//!=null?value.replaceAll("\t", ""):value;}
  public void setMetrics(Metrics metrics) {this.metrics = metrics;}
  public void setTracker(String tracker) {this.tracker=tracker;}
  public void setDownloadLinks(List<String> value) {this.downloadLinks=value;}
  public void setStatus(String value){this.status=value;}
  
  public String toString(){
    return "Document["+String.format("%-3s %-30s %-25s %-30s %-30s %-30s", id,theme,solution,document,tags,link)+"]";
  }
  
  public List<String> getDownloadLinks() {
    if (downloadLinks == null)
      downloadLinks = new ArrayList<String>();
    return downloadLinks;
  }
  
  public List<String> getTags() {
    if (tags == null)
      tags = new ArrayList<String>();
    return tags;
  }

  public Metrics getMetrics() {
    if (null==metrics) metrics=new Metrics();
    return metrics;
  }

  public int compareTo(Object o) {
    return (theme+solution+document).hashCode();
  }
  
  @Override
  public int hashCode() {
    return Objects.hashCode(getId());
  }
  
  @Override
  public boolean equals(Object obj) {
    if (obj == null) return false;
    if (getClass() != obj.getClass())  return false;
    final Document other = (Document)obj;
    return 
      Objects.equal(getId(), other.getId())
      && Objects.equal(getTheme(), other.getTheme())
      && Objects.equal(getSolution(), other.getSolution())
      && Objects.equal(getDocument(), other.getDocument())
      && Objects.equal(getType(), other.getType())
      && Objects.equal(getLink(), other.getLink())
      && Objects.equal(getTags(), other.getTags())
//      && Objects.equal(getTracker(), other.getTracker())
        ;
  }
  
}
