/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.core.sorting;

import org.pentaho.reporting.engine.classic.core.CompoundDataFactoryCore;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryMetaData;

import java.util.ArrayList;
import java.util.Arrays;

public class SortingDataFactoryCore extends CompoundDataFactoryCore {
  public SortingDataFactoryCore() {
  }

  public String[] getReferencedFields( final DataFactoryMetaData metaData, final DataFactory element,
      final String query, final DataRow parameter ) {
    String[] referencedFields = super.getReferencedFields( metaData, element, query, parameter );
    if ( referencedFields == null ) {
      return null;
    }

    ArrayList<String> fields = new ArrayList<String>();
    fields.addAll( Arrays.asList( referencedFields ) );
    fields.add( DataFactory.QUERY_SORT );
    return fields.toArray( new String[fields.size()] );
  }
}
