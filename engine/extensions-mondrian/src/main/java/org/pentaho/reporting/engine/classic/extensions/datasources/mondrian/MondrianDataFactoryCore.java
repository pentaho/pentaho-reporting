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

package org.pentaho.reporting.engine.classic.extensions.datasources.mondrian;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.DefaultDataFactoryCore;
import org.pentaho.reporting.libraries.base.util.StringUtils;

public class MondrianDataFactoryCore extends DefaultDataFactoryCore {
  private static final Log logger = LogFactory.getLog( MondrianDataFactoryCore.class );

  public MondrianDataFactoryCore() {
  }

  public String getDisplayConnectionName( final DataFactoryMetaData metaData,
                                          final DataFactory dataFactory ) {
    final AbstractMDXDataFactory mdxDataFactory = (AbstractMDXDataFactory) dataFactory;
    final String designTimeName = mdxDataFactory.getDesignTimeName();
    final CubeFileProvider cubeFileProvider = mdxDataFactory.getCubeFileProvider();

    final StringBuilder b = new StringBuilder();
    if ( StringUtils.isEmpty( designTimeName ) == false ) {
      b.append( designTimeName );
    }

    if ( cubeFileProvider != null ) {
      final String cubeName = cubeFileProvider.getCubeConnectionName();
      final String fileName = cubeFileProvider.getDesignTimeFile();
      if ( StringUtils.isEmpty( cubeName ) == false ) {
        if ( b.length() != 0 ) {
          b.append( " - " );
        }
        b.append( cubeName );
      }
      if ( StringUtils.isEmpty( fileName ) == false ) {
        if ( b.length() != 0 ) {
          b.append( " " );
        }
        b.append( "(" );
        b.append( fileName );
        b.append( ")" );
      }
    }
    if ( b.length() != 0 ) {
      return b.toString();
    }
    return null;
  }


  public Object getQueryHash( final DataFactoryMetaData dataFactoryMetaData,
                              final DataFactory dataFactory,
                              final String queryName, final DataRow parameter ) {
    try {
      final AbstractMDXDataFactory mdxDataFactory = (AbstractMDXDataFactory) dataFactory;
      return mdxDataFactory.getQueryHash( queryName, parameter );
    } catch ( ReportDataFactoryException e ) {
      logger.warn( "Unable to create query hash", e );
      return null;
    }
  }

  public String[] getReferencedFields( final DataFactoryMetaData metaData,
                                       final DataFactory element,
                                       final String query,
                                       final DataRow parameter ) {
    try {
      final AbstractMDXDataFactory mdxDataFactory = (AbstractMDXDataFactory) element;
      return mdxDataFactory.getReferencedFields( query, parameter );
    } catch ( ReportDataFactoryException e ) {
      logger.warn( "Unable to collect referenced fields", e );
      return null;
    }
  }
}
