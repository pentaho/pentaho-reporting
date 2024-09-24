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

import org.w3c.css.sac.ElementSelector;

import java.io.Serializable;

/**
 * Creation-Date: 30.11.2005, 16:02:27
 *
 * @author Thomas Morgner
 */
public class CSSElementSelector extends AbstractSelector implements ElementSelector, Serializable {
  private short selectorType;
  private String namespace;
  private String localName;

  public CSSElementSelector( final short selectorType,
                             final String namespace,
                             final String localName ) {
    this.selectorType = selectorType;
    this.namespace = namespace;
    this.localName = localName;
  }

  /**
   * Returns the <a href="http://www.w3.org/TR/REC-xml-names/#dt-NSName">namespace URI</a> of this element selector.
   * <p><code>NULL</code> if this element selector can match any namespace.</p>
   */
  public String getNamespaceURI() {
    return namespace;
  }

  /**
   * Returns the <a href="http://www.w3.org/TR/REC-xml-names/#NT-LocalPart">local part</a> of the <a
   * href="http://www.w3.org/TR/REC-xml-names/#ns-qualnames">qualified name</a> of this element. <p><code>NULL</code> if
   * this element selector can match any element.</p> </ul>
   */
  public String getLocalName() {
    return localName;
  }

  /**
   * An integer indicating the type of <code>Selector</code>
   */
  public short getSelectorType() {
    return selectorType;
  }

  protected SelectorWeight createWeight() {
    return new SelectorWeight( 0, 0, 0, 1 );
  }
}
