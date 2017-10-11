/*
 * Copyright (c) 2000 World Wide Web Consortium,
 * (Massachusetts Institute of Technology, Institut National de
 * Recherche en Informatique et en Automatique, Keio University). All
 * Rights Reserved. This program is distributed under the W3C's Software
 * Intellectual Property License. This program is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE.
 * See W3C License http://www.w3.org/Consortium/Legal/ for more details.
 *
 * $Id: BeginHyphenAttributeConditionImpl.java 1830 2006-04-23 14:51:03Z taqua $
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
 * Copyright (c) 2000 - 2017 Hitachi Vantara, World Wide Web Consortium,.  All rights reserved.
 */

package org.w3c.flute.parser.selectors;

import org.w3c.css.sac.AttributeCondition;
import org.w3c.css.sac.Condition;

/**
 * @author Philippe Le Hegaret
 * @version $Revision$
 */
public class BeginHyphenAttributeConditionImpl implements AttributeCondition {

  String localName;
  String value;

  /**
   * Creates a new AttributeConditionImpl
   */
  public BeginHyphenAttributeConditionImpl( String localName, String value ) {
    this.localName = localName;
    this.value = value;
  }

  /**
   * An integer indicating the type of <code>Condition</code>.
   */
  public short getConditionType() {
    return Condition.SAC_BEGIN_HYPHEN_ATTRIBUTE_CONDITION;
  }

  /**
   * Returns the <a href="http://www.w3.org/TR/REC-xml-names/#dt-NSName">namespace URI</a> of this attribute
   * condition.
   * <p><code>NULL</code> if : <ul> <li>this attribute condition can match any namespace. <li>this attribute is an id
   * attribute. </ul>
   */
  public String getNamespaceURI() {
    return null;
  }

  /**
   * Returns the <a href="http://www.w3.org/TR/REC-xml-names/#NT-LocalPart">local part</a> of the <a
   * href="http://www.w3.org/TR/REC-xml-names/#ns-qualnames">qualified name</a> of this attribute.
   * <p><code>NULL</code>
   * if : <ul> <li><p>this attribute condition can match any attribute. <li><p>this attribute is a class attribute.
   * <li><p>this attribute is an id attribute. <li><p>this attribute is a pseudo-class attribute. </ul>
   */
  public String getLocalName() {
    return localName;
  }

  /**
   * Returns <code>true</code> if the attribute must have an explicit value in the original document, <code>false</code>
   * otherwise.
   */
  public boolean getSpecified() {
    return false;
  }

  /**
   * Returns the value of the attribute. If this attribute is a class or a pseudo class attribute, you'll get the
   * class
   * name (or psedo class name) without the '.' or ':'.
   */
  public String getValue() {
    return value;
  }
}

