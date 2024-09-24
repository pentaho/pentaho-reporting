/*
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
* Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

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
