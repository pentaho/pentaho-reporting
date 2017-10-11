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

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.PropertyAttributes;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.ReportParserUtil;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.common.AbstractPropertyXmlReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.common.FunctionsReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.common.IncludeReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base.ClassFactoryCollector;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.datasource.DataSourceCollector;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.elements.ElementFactoryCollector;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.stylekey.StyleKeyFactoryCollector;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.templates.TemplateCollector;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.libraries.xmlns.parser.RootXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.SAXException;

import java.util.HashMap;

public class ReportDefinitionReadHandler extends AbstractPropertyXmlReadHandler {
  public static final String ELEMENT_FACTORY_KEY = "::element-factory";
  public static final String STYLE_FACTORY_KEY = "::stylekey-factory";
  public static final String CLASS_FACTORY_KEY = "::class-factory";
  public static final String DATASOURCE_FACTORY_KEY = "::datasource-factory";
  public static final String TEMPLATE_FACTORY_KEY = "::template-factory";

  private MasterReport report;

  public ReportDefinitionReadHandler() {
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

    if ( ReportParserUtil.isIncluded( rootHandler ) == false ) {
      final String query = attrs.getValue( getUri(), "query" );
      if ( query != null ) {
        report.setQuery( query );
      }

      final String value = attrs.getValue( getUri(), "name" );
      if ( value != null ) {
        report.setName( value );
      }
    }
    final ElementFactoryCollector elementFactory = new ElementFactoryCollector();
    final StyleKeyFactoryCollector styleKeyFactory = new StyleKeyFactoryCollector();
    final ClassFactoryCollector classFactory = new ClassFactoryCollector();
    final DataSourceCollector dataSourceFactory = new DataSourceCollector();
    final TemplateCollector templateFactory = new TemplateCollector();

    classFactory.configure( rootHandler.getParserConfiguration() );
    dataSourceFactory.configure( rootHandler.getParserConfiguration() );
    templateFactory.configure( rootHandler.getParserConfiguration() );

    if ( rootHandler.getHelperObject( ReportParserUtil.HELPER_OBJ_LEGACY_STYLES ) instanceof HashMap == false ) {
      rootHandler.setHelperObject( ReportParserUtil.HELPER_OBJ_LEGACY_STYLES, new HashMap<String, ElementStyleSheet>() );
    }
    rootHandler.setHelperObject( ReportParserUtil.HELPER_OBJ_REPORT_NAME, report );
    rootHandler.setHelperObject( ReportDefinitionReadHandler.ELEMENT_FACTORY_KEY, elementFactory );
    rootHandler.setHelperObject( ReportDefinitionReadHandler.STYLE_FACTORY_KEY, styleKeyFactory );
    rootHandler.setHelperObject( ReportDefinitionReadHandler.CLASS_FACTORY_KEY, classFactory );
    rootHandler.setHelperObject( ReportDefinitionReadHandler.DATASOURCE_FACTORY_KEY, dataSourceFactory );
    rootHandler.setHelperObject( ReportDefinitionReadHandler.TEMPLATE_FACTORY_KEY, templateFactory );

    report.setAttribute( AttributeNames.Internal.NAMESPACE, AttributeNames.Internal.FILEFORMAT, "extended-xml" );
    report.setCompatibilityLevel( ClassicEngineBoot.computeVersionId( 3, 8, 0 ) );
    this.report = report;
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
    if ( isSameNamespace( uri ) == false ) {
      return null;
    }

    if ( "parser-config".equals( tagName ) ) {
      return new ParserConfigReadHandler();
    } else if ( "report-config".equals( tagName ) ) {
      return new ReportConfigReadHandler();
    } else if ( "styles".equals( tagName ) ) {
      return new StylesReadHandler();
    } else if ( "templates".equals( tagName ) ) {
      return new TemplatesReadHandler();
    } else if ( "report-description".equals( tagName ) ) {
      return new ReportDescriptionReadHandler();
    } else if ( "functions".equals( tagName ) ) {
      return new FunctionsReadHandler( report );
    } else if ( "include".equals( tagName ) ) {
      return new IncludeReadHandler();
    }
    return null;
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   */
  public Object getObject() {
    return getRootHandler().getHelperObject( ReportParserUtil.HELPER_OBJ_REPORT_NAME );
  }
}
