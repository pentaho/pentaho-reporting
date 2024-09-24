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

package org.pentaho.reporting.designer.core.editor.parameters;

import org.pentaho.reporting.engine.classic.core.DataFactory;

/**
 * Todo: Document me!
 * <p/>
 * Date: 09.04.2009 Time: 20:11:09
 *
 * @author Thomas Morgner.
 */
public class DataFactoryWrapper {
  private DataFactory originalDataFactory;
  private DataFactory editedDataFactory;

  public DataFactoryWrapper( final DataFactory originalDataFactory ) {
    this.originalDataFactory = originalDataFactory;
    this.editedDataFactory = originalDataFactory;
  }

  public DataFactoryWrapper( final DataFactory originalDataFactory, final DataFactory editedDataFactory ) {
    this.originalDataFactory = originalDataFactory;
    this.editedDataFactory = editedDataFactory;
  }

  public DataFactory getOriginalDataFactory() {
    return originalDataFactory;
  }

  public void setEditedDataFactory( final DataFactory editedDataFactory ) {
    this.editedDataFactory = editedDataFactory;
  }

  public DataFactory getEditedDataFactory() {
    return editedDataFactory;
  }

  public boolean isRemoved() {
    return editedDataFactory == null;
  }
}
