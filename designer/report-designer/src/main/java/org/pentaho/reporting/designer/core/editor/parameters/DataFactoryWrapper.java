/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

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
