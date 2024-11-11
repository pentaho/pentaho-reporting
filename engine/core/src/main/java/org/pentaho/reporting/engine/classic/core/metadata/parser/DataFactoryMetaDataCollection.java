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

import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryMetaData;

import java.io.Serializable;

/**
 * This class represents a parse-result.
 *
 * @author Thomas Morgner
 */
public class DataFactoryMetaDataCollection implements Serializable {
  private DataFactoryMetaData[] elementTypes;

  public DataFactoryMetaDataCollection( final DataFactoryMetaData[] elementTypes ) {
    if ( elementTypes == null ) {
      throw new NullPointerException();
    }
    this.elementTypes = elementTypes.clone();
  }

  public DataFactoryMetaData[] getFactoryMetaData() {
    return elementTypes.clone();
  }
}
