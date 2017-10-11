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

package org.pentaho.reporting.designer.core.editor.structuretree;

import org.pentaho.reporting.engine.classic.core.DataFactory;

/**
 * A wrapper to prevent the data-factory from being edited. Inherited datafactories must be edited on the master-report
 * (or the parent report) or they will wreck havoc among the innocent bystanders.
 *
 * @author Thomas Morgner.
 */
public class InheritedDataFactoryWrapper {
  private DataFactory dataFactory;

  public InheritedDataFactoryWrapper( final DataFactory dataFactory ) {
    this.dataFactory = dataFactory;
  }

  public DataFactory getDataFactory() {
    return dataFactory;
  }
}
