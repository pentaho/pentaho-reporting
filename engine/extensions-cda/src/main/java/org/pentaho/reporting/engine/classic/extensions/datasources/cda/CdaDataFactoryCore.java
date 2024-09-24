/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.extensions.datasources.cda;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ParameterMapping;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.DefaultDataFactoryCore;

import java.util.ArrayList;
import java.util.LinkedHashSet;

/**
 * Core methods to allow caching of the CDA queries.
 * <p/>
 * Created by Andre Simoes (andre.simoes (at) xpand-it (dot) com).
 */
public class CdaDataFactoryCore extends DefaultDataFactoryCore {
  public String[] getReferencedFields( final DataFactoryMetaData metaData,
                                       final DataFactory element,
                                       final String query,
                                       final DataRow parameter ) {
    final CdaDataFactory df = (CdaDataFactory) element;
    final LinkedHashSet<String> list = new LinkedHashSet<String>();

    final ParameterMapping[] params = df.getQueryEntry( query ).getParameters();
    for ( int i = 0; i < params.length; i++ ) {
      String paramName = params[ i ].getName();
      list.add( paramName );
    }

    return list.toArray( new String[ list.size() ] );
  }

  public Object getQueryHash( final DataFactoryMetaData dataFactoryMetaData,
                              final DataFactory dataFactory,
                              final String queryName,
                              final DataRow parameter ) {
    final CdaDataFactory df = (CdaDataFactory) dataFactory;
    final ArrayList<Object> list = new ArrayList<Object>();

    list.add( df.getPath() );
    list.add( df.getQueryEntry( queryName ) );
    list.add( df.getBaseUrl() );
    list.add( df.getUsername() );

    return list;
  }
}
