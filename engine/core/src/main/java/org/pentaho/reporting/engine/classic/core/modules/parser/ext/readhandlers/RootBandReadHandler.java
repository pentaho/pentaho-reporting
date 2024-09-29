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


package org.pentaho.reporting.engine.classic.core.modules.parser.ext.readhandlers;

import org.pentaho.reporting.engine.classic.core.AbstractRootLevelBand;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.PropertyAttributes;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.common.IncludeSubReportReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.SAXException;

import java.util.ArrayList;

/**
 * Creation-Date: Dec 18, 2006, 3:34:02 PM
 *
 * @author Thomas Morgner
 */
public class RootBandReadHandler extends BandReadHandler {
  private ArrayList subReportHandlers;

  public RootBandReadHandler( final Band element ) {
    super( element );
    this.subReportHandlers = new ArrayList();
  }

  /**
   * Returns the handler for a child element.
   *
   * @param tagName
   *          the tag name.
   * @param atts
   *          the attributes.
   * @return the handler or null, if the tagname is invalid.
   * @throws org.xml.sax.SAXException
   *           if there is a parsing error.
   */
  protected XmlReadHandler getHandlerForChild( final String uri, final String tagName, final PropertyAttributes atts )
    throws SAXException {
    if ( isSameNamespace( uri ) ) {
      if ( "sub-report".equals( tagName ) ) {
        final IncludeSubReportReadHandler subReportReadHandler = new IncludeSubReportReadHandler();
        subReportHandlers.add( subReportReadHandler );
        return subReportReadHandler;
      }
    }
    return super.getHandlerForChild( uri, tagName, atts );
  }

  /**
   * Done parsing.
   *
   * @throws org.xml.sax.SAXException
   *           if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    super.doneParsing();
    final Element band = getElement();
    if ( band instanceof AbstractRootLevelBand ) {
      final AbstractRootLevelBand arlb = (AbstractRootLevelBand) band;
      for ( int i = 0; i < subReportHandlers.size(); i++ ) {
        final IncludeSubReportReadHandler handler = (IncludeSubReportReadHandler) subReportHandlers.get( i );
        arlb.addSubReport( (SubReport) handler.getObject() );
      }
    }
  }
}
