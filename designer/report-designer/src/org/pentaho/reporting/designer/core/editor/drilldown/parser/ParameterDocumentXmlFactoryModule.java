package org.pentaho.reporting.designer.core.editor.drilldown.parser;

import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlFactoryModule;
import org.pentaho.reporting.libraries.xmlns.parser.XmlDocumentInfo;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;

/**
 * Todo: Document me!
 * <p/>
 * Date: 13.08.2010
 * Time: 17:26:03
 *
 * @author Thomas Morgner.
 */
public class ParameterDocumentXmlFactoryModule extends AbstractXmlFactoryModule
{
  public ParameterDocumentXmlFactoryModule()
  {
    super("http://reporting.pentaho.org/namespaces/parameter/1.0", "parameters");//NON-NLS
  }

  /**
   * Creates an XmlReadHandler for the root-tag based on the given document
   * information.
   *
   * @param documentInfo the document information that has been extracted from
   *                     the parser.
   * @return the root handler or null.
   */
  public XmlReadHandler createReadHandler(final XmlDocumentInfo documentInfo)
  {
    return new ParameterDocumentReadHandler();
  }
}
