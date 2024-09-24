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

package org.pentaho.reporting.libraries.resourceloader.modules.factory.svg;

import org.apache.batik.bridge.ScriptSecurity;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.util.ParsedURL;

/**
 * Creation-Date: 11.12.2007, 15:01:44
 *
 * @author Thomas Morgner
 */
public class HeadlessSVGUserAgent extends UserAgentAdapter {

  public HeadlessSVGUserAgent() {
  }

  /**
   * Should we prevent users from running scripts? If yes, then add it here.
   *
   * @param string
   * @param parsedURL
   * @param parsedURL1
   * @return
   */
  public ScriptSecurity getScriptSecurity( final String string, final ParsedURL parsedURL,
                                           final ParsedURL parsedURL1 ) {
    return super.getScriptSecurity( string, parsedURL, parsedURL1 );
  }
}
