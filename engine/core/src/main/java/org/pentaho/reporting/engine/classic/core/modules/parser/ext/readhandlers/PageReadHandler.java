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

package org.pentaho.reporting.engine.classic.core.modules.parser.ext.readhandlers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.PropertyAttributes;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.ReportParserUtil;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.common.AbstractPropertyXmlReadHandler;
import org.pentaho.reporting.engine.classic.core.util.PageFormatFactory;
import org.pentaho.reporting.libraries.xmlns.common.ParserUtil;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.awt.print.PageFormat;
import java.awt.print.Paper;

public class PageReadHandler extends AbstractPropertyXmlReadHandler {
  private static final Log logger = LogFactory.getLog( PageReadHandler.class );

  /**
   * Literal text for an XML attribute.
   */
  public static final String PAGEFORMAT_ATT = "pageformat";

  /**
   * Literal text for an XML attribute.
   */
  public static final String LEFTMARGIN_ATT = "leftmargin";

  /**
   * Literal text for an XML attribute.
   */
  public static final String RIGHTMARGIN_ATT = "rightmargin";

  /**
   * Literal text for an XML attribute.
   */
  public static final String TOPMARGIN_ATT = "topmargin";

  /**
   * Literal text for an XML attribute.
   */
  public static final String BOTTOMMARGIN_ATT = "bottommargin";

  /**
   * Literal text for an XML attribute.
   */
  public static final String WIDTH_ATT = "width";

  /**
   * Literal text for an XML attribute.
   */
  public static final String HEIGHT_ATT = "height";

  /**
   * Literal text for an XML attribute.
   */
  public static final String ORIENTATION_ATT = "orientation";

  /**
   * Literal text for an XML attribute.
   */
  public static final String ORIENTATION_PORTRAIT_VAL = "portrait";

  /**
   * Literal text for an XML attribute.
   */
  public static final String ORIENTATION_LANDSCAPE_VAL = "landscape";

  /**
   * Literal text for an XML attribute.
   */
  public static final String ORIENTATION_REVERSE_LANDSCAPE_VAL = "reverse_landscape";

  private float x;
  private float y;
  private PageFormat pageFormat;

  public PageReadHandler() {
  }

  public PageFormat getPageFormat() {
    return pageFormat;
  }

  public float getY() {
    return y;
  }

  public float getX() {
    return x;
  }

