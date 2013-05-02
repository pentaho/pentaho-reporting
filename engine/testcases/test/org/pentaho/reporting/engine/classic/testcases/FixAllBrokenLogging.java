package org.pentaho.reporting.engine.classic.testcases;

public class FixAllBrokenLogging
{
  public static void fixBrokenLogging()
  {
    System.setProperty("KETTLE_REDIRECT_STDOUT", "N");
    System.setProperty("KETTLE_REDIRECT_STDERR", "N");
    System.setProperty("org.apache.commons.logging.diagnostics.dest", "diagnostic-log.txt");
  }
}
