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
