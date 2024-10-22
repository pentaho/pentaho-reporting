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


package org.pentaho.reporting.engine.classic.extensions.modules.mailer;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.Properties;

import jakarta.mail.PasswordAuthentication;

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
