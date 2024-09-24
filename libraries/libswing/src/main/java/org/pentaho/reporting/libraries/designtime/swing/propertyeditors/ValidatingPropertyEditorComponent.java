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

package org.pentaho.reporting.libraries.designtime.swing.propertyeditors;

import java.beans.PropertyChangeListener;

public interface ValidatingPropertyEditorComponent {
  public boolean isValidEditorValue();

  public void addPropertyChangeListener( PropertyChangeListener l );

  public void removePropertyChangeListener( PropertyChangeListener l );

}
