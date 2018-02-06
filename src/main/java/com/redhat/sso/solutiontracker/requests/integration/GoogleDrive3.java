package com.redhat.sso.solutiontracker.requests.integration;

import static com.google.common.base.Predicates.and;
import static com.redhat.sso.solutiontracker.requests.integration.GoogleDrive3.Predicates.byName;
import static com.redhat.sso.solutiontracker.requests.integration.GoogleDrive3.Predicates.byType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
//import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

public class GoogleDrive3 {
  public static final String TOOL="gdrive-linux-x64";
  public static final String[] GOOGLE_LIST=new String[]{TOOL, "list"};
  private File workingFolder;
//  public final static Logger log=Logger.getLogger(GoogleDrive3.class);
  
  public class ArrayBuilder {
    private List<String> result=new ArrayList<String>();
    public ArrayBuilder(){}
    public ArrayBuilder(String[] array){
      for(int i=0;i<=array.length-1;i++)
        result.add(array[i]);
    }
    public ArrayBuilder add(String v){
      result.add(v);
      return this;
    }
    public ArrayBuilder addAll(String... array){
      result.addAll(Arrays.asList(array));
      return this;
    }
    public String[] build(){
      return result.toArray(new String[result.size()]);
    }
  }
  
  public enum MimeTypes{
    // Document options
    DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document"), 
    OPEN_DOC("application/vnd.oasis.opendocument.text"),
    RTF("application/rtf"), 
    HTML("text/html"),
    
    // Spreadsheet options
    XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
    XLS_OASIS("application/vnd.oasis.opendocument.spreadsheet"),
    XLS3_XOASIS("application/x-vnd.oasis.opendocument.spreadsheet"),
    TAB_SEPARATED("application/x-vnd.oasis.opendocument.spreadsheet"),
    CSV("text/csv"),
    
    // Both (Doc & Spreadsheet)
    PDF("application/pdf"),
    ZIP("application/zip"), 
    
    ;
    public String value;
    private MimeTypes(String value){
      this.value=value;
    }
  }
  
  public class GFile{
    private String id;
    private String name;
    private String type;
    private String size;
    private String created;
    
    public GFile(String line){
      id=line.substring(0, 46).trim();
      name=line.substring(47, 90).trim();
      type=line.substring(90, 93).trim();
      size=line.substring(96, 106).trim();
      created=line.substring(106, 125).trim();
    }
    public GFile(String id, String name, String type, String size, String created) {
      super();
      this.id = id;
      this.name = name;
      this.type = type;
      this.size = size;
      this.created = created;
    }
    
    public String getId(){return id;}
    public String getName(){return name;}
    public String getType(){return type;}
    public String getSize(){return size;}
    public String getCreated(){return created;}

    public String toString(){
      return String.format("GFILE[ %-44s, %-41s, %-20s, %-20s]", id, name, type, created);
    }
  }
  
  // ==== FILTERS ====
  public static class Predicates{
    public static Predicate<GFile> byId(final String regExMatcher) { return new Predicate<GFile>() {
        public boolean apply(GFile input) {
          return input.id.matches(regExMatcher);
        }};}
    public static Predicate<GFile> byType(final String regExMatcher) { return new Predicate<GFile>() {
         public boolean apply(GFile input) {
               return input.type.matches(regExMatcher);
         }};}
    public static Predicate<GFile> byName(final String regExMatcher) { return new Predicate<GFile>() {
         public boolean apply(GFile input) {
               return input.name.matches(regExMatcher);
         }};}
  }
  
//  public static void main(String[] asd) throws Exception{
//    GoogleDrive3 drive=new GoogleDrive3();
//    List<GFile> files = drive.list();
//    GFile fileToGrab=null;
//    for(GFile f:files){
//      System.out.println(f);
//      if (f.name.startsWith("LoE Template"))
//        fileToGrab=f;
//    }
////    File file=drive.gexport(fileToGrab, MimeTypes.DOCX);
//    File file=drive.gexport("1QPwK7WWmZetdasI86rWtv8kXQn55T0RCOitCrmU5mdM", MimeTypes.DOCX);
//    System.out.println("FILE:"+file);
//    
//    File toFile=new File(file.getParent(), "MatsTestClient3.docx");
//    if (toFile.exists()) FileUtils.deleteQuietly(toFile);
//    FileUtils.moveFile(file, toFile);
//    
//    GFile destination=Iterables.filter(files, and(byType("dir"),byName("Mats Test Approach Docs"))).iterator().next();
//    
//    drive.gimport(toFile, MimeTypes.DOCX, destination);
//  }
  
