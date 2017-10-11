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

package org.pentaho.reporting.engine.classic.core.modules.parser.simple.readhandlers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.ItemBand;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.NoDataBand;
import org.pentaho.reporting.engine.classic.core.RelationalGroup;
import org.pentaho.reporting.engine.classic.core.SimplePageDefinition;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.ItemBandType;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.NoDataBandType;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.DataFactoryReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.DataFactoryReadHandlerFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.GroupList;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.PropertyAttributes;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.ReportParserUtil;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.common.AbstractPropertyXmlReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.common.ConfigurationReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.common.FunctionsReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.common.IncludeReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.common.ParserConfigurationReadHandler;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.util.PageFormatFactory;
import org.pentaho.reporting.libraries.xmlns.common.ParserUtil;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.RootXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.util.HashMap;

/**
 * <pre>
 * &lt;!ELEMENT report   (configuration?, reportheader?, reportfooter?, pageheader?,
 * pagefooter?, watermark?, groups?, items?, functions?)&gt;
 * &lt;!ATTLIST report
 *   width          CDATA           #IMPLIED
 *   height         CDATA           #IMPLIED
 *   name           CDATA           #IMPLIED
 *   pageformat     %pageFormats;   #IMPLIED
 *   orientation    (%orientations;) "portrait"
 *   leftmargin     CDATA           #IMPLIED
 *   rightmargin    CDATA           #IMPLIED
 *   topmargin      CDATA           #IMPLIED
 *   bottommargin   CDATA           #IMPLIED
 * &gt;
 * </pre>
 */
public class JFreeReportReadHandler extends AbstractPropertyXmlReadHandler {
  private static final Log logger = LogFactory.getLog( JFreeReportReadHandler.class );
  /**
   * Literal text for an XML report element.
   */
  public static final String REPORT_TAG = "report";

  /**
   * Literal text for an XML attribute.
   */
  public static final String NAME_ATT = "name";

  /**
   * Literal text for an XML attribute.
   */
  public static final String PAGEFORMAT_ATT = "pageformat";

  /**
   * Literal text for an XML attribute.
   */
  public static final String PAGESPAN_ATT = "pagespan";

  /**
   * Literal text for an XML attribute.
   */
  public static final String UNIT_ATT = "unit";

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

  private MasterReport report;
  private DataFactoryReadHandler dataFactoryReadHandler;
  private GroupList groupList;

  public JFreeReportReadHandler() {
    this.groupList = new GroupList();
  }

  /**
   * Initialises the handler.
   *
   * @param rootHandler
   *          the root handler.
   * @param tagName
   *          the tag name.
   */
  public void init( final RootXmlReadHandler rootHandler, final String uri, final String tagName ) throws SAXException {
    super.init( rootHandler, uri, tagName );
    rootHandler.setHelperObject( "property-expansion", Boolean.TRUE );
  }

  /**
   * Starts parsing.
   *
   * @param attrs
   *          the attributes.
   * @throws org.xml.sax.SAXException
   *           if there is a parsing error.
   * @noinspection SuspiciousNameCombination
   */
  protected void startParsing( final PropertyAttributes attrs ) throws SAXException {
    RootXmlReadHandler rootHandler = getRootHandler();
    final Object maybeReport = rootHandler.getHelperObject( ReportParserUtil.HELPER_OBJ_REPORT_NAME );
    final MasterReport report;
    if ( maybeReport instanceof MasterReport == false ) {
      // replace it ..
      report = new MasterReport();
      report.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.SOURCE, rootHandler.getSource() );
    } else {
      report = (MasterReport) maybeReport;
    }

    final int groupCount = report.getGroupCount();
    for ( int i = 0; i < groupCount; i++ ) {
      final Group g = report.getGroup( i );
      if ( g instanceof RelationalGroup ) {
        groupList.add( (RelationalGroup) g );
      } else {
        throw new ParseException( "The existing report contains non-default groups. "
            + "This parser cannot handle such a construct." );
      }
    }

