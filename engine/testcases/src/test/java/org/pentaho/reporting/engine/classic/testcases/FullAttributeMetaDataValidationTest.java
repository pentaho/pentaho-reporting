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



package org.pentaho.reporting.engine.classic.testcases;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.metadata.AttributeMetaDataValidationTest;

@SuppressWarnings( "HardCodedStringLiteral" )
public class FullAttributeMetaDataValidationTest extends AttributeMetaDataValidationTest {
  private static final Log logger = LogFactory.getLog( FullAttributeMetaDataValidationTest.class );

  public FullAttributeMetaDataValidationTest() {
  }

  @Test
  public void testMetaData() {
    super.testMetaData();
  }
}
