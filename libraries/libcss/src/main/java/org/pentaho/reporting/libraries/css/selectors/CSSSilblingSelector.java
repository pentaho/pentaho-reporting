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

package org.pentaho.reporting.libraries.css.selectors;

import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SiblingSelector;
import org.w3c.css.sac.SimpleSelector;

import java.io.Serializable;

/**
 * We do not support DOM node types, we always assume elements here (or evaluate both selectors to see if they match).
 *
 * @author Thomas Morgner
 */
public class CSSSilblingSelector extends AbstractSelector
  implements SiblingSelector, Serializable {
  private short nodeType;
  private Selector selector;
  private SimpleSelector silblingSelector;

  public CSSSilblingSelector( final short nodeType,
                              final Selector selector,
                              final SimpleSelector silblingSelector ) {
    this.nodeType = nodeType;
    this.selector = selector;
    this.silblingSelector = silblingSelector;
  }

  /**
   * The node type to considered in the siblings list. All DOM node types are supported. In order to support the "any"
   * node type, the code ANY_NODE is added to the DOM node types.
   */
  public short getNodeType() {
    return nodeType;
  }

  /**
   * Returns the first selector.
   */
  public Selector getSelector() {
    return selector;
  }

  /*
  * Returns the second selector.
  */
  public SimpleSelector getSiblingSelector() {
    return silblingSelector;
  }

  /**
   * An integer indicating the type of <code>Selector</code>
   */
  public short getSelectorType() {
    return SAC_DIRECT_ADJACENT_SELECTOR;
  }

  protected SelectorWeight createWeight() {
    if ( silblingSelector instanceof CSSSelector == false ||
      selector instanceof CSSSelector == false ) {
      throw new ClassCastException( "Invalid selector implementation!" );
    }
    CSSSelector anchestor = (CSSSelector) silblingSelector;
    CSSSelector simple = (CSSSelector) selector;
    return new SelectorWeight( anchestor.getWeight(), simple.getWeight() );
  }
}
