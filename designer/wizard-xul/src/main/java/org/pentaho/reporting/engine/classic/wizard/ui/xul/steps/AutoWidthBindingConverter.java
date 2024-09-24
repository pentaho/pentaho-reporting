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

public class AutoWidthBindingConverter extends BindingConvertor<Length, Boolean> {
  private Length defaultWidth;

  public AutoWidthBindingConverter( final Length defaultWidth ) {
    super();
    this.defaultWidth = defaultWidth;
  }

  /* (non-Javadoc)
   * @see org.pentaho.ui.xul.binding.BindingConvertor#sourceToTarget(java.lang.Object)
   */
  @Override
  public Boolean sourceToTarget( final Length value ) {
    return value == null;
  }

  /* (non-Javadoc)
   * @see org.pentaho.ui.xul.binding.BindingConvertor#targetToSource(java.lang.Object)
   */
  @Override
  public Length targetToSource( final Boolean value ) {
    return value ? null : defaultWidth;
  }

}
