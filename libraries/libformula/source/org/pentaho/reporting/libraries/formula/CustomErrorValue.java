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
 * Copyright (c) 2005-2011 Pentaho Corporation.  All rights reserved.
 */

package org.pentaho.reporting.libraries.formula;

import java.util.Locale;

public class CustomErrorValue implements ErrorValue
{
  private String errorMessage;

  public CustomErrorValue(final String errorMessage)
  {
    this.errorMessage = errorMessage;
  }

  public String getNamespace()
  {
    return "http://jfreereport.sourceforge.net/libformula/usererror";
  }

  public int getErrorCode()
  {
    return -1;
  }

  public String getErrorMessage(final Locale locale)
  {
    return errorMessage;
  }
}
