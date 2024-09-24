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

import org.pentaho.reporting.libraries.css.parser.CSSParserContext;
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

import java.io.Serializable;

/**
 * Creation-Date: 30.11.2005, 15:38:21
 *
 * @author Thomas Morgner
 */
public class CSSSelectorFactory implements SelectorFactory, Serializable {
  public CSSSelectorFactory() {
  }

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
    return new CSSConditionalSelector( selector, condition );
  }

  /**
   * Creates an any node selector.
   *
   * @return the any node selector.
   * @throws CSSException If this selector is not supported.
   */
  public SimpleSelector createAnyNodeSelector() throws CSSException {
    return new CSSAnyNodeSelector();
  }

  /**
   * Creates an root node selector.
   *
   * @return the root node selector.
   * @throws CSSException If this selector is not supported.
   */
  public SimpleSelector createRootNodeSelector() throws CSSException {
    // this one might come in handy from time to time.
    return new CSSRootNodeSelector();
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
    return new CSSNegativeSelector( selector );
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
  public ElementSelector createElementSelector( String namespaceURI,
                                                String tagName )
    throws CSSException {
    if ( namespaceURI == null ) {
      namespaceURI = CSSParserContext.getContext().getDefaultNamespace();
    }

    return new CSSElementSelector
      ( Selector.SAC_ELEMENT_NODE_SELECTOR, namespaceURI, tagName );
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
    // I assume that this is the same thing as the CDATA selector.
    // we do not use DOM anyway. so we either have text, or have no text.
    return new CSSCharacterDataSelector( data );
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
    return new CSSCharacterDataSelector( data );
  }

  /**
   * Creates a processing instruction node selector.
   *
   * @param target the target
   * @param data   the data
   * @return the processing instruction node selector
   * @throws CSSException If this selector is not supported.
   */
  public ProcessingInstructionSelector createProcessingInstructionSelector(
    String target,
    String data ) throws CSSException {
    throw new CSSException
      ( "LibLayout does not support ProcessingInstructions." );
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
    throw new CSSException
      ( "LibLayout does not support CommenSelectors." );
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
    if ( namespaceURI == null ) {
      namespaceURI = CSSParserContext.getContext().getDefaultNamespace();
    }
    return new CSSElementSelector
      ( Selector.SAC_PSEUDO_ELEMENT_SELECTOR, namespaceURI, pseudoName );
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
    return new CSSDescendantSelector( descendant, parent, false );
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
    return new CSSDescendantSelector( child, parent, true );
  }

  /**
   * Creates a sibling selector.
   *
   * @param nodeType the type of nodes in the siblings list.
   * @param child    the child selector
   * @param adjacent the direct adjacent selector
   * @return the sibling selector with nodeType equals to org.w3c.dom.Node.ELEMENT_NODE
   * @throws CSSException If this selector is not supported.
   */
  public SiblingSelector createDirectAdjacentSelector( final short nodeType,
                                                       final Selector child,
                                                       final SimpleSelector directAdjacent )
    throws CSSException {
    return new CSSSilblingSelector( nodeType, child, directAdjacent );
  }
}
