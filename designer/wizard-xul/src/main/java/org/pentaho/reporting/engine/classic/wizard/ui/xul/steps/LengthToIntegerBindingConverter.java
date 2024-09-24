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

package org.pentaho.reporting.engine.classic.wizard.ui.xul.steps;

import org.pentaho.reporting.engine.classic.wizard.model.Length;
import org.pentaho.ui.xul.binding.BindingConvertor;

/**
 * @author wseyler
 *         <p/>
 *         Provides a converter between the Length type and a string that represents the length
 */
public class LengthToIntegerBindingConverter extends BindingConvertor<Length, Integer> {

  /* (non-Javadoc)
   * @see org.pentaho.ui.xul.binding.BindingConvertor#sourceToTarget(java.lang.Object)
   */
  @Override
  public Integer sourceToTarget( final Length value ) {
    if ( value == null ) {
      return 0;
    }
    return Integer.valueOf( (int) value.getValue() );
  }

  /* (non-Javadoc)
   * @see org.pentaho.ui.xul.binding.BindingConvertor#targetToSource(java.lang.Object)
   */
  @Override
  public Length targetToSource( final Integer value ) {
    if ( value == null || value == 0 ) {
      return null;
    }

    try {
      final String strValue = value.toString() + "%"; //$NON-NLS-1$
      return Length.parseLength( strValue );
    } catch ( Exception ex ) {
      return null;
    }
  }
}
