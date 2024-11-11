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