    final RootXmlReadHandler parser = rootHandler;
    if ( ReportParserUtil.isIncluded( parser ) == false ) {
      final String query = attrs.getValue( getUri(), "query" );
      if ( query != null ) {
        report.setQuery( query );
      }

      final String name = attrs.getValue( getUri(), JFreeReportReadHandler.NAME_ATT );
      if ( name != null ) {
        report.setName( name );
      }

      PageFormat format = report.getPageDefinition().getPageFormat( 0 );
      float defTopMargin = (float) format.getImageableY();
      float defBottomMargin = (float) ( format.getHeight() - format.getImageableHeight() - format.getImageableY() );
      float defLeftMargin = (float) format.getImageableX();
      float defRightMargin = (float) ( format.getWidth() - format.getImageableWidth() - format.getImageableX() );

      format = createPageFormat( format, attrs );

      defTopMargin =
          ParserUtil.parseFloat( attrs.getValue( getUri(), JFreeReportReadHandler.TOPMARGIN_ATT ), defTopMargin );
      defBottomMargin =
          ParserUtil.parseFloat( attrs.getValue( getUri(), JFreeReportReadHandler.BOTTOMMARGIN_ATT ), defBottomMargin );
      defLeftMargin =
          ParserUtil.parseFloat( attrs.getValue( getUri(), JFreeReportReadHandler.LEFTMARGIN_ATT ), defLeftMargin );
      defRightMargin =
          ParserUtil.parseFloat( attrs.getValue( getUri(), JFreeReportReadHandler.RIGHTMARGIN_ATT ), defRightMargin );

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
          throw new IllegalStateException( "Unexpected paper orientation." );
      }

      final int pageSpan = ParserUtil.parseInt( attrs.getValue( getUri(), JFreeReportReadHandler.PAGESPAN_ATT ), 1 );

