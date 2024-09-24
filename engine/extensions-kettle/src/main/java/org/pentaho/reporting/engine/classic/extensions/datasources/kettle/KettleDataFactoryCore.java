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

package org.pentaho.reporting.engine.classic.extensions.datasources.kettle;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.DefaultDataFactoryCore;

import java.util.Locale;

public class KettleDataFactoryCore extends DefaultDataFactoryCore {
  private static final long serialVersionUID = -8347624479657990545L;

  public KettleDataFactoryCore() {
  }

  public String[] getReferencedFields( final DataFactoryMetaData metaData,
                                       final DataFactory element,
                                       final String query,
                                       final DataRow parameter ) {
    final KettleDataFactory kettleDataFactory = (KettleDataFactory) element;
    final KettleTransformationProducer transformationProducer = kettleDataFactory.getQuery( query );
    if ( transformationProducer == null ) {
      return null;
    }

    try {
      return transformationProducer.getReferencedFields();
    } catch ( Exception e ) {
      return null;
    }
  }

  public Object getQueryHash( final DataFactoryMetaData dataFactoryMetaData,
                              final DataFactory dataFactory,
                              final String queryName,
                              final DataRow parameter ) {
    final KettleDataFactory kettleDataFactory = (KettleDataFactory) dataFactory;
    return kettleDataFactory.getQueryHash( queryName );
  }

  public String getDisplayConnectionName( final DataFactoryMetaData metaData, final DataFactory dataFactory ) {
    KettleDataFactory df = (KettleDataFactory) dataFactory;
    if ( df.getMetaData() != null ) {
      return df.getMetaData().getDisplayName( Locale.getDefault() );
    }
    return metaData.getDisplayName( Locale.getDefault() );

  }
}
