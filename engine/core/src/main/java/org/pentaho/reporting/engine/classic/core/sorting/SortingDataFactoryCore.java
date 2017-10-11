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
