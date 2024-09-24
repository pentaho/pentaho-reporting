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

package org.pentaho.reporting.libraries.css.resolver;

import org.pentaho.reporting.libraries.css.PageAreaType;
import org.pentaho.reporting.libraries.css.PseudoPage;
import org.pentaho.reporting.libraries.css.dom.DocumentContext;
import org.pentaho.reporting.libraries.css.dom.LayoutElement;
import org.pentaho.reporting.libraries.css.dom.LayoutStyle;
import org.pentaho.reporting.libraries.css.values.CSSValue;


/**
 * Creation-Date: 05.12.2005, 18:03:52
 *
 * @author Thomas Morgner
 */
public interface StyleResolver {
  public StyleResolver deriveInstance();

  /**
   * Resolves the style. This is guaranteed to be called in the order of the document elements traversing the document
   * tree using the 'deepest-node-first' strategy.
   *
   * @param element
   */
  public void resolveStyle( LayoutElement element );

  /**
   * Performs tests, whether there is a pseudo-element definition for the given element. The element itself can be a
   * pseudo-element as well.
   *
   * @param element
   * @param pseudo
   * @return
   */
  public boolean isPseudoElementStyleResolvable( LayoutElement element,
                                                 String pseudo );

  public void initialize( DocumentContext documentContext );

  public LayoutStyle resolvePageStyle
    ( CSSValue pageName, PseudoPage[] pseudoPages, PageAreaType pageArea );

  /**
   * Returns the style for a generic element for which none of the defined selectors (except the global one) match.
   *
   * @return the initial style.
   */
  public LayoutStyle getInitialStyle();

}
