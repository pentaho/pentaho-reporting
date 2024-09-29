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


package org.pentaho.reporting.engine.classic.core.bugs;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.ResultSetTableModelFactory;
import org.pentaho.reporting.libraries.xmlns.common.AttributeMap;

public class Backlog6426Test {
  @Before
  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  @Test
  public void testErrorHandling() throws Exception {
    ResultSetMetaData mock = mock( ResultSetMetaData.class );
    doReturn( String.class.getName() ).when( mock ).getColumnClassName( 0 );
    doThrow( new SQLException() ).when( mock ).getSchemaName( 0 );
    doThrow( new SQLException() ).when( mock ).getCatalogName( 0 );
    doThrow( new SQLException() ).when( mock ).getTableName( 0 );
    doThrow( new SQLException() ).when( mock ).getColumnLabel( 0 );
    doThrow( new SQLException() ).when( mock ).getColumnDisplaySize( 0 );
    doThrow( new SQLException() ).when( mock ).getPrecision( 0 );
    doThrow( new SQLException() ).when( mock ).getScale( 0 );
    doThrow( new SQLException() ).when( mock ).isCurrency( 0 );
    doThrow( new SQLException() ).when( mock ).isSigned( 0 );
    AttributeMap<Object> name = ResultSetTableModelFactory.collectData( mock, 0, "name" );
    // we are more interested in ensuring that this method does not throw exceptions than the contents ..
    Assert.assertNotNull( name );
  }
}
