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

package org.pentaho.reporting.designer.core.util.table;

import org.pentaho.reporting.engine.classic.core.metadata.MetaData;

import java.io.Serializable;
import java.util.Locale;

public class GroupedName implements Serializable, Comparable {
  private String name;
  private String groupName;
  private MetaData metaData;

  public GroupedName( final MetaData metaData ) {
    this.metaData = metaData;
    this.name = metaData.getDisplayName( Locale.getDefault() );
    this.groupName = metaData.getGrouping( Locale.getDefault() );
  }

  public GroupedName( final MetaData metaData, final String name, final String groupName ) {
    this.metaData = metaData;
    if ( groupName == null ) {
      throw new NullPointerException();
    }
    if ( name == null ) {
      throw new NullPointerException();
    }
    this.name = name;
    this.groupName = groupName;
  }

  public String getName() {
    return name;
  }

  public void setName( final String name ) {
    this.name = name;
  }

  public String getGroupName() {
    return groupName;
  }

  public MetaData getMetaData() {
    return metaData;
  }

  public int compareTo( final Object o ) {
    final GroupedName other = (GroupedName) o;
    if ( other == null ) {
      return 1;
    }
    final int nameResult = name.compareTo( other.name );
    if ( nameResult != 0 ) {
      return nameResult;
    }

    return groupName.compareTo( other.groupName );
  }
}
