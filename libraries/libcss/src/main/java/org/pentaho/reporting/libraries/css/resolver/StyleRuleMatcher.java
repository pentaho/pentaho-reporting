/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.libraries.css.resolver;

import org.pentaho.reporting.libraries.css.PseudoPage;
import org.pentaho.reporting.libraries.css.dom.DocumentContext;
import org.pentaho.reporting.libraries.css.dom.LayoutElement;
import org.pentaho.reporting.libraries.css.model.CSSPageRule;
import org.pentaho.reporting.libraries.css.model.CSSStyleRule;
import org.pentaho.reporting.libraries.css.values.CSSValue;

/**
 * A (possibly statefull) style matcher. This class is responsible for checking which style rule applies to the given
 * document.
 * <p/>
 * It is guaranteed, that the matcher receives the elements in the order in which they appear in the document.
 * <p/>
 * Although the style rule matcher does not receive explicit element-opened and element-closed events, these events can
 * be derived from the layout element and its relation to the parent (and possibly previously received element and its
 * parent).
 *
 * @author Thomas Morgner
 */
public interface StyleRuleMatcher {
  public void initialize( final DocumentContext layoutProcess );

  /**
   * Creates an independent copy of this style rule matcher.
   *
   * @return
   */
  public StyleRuleMatcher deriveInstance();

  public CSSStyleRule[] getMatchingRules( LayoutElement element );

  public boolean isMatchingPseudoElement( LayoutElement element, String pseudo );

  public CSSPageRule[] getPageRule( CSSValue pageName, PseudoPage[] pseudoPages );
}
