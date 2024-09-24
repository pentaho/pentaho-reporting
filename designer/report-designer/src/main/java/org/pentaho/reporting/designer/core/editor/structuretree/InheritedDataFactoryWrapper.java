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
