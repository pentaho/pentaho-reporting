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

import org.pentaho.reporting.engine.classic.wizard.ui.xul.Messages;
import org.pentaho.ui.xul.binding.BindingConvertor;

/**
 * @author wseyler
 */
public class DataFormatBindingConvertor extends BindingConvertor<String, Object> {
  private String emptyDateFormatMessage;

  public DataFormatBindingConvertor() {
    emptyDateFormatMessage = Messages.getInstance().getString( "FORMAT_STEP.None" );
  }

  /* (non-Javadoc)
   * @see org.pentaho.ui.xul.binding.BindingConvertor#sourceToTarget(java.lang.Object)
   */
  @Override
  public Object sourceToTarget( final String value ) {
    if ( value == null || value.length() < 1 ) {
      return emptyDateFormatMessage;  //$NON-NLS-1$
    }
    return value;
  }

  /* (non-Javadoc)
   * @see org.pentaho.ui.xul.binding.BindingConvertor#targetToSource(java.lang.Object)
   */
  @Override
  public String targetToSource( final Object value ) {
    if ( value.toString().equals( emptyDateFormatMessage ) ) {  //$NON-NLS-1$
      return null;
    }
    return value.toString();
  }

}
