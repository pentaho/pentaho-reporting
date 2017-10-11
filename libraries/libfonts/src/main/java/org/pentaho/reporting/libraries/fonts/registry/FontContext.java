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

/**
 * The font context decribes how a certain font will be used. The context influences the font metrics and therefore a
 * certain metrics object is only valid for a given font context.
 *
 * @author Thomas Morgner
 */
public interface FontContext {
  public String getEncoding();

  public boolean isEmbedded();

  /**
   * This is controlled by the output target and the stylesheet. If the output target does not support aliasing, it
   * makes no sense to enable it and all such requests are ignored.
   *
   * @return
   */
  public boolean isAntiAliased();

  /**
   * This is defined by the output target. This is not controlled by the stylesheet.
   *
   * @return
   */
  public boolean isFractionalMetrics();

  /**
   * The requested font size. A font may have a fractional font size (ie. 8.5 point). The font size may be influenced by
   * the output target.
   *
   * @return the font size.
   */
  public double getFontSize();

}
