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

import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.css.selector.CSSSelector;

import java.util.ArrayList;
import java.util.List;

public class ElementStyleRule extends ElementStyleSheet {
  private ArrayList<CSSSelector> selectorList;

  public ElementStyleRule() {
    selectorList = new ArrayList<CSSSelector>();
  }

  public List<CSSSelector> getSelectorList() {
    return selectorList;
  }

  public void addSelector( final CSSSelector selector ) {
    this.selectorList.add( selector );
  }

  public void addSelector( final int index, final CSSSelector selector ) {
    this.selectorList.add( index, selector );
  }

  public CSSSelector getSelector( final int index ) {
    return this.selectorList.get( index );
  }

  public void removeSelector( final int index ) {
    this.selectorList.remove( index );
  }

  public void removeSelector( final CSSSelector element ) {
    this.selectorList.remove( element );
  }

  public int getSelectorCount() {
    return selectorList.size();
  }

  public ElementStyleRule clone() {
    final ElementStyleRule clone = (ElementStyleRule) super.clone();
    clone.selectorList = (ArrayList<CSSSelector>) selectorList.clone();
    return clone;
  }

  public ElementStyleRule derive( final boolean preserveId ) {
    final ElementStyleRule clone = (ElementStyleRule) super.derive( preserveId );
    clone.selectorList = (ArrayList<CSSSelector>) selectorList.clone();
    return clone;
  }

  public void clearSelectors() {
    selectorList.clear();
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( !( o instanceof ElementStyleRule ) ) {
      return false;
    }

    final ElementStyleRule that = (ElementStyleRule) o;

    if ( selectorList != null ? !selectorList.equals( that.selectorList ) : that.selectorList != null ) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    return selectorList != null ? selectorList.hashCode() : 0;
  }
}
