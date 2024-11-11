/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.report;

import org.pentaho.reporting.engine.classic.core.util.PageFormatFactory;
import org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.converter.DoubleDimensionConverter;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.awt.geom.Dimension2D;
import java.awt.print.PageFormat;
import java.awt.print.Paper;

public class PageDefinitionModelReadHandler extends AbstractXmlReadHandler {
  private PageFormat pageFormat;

  public PageDefinitionModelReadHandler() {
  }

  /**
   * Starts parsing.
   *
   * @param attrs the attributes.
   * @throws SAXException if there is a parsing error.
   */
  protected void startParsing( final Attributes attrs ) throws SAXException {
    final Dimension2D pageSize = DoubleDimensionConverter.getObject( attrs.getValue( getUri(), "pageSize" ) );
    final double topBorder = Double.parseDouble( attrs.getValue( getUri(), "topBorder" ) );
    final double leftBorder = Double.parseDouble( attrs.getValue( getUri(), "leftBorder" ) );
    final double bottomBorder = Double.parseDouble( attrs.getValue( getUri(), "bottomBorder" ) );
    final double rightBorder = Double.parseDouble( attrs.getValue( getUri(), "rightBorder" ) );

    final Paper paper = PageFormatFactory.getInstance().createPaper( pageSize.getWidth(), pageSize.getHeight() );
    PageFormatFactory.getInstance().setBorders( paper, topBorder, leftBorder, bottomBorder, rightBorder );

    pageFormat = new PageFormat();
    pageFormat.setPaper( paper );

  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException if an parser error occured.
   */
  public Object getObject() throws SAXException {
    return pageFormat;
  }
}
