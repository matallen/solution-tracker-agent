package com.redhat.sso.solutiontracker;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;


public class InitServlet extends HttpServlet {
	
  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
//    Summarizer.start();
//    String intervalString=DBConfig.get().getOptions().get("heartbeat.interval");
//    int interval=Integer.parseInt(intervalString);
//    Heartbeat.start(interval);
    
    
//    CamelContext ctx=new DefaultCamelContext();
//    new RouteBuilder(ctx) {
//      @Override
//      public void configure() throws Exception {
//        from("direct:track")
//        .to("")
//        ;
//      }
//    }.addRoutesToCamelContext(ctx);;
    
    
  }

  @Override
  public void destroy() {
    super.destroy();
//    Summarizer.stop();
//    Heartbeat.stop();
  }

}