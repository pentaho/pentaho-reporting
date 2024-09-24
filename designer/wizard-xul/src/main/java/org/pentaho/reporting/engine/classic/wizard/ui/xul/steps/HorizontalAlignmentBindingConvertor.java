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

import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.ui.xul.binding.BindingConvertor;

/**
 * @author wseyler
 *         <p/>
 *         Handles conversion between the an ElementAlignment type and an Integer that represents the current selection
 *         in the GUI
 */
public class HorizontalAlignmentBindingConvertor extends BindingConvertor<ElementAlignment, Integer> {

  /* (non-Javadoc)
   * @see org.pentaho.ui.xul.binding.BindingConvertor#sourceToTarget(java.lang.Object)
   */
  @Override
  public Integer sourceToTarget( final ElementAlignment value ) {
    if ( value == null ) {
      return 0;
    }
    if ( value.equals( ElementAlignment.LEFT ) ) {
      return 1;
    } else if ( value.equals( ElementAlignment.MIDDLE ) ) {
      return 2;
    } else if ( value.equals( ElementAlignment.RIGHT ) ) {
      return 3;
    } else if ( value.equals( ElementAlignment.JUSTIFY ) ) {
      return 4;
    }

    return 0;
  }

  /* (non-Javadoc)
   * @see org.pentaho.ui.xul.binding.BindingConvertor#targetToSource(java.lang.Object)
   */
  @Override
  public ElementAlignment targetToSource( final Integer value ) {
    switch( value ) {
      case 0:
        return null;
      case 1:
        return ElementAlignment.LEFT;
      case 2:
        return ElementAlignment.MIDDLE;
      case 3:
        return ElementAlignment.RIGHT;
      case 4:
        return ElementAlignment.JUSTIFY;
      default:
        return null;
    }
  }
}
