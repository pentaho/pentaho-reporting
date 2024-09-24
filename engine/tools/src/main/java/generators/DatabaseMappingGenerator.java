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
