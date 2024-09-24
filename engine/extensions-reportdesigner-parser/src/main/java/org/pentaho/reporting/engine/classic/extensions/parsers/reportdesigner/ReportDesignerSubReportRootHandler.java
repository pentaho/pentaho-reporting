/*!
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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.GroupDataBody;
import org.pentaho.reporting.engine.classic.core.ItemBand;
import org.pentaho.reporting.engine.classic.core.NoDataBand;
import org.pentaho.reporting.engine.classic.core.PageFooter;
import org.pentaho.reporting.engine.classic.core.PageHeader;
import org.pentaho.reporting.engine.classic.core.ParameterMapping;
import org.pentaho.reporting.engine.classic.core.ReportFooter;
import org.pentaho.reporting.engine.classic.core.ReportHeader;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.Watermark;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.GroupDataBodyType;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.ReportParserUtil;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.datasets.DataSetsReadHandler;
import org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.datasets.ReportFunctionsReadHandler;
import org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.elements.BandTopLevelElementReadHandler;
import org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.elements.SubReportParametersReadHandler;
import org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.model.Guideline;
import org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.report.LinealModelReadHandler;
import org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.report.ReportGroupsReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.IgnoreAnyChildReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.PropertiesReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.RootXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.HashMap;
import java.util.Properties;

public class ReportDesignerSubReportRootHandler extends PropertiesReadHandler {
  private SubReport report;
  private LinealModelReadHandler horizontalLinealReadHandler;
  private DataSetsReadHandler dataSetReadHandler;
  private SubReportParametersReadHandler parametersReadHandler;

  private BandTopLevelElementReadHandler watermarkHandler;
  private BandTopLevelElementReadHandler pageHeaderHandler;
  private BandTopLevelElementReadHandler pageFooterHandler;
  private BandTopLevelElementReadHandler reportHeaderHandler;
  private BandTopLevelElementReadHandler reportFooterHandler;
  private BandTopLevelElementReadHandler itemBandHandler;
  private BandTopLevelElementReadHandler noDataBandHandler;

  public ReportDesignerSubReportRootHandler() {
  }

  /**
   * Initialises the handler.
   *
   * @param rootHandler the root handler.
   * @param tagName     the tag name.
   */
  public void init( final RootXmlReadHandler rootHandler, final String uri, final String tagName ) throws SAXException {
    super.init( rootHandler, uri, tagName );
    rootHandler.setHelperObject( "property-expansion", Boolean.FALSE );
  }

  /**
   * Starts parsing.
   *
   * @param attrs the attributes.
   * @throws SAXException if there is a parsing error.
   */
  protected void startParsing( final Attributes attrs ) throws SAXException {
    super.startParsing( attrs );

    final Object maybeReport = getRootHandler().getHelperObject( ReportParserUtil.HELPER_OBJ_REPORT_NAME );
    final SubReport report;
    if ( maybeReport instanceof SubReport == false ) {
      // replace it ..
      report = new SubReport();
      report.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.SOURCE, getRootHandler().getSource() );
    } else {
      report = (SubReport) maybeReport;
    }

    getRootHandler()
      .setHelperObject( ReportParserUtil.HELPER_OBJ_LEGACY_STYLES, new HashMap<String, ElementStyleSheet>() );
    getRootHandler().setHelperObject( ReportParserUtil.HELPER_OBJ_REPORT_NAME, report );
    this.report = report;
  }

  /**
   * Returns the handler for a child element.
   *
   * @param uri     the URI of the namespace of the current element.
   * @param tagName the tag name.
   * @param atts    the attributes.
   * @return the handler or null, if the tagname is invalid.
   * @throws SAXException if there is a parsing error.
   */
  protected XmlReadHandler getHandlerForChild( final String uri,
                                               final String tagName,
                                               final Attributes atts ) throws SAXException {
    if ( isSameNamespace( uri ) == false ) {
      return null;
    }
    if ( "parameters".equals( tagName ) ) {
      parametersReadHandler = new SubReportParametersReadHandler();
      return parametersReadHandler;
    }
    if ( "reportConfiguration".equals( tagName ) ) {
      return new IgnoreAnyChildReadHandler();
    }
    if ( "padding".equals( tagName ) ) {
      return new IgnoreAnyChildReadHandler();
    }
    if ( "horizontalLinealModel".equals( tagName ) ) {
      horizontalLinealReadHandler = new LinealModelReadHandler();
      return horizontalLinealReadHandler;
    }
    if ( "pageDefinition".equals( tagName ) ) {
      return new IgnoreAnyChildReadHandler();
    }


    if ( "child".equals( tagName ) ) {
      final String type = atts.getValue( uri, "type" );
      if ( "org.pentaho.reportdesigner.crm.report.model.dataset.DataSetsReportElement".equals( type ) ) {
        dataSetReadHandler = new DataSetsReadHandler();
        return dataSetReadHandler;
      }

      if ( "org.pentaho.reportdesigner.crm.report.model.ReportFunctionsElement".equals( type ) ) {
        return new ReportFunctionsReadHandler();
      }

      if ( "org.pentaho.reportdesigner.crm.report.model.BandToplevelPageReportElement".equals( type ) ||
        "org.pentaho.reportdesigner.crm.report.model.BandToplevelReportElement".equals( type ) ||
        "org.pentaho.reportdesigner.crm.report.model.BandToplevelItemReportElement".equals( type ) ) {
        final String bandtype = atts.getValue( uri, "bandToplevelType" );
        if ( "PAGE_HEADER".equals( bandtype ) ) {
          pageHeaderHandler = new BandTopLevelElementReadHandler( new PageHeader(), bandtype );
          return pageHeaderHandler;
        }
        if ( "PAGE_FOOTER".equals( bandtype ) ) {
          pageFooterHandler = new BandTopLevelElementReadHandler( new PageFooter(), bandtype );
          return pageFooterHandler;
        }
        if ( "WATERMARK".equals( bandtype ) ) {
          watermarkHandler = new BandTopLevelElementReadHandler( new Watermark(), bandtype );
          return watermarkHandler;
        }
        if ( "NO_DATA_BAND".equals( bandtype ) ) {
          noDataBandHandler = new BandTopLevelElementReadHandler( new NoDataBand(), bandtype );
          return noDataBandHandler;
        }
        if ( "ITEM_BAND".equals( bandtype ) ) {
          itemBandHandler = new BandTopLevelElementReadHandler( new ItemBand(), bandtype );
          return itemBandHandler;
        }
        if ( "REPORT_FOOTER".equals( bandtype ) ) {
          reportFooterHandler = new BandTopLevelElementReadHandler( new ReportFooter(), bandtype );
          return reportFooterHandler;
        }
        if ( "REPORT_HEADER".equals( bandtype ) ) {
          reportHeaderHandler = new BandTopLevelElementReadHandler( new ReportHeader(), bandtype );
          return reportHeaderHandler;
        }
      }

      if ( "org.pentaho.reportdesigner.crm.report.model.ReportGroups".equals( type ) ) {
        return new ReportGroupsReadHandler();
      } else if ( "org.pentaho.reportdesigner.crm.report.model.SubReportDataElement".equals( type ) ) {
        return new IgnoreAnyChildReadHandler();
      }
    }

    return super.getHandlerForChild( uri, tagName, atts );
  }

  /**
   * Done parsing.
   *
   * @throws SAXException if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    super.doneParsing();
    final Properties p = getResult();
    final String name = p.getProperty( "name" );
    if ( name != null ) {
      report.setName( name );
    }

    if ( dataSetReadHandler != null ) {
      report.setDataFactory( (DataFactory) dataSetReadHandler.getObject() );
    }

    if ( horizontalLinealReadHandler != null ) {
      final Guideline[] guidelines = horizontalLinealReadHandler.getGuidelineValues();
      if ( guidelines != null && guidelines.length > 0 ) {
        final StringBuffer b = new StringBuffer( 100 );
        for ( int i = 0; i < guidelines.length; i++ ) {
          final Guideline guideline = guidelines[ i ];
          if ( i != 0 ) {
            b.append( ' ' );
          }
          b.append( guideline.externalize() );
        }
        report.setAttribute( ReportDesignerParserModule.NAMESPACE,
          ReportDesignerParserModule.HORIZONTAL_GUIDE_LINES_ATTRIBUTE, b.toString() );
      }
    }

    if ( parametersReadHandler != null ) {
      if ( parametersReadHandler.isGlobalImport() ) {
        report.addInputParameter( "*", "*" );
      } else {
        final ParameterMapping[] importMapping = parametersReadHandler.getImportParameterMappings();
        for ( int i = 0; i < importMapping.length; i++ ) {
          final ParameterMapping mapping = importMapping[ i ];
          report.addInputParameter( mapping.getName(), mapping.getAlias() );
        }
      }

      if ( parametersReadHandler.isGlobalExport() ) {
        report.addExportParameter( "*", "*" );
      } else {
        final ParameterMapping[] exportMapping = parametersReadHandler.getImportParameterMappings();
        for ( int i = 0; i < exportMapping.length; i++ ) {
          final ParameterMapping mapping = exportMapping[ i ];
          report.addExportParameter( mapping.getName(), mapping.getAlias() );
        }
      }
    }

    if ( reportFooterHandler != null ) {
      report.setReportFooter( (ReportFooter) reportFooterHandler.getBand() );
    }
    if ( reportHeaderHandler != null ) {
      report.setReportHeader( (ReportHeader) reportHeaderHandler.getBand() );
    }
    if ( itemBandHandler != null ) {
      final GroupDataBody dataBody = (GroupDataBody) report.getChildElementByType( GroupDataBodyType.INSTANCE );
      dataBody.setItemBand( (ItemBand) itemBandHandler.getBand() );
    }
    if ( noDataBandHandler != null ) {
      final GroupDataBody dataBody = (GroupDataBody) report.getChildElementByType( GroupDataBodyType.INSTANCE );
      dataBody.setNoDataBand( (NoDataBand) noDataBandHandler.getBand() );
    }
    if ( pageFooterHandler != null ) {
      report.setPageFooter( (PageFooter) pageFooterHandler.getBand() );
    }
    if ( pageHeaderHandler != null ) {
      report.setPageHeader( (PageHeader) pageHeaderHandler.getBand() );
    }
    if ( watermarkHandler != null ) {
      report.setWatermark( (Watermark) watermarkHandler.getBand() );
    }

    report.setAttribute( AttributeNames.Internal.NAMESPACE, AttributeNames.Internal.FILEFORMAT,
      "legacy-report-designer" );
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException if an parser error occured.
   */
  public Object getObject() throws SAXException {
    return report;
  }
}
