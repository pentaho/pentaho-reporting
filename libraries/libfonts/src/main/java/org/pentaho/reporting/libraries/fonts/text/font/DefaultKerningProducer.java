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

package org.pentaho.reporting.libraries.fonts.text.font;

import org.pentaho.reporting.libraries.fonts.registry.FontMetrics;
import org.pentaho.reporting.libraries.fonts.text.ClassificationProducer;

/**
 * Creation-Date: 11.06.2006, 18:30:42
 *
 * @author Thomas Morgner
 */
public class DefaultKerningProducer implements KerningProducer {
  private int lastCodePoint;
  private FontMetrics fontMetrics;

  public DefaultKerningProducer( final FontMetrics fontMetrics ) {
    if ( fontMetrics == null ) {
      throw new NullPointerException();
    }
    this.fontMetrics = fontMetrics;
  }

  public long getKerning( final int codePoint ) {
    if ( codePoint == ClassificationProducer.START_OF_TEXT || codePoint == ClassificationProducer.END_OF_TEXT ) {
      lastCodePoint = 0;
      return 0;
    }

    final long d = fontMetrics.getKerning( lastCodePoint, codePoint );
    lastCodePoint = codePoint;
    return d;
  }

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

}
