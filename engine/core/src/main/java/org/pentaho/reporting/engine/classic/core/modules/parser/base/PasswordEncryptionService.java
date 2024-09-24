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
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.parser.base;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.content.ContentRootElementHandler;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.xmlns.parser.RootXmlReadHandler;

import java.util.HashMap;

public class PasswordEncryptionService {
  private static final Log logger = LogFactory.getLog( PasswordEncryptionService.class );
  private static PasswordEncryptionService instance;
  private PasswordEncryptionServiceProvider provider;
  private HashMap<String, PasswordEncryptionServiceProvider> services;

  private PasswordEncryptionService() {
    services = new HashMap<String, PasswordEncryptionServiceProvider>();
    provider = ClassicEngineBoot.getInstance().getObjectFactory().get( PasswordEncryptionServiceProvider.class );
    registerService( provider );
    logger.debug( "Selected " + provider.getClass() + " as default provider." );
  }

  public static synchronized PasswordEncryptionService getInstance() {
    if ( instance == null ) {
      instance = new PasswordEncryptionService();
    }
    return instance;
  }

  public void registerService( final PasswordEncryptionServiceProvider provider ) {
    if ( provider == null ) {
      throw new NullPointerException();
    }
    services.put( provider.getPrefix(), provider );
  }

  public PasswordEncryptionServiceProvider getProvider() {
    return provider;
  }

  public String encrypt( final String rawPassword ) {
    if ( rawPassword == null ) {
      return null;
    }
    return provider.getPrefix() + ":" + provider.encrypt( rawPassword );
  }

  public String decrypt( final RootXmlReadHandler root, final String encryptedPassword ) {
    if ( StringUtils.isEmpty( encryptedPassword ) ) {
      // empty string vs. null may have significance.
      return encryptedPassword;
    }

    final Object helperObject = root.getHelperObject( ContentRootElementHandler.PRPT_SPEC_VERSION );
    final boolean legacyFix;
    if ( helperObject instanceof Integer ) {
      final Integer version = (Integer) helperObject;
      if ( version == -1 ) {
        logger.warn( "Decrypting password skipped, as we are dealing with an older version. " );
        return encryptedPassword;
      }

      legacyFix = ( version.intValue() < ClassicEngineBoot.computeVersionId( 5, 0, 0 ) );
    } else {
      legacyFix = false;
    }

    final int separatorPos = encryptedPassword.indexOf( ':' );
    if ( separatorPos == -1 ) {
      // assume legacy mode
      logger.warn( "Decrypting password skipped, as the password-text has no service indicator. " );
      return encryptedPassword;
    }

    final String serviceName = encryptedPassword.substring( 0, separatorPos );
    final String payload = encryptedPassword.substring( separatorPos + 1 );
    final PasswordEncryptionServiceProvider provider = services.get( serviceName );

    if ( legacyFix && ObscurificatePasswordEncryptionServiceProvider.SERVICE_TAG.equals( serviceName ) ) {
      return new Obscurificate48PasswordEncryptionServiceProvider().decrypt( payload );
    }
    if ( provider != null ) {
      return provider.decrypt( payload );
    }
    logger.debug( "Decrypting password skipped, as the service indicator is not recognized. " );
    return encryptedPassword;
  }
}
