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
 * Copyright 2005 - 2008 Pentaho Corporation.  All rights reserved.
 *
 * @created Jul 07, 2008 
 * @author rmansoor
 */
package org.pentaho.reporting.engine.classic.core.modules.misc.connections;

public class DuplicateDatasourceException extends DatasourceMgmtServiceException
{
  public DuplicateDatasourceException()
  {
  }

  public DuplicateDatasourceException(String msg)
  {
    super(msg);
  }

  /**
   * @param message
   * @param reas
   */
  public DuplicateDatasourceException(String message, Throwable reas)
  {
    super(message, reas);
  }

  /**
   * @param reas
   */
  public DuplicateDatasourceException(Throwable reas)
  {
    super(reas);
  }
}
