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
