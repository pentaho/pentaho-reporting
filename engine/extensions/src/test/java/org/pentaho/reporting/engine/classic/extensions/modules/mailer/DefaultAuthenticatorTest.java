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
 * Copyright (c) 2005-2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.extensions.modules.mailer;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.Properties;

import javax.mail.PasswordAuthentication;

import org.junit.Test;

public class DefaultAuthenticatorTest {

  @Test
  public void testGetPasswordAuthentication() {
    Properties props = mock( Properties.class );
    doReturn( "default_test_user_name" ).when( props ).getProperty( "mail.user" );
    doReturn( "default_test_user_password" ).when( props ).getProperty( "mail.password" );
    doReturn( "test_user_name" ).when( props ).getProperty( "mail.null.user", "default_test_user_name" );
    doReturn( "test_user_password" ).when( props ).getProperty( "mail.null.password", "default_test_user_password" );

    DefaultAuthenticator auth = new DefaultAuthenticator( props );
    PasswordAuthentication credential = auth.getPasswordAuthentication();

    assertThat( credential, is( notNullValue() ) );
    assertThat( credential.getUserName(), is( equalTo( "test_user_name" ) ) );
    assertThat( credential.getPassword(), is( equalTo( "test_user_password" ) ) );
  }
}