  /**
   * Starts parsing.
   *
   * @param attrs
   *          the attributes.
   * @throws org.xml.sax.SAXException
   *           if there is a parsing error.
   */
  protected void startParsing( final PropertyAttributes attrs ) throws SAXException {
    handlePageFormat( attrs );
    x = ParserUtil.parseFloat( attrs.getValue( getUri(), "x" ), 0 );
    y = ParserUtil.parseFloat( attrs.getValue( getUri(), "y" ), 0 );
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
  private void handlePageFormat( final Attributes atts ) throws SAXException {
    final MasterReport report =
        (MasterReport) getRootHandler().getHelperObject( ReportParserUtil.HELPER_OBJ_REPORT_NAME );

    // grab the default page definition ...
    PageFormat format = report.getPageDefinition().getPageFormat( 0 );
    float defTopMargin = (float) format.getImageableY();
    float defBottomMargin = (float) ( format.getHeight() - format.getImageableHeight() - format.getImageableY() );
    float defLeftMargin = (float) format.getImageableX();
    float defRightMargin = (float) ( format.getWidth() - format.getImageableWidth() - format.getImageableX() );

    format = createPageFormat( format, atts );

    defTopMargin = ParserUtil.parseFloat( atts.getValue( getUri(), PageReadHandler.TOPMARGIN_ATT ), defTopMargin );
    defBottomMargin =
        ParserUtil.parseFloat( atts.getValue( getUri(), PageReadHandler.BOTTOMMARGIN_ATT ), defBottomMargin );
    defLeftMargin = ParserUtil.parseFloat( atts.getValue( getUri(), PageReadHandler.LEFTMARGIN_ATT ), defLeftMargin );
    defRightMargin = ParserUtil.parseFloat( atts.getValue( getUri(), PageReadHandler.RIGHTMARGIN_ATT ), defRightMargin );

    final Paper p = format.getPaper();
    switch ( format.getOrientation() ) {
      case PageFormat.PORTRAIT:
        PageFormatFactory.getInstance().setBorders( p, defTopMargin, defLeftMargin, defBottomMargin, defRightMargin );
        break;
      case PageFormat.LANDSCAPE:
        // right, top, left, bottom
        PageFormatFactory.getInstance().setBorders( p, defRightMargin, defTopMargin, defLeftMargin, defBottomMargin );
        break;
      case PageFormat.REVERSE_LANDSCAPE:
        PageFormatFactory.getInstance().setBorders( p, defLeftMargin, defBottomMargin, defRightMargin, defTopMargin );
        break;
      default:
        // will not happen..
        throw new IllegalArgumentException( "Unexpected paper orientation." );
    }

    format.setPaper( p );
    pageFormat = format;
  }

  /**
   * Creates the pageFormat by using the given Attributes. If an PageFormat name is given, the named PageFormat is used
   * and the parameters width and height are ignored. If no name is defined, height and width attributes are used to
   * create the pageformat. The attributes define the dimension of the PageFormat in points, where the printing
   * resolution is defined at 72 pixels per inch.
   *
   * @param format
   *          the page format.
   * @param atts
   *          the element attributes.
   * @return the page format.
   * @throws SAXException
   *           if there is an error parsing the report.
   */
  private PageFormat createPageFormat( final PageFormat format, final Attributes atts ) throws SAXException {
    final String pageformatName = atts.getValue( getUri(), PageReadHandler.PAGEFORMAT_ATT );

    final int orientationVal;
    final String orientation = atts.getValue( getUri(), PageReadHandler.ORIENTATION_ATT );
    if ( orientation == null ) {
      orientationVal = PageFormat.PORTRAIT;
    } else if ( orientation.equals( PageReadHandler.ORIENTATION_LANDSCAPE_VAL ) ) {
      orientationVal = PageFormat.LANDSCAPE;
    } else if ( orientation.equals( PageReadHandler.ORIENTATION_REVERSE_LANDSCAPE_VAL ) ) {
      orientationVal = PageFormat.REVERSE_LANDSCAPE;
    } else if ( orientation.equals( PageReadHandler.ORIENTATION_PORTRAIT_VAL ) ) {
      orientationVal = PageFormat.PORTRAIT;
    } else {
      throw new ParseException( "Orientation value in REPORT-Tag is invalid.", getRootHandler().getDocumentLocator() );
    }
    if ( pageformatName != null ) {
      final Paper p = PageFormatFactory.getInstance().createPaper( pageformatName );
      if ( p == null ) {
        PageReadHandler.logger.warn( "Unable to create the requested Paper. " + pageformatName );
        return format;
      }
      return PageFormatFactory.getInstance().createPageFormat( p, orientationVal );
    }

    if ( atts.getValue( getUri(), PageReadHandler.WIDTH_ATT ) != null
        && atts.getValue( getUri(), PageReadHandler.HEIGHT_ATT ) != null ) {
      final int[] pageformatData = new int[2];
      pageformatData[0] =
          ParserUtil.parseInt( atts.getValue( getUri(), PageReadHandler.WIDTH_ATT ), "No Width set", getLocator() );
      pageformatData[1] =
          ParserUtil.parseInt( atts.getValue( getUri(), PageReadHandler.HEIGHT_ATT ), "No Height set", getLocator() );
      final Paper p = PageFormatFactory.getInstance().createPaper( pageformatData );
      if ( p == null ) {
        PageReadHandler.logger.warn( "Unable to create the requested Paper. Paper={" + pageformatData[0] + ", "
            + pageformatData[1] + '}' );
        return format;
      }
      return PageFormatFactory.getInstance().createPageFormat( p, orientationVal );
    }

    PageReadHandler.logger.info( "Insufficient Data to create a pageformat: Returned default." );
    return format;
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   */
  public Object getObject() {
    return null;
  }
}
