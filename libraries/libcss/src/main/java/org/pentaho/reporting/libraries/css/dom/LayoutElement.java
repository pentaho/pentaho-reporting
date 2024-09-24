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
