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

package org.pentaho.reporting.designer.core.settings.ui;


/**
 * User: Martin Date: 01.03.2006 Time: 18:14:22
 */
public class ValidationMessage {
  public enum Severity {
    WARN,
    ERROR
  }

  private Severity severity;
  private String message;

  public ValidationMessage( final Severity severity, final String message ) {
    if ( severity == null ) {
      throw new NullPointerException();
    }
    if ( message == null ) {
      throw new NullPointerException();
    }
    this.severity = severity;
    this.message = message;
  }


  public Severity getSeverity() {
    return severity;
  }


  public String getMessage() {
    return message;
  }
}
