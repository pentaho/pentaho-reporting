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

package org.pentaho.openformula.ui.util;

import org.pentaho.openformula.ui.FieldDefinition;

import javax.swing.*;

public class FieldListModel extends AbstractListModel {
  private FieldDefinition[] fields;
  private static final FieldDefinition[] EMPTY = new FieldDefinition[ 0 ];

  public FieldListModel() {
    this.fields = EMPTY;
  }

  public FieldListModel( final FieldDefinition[] fields ) {
    this.fields = fields.clone();
  }

  public FieldDefinition getField( final int index ) {
    return fields[ index ];
  }

  public FieldDefinition getElementAt( final int index ) {
    return fields[ index ];
  }

  public int getSize() {
    if ( fields == null ) {
      return 0;
    }
    return fields.length;
  }


}
