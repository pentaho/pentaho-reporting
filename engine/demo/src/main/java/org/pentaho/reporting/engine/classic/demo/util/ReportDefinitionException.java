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
* Copyright (c) 2006 - 2013 Pentaho Corporation and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.demo.util;

/**
 * An exception that is thrown, if a report could not be defined. This encapsulates parse errors as well as runtime
 * exceptions caused by invalid setup code.
 *
 * @author: Thomas Morgner
 */
public class ReportDefinitionException extends Exception
{
  public ReportDefinitionException()
  {
  }

  public ReportDefinitionException(final String message, final Exception ex)
  {
    super(message, ex);
  }

  public ReportDefinitionException(final String message)
  {
    super(message);
  }
}
