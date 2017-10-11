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

package org.pentaho.reporting.libraries.fonts.text;

/**
 * Creation-Date: 11.06.2006, 18:37:39
 *
 * @author Thomas Morgner
 */
public class StaticSpacingProducer implements SpacingProducer {
  private Spacing spacing;

  public StaticSpacingProducer( final Spacing spacing ) {
    if ( spacing == null ) {
      this.spacing = Spacing.EMPTY_SPACING;
    } else {
      this.spacing = spacing;
    }
  }

  public Spacing createSpacing( final int codePoint ) {
    return spacing;
  }


  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }
}
