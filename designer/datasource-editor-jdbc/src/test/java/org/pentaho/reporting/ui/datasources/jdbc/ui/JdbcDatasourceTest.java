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

package org.pentaho.reporting.ui.datasources.jdbc.ui;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class JdbcDatasourceTest {

  private JdbcDataSourceDialog.InvokeQueryDesignerAction invokeQueryDesignerAction =
    Mockito.mock( JdbcDataSourceDialog.InvokeQueryDesignerAction.class );

  @Before
  public void setUp() {
    Mockito.when( invokeQueryDesignerAction.performQuerySchema( null ) )
      .thenReturn( JdbcDataSourceDialog.InvokeQueryDesignerAction.DEFAULT_SCHEMA );
  }

  @Test
  public void testPerformQuerySchema() {
    String result = invokeQueryDesignerAction.performQuerySchema( null );
    Assert.assertTrue( result.equals( "PUBLIC" ) );
  }
}
