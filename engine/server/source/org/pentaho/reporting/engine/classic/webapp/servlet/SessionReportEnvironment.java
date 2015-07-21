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
 *  Copyright (c) 2006 - 2013 Pentaho Corporation..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.webapp.servlet;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.DefaultReportEnvironment;
import org.pentaho.reporting.engine.classic.core.ReportEnvironment;

import javax.servlet.http.HttpSession;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class SessionReportEnvironment implements ReportEnvironment {
  public static final String SESSION_PREFIX = "session:";
  private ReportEnvironment parent;
  private HttpSession session;

  public SessionReportEnvironment( final ReportEnvironment parent, HttpSession session ) {
    if ( parent == null ) {
      throw new NullPointerException( "parent" );
    }
    if ( session == null ) {
      throw new NullPointerException( "session" );
    }
    this.session = session;
    this.parent = parent;
  }

  public SessionReportEnvironment( HttpSession session ) {
    this( new DefaultReportEnvironment( ClassicEngineBoot.getInstance().getGlobalConfig() ), session );
  }

  public Object getEnvironmentProperty( final String key ) {
    if ( key.startsWith( SESSION_PREFIX ) ) {
      session.getAttribute( key.substring( SESSION_PREFIX.length() ) );
    }
    return parent.getEnvironmentProperty( key );
  }

  public String getURLEncoding() {
    return parent.getURLEncoding();
  }

  public Locale getLocale() {
    return parent.getLocale();
  }

  public TimeZone getTimeZone() {
    return parent.getTimeZone();
  }

  public SessionReportEnvironment clone() {
    try {
      SessionReportEnvironment re = (SessionReportEnvironment) super.clone();
      re.parent = (ReportEnvironment) parent.clone();
      return re;
    } catch ( CloneNotSupportedException e ) {
      throw new IllegalStateException( e );
    }
  }

  public Map<String, String[]> getUrlExtraParameter() {
    return parent.getUrlExtraParameter();
  }
}
