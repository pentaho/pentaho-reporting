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



package org.pentaho.reporting.engine.classic.extensions.datasources.kettle;

import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;

public class TransformationMetadataTest {

  @Test
  public void testRegisterDatasources() {
    try {
      TransformationDatasourceMetadata.registerDatasources();
    } catch ( ReportDataFactoryException e ) {
    }
  }

}
