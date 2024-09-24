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

package org.pentaho.reporting.libraries.css.resolver.values.computed.line;

import org.pentaho.reporting.libraries.css.keys.line.VerticalAlign;
import org.pentaho.reporting.libraries.css.resolver.values.computed.ConstantsResolveHandler;

public class VerticalAlignResolveHandler extends ConstantsResolveHandler {
  public VerticalAlignResolveHandler() {
    addNormalizeValue( VerticalAlign.BASELINE );
    addNormalizeValue( VerticalAlign.BOTTOM );
    addNormalizeValue( VerticalAlign.CENTRAL );
    addNormalizeValue( VerticalAlign.MIDDLE );
    addNormalizeValue( VerticalAlign.SUB );
    addNormalizeValue( VerticalAlign.SUPER );
    addNormalizeValue( VerticalAlign.TEXT_BOTTOM );
    addNormalizeValue( VerticalAlign.TEXT_TOP );
    addNormalizeValue( VerticalAlign.TOP );
    // we do not detect scripts right now ...
    setFallback( VerticalAlign.BASELINE );
  }

}
