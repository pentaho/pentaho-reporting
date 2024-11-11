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


package org.pentaho.reporting.engine.classic.core.backlog6746;

import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaDataParser;
import org.pentaho.reporting.libraries.base.boot.AbstractModule;
import org.pentaho.reporting.libraries.base.boot.ModuleInitializeException;
import org.pentaho.reporting.libraries.base.boot.SubSystem;

/**
 * This is taken care of for you in LegacyChartModule. But you will have to update the various
 * chart-expression implementations to list the new property.
 */
public class Backlog6746Module extends AbstractModule {
  public Backlog6746Module() throws ModuleInitializeException {
    loadModuleInfo();
  }


  @Override
  public void initialize( SubSystem subSystem ) throws ModuleInitializeException {
    ElementMetaDataParser.initializeOptionalExpressionsMetaData
        ( "org/pentaho/reporting/engine/classic/core/backlog6746/expressions.xml" );

  }
}
