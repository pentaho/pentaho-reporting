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

package org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.elements;

import org.pentaho.reporting.engine.classic.core.Element;

import java.io.Serializable;

/**
 * The interface that defines an element factory.
 *
 * @author Thomas Morgner
 */
public interface ElementFactory extends Serializable {
  /**
   * Returns an element for the given type.
   *
   * @param type
   *          the type.
   * @return The element.
   */
  public Element getElementForType( String type );
}
