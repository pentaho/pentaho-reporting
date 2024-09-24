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

package org.pentaho.reporting.libraries.css.parser.stylehandler.line;

import org.pentaho.reporting.libraries.css.keys.line.AlignmentBaseline;
import org.pentaho.reporting.libraries.css.parser.stylehandler.OneOfConstantsReadHandler;

/**
 * Creation-Date: 28.11.2005, 18:12:27
 *
 * @author Thomas Morgner
 */
public class AlignmentBaselineReadHandler extends OneOfConstantsReadHandler {
  public AlignmentBaselineReadHandler() {
    super( true );
    addValue( AlignmentBaseline.AFTER_EDGE );
    addValue( AlignmentBaseline.ALPHABETIC );
    addValue( AlignmentBaseline.CENTRAL );
    addValue( AlignmentBaseline.HANGING );
    addValue( AlignmentBaseline.IDEOGRAPHIC );
    addValue( AlignmentBaseline.MATHEMATICAL );
    addValue( AlignmentBaseline.MIDDLE );
    addValue( AlignmentBaseline.BEFORE_EDGE );
    addValue( AlignmentBaseline.TEXT_AFTER_EDGE );
    addValue( AlignmentBaseline.TEXT_BEFORE_EDGE );
    addValue( AlignmentBaseline.USE_SCRIPT );
    addValue( AlignmentBaseline.BASELINE );
  }
}
