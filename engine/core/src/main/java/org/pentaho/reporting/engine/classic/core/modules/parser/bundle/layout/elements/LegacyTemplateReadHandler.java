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

package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.elements;

import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base.ArrayClassFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base.ClassFactoryCollector;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base.URLClassFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.datasource.DataSourceCollector;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.datasource.DefaultDataSourceFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.elements.DefaultElementFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.elements.ElementFactoryCollector;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.objects.BandLayoutClassFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.objects.DefaultClassFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.stylekey.DefaultStyleKeyFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.stylekey.PageableLayoutStyleKeyFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.stylekey.StyleKeyFactoryCollector;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.templates.DefaultTemplateCollection;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.templates.TemplateCollector;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.readhandlers.ReportDefinitionReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.readhandlers.TemplateReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.RootXmlReadHandler;
import org.xml.sax.SAXException;

public class LegacyTemplateReadHandler extends TemplateReadHandler {
  public LegacyTemplateReadHandler() {
    super( false );
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
    // Perform legacy initialization..
    if ( rootHandler.getHelperObject( ReportDefinitionReadHandler.ELEMENT_FACTORY_KEY ) == null
        || rootHandler.getHelperObject( ReportDefinitionReadHandler.STYLE_FACTORY_KEY ) == null
        || rootHandler.getHelperObject( ReportDefinitionReadHandler.CLASS_FACTORY_KEY ) == null
        || rootHandler.getHelperObject( ReportDefinitionReadHandler.DATASOURCE_FACTORY_KEY ) == null
        || rootHandler.getHelperObject( ReportDefinitionReadHandler.TEMPLATE_FACTORY_KEY ) == null ) {
      final ElementFactoryCollector elementFactory = new ElementFactoryCollector();
      elementFactory.addFactory( new DefaultElementFactory() );

      final StyleKeyFactoryCollector styleKeyFactory = new StyleKeyFactoryCollector();
      styleKeyFactory.addFactory( new DefaultStyleKeyFactory() );
      styleKeyFactory.addFactory( new PageableLayoutStyleKeyFactory() );

      final ClassFactoryCollector classFactory = new ClassFactoryCollector();
      classFactory.addFactory( new URLClassFactory() );
      classFactory.addFactory( new DefaultClassFactory() );
      classFactory.addFactory( new BandLayoutClassFactory() );
      classFactory.addFactory( new ArrayClassFactory() );

      final DataSourceCollector dataSourceFactory = new DataSourceCollector();
      dataSourceFactory.addFactory( new DefaultDataSourceFactory() );

      final TemplateCollector templateFactory = new TemplateCollector();
      templateFactory.addTemplateCollection( new DefaultTemplateCollection() );

      classFactory.configure( rootHandler.getParserConfiguration() );
      dataSourceFactory.configure( rootHandler.getParserConfiguration() );
      templateFactory.configure( rootHandler.getParserConfiguration() );

      rootHandler.setHelperObject( ReportDefinitionReadHandler.ELEMENT_FACTORY_KEY, elementFactory );
      rootHandler.setHelperObject( ReportDefinitionReadHandler.STYLE_FACTORY_KEY, styleKeyFactory );
      rootHandler.setHelperObject( ReportDefinitionReadHandler.CLASS_FACTORY_KEY, classFactory );
      rootHandler.setHelperObject( ReportDefinitionReadHandler.DATASOURCE_FACTORY_KEY, dataSourceFactory );
      rootHandler.setHelperObject( ReportDefinitionReadHandler.TEMPLATE_FACTORY_KEY, templateFactory );
    }

    super.init( rootHandler, uri, tagName );
  }
}
