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

package org.pentaho.reporting.libraries.css.dom;

import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.values.CSSValue;

/**
 * Creation-Date: 18.11.2006, 18:55:03
 *
 * @author Thomas Morgner
 */
public interface LayoutStyle {
  public CSSValue getValue( StyleKey key );

  public void setValue( StyleKey key, CSSValue value );

  public boolean copyFrom( LayoutStyle style );
}
