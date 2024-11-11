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


package org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.elements;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.extensions.legacy.charts.LegacyChartElementModule;
import org.pentaho.reporting.engine.classic.extensions.legacy.charts.LegacyChartType;
import org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.datasets.ReportFunctionReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class ChartElementReadHandler extends AbstractReportElementReadHandler {
  private Element element;
  private ReportFunctionReadHandler chartFunctionReadHandler;
  private ReportFunctionReadHandler dataCollectorFunction;
  private ReportFunctionReadHandler dataCollectorFunction2;

  public ChartElementReadHandler() {
    this.element = new Element();
    this.element.setElementType( new LegacyChartType() );
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
    if ( isSameNamespace( uri ) ) {
      if ( "chartFunction".equals( tagName ) ) {
        chartFunctionReadHandler = new ReportFunctionReadHandler();
        return chartFunctionReadHandler;
      }
      if ( "dataCollectorFunction".equals( tagName ) ) {
        dataCollectorFunction = new ReportFunctionReadHandler();
        return dataCollectorFunction;
      }
      if ( "dataCollectorFunction2".equals( tagName ) ) {
        dataCollectorFunction2 = new ReportFunctionReadHandler();
        return dataCollectorFunction2;
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

    //final String chartType = getResult().getProperty("chartType");
    //element.setAttribute(ReportDesignerParserModule.NAMESPACE, "ChartTypeHint", chartType);
    // in fact, the chart type is redundant, as it can be derived from the chart expression itself.

    if ( chartFunctionReadHandler != null ) {
      element.setAttributeExpression
        ( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, chartFunctionReadHandler.getExpression() );
    }

    if ( dataCollectorFunction != null ) {
      final Expression ex = dataCollectorFunction.getExpression();
      // redundant, as this can be dereived from the property 'dataSource'
      // element.setAttribute(ReportDesignerParserModule.NAMESPACE, "DataCollectorFunction", ex.getName());
      element.setAttribute( LegacyChartElementModule.NAMESPACE,
        LegacyChartElementModule.PRIMARY_DATA_COLLECTOR_FUNCTION_ATTRIBUTE, ex );
    }
    if ( dataCollectorFunction2 != null ) {
      // Only valid on BarLineChartExpression, can be retrieved via 'linesDataSource' property
      final Expression ex = dataCollectorFunction2.getExpression();
      //element.setAttribute(ReportDesignerParserModule.NAMESPACE, "DataCollectorFunction2", ex.getName());
      element.setAttribute( LegacyChartElementModule.NAMESPACE,
        LegacyChartElementModule.SECONDARY_DATA_COLLECTOR_FUNCTION_ATTRIBUTE, ex );
    }
  }

  protected Element getElement() {
    return element;
  }
}
