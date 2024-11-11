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


package org.pentaho.reporting.engine.classic.core.metadata.parser;

import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;

import java.io.Serializable;

/**
 * This class represents a parse-result.
 *
 * @author Thomas Morgner
 */
public class ElementTypeCollection implements Serializable {
  private ElementMetaData[] elementTypes;

  public ElementTypeCollection( final ElementMetaData[] elementTypes ) {
    if ( elementTypes == null ) {
      throw new NullPointerException();
    }
    this.elementTypes = elementTypes.clone();
  }

  public ElementMetaData[] getElementTypes() {
    return elementTypes.clone();
  }
}
