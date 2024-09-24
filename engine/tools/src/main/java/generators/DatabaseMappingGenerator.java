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

package generators;

import org.pentaho.di.core.database.DatabaseInterface;
import org.pentaho.di.core.database.DatabaseMeta;

import java.util.HashSet;

public class DatabaseMappingGenerator {

  public static void main( String[] args ) {
    final HashSet knownDrivers = new HashSet();
    final DatabaseInterface[] interfaces = DatabaseMeta.getDatabaseInterfaces();
    for ( int i = 0; i < interfaces.length; i++ ) {
      final DatabaseInterface dbi = interfaces[ i ];
      final int[] accessTypeList = dbi.getAccessTypeList();
      for ( int j = 0; j < accessTypeList.length; j++ ) {
        final int al = accessTypeList[ j ];
        if ( al != DatabaseMeta.TYPE_ACCESS_ODBC ) {

          dbi.setAccessType( al );
          final String driver = dbi.getDriverClass();
          if ( knownDrivers.contains( driver ) == false ) {
            System.out.println( driver + "=" + dbi.getClass().getName() );
            knownDrivers.add( driver );
          }
        }
      }
    }

  }
}
