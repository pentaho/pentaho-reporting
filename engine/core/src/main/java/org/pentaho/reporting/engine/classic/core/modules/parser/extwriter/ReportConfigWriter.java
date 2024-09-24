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

package org.pentaho.reporting.engine.classic.core.modules.parser.extwriter;

import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.PageDefinition;
import org.pentaho.reporting.engine.classic.core.SimplePageDefinition;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.ExtParserModule;
import org.pentaho.reporting.engine.classic.core.util.PageFormatFactory;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriterSupport;

import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.io.IOException;
import java.util.Enumeration;

/**
 * A report configuration writer.
 *
 * @author Thomas Morgner.
 */
public class ReportConfigWriter extends AbstractXMLDefinitionWriter {
  protected static final String PAGE_DEFINITION_TAG = "page-definition";
  protected static final String SIMPLE_PAGE_DEFINITION_TAG = "simple-page-definition";
  protected static final String PAGE_TAG = "page";

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
   * A constant for the top border.
   */
  private static final int TOP_BORDER = 0;

  /**
   * A constant for the left border.
   */
  private static final int LEFT_BORDER = 1;

  /**
   * A constant for the bottom border.
   */
  private static final int BOTTOM_BORDER = 2;

  /**
   * A constant for the right border.
   */
  private static final int RIGHT_BORDER = 3;

  /**
   * A report configuration writer.
   *
   * @param reportWriter
   *          the report writer.
   * @param xmlWriter
   *          the current indention level.
   */
  public ReportConfigWriter( final ReportWriterContext reportWriter, final XmlWriter xmlWriter ) {
    super( reportWriter, xmlWriter );
  }

  /**
   * Writes the report configuration element.
   *
   * @throws java.io.IOException
   *           if there is an I/O problem.
   */
  public void write() throws IOException, ReportWriterException {
    final XmlWriter xmlWriter = getXmlWriter();
    xmlWriter
        .writeTag( ExtParserModule.NAMESPACE, AbstractXMLDefinitionWriter.REPORT_CONFIG_TAG, XmlWriterSupport.OPEN );

    final AbstractReportDefinition report = getReport();
    if ( report instanceof MasterReport ) {
      final MasterReport masterReport = (MasterReport) report;
      final DataFactoryWriter writer = new DataFactoryWriter( getReportWriter(), getXmlWriter() );
      writer.write();

      writePageDefinition();
      writeReportConfig( masterReport.getConfiguration() );
    }

    xmlWriter.writeCloseTag();
  }

  private void writeReportConfig( final Configuration config ) throws IOException {
    final XmlWriter writer = getXmlWriter();
    final Enumeration properties = config.getConfigProperties();

    if ( properties.hasMoreElements() ) {
      writer.writeTag( ExtParserModule.NAMESPACE, AbstractXMLDefinitionWriter.CONFIGURATION_TAG, XmlWriterSupport.OPEN );
      while ( properties.hasMoreElements() ) {
        final String key = (String) properties.nextElement();
        final String value = config.getConfigProperty( key );
        if ( value != null ) {
          writer.writeTag( ExtParserModule.NAMESPACE, "property", "name", key, XmlWriterSupport.OPEN );
          writer.writeTextNormalized( value, false );
          writer.writeCloseTag();
        }
      }
      writer.writeCloseTag();
    }

  }

  private void writePageDefinition() throws IOException {
    final XmlWriter xmlWriter = getXmlWriter();
    final PageDefinition pageDefinition = getReport().getPageDefinition();
    if ( pageDefinition instanceof SimplePageDefinition ) {
      final SimplePageDefinition spdef = (SimplePageDefinition) pageDefinition;
      final AttributeList attr = new AttributeList();
      attr.setAttribute( ExtParserModule.NAMESPACE, "width", String.valueOf( spdef.getPageCountHorizontal() ) );
      attr.setAttribute( ExtParserModule.NAMESPACE, "height", String.valueOf( spdef.getPageCountVertical() ) );
      xmlWriter.writeTag( ExtParserModule.NAMESPACE, ReportConfigWriter.SIMPLE_PAGE_DEFINITION_TAG, attr,
          XmlWriterSupport.OPEN );

      final AttributeList attributes = buildPageFormatProperties( spdef.getPageFormat( 0 ) );
      xmlWriter.writeTag( ExtParserModule.NAMESPACE, ReportConfigWriter.PAGE_TAG, attributes, XmlWriterSupport.CLOSE );
      xmlWriter.writeCloseTag();
    } else {
      xmlWriter.writeTag( ExtParserModule.NAMESPACE, ReportConfigWriter.PAGE_DEFINITION_TAG, XmlWriterSupport.OPEN );

      final int max = pageDefinition.getPageCount();
      for ( int i = 0; i < max; i++ ) {
        final PageFormat fmt = pageDefinition.getPageFormat( i );

        final AttributeList attributes = buildPageFormatProperties( fmt );
        xmlWriter.writeTag( ExtParserModule.NAMESPACE, ReportConfigWriter.PAGE_TAG, attributes, XmlWriterSupport.CLOSE );
      }
      xmlWriter.writeCloseTag();
    }
  }

