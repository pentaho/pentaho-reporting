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

import org.w3c.css.sac.AttributeCondition;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.CombinatorCondition;
import org.w3c.css.sac.Condition;
import org.w3c.css.sac.ConditionFactory;
import org.w3c.css.sac.ContentCondition;
import org.w3c.css.sac.LangCondition;
import org.w3c.css.sac.NegativeCondition;
import org.w3c.css.sac.PositionalCondition;

public class CSSConditionFactory implements ConditionFactory {
  public CSSConditionFactory() {
  }

  /**
   * Creates an and condition
   *
   * @param first
   *          the first condition
   * @param second
   *          the second condition
   * @return A combinator condition
   * @throws CSSException
   *           if this exception is not supported.
   */
  public CombinatorCondition createAndCondition( final Condition first, final Condition second ) throws CSSException {
    return new AndCSSCondition( (CSSCondition) first, (CSSCondition) second );
  }

  /**
   * Creates an or condition
   *
   * @param first
   *          the first condition
   * @param second
   *          the second condition
   * @return A combinator condition
   * @throws CSSException
   *           if this exception is not supported.
   */
  public CombinatorCondition createOrCondition( final Condition first, final Condition second ) throws CSSException {
    return new OrCSSCondition( (CSSCondition) first, (CSSCondition) second );
  }

  /**
   * Creates a negative condition
   *
   * @param condition
   *          the condition
   * @return A negative condition
   * @throws CSSException
   *           if this exception is not supported.
   */
  public NegativeCondition createNegativeCondition( final Condition condition ) throws CSSException {
    return new NegativeCSSCondition( (CSSCondition) condition );
  }

  /**
   * Creates a positional condition
   *
   * @param position
   *          the position of the node in the list.
   * @param typeNode
   *          <code>true</code> if the list should contain only nodes of the same type (element, text node, ...).
   * @param type
   *          <code>true</code> true if the list should contain only nodes of the same node (for element, same localName
   *          and same namespaceURI).
   * @return A positional condition
   * @throws CSSException
   *           if this exception is not supported.
   */
  public PositionalCondition createPositionalCondition( final int position, final boolean typeNode, final boolean type )
    throws CSSException {
    return new PositionalCSSCondition( position, typeNode, type );
  }

  /**
   * Creates an attribute condition
   *
   * @param localName
   *          the localName of the attribute
   * @param namespaceURI
   *          the namespace URI of the attribute
   * @param specified
   *          <code>true</code> if the attribute must be specified in the document.
   * @param value
   *          the value of this attribute.
   * @return An attribute condition
   * @throws CSSException
   *           if this exception is not supported.
   */
  public AttributeCondition createAttributeCondition( final String localName, final String namespaceURI,
      final boolean specified, final String value ) throws CSSException {
    return new AttributeCSSCondition( localName, namespaceURI, specified, value );
  }

  /**
   * Creates an id condition
   *
   * @param value
   *          the value of the id.
   * @return An Id condition
   * @throws CSSException
   *           if this exception is not supported.
   */
  public AttributeCondition createIdCondition( final String value ) throws CSSException {
    return new IdCSSCondition( value );
  }

  /**
   * Creates a lang condition
   *
   * @param lang
   *          the value of the language.
   * @return A lang condition
   * @throws CSSException
   *           if this exception is not supported.
   */
  public LangCondition createLangCondition( final String lang ) throws CSSException {
    return new LangCSSCondition( lang );
  }

  /**
   * Creates a "one of" attribute condition
   *
   * @param localName
   *          the localName of the attribute
   * @param namespaceURI
   *          the namespace URI of the attribute
   * @param specified
   *          <code>true</code> if the attribute must be specified in the document.
   * @param value
   *          the value of this attribute.
   * @return A "one of" attribute condition
   * @throws CSSException
   *           if this exception is not supported.
   */
  public AttributeCondition createOneOfAttributeCondition( final String localName, final String namespaceURI,
      final boolean specified, final String value ) throws CSSException {
    return new OneOfAttributeCSSCondition( localName, namespaceURI, specified, value );
  }

  /**
   * Creates a class condition
   *
   * @param namespaceURI
   *          the namespace URI of the attribute
   * @param value
   *          the name of the class.
   * @return A class condition
   * @throws CSSException
   *           if this exception is not supported.
   */
  public AttributeCondition createClassCondition( final String namespaceURI, final String value ) throws CSSException {
    return new ClassCSSCondition( namespaceURI, value );
  }

  /**
   * Creates a pseudo class condition
   *
   * @param namespaceURI
   *          the namespace URI of the attribute
   * @param value
   *          the name of the pseudo class
   * @return A pseudo class condition
   * @throws CSSException
   *           if this exception is not supported.
   */
  public AttributeCondition createPseudoClassCondition( final String namespaceURI, final String value )
    throws CSSException {
    return new PseudoClassCSSCondition( namespaceURI, value );
  }

  /**
   * Creates a "begin hyphen" attribute condition
   *
   * @param localName
   *          the localName of the attribute
   * @param namespaceURI
   *          the namespace URI of the attribute
   * @param specified
   *          <code>true</code> if the attribute must be specified in the document.
   * @param value
   *          the value of this attribute.
   * @return A "begin hyphen" attribute condition
   * @throws CSSException
   *           if this exception is not supported.
   */
  public AttributeCondition createBeginHyphenAttributeCondition( final String localName, final String namespaceURI,
      final boolean specified, final String value ) throws CSSException {
    return new BeginHyphenAttributeCSSCondition( localName, namespaceURI, specified, value );
  }

  /**
   * Creates a "only one" child condition
   *
   * @return A "only one" child condition
   * @throws CSSException
   *           if this exception is not supported.
   */
  public Condition createOnlyChildCondition() throws CSSException {
    return null;
  }

  /**
   * Creates a "only one" type condition
   *
   * @return A "only one" type condition
   * @throws CSSException
   *           if this exception is not supported.
   */
  public Condition createOnlyTypeCondition() throws CSSException {
    return null;
  }

  /**
   * Creates a content condition
   *
   * @param data
   *          the data in the content
   * @return A content condition
   * @throws CSSException
   *           if this exception is not supported.
   */
  public ContentCondition createContentCondition( final String data ) throws CSSException {
    return new ContentCSSCondition( data );
  }
}
