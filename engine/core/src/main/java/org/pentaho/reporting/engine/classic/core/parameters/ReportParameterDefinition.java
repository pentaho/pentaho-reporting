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

package org.pentaho.reporting.engine.classic.core.parameters;

import java.io.Serializable;

/**
 * The ReportParameterDefinition provides user-supplied meta-data about the contents of the report-properties. It
 * contains all information to automatically generated a parameter-page for these parameters and contains a reference to
 * code that can validate the parameters.
 * <p/>
 * The reporting engine itself will never generate parameter-pages, but the engine can validate parameters to prevent
 * incomplete or ill-defined reports.
 *
 * @author Thomas Morgner
 */
public interface ReportParameterDefinition extends Serializable, Cloneable {
  public Object clone();

  public int getParameterCount();

  public ParameterDefinitionEntry[] getParameterDefinitions();

  public ParameterDefinitionEntry getParameterDefinition( int parameter );

  public ReportParameterValidator getValidator();
}
