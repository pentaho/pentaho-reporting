/*
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
* Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.css.resolver.values.computed.border;

import org.pentaho.reporting.libraries.css.keys.border.BorderStyle;
import org.pentaho.reporting.libraries.css.resolver.values.computed.ConstantsResolveHandler;

/**
 * Creation-Date: 14.12.2005, 23:27:55
 *
 * @author Thomas Morgner
 */
public class BorderStyleResolveHandler extends ConstantsResolveHandler {
  public BorderStyleResolveHandler() {
    addNormalizeValue( BorderStyle.DASHED );
    addNormalizeValue( BorderStyle.DOT_DASH );
    addNormalizeValue( BorderStyle.DOT_DOT_DASH );
    addNormalizeValue( BorderStyle.DOTTED );
    addNormalizeValue( BorderStyle.DOUBLE );
    addNormalizeValue( BorderStyle.GROOVE );
    addNormalizeValue( BorderStyle.HIDDEN );
    addNormalizeValue( BorderStyle.INSET );
    addNormalizeValue( BorderStyle.NONE );
    addNormalizeValue( BorderStyle.OUTSET );
    addNormalizeValue( BorderStyle.RIDGE );
    addNormalizeValue( BorderStyle.SOLID );
    addNormalizeValue( BorderStyle.WAVE );
    setFallback( BorderStyle.NONE );
  }


}
