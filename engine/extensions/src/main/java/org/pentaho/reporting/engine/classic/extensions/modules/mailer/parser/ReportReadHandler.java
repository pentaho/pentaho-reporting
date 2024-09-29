/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.extensions.modules.mailer.parser;

import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class ReportReadHandler extends AbstractXmlReadHandler {
  private String targetType;
  private MasterReport report;

  public ReportReadHandler() {
  }

  /**
   * Starts parsing.
   *
   * @param attrs
   *          the attributes.
   * @throws org.xml.sax.SAXException
   *           if there is a parsing error.
   */
  protected void startParsing( final Attributes attrs ) throws SAXException {
    targetType = attrs.getValue( getUri(), "target-type" );
    if ( targetType == null ) {
      throw new ParseException( "Mandatory attribute 'target-type' is missing.", getLocator() );
    }
    final String href = attrs.getValue( getUri(), "href" );
    if ( href == null ) {
      throw new ParseException( "Mandatory attribute 'href' is missing.", getLocator() );
    }

    try {
      final ResourceManager resourceManager = getRootHandler().getResourceManager();
      final ResourceKey key = resourceManager.deriveKey( getRootHandler().getSource(), href );
      final Resource resource = resourceManager.create( key, null, MasterReport.class );
      report = (MasterReport) resource.getResource();
    } catch ( ResourceException re ) {
      throw new ParseException( "Mandatory attribute 'href' is not pointing to a valid report.", re, getLocator() );
    }
  }

  public String getTargetType() {
    return targetType;
  }

  public MasterReport getReport() {
    return report;
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws org.xml.sax.SAXException
   *           if an parser error occurred.
   */
  public Object getObject() throws SAXException {
    return null;
  }
}