  public GoogleDrive3(){
//    workingFolder=new File(System.getProperty("user.home")); // cant use this, it has permission issues
//    workingFolder=new File(System.getProperty("java.io.tmpdir")); // cant use this because on apache it change it to apache's temp folder and gdrive-bin can't be found
    workingFolder=new File("/tmp");
//    log.debug("Working Folder = "+workingFolder);
  }
  public GoogleDrive3(File workingFolder){
    this.workingFolder=workingFolder;
  }
  
  
  // ==== OPERATIONS ===
  public List<GFile> list() throws IOException, InterruptedException{
//    if (log.isDebugEnabled()) log.debug("gdrive.list() called");
    String output=run(new ArrayBuilder().add("list").build());
    String rawData=output.substring(output.indexOf("\n"), output.lastIndexOf("\n"));
//    if (log.isDebugEnabled()) log.debug("gdrive.list() returned: \n"+rawData);
//    System.out.println(rawData);
    List<GFile> result=new ArrayList<GFile>();
    for(String line:rawData.split("\n")){
      if (line.trim().length()>0){
        result.add(new GFile(line));
      }
    }
    return result;
  }

  public File gexport(String id, MimeTypes type) throws IOException, InterruptedException{
//    if (log.isDebugEnabled()) log.debug("gdrive.export() called");
    String output=run(new ArrayBuilder().addAll("export", "--force", "--mime", type.value, id).build());
    Matcher m = Pattern.compile("'(.+?)'").matcher(output);
    if (m.find()){
      String filename=m.group(1);
      boolean success=output.equals("Exported '"+filename+"' with mime type: '"+type.value+"'\n");
      if (!success) throw new RuntimeException("Unable to determine file exported correctly. Raw command output was: "+output);
      return new File(workingFolder, filename);
    }
    throw new RuntimeException("Unable to identify filename in output. Raw output was: "+output);
  }

  public File gexport(GFile gfile, MimeTypes type) throws IOException, InterruptedException{
//    if (log.isDebugEnabled()) log.debug("gdrive.export() called");
    String output=run(new ArrayBuilder().addAll("export", "--force", "--mime", type.value, gfile.id).build());
    boolean success=output.equals("Exported '"+gfile.name+"."+type.name().toLowerCase()+"' with mime type: '"+type.value+"'\n");
    if (!success) throw new RuntimeException("Unable to determine file exported correctly. Raw command output was: "+output);
    return new File(workingFolder, gfile.name+"."+type.name().toLowerCase());
  }
  
  public boolean gimport(File file, MimeTypes sourceFileMime, GFile destination) throws IOException, InterruptedException{
//    if (log.isDebugEnabled()) log.debug("gdrive.import() called");
    String output=run(new ArrayBuilder().addAll("import", "-p", destination.id, "--mime", ""+sourceFileMime.value+"", file.getName()).build());
    String expected="Imported .+ with mime type: '.+'\n";
    boolean success=output.matches(expected);
//    if (!success) throw new RuntimeException("Unable to determine if file imported correctly. File was: "+file.getAbsolutePath()+". Raw command output was: "+output);
//    if (!success) log.error("Unable to determine if file imported correctly. File was: "+file.getAbsolutePath()+". Raw command output was: "+output);
    return success;
  }
  
  

