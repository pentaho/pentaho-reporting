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
* Copyright (c) 2008 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.formatting;

import java.io.Serializable;
import java.util.Locale;

/**
 * A fast-format is a wrapper around the existing java.text.Formatter objects. The fast-format reduces the possible
 * interactions with the wrapped formatter and therefore allows to treat the formatter as some sort of immutable object.
 * This later simplifies cloning, as now the formatter and all of its internal objects no longer need to be cloned.
 *
 * @author Thomas Morgner
 */
public interface FastFormat extends Serializable, Cloneable {
  /**
   * Returns the current locale of the formatter.
   *
   * @return the current locale, never null.
   */
  public Locale getLocale();

  /**
   * Formats the given object in a formatter-specific way.
   *
   * @param parameters the parameters for the formatting.
   * @return the formatted string.
   */
  public String format( Object parameters );

  /**
   * Clones the formatter.
   *
   * @return the clone.
   * @throws CloneNotSupportedException if cloning failed.
   */
  public Object clone() throws CloneNotSupportedException;
}
