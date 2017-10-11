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

package org.pentaho.reporting.engine.classic.core.style.css;

/**
 * There are two kinds of style-references. Type one simply references a inline style, which has no style-source. The
 * second one is a external stylesheet that has a style-source, and possibly has a style-content as well.
 *
 * @author : Thomas Morgner
 */
public class StyleReference {
  public static final int LINK = 0;
  public static final int INLINE = 1;

  private String styleContent;
  private int type;

  public StyleReference( final int type, final String styleContent ) {
    this.type = type;
    this.styleContent = styleContent;
  }

  public int getType() {
    return type;
  }

  public String getStyleContent() {
    return styleContent;
  }
}
