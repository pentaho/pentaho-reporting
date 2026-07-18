/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
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
