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

import org.pentaho.reporting.libraries.css.keys.line.LineStackingStrategy;
import org.pentaho.reporting.libraries.css.resolver.values.computed.ConstantsResolveHandler;

public class LineStackingStrategyResolveHandler extends ConstantsResolveHandler {
  public LineStackingStrategyResolveHandler() {
    addNormalizeValue( LineStackingStrategy.BLOCK_LINE_HEIGHT );
    addNormalizeValue( LineStackingStrategy.GRID_HEIGHT );
    addNormalizeValue( LineStackingStrategy.INLINE_LINE_HEIGHT );
    addNormalizeValue( LineStackingStrategy.MAX_LINE_HEIGHT );
    setFallback( LineStackingStrategy.INLINE_LINE_HEIGHT );
  }

}
