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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class ClearProperties {
  public static void main( String[] args ) throws IOException {
    String path =
      "/Volumes/Datastorex/source/jfreereport/branch-3"
        + ".8/engines/classic/core/source/org/pentaho/reporting/engine/classic/core/modules/gui/base/messages"
        + "/messages_ja.properties";
    //"/Volumes/Datastorex/source/jfreereport/branch-3
    // .8/engines/classic/core/source/org/pentaho/reporting/engine/classic/core/modules/misc/datafactory/messages_ja
    // .properties";
    //        "/Volumes/Datastorex/source/jfreereport/branch-3
    // .8/engines/classic/core/source/org/pentaho/reporting/engine/classic/core/metadata/messages_ja.properties";
    final BufferedReader fin = new BufferedReader( new InputStreamReader( new FileInputStream( path ), "ISO-8859-1" ) );
    String s = fin.readLine();
    while ( s != null ) {
      if ( s.indexOf( ".ordinal" ) != -1 ) {
        s = fin.readLine();
        continue;
      }
      if ( s.indexOf( ".icon" ) != -1 ) {
        s = fin.readLine();
        continue;
      }


      System.out.println( s );
      s = fin.readLine();
    }
    fin.close();

  }
}
