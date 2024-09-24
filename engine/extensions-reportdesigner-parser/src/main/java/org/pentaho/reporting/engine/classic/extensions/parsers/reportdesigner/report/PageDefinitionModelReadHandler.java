/*
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
* Foundation.
*
* You should have received a copy of the GNU Lesser General Public License along with this
* program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
* or from the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU Lesser General Public License for more details.
*
* Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
*/

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