  // ==== OVERRIDEABLE FUNCTIONS ====
  public int getExcelHeaderRow(){
    return 0;
  }
  public boolean valid(Map<String,String> entry){
    return true;
  }

  
  // ==== INTERNAL FUNCTIONS ====
  private String run(String[] cmd) throws IOException, InterruptedException{
    return run(cmd, workingFolder);
  }
  
  private String run(String[] cmd, File workingFolder) throws IOException, InterruptedException{
    StringBuffer sb=new StringBuffer();
    for(String s:cmd)
       sb.append(s).append(" ");
    System.out.println("gdrive.run() executing: "+TOOL+" "+sb.toString());
//    if (log.isDebugEnabled()) 
//      log.debug("gdrive.run() executing: "+sb.toString());
    Process exec = Runtime.getRuntime().exec(new ArrayBuilder().add(TOOL).addAll(cmd).build(), null, workingFolder);
    exec.waitFor();
    String syserr = IOUtils.toString(exec.getErrorStream());
    String sysout = IOUtils.toString(exec.getInputStream());
//    if (log.isDebugEnabled()) log.debug("gdrive.run() returned: sysout=\""+sysout+"\"; syserr=\""+syserr+"\"");
    System.out.println("gdrive.run() returned: sysout=\""+sysout+"\"; syserr=\""+syserr+"\"");
//    if (!sysout.contains("Processing...") && !sysout.contains("Resolving...") && !sysout.contains("Everything is up-to-date"))
//      throw new RuntimeException("Error running google drive script: " + sysout);
    
    return sysout;
  }
  
  public List<Map<String,String>> parseExcelDocument(File file, int maxColumns) {
    return parseExcelDocument(file, null, maxColumns); // default to the first sheet
  }
  
  public List<Map<String,String>> parseExcelDocument(File file, String sheetName, int maxColumns) {
    // parse excel file using apache poi
    // read out "tasks" and create/update solutions
    // use timestamp (column A) as the unique identifier (if in doubt i'll hash it with the requester's username)
    List<Map<String,String>> entries=new ArrayList<Map<String,String>>();
    FileInputStream in=null;
    try{
      in=new FileInputStream(file);
      XSSFWorkbook wb=new XSSFWorkbook(in);
      XSSFSheet s=sheetName==null?wb.getSheetAt(0):wb.getSheet(sheetName);
      
      for(int iRow=getExcelHeaderRow()+1;iRow<=s.getLastRowNum();iRow++){
        Map<String,String> e=new LinkedHashMap<String,String>();
        for(int iColumn=0;iColumn<=maxColumns;iColumn++){
          if (s.getRow(getExcelHeaderRow()).getCell(iColumn)==null) continue;
          String header=s.getRow(getExcelHeaderRow()).getCell(iColumn).getStringCellValue();
          XSSFRow row = s.getRow(iRow);
          if (row==null) break; // next line/row
          XSSFCell cell=row.getCell(iColumn);
          if (cell==null) continue; // try next cell/column
          
          
          switch(cell.getCellType()){
          case XSSFCell.CELL_TYPE_NUMERIC: e.put(header, Integer.toString((int)cell.getNumericCellValue())); break;
          case XSSFCell.CELL_TYPE_STRING:  e.put(header, cell.getStringCellValue()); break;
//          case XSSFCell.CELL_TYPE_FORMULA:  break;
//          case XSSFCell.CELL_TYPE_BLANK:   e.put(header, ""); break;
          case XSSFCell.CELL_TYPE_BOOLEAN: e.put(header, cell.getStringCellValue()); break;
//          case XSSFCell.CELL_TYPE_ERROR:   e.put(header, ""); break;
          }
          
        }
        if (null!=e && e.size()==0){ // then we found an empty row, so exit?
          break;
        }
        if (valid(e)){
          e.put("ROW_#", String.valueOf(iRow));
          entries.add(e);
        }
      }
    }catch(FileNotFoundException e){
      e.printStackTrace();
    } catch (IOException e1) {
      e1.printStackTrace();
    }finally{
      IOUtils.closeQuietly(in);
    }
    return entries;
  }
}
