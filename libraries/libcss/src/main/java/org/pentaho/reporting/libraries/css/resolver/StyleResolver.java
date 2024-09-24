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