      format.setPaper( p );
      report.setPageDefinition( new SimplePageDefinition( format, pageSpan, 1 ) );
    }
    if ( rootHandler.getHelperObject( ReportParserUtil.HELPER_OBJ_LEGACY_STYLES ) instanceof HashMap == false ) {
      rootHandler.setHelperObject( ReportParserUtil.HELPER_OBJ_LEGACY_STYLES, new HashMap<String, ElementStyleSheet>() );
    }
    rootHandler.setHelperObject( ReportParserUtil.HELPER_OBJ_REPORT_NAME, report );

    final String useMinChunkWidth = attrs.getValue( getUri(), "use-min-chunkwidth" );
    if ( useMinChunkWidth != null ) {
      report.getStyle().setStyleProperty( ElementStyleKeys.USE_MIN_CHUNKWIDTH,
          ReportParserUtil.parseBoolean( useMinChunkWidth, getLocator() ) );
    }

    report.setCompatibilityLevel( ClassicEngineBoot.computeVersionId( 3, 8, 0 ) );
    this.report = report;
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
    final String pageformatName = atts.getValue( getUri(), JFreeReportReadHandler.PAGEFORMAT_ATT );

    final int orientationVal;
    final String orientation = atts.getValue( getUri(), JFreeReportReadHandler.ORIENTATION_ATT );
    if ( orientation == null ) {
      orientationVal = PageFormat.PORTRAIT;
    } else if ( orientation.equals( JFreeReportReadHandler.ORIENTATION_LANDSCAPE_VAL ) ) {
      orientationVal = PageFormat.LANDSCAPE;
    } else if ( orientation.equals( JFreeReportReadHandler.ORIENTATION_REVERSE_LANDSCAPE_VAL ) ) {
      orientationVal = PageFormat.REVERSE_LANDSCAPE;
    } else if ( orientation.equals( JFreeReportReadHandler.ORIENTATION_PORTRAIT_VAL ) ) {
      orientationVal = PageFormat.PORTRAIT;
    } else {
      throw new ParseException( "Orientation value in REPORT-Tag is invalid.", getRootHandler().getDocumentLocator() );
    }
    if ( pageformatName != null ) {
      final Paper p = PageFormatFactory.getInstance().createPaper( pageformatName );
      if ( p == null ) {
        JFreeReportReadHandler.logger.warn( "Unable to create the requested Paper. " + pageformatName );
        return format;
      }
      return PageFormatFactory.getInstance().createPageFormat( p, orientationVal );
    }

    if ( atts.getValue( getUri(), JFreeReportReadHandler.WIDTH_ATT ) != null
        && atts.getValue( getUri(), JFreeReportReadHandler.HEIGHT_ATT ) != null ) {
      final int[] pageformatData = new int[2];
      pageformatData[0] =
          ParserUtil.parseInt( atts.getValue( getUri(), JFreeReportReadHandler.WIDTH_ATT ), "No Width set",
              getLocator() );
      pageformatData[1] =
          ParserUtil.parseInt( atts.getValue( getUri(), JFreeReportReadHandler.HEIGHT_ATT ), "No Height set",
              getLocator() );
      final Paper p = PageFormatFactory.getInstance().createPaper( pageformatData );
      if ( p == null ) {
        JFreeReportReadHandler.logger.warn( "Unable to create the requested Paper. Paper={" + pageformatData[0] + ", "
            + pageformatData[1] + '}' );
        return format;
      }
      return PageFormatFactory.getInstance().createPageFormat( p, orientationVal );
    }

    JFreeReportReadHandler.logger.info( "Insufficient Data to create a pageformat: Returned default." );
    return format;
  }

  /**
   * Returns the handler for a child element.
   *
   * @param tagName
   *          the tag name.
   * @param atts
   *          the attributes.
   * @return the handler.
   * @throws org.xml.sax.SAXException
   *           if there is a parsing error.
   */
  protected XmlReadHandler getHandlerForChild( final String uri, final String tagName, final PropertyAttributes atts )
    throws SAXException {

    final DataFactoryReadHandlerFactory factory = DataFactoryReadHandlerFactory.getInstance();
    final DataFactoryReadHandler handler = (DataFactoryReadHandler) factory.getHandler( uri, tagName );
    if ( handler != null ) {
      dataFactoryReadHandler = handler;
      return handler;
    }

    if ( getUri().equals( uri ) == false ) {
      return null;
    }

    if ( "configuration".equals( tagName ) ) {
      return new ConfigurationReadHandler( report.getReportConfiguration() );
    } else if ( "reportheader".equals( tagName ) ) {
      return new ReportHeaderReadHandler( report.getReportHeader() );
    } else if ( "reportfooter".equals( tagName ) ) {
      return new ReportFooterReadHandler( report.getReportFooter() );
    } else if ( "pageheader".equals( tagName ) ) {
      return new PageBandReadHandler( report.getPageHeader() );
    } else if ( "pagefooter".equals( tagName ) ) {
      return new PageBandReadHandler( report.getPageFooter() );
    } else if ( "watermark".equals( tagName ) ) {
      return new WatermarkReadHandler( report.getWatermark() );
    } else if ( "no-data-band".equals( tagName ) ) {
      final NoDataBand noDataBand = (NoDataBand) report.getChildElementByType( NoDataBandType.INSTANCE );
      if ( noDataBand == null ) {
        throw new ParseException( "Not a relational report" );
      }
      return new RootLevelBandReadHandler( noDataBand );
    } else if ( "groups".equals( tagName ) ) {
      return new GroupsReadHandler( groupList );
    } else if ( "items".equals( tagName ) ) {
      final ItemBand itemBand = (ItemBand) report.getChildElementByType( ItemBandType.INSTANCE );
      if ( itemBand == null ) {
        throw new ParseException( "Not a relational report" );
      }
      return new RootLevelBandReadHandler( itemBand );
    } else if ( "functions".equals( tagName ) ) {
      return new FunctionsReadHandler( report );
    } else if ( "include".equals( tagName ) ) {
      return new IncludeReadHandler();
    } else if ( "parser-config".equals( tagName ) ) {
      return new ParserConfigurationReadHandler();
    } else {
      return null;
    }
  }

  /**
   * Done parsing.
   *
   * @throws org.xml.sax.SAXException
   *           if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    if ( dataFactoryReadHandler != null ) {
      final DataFactory dataFactory = dataFactoryReadHandler.getDataFactory();
      if ( dataFactory != null ) {
        report.setDataFactory( dataFactory );
      }
    }

    try {
      final GroupList clone = (GroupList) groupList.clone();
      clone.installIntoReport( report );
    } catch ( CloneNotSupportedException e ) {
      throw new ParseException( "Failed to add group-list to report", getLocator() );
    }

    report.setAttribute( AttributeNames.Internal.NAMESPACE, AttributeNames.Internal.FILEFORMAT, "simple-xml" );
  }

  /**
   * Returns the object for this element.
   *
   * @return the object.
   */
  public Object getObject() {
    return report;
  }
}
