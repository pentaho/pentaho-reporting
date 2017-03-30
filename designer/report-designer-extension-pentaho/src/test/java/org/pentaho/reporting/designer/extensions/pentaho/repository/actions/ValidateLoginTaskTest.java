/*!
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
* Foundation.
*
* You should have received a copy of the GNU Lesser General Public License along with this
* program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
* or from the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU Lesser General Public License for more details.
*
* Copyright (c) 2017 Pentaho Corporation..  All rights reserved.
*/

package org.pentaho.reporting.designer.extensions.pentaho.repository.actions;

import org.apache.commons.vfs2.FileSystemException;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.assertEquals;


public class ValidateLoginTaskTest {

  @Test
  public void testDefaultConfiguartion() throws FileSystemException {
    final ValidateLoginTask validateLoginTask = Mockito.spy( new ValidateLoginTask( Mockito.mock( LoginTask.class ) ) );
    Mockito.when( validateLoginTask.validateLoginData() ).then( new Answer<Object>() {
      @Override public Object answer( InvocationOnMock invocation ) throws Throwable {
        throw new IllegalStateException();
      }
    } );

    Mockito.when( validateLoginTask.validateLoginDataFast() ).then( new Answer<Object>() {
      @Override public Object answer( InvocationOnMock invocation ) throws Throwable {
        return true;
      }
    } );

    Mockito.when( validateLoginTask.useOldVfsValidation() ).thenReturn( false );

    validateLoginTask.run();
    assertEquals( true, validateLoginTask.isLoginComplete() );
  }

  @Test
  public void testOldConfiguartion() throws FileSystemException {
    final ValidateLoginTask validateLoginTask = Mockito.spy( new ValidateLoginTask( Mockito.mock( LoginTask.class ) ) );
    Mockito.when( validateLoginTask.validateLoginData() ).then( new Answer<Object>() {
      @Override public Object answer( InvocationOnMock invocation ) throws Throwable {
        return true;
      }
    } );

    Mockito.when( validateLoginTask.validateLoginDataFast() ).then( new Answer<Object>() {
      @Override public Object answer( InvocationOnMock invocation ) throws Throwable {
        throw new IllegalStateException();
      }
    } );

    Mockito.when( validateLoginTask.useOldVfsValidation() ).thenReturn( true );

    validateLoginTask.run();
    assertEquals( true, validateLoginTask.isLoginComplete() );
  }

  @Test
  public void testEmpyData() {
    final LoginTask loginTask = Mockito.mock( LoginTask.class );
    Mockito.when( loginTask.getLoginData() ).thenReturn( null );
    assertEquals( true, new ValidateLoginTask( loginTask ).validateLoginDataFast() );
  }

}
