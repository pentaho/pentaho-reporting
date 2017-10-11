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
 * $Id: SelectorFactoryImpl.java 1830 2006-04-23 14:51:03Z taqua $
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

import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.CharacterDataSelector;
import org.w3c.css.sac.Condition;
import org.w3c.css.sac.ConditionalSelector;
import org.w3c.css.sac.DescendantSelector;
import org.w3c.css.sac.ElementSelector;
import org.w3c.css.sac.NegativeSelector;
import org.w3c.css.sac.ProcessingInstructionSelector;
import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SelectorFactory;
import org.w3c.css.sac.SiblingSelector;
import org.w3c.css.sac.SimpleSelector;

/**
 * @author Philippe Le Hegaret
 * @version $Revision$
 */
public class SelectorFactoryImpl implements SelectorFactory {

  /**
   * Creates a conditional selector.
   *
   * @param selector  a selector.
   * @param condition a condition
   * @return the conditional selector.
   * @throws CSSException If this selector is not supported.
   */
  public ConditionalSelector createConditionalSelector( SimpleSelector selector,
                                                        Condition condition )
    throws CSSException {
    return new ConditionalSelectorImpl( selector, condition );
  }

  /**
   * Creates an any node selector.
   *
   * @return the any node selector.
   * @throws CSSException If this selector is not supported.
   */
  public SimpleSelector createAnyNodeSelector() throws CSSException {
    throw new CSSException( CSSException.SAC_NOT_SUPPORTED_ERR );
  }

  /**
   * Creates an root node selector.
   *
   * @return the root node selector.
   * @throws CSSException If this selector is not supported.
   */
  public SimpleSelector createRootNodeSelector() throws CSSException {
    throw new CSSException( CSSException.SAC_NOT_SUPPORTED_ERR );
  }

  /**
   * Creates an negative selector.
   *
   * @param selector a selector.
   * @return the negative selector.
   * @throws CSSException If this selector is not supported.
   */
  public NegativeSelector createNegativeSelector( SimpleSelector selector )
    throws CSSException {
    throw new CSSException( CSSException.SAC_NOT_SUPPORTED_ERR );
  }

  /**
   * Creates an element selector.
   *
   * @param namespaceURI the <a href="http://www.w3.org/TR/REC-xml-names/#dt-NSName">namespace URI</a> of the element
   *                     selector.
   * @param tagName      the <a href="http://www.w3.org/TR/REC-xml-names/#NT-LocalPart">local part</a> of the element
   *                     name. <code>NULL</code> if this element selector can match any element.</p>
   * @return the element selector
   * @throws CSSException If this selector is not supported.
   */
  public ElementSelector createElementSelector( String namespaceURI, String localName )
    throws CSSException {
    if ( namespaceURI != null ) {
      throw new CSSException( CSSException.SAC_NOT_SUPPORTED_ERR );
    } else {
      return new ElementSelectorImpl( localName );
    }
  }

  /**
   * Creates a text node selector.
   *
   * @param data the data
   * @return the text node selector
   * @throws CSSException If this selector is not supported.
   */
  public CharacterDataSelector createTextNodeSelector( String data )
    throws CSSException {
    throw new CSSException( CSSException.SAC_NOT_SUPPORTED_ERR );
  }

  /**
   * Creates a cdata section node selector.
   *
   * @param data the data
   * @return the cdata section node selector
   * @throws CSSException If this selector is not supported.
   */
  public CharacterDataSelector createCDataSectionSelector( String data )
    throws CSSException {
    throw new CSSException( CSSException.SAC_NOT_SUPPORTED_ERR );
  }

  /**
   * Creates a processing instruction node selector.
   *
   * @param target the target
   * @param data   the data
   * @return the processing instruction node selector
   * @throws CSSException If this selector is not supported.
   */
  public ProcessingInstructionSelector
  createProcessingInstructionSelector( String target,
                                       String data )
    throws CSSException {
    throw new CSSException( CSSException.SAC_NOT_SUPPORTED_ERR );
  }

  /**
   * Creates a comment node selector.
   *
   * @param data the data
   * @return the comment node selector
   * @throws CSSException If this selector is not supported.
   */
  public CharacterDataSelector createCommentSelector( String data )
    throws CSSException {
    throw new CSSException( CSSException.SAC_NOT_SUPPORTED_ERR );
  }

  /**
   * Creates a pseudo element selector.
   *
   * @param pseudoName the pseudo element name. <code>NULL</code> if this element selector can match any pseudo
   *                   element.</p>
   * @return the element selector
   * @throws CSSException If this selector is not supported.
   */
  public ElementSelector createPseudoElementSelector( String namespaceURI,
                                                      String pseudoName )
    throws CSSException {
    if ( namespaceURI != null ) {
      throw new CSSException( CSSException.SAC_NOT_SUPPORTED_ERR );
    } else {
      return new PseudoElementSelectorImpl( pseudoName );
    }
  }

  /**
   * Creates a descendant selector.
   *
   * @param parent     the parent selector
   * @param descendant the descendant selector
   * @return the combinator selector.
   * @throws CSSException If this selector is not supported.
   */
  public DescendantSelector createDescendantSelector( Selector parent,
                                                      SimpleSelector descendant )
    throws CSSException {
    return new DescendantSelectorImpl( parent, descendant );
  }

  /**
   * Creates a child selector.
   *
   * @param parent the parent selector
   * @param child  the child selector
   * @return the combinator selector.
   * @throws CSSException If this selector is not supported.
   */
  public DescendantSelector createChildSelector( Selector parent,
                                                 SimpleSelector child )
    throws CSSException {
    return new ChildSelectorImpl( parent, child );
  }

  /**
   * Creates a direct adjacent selector.
   *
   * @param child    the child selector
   * @param adjacent the direct adjacent selector
   * @return the combinator selector.
   * @throws CSSException If this selector is not supported.
   */
  public SiblingSelector createDirectAdjacentSelector( short nodeType,
                                                       Selector child,
                                                       SimpleSelector directAdjacent )
    throws CSSException {
    if ( nodeType != 1 ) {
      throw new CSSException( CSSException.SAC_NOT_SUPPORTED_ERR );
    } else {
      return new DirectAdjacentSelectorImpl( child, directAdjacent );
    }
  }

}
