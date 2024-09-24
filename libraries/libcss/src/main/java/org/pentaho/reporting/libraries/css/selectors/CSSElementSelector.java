/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

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
