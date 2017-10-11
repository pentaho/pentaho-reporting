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
 * $Id: ConditionFactoryImpl.java 1830 2006-04-23 14:51:03Z taqua $
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
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.CombinatorCondition;
import org.w3c.css.sac.Condition;
import org.w3c.css.sac.ConditionFactory;
import org.w3c.css.sac.ContentCondition;
import org.w3c.css.sac.LangCondition;
import org.w3c.css.sac.NegativeCondition;
import org.w3c.css.sac.PositionalCondition;

/**
 * @author Philippe Le Hegaret
 * @version $Revision$
 */
public class ConditionFactoryImpl implements ConditionFactory {

  /**
   * Creates an and condition
   *
   * @param first  the first condition
   * @param second the second condition
   * @return A combinator condition
   * @throws CSSException if this exception is not supported.
   */
  public CombinatorCondition createAndCondition( Condition first,
                                                 Condition second )
    throws CSSException {
    return new AndConditionImpl( first, second );
  }

  /**
   * Creates an or condition
   *
   * @param first  the first condition
   * @param second the second condition
   * @return A combinator condition
   * @throws CSSException if this exception is not supported.
   */
  public CombinatorCondition createOrCondition( Condition first,
                                                Condition second )
    throws CSSException {
    throw new CSSException( CSSException.SAC_NOT_SUPPORTED_ERR );
  }

  /**
   * Creates a negative condition
   *
   * @param condition the condition
   * @return A negative condition
   * @throws CSSException if this exception is not supported.
   */
  public NegativeCondition createNegativeCondition( Condition condition )
    throws CSSException {
    throw new CSSException( CSSException.SAC_NOT_SUPPORTED_ERR );
  }


  /**
   * Creates a positional condition
   *
   * @param position the position of the node in the list.
   * @param typeNode <code>true</code> if the list should contain only nodes of the same type (element, text node,
   *                 ...).
   * @param type     <code>true</code> true if the list should contain only nodes of the same node (for element, same
   *                 localName and same namespaceURI).
   * @return A positional condition
   * @throws CSSException if this exception is not supported.
   */
  public PositionalCondition createPositionalCondition( int position,
                                                        boolean typeNode,
                                                        boolean type )
    throws CSSException {
    throw new CSSException( CSSException.SAC_NOT_SUPPORTED_ERR );
  }

  /**
   * creates an attribute condition
   *
   * @param localName    the localName of the attribute
   * @param namespaceURI the namespace URI of the attribute
   * @param specified    <code>true</code> if the attribute must be specified in the document.
   * @param value        the value of this attribute.
   * @return An attribute condition
   * @throws CSSException if this exception is not supported.
   */
  public AttributeCondition createAttributeCondition( String localName,
                                                      String namespaceURI,
                                                      boolean specified,
                                                      String value )
    throws CSSException {
    if ( ( namespaceURI != null ) || specified ) {
      throw new CSSException( CSSException.SAC_NOT_SUPPORTED_ERR );
    } else {
      return new AttributeConditionImpl( localName, value );
    }
  }

  /**
   * Creates an id condition
   *
   * @param value the value of the id.
   * @return An Id condition
   * @throws CSSException if this exception is not supported.
   */
  public AttributeCondition createIdCondition( String value )
    throws CSSException {
    return new IdConditionImpl( value );
  }

  /**
   * Creates a lang condition
   *
   * @param value the value of the language.
   * @return A lang condition
   * @throws CSSException if this exception is not supported.
   */
  public LangCondition createLangCondition( String lang )
    throws CSSException {
    return new LangConditionImpl( lang );
  }

  /**
   * Creates a "one of" attribute condition
   *
   * @param localName    the localName of the attribute
   * @param namespaceURI the namespace URI of the attribute
   * @param specified    <code>true</code> if the attribute must be specified in the document.
   * @param value        the value of this attribute.
   * @return A "one of" attribute condition
   * @throws CSSException if this exception is not supported.
   */
  public AttributeCondition createOneOfAttributeCondition( String localName,
                                                           String namespaceURI,
                                                           boolean specified,
                                                           String value )
    throws CSSException {
    if ( ( namespaceURI != null ) || specified ) {
      throw new CSSException( CSSException.SAC_NOT_SUPPORTED_ERR );
    } else {
      return new OneOfAttributeConditionImpl( localName, value );
    }
  }

  /**
   * Creates a "begin hyphen" attribute condition
   *
   * @param localName    the localName of the attribute
   * @param namespaceURI the namespace URI of the attribute
   * @param specified    <code>true</code> if the attribute must be specified in the document.
   * @param value        the value of this attribute.
   * @return A "begin hyphen" attribute condition
   * @throws CSSException if this exception is not supported.
   */
  public AttributeCondition createBeginHyphenAttributeCondition( String localName,
                                                                 String namespaceURI,
                                                                 boolean specified,
                                                                 String value )
    throws CSSException {
    if ( ( namespaceURI != null ) || specified ) {
      throw new CSSException( CSSException.SAC_NOT_SUPPORTED_ERR );
    } else {
      return new BeginHyphenAttributeConditionImpl( localName, value );
    }
  }

  /**
   * Creates a class condition
   *
   * @param localName    the localName of the attribute
   * @param namespaceURI the namespace URI of the attribute
   * @param specified    <code>true</code> if the attribute must be specified in the document.
   * @param value        the name of the class.
   * @return A class condition
   * @throws CSSException if this exception is not supported.
   */
  public AttributeCondition createClassCondition( String namespaceURI,
                                                  String value )
    throws CSSException {
    return new ClassConditionImpl( value );
  }

  /**
   * Creates a pseudo class condition
   *
   * @param namespaceURI the namespace URI of the attribute
   * @param value        the name of the pseudo class
   * @return A pseudo class condition
   * @throws CSSException if this exception is not supported.
   */
  public AttributeCondition createPseudoClassCondition( String namespaceURI,
                                                        String value )
    throws CSSException {
    return new PseudoClassConditionImpl( value );
  }

  /**
   * Creates a "only one" child condition
   *
   * @return A "only one" child condition
   * @throws CSSException if this exception is not supported.
   */
  public Condition createOnlyChildCondition() throws CSSException {
    throw new CSSException( CSSException.SAC_NOT_SUPPORTED_ERR );
  }

  /**
   * Creates a "only one" type condition
   *
   * @return A "only one" type condition
   * @throws CSSException if this exception is not supported.
   */
  public Condition createOnlyTypeCondition() throws CSSException {
    throw new CSSException( CSSException.SAC_NOT_SUPPORTED_ERR );
  }

  /**
   * Creates a content condition
   *
   * @param data the data in the content
   * @return A content condition
   * @throws CSSException if this exception is not supported.
   */
  public ContentCondition createContentCondition( String data )
    throws CSSException {
    throw new CSSException( CSSException.SAC_NOT_SUPPORTED_ERR );
  }
}
