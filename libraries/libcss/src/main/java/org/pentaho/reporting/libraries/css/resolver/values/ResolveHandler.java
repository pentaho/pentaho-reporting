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


package org.pentaho.reporting.libraries.css.resolver.values;

import org.pentaho.reporting.libraries.css.dom.DocumentContext;
import org.pentaho.reporting.libraries.css.dom.LayoutElement;
import org.pentaho.reporting.libraries.css.model.StyleKey;

/**
 * Creation-Date: 11.12.2005, 14:43:15
 *
 * @author Thomas Morgner
 */
public interface ResolveHandler {
  /**
   * This indirectly defines the resolve order. The higher the order, the more dependent is the resolver on other
   * resolvers to be complete.
   *
   * @return the array of required style keys.
   */
  public StyleKey[] getRequiredStyles();

  /**
   * Resolves a single property.
   *
   * @param process     the current layout process controlling everyting
   * @param currentNode the current layout element that is processed
   * @param key         the style key that is computed.
   */
  public void resolve( DocumentContext process,
                       LayoutElement currentNode,
                       StyleKey key );
}
