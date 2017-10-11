/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

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
