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

package org.pentaho.reporting.engine.classic.extensions.datasources.olap4j;

import org.olap4j.metadata.Dimension;
import org.olap4j.metadata.Hierarchy;
import org.olap4j.metadata.Member;

public class Olap4jUtil {

  private static String quoteMdxIdentifier( String ident ) {
    return "[" + ident.replaceAll( "]", "]]" ) + "]";
  }

  public static String getUniqueMemberName( Member member ) {
    String memberValue = quoteMdxIdentifier( member.getName() );
    while ( member.getParentMember() != null ) {
      memberValue = quoteMdxIdentifier( member.getParentMember().getName() ) + "." + memberValue;
      member = member.getParentMember();
    }
    final Hierarchy hierarchy = member.getHierarchy();
    final Dimension dimension = hierarchy.getDimension();
    if ( hierarchy.getName().equals( dimension.getName() ) ) {
      return quoteMdxIdentifier( hierarchy.getName() ) + "." + memberValue;
    } else {
      return quoteMdxIdentifier( dimension.getName() ) + "." + quoteMdxIdentifier( hierarchy.getName() ) + "." +
        memberValue;
    }
  }
}
