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

package org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.types;

import org.olap4j.metadata.Member;

import java.util.Locale;

public class LocalizedString {
  private Member member;
  private boolean description;

  public LocalizedString( final Member member, final boolean description ) {
    this.member = member;
    this.description = description;
  }

  public String getValue( final Locale locale ) {
    if ( description ) {
      return member.getDescription();
    }
    return member.getCaption();
  }

  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append( "LocalizedString" );
    sb.append( "{description=" ).append( description );
    sb.append( ", member=" ).append( member.getUniqueName() );
    sb.append( '}' );
    return sb.toString();
  }
}