  /**
   * Compiles a collection of page format properties.
   *
   * @return The properties.
   */
  private AttributeList buildPageFormatProperties( final PageFormat fmt ) {
    final AttributeList retval = new AttributeList();
    final int[] borders = getBorders( fmt.getPaper() );

    if ( fmt.getOrientation() == PageFormat.LANDSCAPE ) {
      retval.setAttribute( ExtParserModule.NAMESPACE, ReportConfigWriter.ORIENTATION_ATT,
          ReportConfigWriter.ORIENTATION_LANDSCAPE_VAL );
      retval.setAttribute( ExtParserModule.NAMESPACE, ReportConfigWriter.TOPMARGIN_ATT, String
          .valueOf( borders[ReportConfigWriter.RIGHT_BORDER] ) );
      retval.setAttribute( ExtParserModule.NAMESPACE, ReportConfigWriter.LEFTMARGIN_ATT, String
          .valueOf( borders[ReportConfigWriter.TOP_BORDER] ) );
      retval.setAttribute( ExtParserModule.NAMESPACE, ReportConfigWriter.BOTTOMMARGIN_ATT, String
          .valueOf( borders[ReportConfigWriter.LEFT_BORDER] ) );
      retval.setAttribute( ExtParserModule.NAMESPACE, ReportConfigWriter.RIGHTMARGIN_ATT, String
          .valueOf( borders[ReportConfigWriter.BOTTOM_BORDER] ) );
    } else if ( fmt.getOrientation() == PageFormat.PORTRAIT ) {
      retval.setAttribute( ExtParserModule.NAMESPACE, ReportConfigWriter.ORIENTATION_ATT,
          ReportConfigWriter.ORIENTATION_PORTRAIT_VAL );
      retval.setAttribute( ExtParserModule.NAMESPACE, ReportConfigWriter.TOPMARGIN_ATT, String
          .valueOf( borders[ReportConfigWriter.TOP_BORDER] ) );
      retval.setAttribute( ExtParserModule.NAMESPACE, ReportConfigWriter.LEFTMARGIN_ATT, String
          .valueOf( borders[ReportConfigWriter.LEFT_BORDER] ) );
      retval.setAttribute( ExtParserModule.NAMESPACE, ReportConfigWriter.BOTTOMMARGIN_ATT, String
          .valueOf( borders[ReportConfigWriter.BOTTOM_BORDER] ) );
      retval.setAttribute( ExtParserModule.NAMESPACE, ReportConfigWriter.RIGHTMARGIN_ATT, String
          .valueOf( borders[ReportConfigWriter.RIGHT_BORDER] ) );
    } else {
      retval.setAttribute( ExtParserModule.NAMESPACE, ReportConfigWriter.ORIENTATION_ATT,
          ReportConfigWriter.ORIENTATION_REVERSE_LANDSCAPE_VAL );
      retval.setAttribute( ExtParserModule.NAMESPACE, ReportConfigWriter.TOPMARGIN_ATT, String
          .valueOf( borders[ReportConfigWriter.LEFT_BORDER] ) );
      retval.setAttribute( ExtParserModule.NAMESPACE, ReportConfigWriter.LEFTMARGIN_ATT, String
          .valueOf( borders[ReportConfigWriter.BOTTOM_BORDER] ) );
      retval.setAttribute( ExtParserModule.NAMESPACE, ReportConfigWriter.BOTTOMMARGIN_ATT, String
          .valueOf( borders[ReportConfigWriter.RIGHT_BORDER] ) );
      retval.setAttribute( ExtParserModule.NAMESPACE, ReportConfigWriter.RIGHTMARGIN_ATT, String
          .valueOf( borders[ReportConfigWriter.TOP_BORDER] ) );
    }

    final int w = (int) fmt.getPaper().getWidth();
    final int h = (int) fmt.getPaper().getHeight();

    final String pageDefinition = PageFormatFactory.getInstance().getPageFormatName( w, h );
    if ( pageDefinition != null ) {
      retval.setAttribute( ExtParserModule.NAMESPACE, ReportConfigWriter.PAGEFORMAT_ATT, pageDefinition );
    } else {
      retval.setAttribute( ExtParserModule.NAMESPACE, AbstractXMLDefinitionWriter.WIDTH_ATT, String.valueOf( w ) );
      retval.setAttribute( ExtParserModule.NAMESPACE, AbstractXMLDefinitionWriter.HEIGHT_ATT, String.valueOf( h ) );
    }
    return retval;
  }

  /**
   * Returns the borders for the given paper.
   *
   * @param p
   *          the paper.
   * @return The borders.
   */
  private int[] getBorders( final Paper p ) {
    final int[] retval = new int[4];

    retval[ReportConfigWriter.TOP_BORDER] = (int) p.getImageableY();
    retval[ReportConfigWriter.LEFT_BORDER] = (int) p.getImageableX();
    retval[ReportConfigWriter.BOTTOM_BORDER] = (int) ( p.getHeight() - ( p.getImageableY() + p.getImageableHeight() ) );
    retval[ReportConfigWriter.RIGHT_BORDER] = (int) ( p.getWidth() - ( p.getImageableX() + p.getImageableWidth() ) );
    return retval;
  }
}
