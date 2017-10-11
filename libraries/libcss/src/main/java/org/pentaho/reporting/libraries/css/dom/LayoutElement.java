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

package org.pentaho.reporting.libraries.css.dom;

import java.util.Locale;
import java.util.Map;

/**
 * This is where the computed style goes into. // todo: Produce the computed counterset!
 *
 * @author Thomas Morgner
 */
public interface LayoutElement {
  public LayoutStyle getLayoutStyle();

  public Map getCounters();

  public Map getStrings();

  /**
   * An element can be exactly one pseudo-element type. It is not possible for an element to fullfill two roles, an
   * element is either a 'before' or a 'marker', but can as well be a 'before' of an 'marker' (where the marker element
   * would be the parent).
   *
   * @return
   */
  public String getPseudoElement();

  /**
   * May be null.
   *
   * @return
   */
  public String getNamespace();

  /**
   * May be null.
   *
   * @return
   */
  public String getTagName();

  /**
   * May never be null.
   *
   * @return
   */
  public Object getAttribute( final String namespace, final String name );

  /**
   * Returns the language definition of this layout context. If not set, it defaults to the parent's language. If the
   * root's language is also not defined, then use the system default.
   *
   * @return the defined language, never null.
   */
  public Locale getLanguage();

  public boolean isPseudoElement();

  public LayoutElement getParentLayoutElement();

  public LayoutElement getPreviousLayoutElement();
}
