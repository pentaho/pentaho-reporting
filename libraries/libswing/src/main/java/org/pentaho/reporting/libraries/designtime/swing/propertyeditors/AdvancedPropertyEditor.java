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

import java.beans.PropertyEditor;

/**
 * Adds the ability for a PropertyEditor to specify if it supports being able to be set via a text field.
 */
public interface AdvancedPropertyEditor extends PropertyEditor {
  public boolean supportsText();
}
