/*
 * Copyright (c) 1999 World Wide Web Consortium,
 * (Massachusetts Institute of Technology, Institut National de
 * Recherche en Informatique et en Automatique, Keio University). All
 * Rights Reserved. This program is distributed under the W3C's Software
 * Intellectual Property License. This program is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE.
 * See W3C License http://www.w3.org/Consortium/Legal/ for more details.
 *
 * $Id: SelectorListImpl.java 1830 2006-04-23 14:51:03Z taqua $
 */
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
 * Copyright (c) 1999 - 2017 Hitachi Vantara, World Wide Web Consortium,.  All rights reserved.
 */

package org.w3c.flute.parser;

import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SelectorList;

/**
 * @author Philippe Le Hegaret
 * @version $Revision$
 */
class SelectorListImpl implements SelectorList {

  Selector[] selectors = new Selector[ 5 ];
  int current;

  public Selector item( int index ) {
    if ( ( index < 0 ) || ( index >= current ) ) {
      return null;
    }
    return selectors[ index ];
  }

  public Selector itemSelector( int index ) {
    if ( ( index < 0 ) || ( index >= current ) ) {
      return null;
    }
    return selectors[ index ];
  }

  public int getLength() {
    return current;
  }

  void addSelector( Selector selector ) {
    if ( current == selectors.length ) {
      Selector[] old = selectors;
      selectors = new Selector[ old.length + old.length ];
      System.arraycopy( old, 0, selectors, 0, old.length );
    }
    selectors[ current++ ] = selector;
  }
}
