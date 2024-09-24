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

package org.pentaho.reporting.engine.classic.core.style.css.selector.conditions;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.style.css.namespaces.NamespaceCollection;
import org.w3c.css.sac.AttributeCondition;

/**
 * Creation-Date: 24.11.2005, 19:54:48
 *
 * @author Thomas Morgner
 */
public class IdCSSCondition implements AttributeCondition, CSSCondition {
  private String value;

  public IdCSSCondition( final String value ) {
    this.value = value;
  }

  /**
   * An integer indicating the type of <code>Condition</code>.
   */
  public short getConditionType() {
    return SAC_ID_CONDITION;
  }

  /**
   * Returns the <a href="http://www.w3.org/TR/REC-xml-names/#dt-NSName">namespace URI</a> of this attribute condition.
   * <p>
   * <code>NULL</code> if :
   * <ul>
   * <li>this attribute condition can match any namespace.
   * <li>this attribute is an id attribute.
   * </ul>
   */
  public String getNamespaceURI() {
    return AttributeNames.Xml.NAMESPACE;
  }

  /**
   * Returns <code>true</code> if the attribute must have an explicit value in the original document, <code>false</code>
   * otherwise.
   */
  public final boolean getSpecified() {
    return false;
  }

  public String getValue() {
    return value;
  }

  /**
   * Returns the <a href="http://www.w3.org/TR/REC-xml-names/#NT-LocalPart">local part</a> of the <a
   * href="http://www.w3.org/TR/REC-xml-names/#ns-qualnames">qualified name</a> of this attribute.
   * <p>
   * <code>NULL</code> if :
   * <ul>
   * <li>
   * <p>
   * this attribute condition can match any attribute.
   * <li>
   * <p>
   * this attribute is a class attribute.
   * <li>
   * <p>
   * this attribute is an id attribute.
   * <li>
   * <p>
   * this attribute is a pseudo-class attribute.
   * </ul>
   */
  public String getLocalName() {
    return "id";
  }

  public String print( final NamespaceCollection namespaces ) {
    return "#" + value;
  }
}
