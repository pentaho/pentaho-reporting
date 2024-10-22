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

import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import java.util.Properties;

public class DefaultAuthenticator extends Authenticator {
  private Properties properties;

  public DefaultAuthenticator( final Properties properties ) {
    this.properties = properties;
  }

  protected PasswordAuthentication getPasswordAuthentication() {
    final String protocol = getRequestingProtocol();

    final String userName =
        properties.getProperty( "mail." + protocol + ".user", properties.getProperty( "mail.user" ) );
    final String password =
        properties.getProperty( "mail." + protocol + ".password", properties.getProperty( "mail.password" ) );
    return new PasswordAuthentication( userName, password );
  }
}
