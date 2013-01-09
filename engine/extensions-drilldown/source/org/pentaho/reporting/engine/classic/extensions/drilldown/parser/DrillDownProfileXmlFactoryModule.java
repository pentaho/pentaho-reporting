package org.pentaho.reporting.engine.classic.extensions.drilldown.parser;

import org.pentaho.reporting.engine.classic.extensions.drilldown.DrillDownModule;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlFactoryModule;
import org.pentaho.reporting.libraries.xmlns.parser.XmlDocumentInfo;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;

public class DrillDownProfileXmlFactoryModule extends AbstractXmlFactoryModule
{
  public DrillDownProfileXmlFactoryModule()
  {
    super(DrillDownModule.DRILLDOWN_PROFILE_NAMESPACE, "drilldown-profiles");
  }

  /**
   * Creates an XmlReadHandler for the root-tag based on the given document information.
   *
   * @param documentInfo the document information that has been extracted from the parser.
   * @return the root handler or null.
   */
  public XmlReadHandler createReadHandler(final XmlDocumentInfo documentInfo)
  {
    return new DrillDownProfilesReadHandler();
  }
}
