package org.pentaho.reporting.libraries.base.util;

import junit.framework.TestCase;

import java.net.URISyntaxException;

/**
 * Testcases for the UrlBuilder class
 */
public class UrlBuilderTest extends TestCase
{
  // Test Data (expected, server, path, parameters, fragment)
  private static final String[][] urlBuilderTestData = new String[][]
    {
      new String[] {
          "http://source.pentaho.com/pentaho-reporting",
          "http://source.pentaho.com", "pentaho-reporting", null, null
      },
      new String[] {
          "http://source.pentaho.com/pentaho-reporting",
          "http://source.pentaho.com", "/pentaho-reporting", "", ""
      },
      new String[] {
          "http://source.pentaho.com/pentaho-reporting",
          "http://source.pentaho.com/", "pentaho-reporting", "", ""
      },
      new String[] {
          "http://source.pentaho.com/pentaho-reporting",
          "http://source.pentaho.com/", "/pentaho-reporting", null, null
      },
      new String[] {
          "http://source.pentaho.org/viewvc/pentaho-reporting/tools/",
          "http://source.pentaho.org/viewvc", "pentaho-reporting/tools/", "", ""
      },
      new String[] {
          "http://source.pentaho.org/viewvc/pentaho-reporting/tools/",
          "http://source.pentaho.org/viewvc", "/pentaho-reporting/tools/", null, null
      },
      new String[] {
          "http://source.pentaho.org/viewvc/pentaho-reporting/tools/",
          "http://source.pentaho.org/viewvc/", "pentaho-reporting/tools/", null, null
      },
      new String[] {
          "http://source.pentaho.org/viewvc/pentaho-reporting/tools/",
          "http://source.pentaho.org/viewvc/", "/pentaho-reporting/tools/", "", ""
      },
        new String[] {
            "http://localhost:8080/pentaho/api/repo/:rest:path%20to%20a%20file:file%20name.prpt/parameters?named+query=this+and+that",
            "http://localhost:8080/pentaho/", "/api/repo/:rest:path to a file:file name.prpt/parameters", "named+query=this+and+that", null
        },
        new String[] {
            "http://localhost:8080/pentaho/api/repo/:rest:path%20to%20a%20file:file%20name.prpt/parameters#this+fragment",
            "http://localhost:8080/pentaho/", "/api/repo/:rest:path to a file:file name.prpt/parameters", null, "this+fragment"
        },
    };

  public void testUrlBuilder() throws URISyntaxException
  {
    for (final String [] testData : urlBuilderTestData)
    {
      assertEquals(testData[0], UrlBuilder.generateUrl(testData[1], testData[2], testData[3], testData[4]));
    }
  }
}
