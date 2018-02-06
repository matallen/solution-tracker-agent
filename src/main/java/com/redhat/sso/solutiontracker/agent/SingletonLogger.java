package com.redhat.sso.solutiontracker.agent;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class SingletonLogger {
  private static final SingletonLogger inst=new SingletonLogger();

  private SingletonLogger() {
      super();
  }
  
  public static SingletonLogger getInstance() {
    return inst;
  }

  public synchronized void writeToFile(String str) throws IOException {
    File file=new File("analytics-pre-data.txt");
//    FileOutputStream fos=new FileOutputStream(new File("analytics-pre-data.txt"));
//    IOUtils.write(str.getBytes(), fos);
    FileUtils.writeStringToFile(file, str+"\n", true);
//    fos.close();
  }

  public synchronized String readFile(boolean clear) throws IOException {
    File file=new File("analytics-pre-data.txt");  
    FileInputStream fis=new FileInputStream(file);
    String content=IOUtils.toString(fis);
    fis.close();
    
    if (clear){
      // clear the file (without removing it)
      PrintWriter writer = new PrintWriter(file);
      writer.print("");
      writer.close();
    }
    
    return content;
  }


}