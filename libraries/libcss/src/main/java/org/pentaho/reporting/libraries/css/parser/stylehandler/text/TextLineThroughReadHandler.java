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

package org.pentaho.reporting.libraries.css.parser.stylehandler.text;

import org.pentaho.reporting.libraries.css.keys.text.TextStyleKeys;
import org.pentaho.reporting.libraries.css.parser.stylehandler.AbstractCompoundValueReadHandler;
import org.pentaho.reporting.libraries.css.parser.stylehandler.color.ColorReadHandler;

/**
 * Creation-Date: 03.12.2005, 19:06:09
 *
 * @author Thomas Morgner
 */
public class TextLineThroughReadHandler extends AbstractCompoundValueReadHandler {
  public TextLineThroughReadHandler() {
    addHandler( TextStyleKeys.TEXT_LINE_THROUGH_STYLE, new TextDecorationStyleReadHandler() );
    addHandler( TextStyleKeys.TEXT_LINE_THROUGH_COLOR, new ColorReadHandler() );
    addHandler( TextStyleKeys.TEXT_LINE_THROUGH_WIDTH, new TextDecorationWidthReadHandler() );
    addHandler( TextStyleKeys.TEXT_LINE_THROUGH_MODE, new TextDecorationModeReadHandler() );
  }
}
