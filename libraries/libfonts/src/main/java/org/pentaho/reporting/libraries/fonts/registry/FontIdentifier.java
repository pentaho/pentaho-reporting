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

package org.pentaho.reporting.libraries.fonts.registry;

import java.io.Serializable;

/**
 * A font identifier is a general handle to map Font-Metrics for a given font. The same font identifier may be used by
 * several fonts, if the fonts share the same metrics (this is commonly true for TrueType fonts).
 *
 * @author Thomas Morgner
 */
public interface FontIdentifier extends Serializable {
  public boolean equals( Object o );

  public int hashCode();

  /**
   * Defines, whether the font identifier represents a scalable font type. Such fonts usually create one font metric
   * object for each physical font, and apply the font size afterwards.
   *
   * @return true, if the font is scalable, false otherwise
   */
  public boolean isScalable();

  /**
   * Returns the general type of this font identifier. This is for debugging, not for the real world.
   *
   * @return
   */
  public FontType getFontType();
}
