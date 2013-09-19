package org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.parser;

import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.DataSourceProvider;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.JndiDataSourceProvider;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.StringReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Todo: Document me!
 * <p/>
 * Date: 25.08.2009
 * Time: 10:09:05
 *
 * @author Thomas Morgner.
 */
public class JndiDataSourceProviderReadHandler extends AbstractXmlReadHandler implements DataSourceProviderReadHandler
{
  private StringReadHandler pathReadHandler;
  private JndiDataSourceProvider dataSourceProvider;

  public JndiDataSourceProviderReadHandler()
  {
  }

  /**
   * Returns the object for this element or null, if this element does
   * not create an object.
   *
   * @return the object.
   * @throws org.xml.sax.SAXException if an parser error occured.
   */
  public Object getObject() throws SAXException
  {
    return getProvider();
  }


  /**
   * Returns the handler for a child element.
   *
   * @param tagName the tag name.
   * @param atts    the attributes.
   * @return the handler or null, if the tagname is invalid.
   * @throws SAXException if there is a parsing error.
   */
  protected XmlReadHandler getHandlerForChild(final String uri,
                                              final String tagName,
                                              final Attributes atts)
      throws SAXException
  {
    if (isSameNamespace(uri) == false)
    {
      return null;
    }
    if ("path".equals(tagName))
    {
      pathReadHandler = new StringReadHandler();
      return pathReadHandler;
    }
    return null;
  }

  /**
   * Done parsing.
   *
   * @throws SAXException if there is a parsing error.
   */
  protected void doneParsing() throws SAXException
  {
    if (pathReadHandler == null)
    {
      throw new ParseException("JNDI connections need a JNDI path", getLocator());
    }
    this.dataSourceProvider = new JndiDataSourceProvider(pathReadHandler.getResult());
  }
  protected String getPath()
  {
    return pathReadHandler.getResult();
  }


  public DataSourceProvider getProvider()
  {
    return dataSourceProvider;
  }
}