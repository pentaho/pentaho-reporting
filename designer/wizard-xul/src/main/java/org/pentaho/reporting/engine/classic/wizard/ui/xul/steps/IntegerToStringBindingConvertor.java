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

import org.pentaho.ui.xul.binding.BindingConvertor;

/**
 * @author wseyler
 */
public class IntegerToStringBindingConvertor extends BindingConvertor<Integer, String> {

  /* (non-Javadoc)
   * @see org.pentaho.ui.xul.binding.BindingConvertor#sourceToTarget(java.lang.Object)
   */
  @Override
  public String sourceToTarget( final Integer value ) {
    if ( value == null ) {
      return ""; //$NON-NLS-1$
    }
    return value.toString();
  }

  /* (non-Javadoc)
   * @see org.pentaho.ui.xul.binding.BindingConvertor#targetToSource(java.lang.Object)
   */
  @Override
  public Integer targetToSource( final String value ) {
    return new Integer( value );
  }

}
