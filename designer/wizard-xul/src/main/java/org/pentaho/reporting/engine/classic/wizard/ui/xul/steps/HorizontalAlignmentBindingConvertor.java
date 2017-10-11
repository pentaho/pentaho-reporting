/*!
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
* Foundation.
*
* You should have received a copy of the GNU Lesser General Public License along with this
* program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
* or from the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU Lesser General Public License for more details.
*
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

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
