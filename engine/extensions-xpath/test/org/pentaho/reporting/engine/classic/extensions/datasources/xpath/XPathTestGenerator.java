package org.pentaho.reporting.engine.classic.extensions.datasources.xpath;

public final class XPathTestGenerator
{
  private XPathTestGenerator()
  {
  }

  public static void main(final String[] args) throws Exception
  {
    NGXPathQueryTest.main(args);
    XPathQueryTest.main(args);
  }
}
