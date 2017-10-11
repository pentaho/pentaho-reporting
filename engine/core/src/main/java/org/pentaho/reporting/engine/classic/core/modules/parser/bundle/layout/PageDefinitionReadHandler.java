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

package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.PageDefinition;
import org.pentaho.reporting.engine.classic.core.SimplePageDefinition;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.ReportParserUtil;
import org.pentaho.reporting.engine.classic.core.util.PageFormatFactory;
import org.pentaho.reporting.libraries.xmlns.common.ParserUtil;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.awt.print.PageFormat;
import java.awt.print.Paper;

public class PageDefinitionReadHandler extends AbstractXmlReadHandler {
  private static final Log logger = LogFactory.getLog( PageDefinitionReadHandler.class );
  private PageDefinition pageDefinition;

  public PageDefinitionReadHandler() {
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

    final Object o = getRootHandler().getHelperObject( ReportParserUtil.HELPER_OBJ_REPORT_NAME );
    if ( o instanceof MasterReport == false ) {
      return;
    }

    final MasterReport report = (MasterReport) o;
    // grab the default page definition ...
    final PageFormat format = report.getPageDefinition().getPageFormat( 0 );

    final PageFormat definedFormat = configurePageSizeAndMargins( attrs, format );
    final int horizontalSpan = ParserUtil.parseInt( attrs.getValue( getUri(), "horizontal-span" ), 1 );
    final int verticalSpan = ParserUtil.parseInt( attrs.getValue( getUri(), "vertical-span" ), 1 );
    pageDefinition = new SimplePageDefinition( definedFormat, horizontalSpan, verticalSpan );
    report.setPageDefinition( pageDefinition );
  }

  /**
   * Creates the pageFormat by using the given Attributes. If an PageFormat name is given, the named PageFormat is used
   * and the parameters width and height are ignored. If no name is defined, height and width attributes are used to
   * create the pageformat. The attributes define the dimension of the PageFormat in points, where the printing
   * resolution is defined at 72 pixels per inch.
   *
   * @param defaultPageFormat
   *          the page format.
   * @param atts
   *          the element attributes.
   * @return the page format.
   * @throws SAXException
   *           if there is an error parsing the report.
   */
  private PageFormat configurePageSize( final PageFormat defaultPageFormat, final Attributes atts ) throws SAXException {
    final String pageformatName = atts.getValue( getUri(), "pageformat" );

    final int orientationVal;
    final String orientation = atts.getValue( getUri(), "orientation" );
    if ( orientation == null ) {
      orientationVal = PageFormat.PORTRAIT;
    } else if ( "landscape".equals( orientation ) ) {
      orientationVal = PageFormat.LANDSCAPE;
    } else if ( "reverse-landscape".equals( orientation ) ) {
      orientationVal = PageFormat.REVERSE_LANDSCAPE;
    } else {
      orientationVal = PageFormat.PORTRAIT;
    }

    if ( pageformatName != null ) {
      final Paper p = PageFormatFactory.getInstance().createPaper( pageformatName );
      if ( p == null ) {
        PageDefinitionReadHandler.logger.warn( "Paper size '" + pageformatName + "' is not regognized." );
        return defaultPageFormat;
      }
      return PageFormatFactory.getInstance().createPageFormat( p, orientationVal );
    }

    if ( atts.getValue( getUri(), "width" ) != null && atts.getValue( getUri(), "height" ) != null ) {
      final int[] pageformatData = new int[2];
      pageformatData[0] =
          ParserUtil.parseInt( atts.getValue( getUri(), "width" ), "Specified attribute 'width' is not valid",
              getLocator() );
      pageformatData[1] =
          ParserUtil.parseInt( atts.getValue( getUri(), "height" ), "Specified attribute 'height' is not valid",
              getLocator() );
      final Paper p = PageFormatFactory.getInstance().createPaper( pageformatData );
      if ( p == null ) {
        PageDefinitionReadHandler.logger.warn( "Unable to create the requested Paper size with width "
            + pageformatData[0] + " and height " + pageformatData[1] );
        return defaultPageFormat;
      }
      return PageFormatFactory.getInstance().createPageFormat( p, orientationVal );
    }

    PageDefinitionReadHandler.logger.info( "Insufficient Data to create a pageformat: Returned default." );
    return defaultPageFormat;
  }

  /**
   * Handles the page format.
   *
   * @param atts
   *          the attributes.
   * @throws SAXException
   *           if a parser error occurs or the validation failed.
   * @noinspection SuspiciousNameCombination
   */
  private PageFormat configurePageSizeAndMargins( final Attributes atts, PageFormat format ) throws SAXException {
    // (1) Grab the existing default ...
    float defTopMargin = (float) format.getImageableY();
    float defBottomMargin = (float) ( format.getHeight() - format.getImageableHeight() - format.getImageableY() );
    float defLeftMargin = (float) format.getImageableX();
    float defRightMargin = (float) ( format.getWidth() - format.getImageableWidth() - format.getImageableX() );

    // (2) Now configure the new paper-size
    format = configurePageSize( format, atts );

    // (3) Reconfigure margins as requested
    defTopMargin = ParserUtil.parseFloat( atts.getValue( getUri(), "margin-top" ), defTopMargin );
    defBottomMargin = ParserUtil.parseFloat( atts.getValue( getUri(), "margin-bottom" ), defBottomMargin );
    defLeftMargin = ParserUtil.parseFloat( atts.getValue( getUri(), "margin-left" ), defLeftMargin );
    defRightMargin = ParserUtil.parseFloat( atts.getValue( getUri(), "margin-right" ), defRightMargin );

    final Paper p = format.getPaper();
    switch ( format.getOrientation() ) {
      case PageFormat.PORTRAIT:
        PageFormatFactory.getInstance().setBorders( p, defTopMargin, defLeftMargin, defBottomMargin, defRightMargin );
        break;
      case PageFormat.REVERSE_LANDSCAPE:
        PageFormatFactory.getInstance().setBorders( p, defLeftMargin, defBottomMargin, defRightMargin, defTopMargin );
        break;
      case PageFormat.LANDSCAPE:
        PageFormatFactory.getInstance().setBorders( p, defRightMargin, defTopMargin, defLeftMargin, defBottomMargin );
        break;
      default:
        // will not happen..
        throw new IllegalArgumentException( "Unexpected paper orientation." );
    }

    format.setPaper( p );
    return format;
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException
   *           if an parser error occured.
   */
  public Object getObject() throws SAXException {
    return pageDefinition;
  }
}
