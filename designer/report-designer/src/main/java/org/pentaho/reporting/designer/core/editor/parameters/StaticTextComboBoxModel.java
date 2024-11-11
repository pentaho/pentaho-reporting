/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.designer.core.editor.parameters;

import org.pentaho.reporting.libraries.base.util.StringUtils;

import javax.swing.*;

/**
 * Todo: Document me!
 * <p/>
 * Date: 09.04.2009 Time: 18:36:34
 *
 * @author Thomas Morgner.
 */
public class StaticTextComboBoxModel extends AbstractListModel implements ComboBoxModel {
  private String[] values;
  private String selectedItem;

  public StaticTextComboBoxModel() {
    values = new String[ 0 ];
  }

  public void setValues( final String[] values ) {
    this.values = values.clone();
    fireContentsChanged( this, 0, values.length );
  }

  public int getSize() {
    return values.length;
  }

  public Object getElementAt( final int index ) {
    return values[ index ];
  }

  public void setSelectedItem( final Object anItem ) {
    selectedItem = (String) anItem;
    fireContentsChanged( this, -1, -1 );
  }

  public Object getSelectedItem() {
    return StringUtils.isEmpty( selectedItem ) ? null : selectedItem;
  }
}
