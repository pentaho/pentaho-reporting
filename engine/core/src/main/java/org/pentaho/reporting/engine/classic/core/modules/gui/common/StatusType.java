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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.gui.common;

/**
 * Creation-Date: 15.05.2007, 16:47:58
 *
 * @author Thomas Morgner
 */
public class StatusType {
  public static final StatusType ERROR = new StatusType( "ERROR" ); //$NON-NLS-1$
  public static final StatusType WARNING = new StatusType( "WARNING" ); //$NON-NLS-1$
  public static final StatusType INFORMATION = new StatusType( "INFORMATION" ); //$NON-NLS-1$
  public static final StatusType NONE = new StatusType( "NONE" ); //$NON-NLS-1$

  private final String myName; // for debug only

  private StatusType( final String name ) {
    myName = name;
  }

  public String toString() {
    return myName;
  }
}
