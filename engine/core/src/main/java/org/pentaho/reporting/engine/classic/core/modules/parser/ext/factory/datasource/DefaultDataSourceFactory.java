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

package org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.datasource;

import org.pentaho.reporting.engine.classic.core.filter.AnchorFilter;
import org.pentaho.reporting.engine.classic.core.filter.ComponentDrawableFilter;
import org.pentaho.reporting.engine.classic.core.filter.DataRowDataSource;
import org.pentaho.reporting.engine.classic.core.filter.DateFormatFilter;
import org.pentaho.reporting.engine.classic.core.filter.DateFormatParser;
import org.pentaho.reporting.engine.classic.core.filter.DecimalFormatFilter;
import org.pentaho.reporting.engine.classic.core.filter.DecimalFormatParser;
import org.pentaho.reporting.engine.classic.core.filter.DrawableLoadFilter;
import org.pentaho.reporting.engine.classic.core.filter.EmptyDataSource;
import org.pentaho.reporting.engine.classic.core.filter.FormatFilter;
import org.pentaho.reporting.engine.classic.core.filter.FormatParser;
import org.pentaho.reporting.engine.classic.core.filter.ImageLoadFilter;
import org.pentaho.reporting.engine.classic.core.filter.ImageRefFilter;
import org.pentaho.reporting.engine.classic.core.filter.MessageFormatFilter;
import org.pentaho.reporting.engine.classic.core.filter.NumberFormatFilter;
import org.pentaho.reporting.engine.classic.core.filter.NumberFormatParser;
import org.pentaho.reporting.engine.classic.core.filter.ResourceFileFilter;
import org.pentaho.reporting.engine.classic.core.filter.ResourceMessageFormatFilter;
import org.pentaho.reporting.engine.classic.core.filter.ShapeFilter;
import org.pentaho.reporting.engine.classic.core.filter.SimpleDateFormatFilter;
import org.pentaho.reporting.engine.classic.core.filter.SimpleDateFormatParser;
import org.pentaho.reporting.engine.classic.core.filter.StaticDataSource;
import org.pentaho.reporting.engine.classic.core.filter.StringFilter;
import org.pentaho.reporting.engine.classic.core.filter.URLFilter;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base.BeanObjectDescription;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.templates.DefaultTemplateCollection;

/**
 * A default implementation of the {@link DataSourceFactory} interface.
 *
 * @author Thomas Morgner
 */
public class DefaultDataSourceFactory extends AbstractDataSourceFactory {
  /**
   * Creates a new factory.
   */
  public DefaultDataSourceFactory() {
    registerDataSources( "AnchorFilter", new BeanObjectDescription( AnchorFilter.class ) );
    registerDataSources( "ComponentDrawableFilter", new BeanObjectDescription( ComponentDrawableFilter.class ) );
    registerDataSources( "DataRowDataSource", new BeanObjectDescription( DataRowDataSource.class ) );
    registerDataSources( "DateFormatFilter", new BeanObjectDescription( DateFormatFilter.class ) );
    registerDataSources( "DateFormatParser", new BeanObjectDescription( DateFormatParser.class ) );
    registerDataSources( "DecimalFormatFilter", new BeanObjectDescription( DecimalFormatFilter.class ) );
    registerDataSources( "DecimalFormatParser", new BeanObjectDescription( DecimalFormatParser.class ) );
    registerDataSources( "DrawableLoadFilter", new BeanObjectDescription( DrawableLoadFilter.class ) );
    registerDataSources( "EmptyDataSource", new BeanObjectDescription( EmptyDataSource.class ) );
    registerDataSources( "FormatFilter", new BeanObjectDescription( FormatFilter.class ) );
    registerDataSources( "FormatParser", new BeanObjectDescription( FormatParser.class ) );
    registerDataSources( "ImageLoadFilter", new BeanObjectDescription( ImageLoadFilter.class ) );
    registerDataSources( "ImageRefFilter", new BeanObjectDescription( ImageRefFilter.class ) );
    registerDataSources( "MessageFormatFilter", new BeanObjectDescription( MessageFormatFilter.class ) );
    registerDataSources( "NumberFormatFilter", new BeanObjectDescription( NumberFormatFilter.class ) );
    registerDataSources( "NumberFormatParser", new BeanObjectDescription( NumberFormatParser.class ) );
    registerDataSources( "ResourceFileFilter", new BeanObjectDescription( ResourceFileFilter.class ) );
    registerDataSources( "ResourceMessageFormatFilter", new BeanObjectDescription( ResourceMessageFormatFilter.class ) );
    registerDataSources( "ShapeFilter", new BeanObjectDescription( ShapeFilter.class ) );
    registerDataSources( "SimpleDateFormatFilter", new BeanObjectDescription( SimpleDateFormatFilter.class ) );
    registerDataSources( "SimpleDateFormatParser", new BeanObjectDescription( SimpleDateFormatParser.class ) );
    registerDataSources( "StaticDataSource", new BeanObjectDescription( StaticDataSource.class ) );
    registerDataSources( "StringFilter", new BeanObjectDescription( StringFilter.class ) );
    registerDataSources( "URLFilter", new URLFilterObjectDescription( URLFilter.class ) );

    final DefaultTemplateCollection templateCollection = new DefaultTemplateCollection();

    final String[] templateNames = templateCollection.getNames();
    for ( int i = 0; i < templateNames.length; i++ ) {
      final String name = templateNames[i];
      registerDataSources( name, templateCollection.getTemplate( name ) );
    }
  }

}
