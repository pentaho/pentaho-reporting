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
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.ItemBand;
import org.pentaho.reporting.engine.classic.core.NoDataBand;
import org.pentaho.reporting.engine.classic.core.RelationalGroup;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.ItemBandType;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.NoDataBandType;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.GroupList;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.PropertyAttributes;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.ReportParserUtil;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.SubReportReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.common.AbstractPropertyXmlReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.common.FunctionsReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.common.IncludeReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.common.ParameterMappingReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.common.ParserConfigurationReadHandler;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.libraries.xmlns.parser.IgnoreAnyChildReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.RootXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.SAXException;

import java.util.ArrayList;
import java.util.HashMap;

public class SimpleSubReportReadHandler extends AbstractPropertyXmlReadHandler implements SubReportReadHandler {
  private static final Log logger = LogFactory.getLog( SimpleSubReportReadHandler.class );
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

  private SubReport report;
  private ArrayList<ParameterMappingReadHandler> importParameters;
  private ArrayList<ParameterMappingReadHandler> exportParameters;
  private boolean disableRootTagWarning;
  private GroupList groupList;

  public SimpleSubReportReadHandler() {
    importParameters = new ArrayList<ParameterMappingReadHandler>();
    exportParameters = new ArrayList<ParameterMappingReadHandler>();
    groupList = new GroupList();
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

  public void setDisableRootTagWarning( final boolean disableWarning ) {
    disableRootTagWarning = disableWarning;
  }

  public boolean isDisableRootTagWarning() {
    return disableRootTagWarning;
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
    if ( "sub-report".equals( getTagName() ) && disableRootTagWarning == false ) {
      SimpleSubReportReadHandler.logger
          .info( "Encountered a subreport with an <sub-report> root-element. As of version 0.8.9-rc1, "
              + "this tag has been deprecated and the common <report> tag should be used for both "
              + "standalone and sub-reports." );
    }

    RootXmlReadHandler rootHandler = getRootHandler();
    final Object maybeReport = rootHandler.getHelperObject( ReportParserUtil.HELPER_OBJ_REPORT_NAME );
    if ( maybeReport instanceof SubReport ) {
      report = (SubReport) maybeReport;
    } else {
      report = new SubReport();
      report.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.SOURCE, rootHandler.getSource() );
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

    if ( ReportParserUtil.isIncluded( rootHandler ) == false ) {
      final String query = attrs.getValue( getUri(), "query" );
      if ( query != null ) {
        report.setQuery( query );
      }
    }

    final String useMinChunkWidth = attrs.getValue( getUri(), "use-min-chunkwidth" );
    if ( useMinChunkWidth != null ) {
      report.getStyle().setStyleProperty( ElementStyleKeys.USE_MIN_CHUNKWIDTH,
          ReportParserUtil.parseBoolean( useMinChunkWidth, getLocator() ) );
    }

    if ( rootHandler.getHelperObject( ReportParserUtil.HELPER_OBJ_LEGACY_STYLES ) instanceof HashMap == false ) {
      rootHandler.setHelperObject( ReportParserUtil.HELPER_OBJ_LEGACY_STYLES, new HashMap<String, ElementStyleSheet>() );
    }
    rootHandler.setHelperObject( ReportParserUtil.HELPER_OBJ_REPORT_NAME, report );
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
    if ( getUri().equals( uri ) == false ) {
      return null;
    } else if ( "data-factory".equals( tagName ) ) {
      return new IgnoreAnyChildReadHandler();
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
    } else if ( "import-parameter".equals( tagName ) ) {
      final ParameterMappingReadHandler handler = new ParameterMappingReadHandler();
      importParameters.add( handler );
      return handler;
    } else if ( "export-parameter".equals( tagName ) ) {
      final ParameterMappingReadHandler handler = new ParameterMappingReadHandler();
      exportParameters.add( handler );
      return handler;
    } else {
      return null;
    }
  }

  /**
   * Done parsing.
   *
   * @throws SAXException
   *           if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    super.doneParsing();
    final SubReport report = getSubReport();
    for ( int i = 0; i < importParameters.size(); i++ ) {
      final ParameterMappingReadHandler handler = importParameters.get( i );
      report.addInputParameter( handler.getName(), handler.getAlias() );
    }
    for ( int i = 0; i < exportParameters.size(); i++ ) {
      final ParameterMappingReadHandler handler = exportParameters.get( i );
      report.addExportParameter( handler.getAlias(), handler.getName() );
    }
    try {
      final GroupList clone = (GroupList) groupList.clone();
      clone.installIntoReport( report );
    } catch ( CloneNotSupportedException e ) {
      throw new ParseException( "Failed to add group-list to report", getLocator() );
    }
  }

  /**
   * Returns the object for this element.
   *
   * @return the object.
   */
  public Object getObject() {
    return report;
  }

  public SubReport getSubReport() {
    return report;
  }
}
