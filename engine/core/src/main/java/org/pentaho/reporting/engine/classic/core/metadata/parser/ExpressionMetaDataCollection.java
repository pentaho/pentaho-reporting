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

import org.pentaho.reporting.engine.classic.core.metadata.ExpressionMetaData;

import java.io.Serializable;

public class ExpressionMetaDataCollection implements Serializable {
  private ExpressionMetaData[] expressionMetaData;

  public ExpressionMetaDataCollection( final ExpressionMetaData[] expressionMetaData ) {
    if ( expressionMetaData == null ) {
      throw new NullPointerException();
    }

    this.expressionMetaData = expressionMetaData.clone();
  }

  public ExpressionMetaData[] getExpressionMetaData() {
    return expressionMetaData.clone();
  }
}
