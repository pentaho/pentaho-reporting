/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.jfreereport.legacy;

import org.pentaho.reporting.engine.classic.core.metadata.ExpressionRegistry;
import org.pentaho.reporting.libraries.base.boot.AbstractModule;
import org.pentaho.reporting.libraries.base.boot.ModuleInitializeException;
import org.pentaho.reporting.libraries.base.boot.SubSystem;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import java.net.URL;

public class LegacyFunctionsModule extends AbstractModule {
  public LegacyFunctionsModule() throws ModuleInitializeException {
    loadModuleInfo();
  }

  public void initialize( final SubSystem subSystem ) throws ModuleInitializeException {
    final URL expressionMetaSource = ObjectUtilities.getResource
      ( "org/pentaho/jfreereport/legacy/meta-expressions.xml", LegacyFunctionsModule.class );
    if ( expressionMetaSource == null ) {
      throw new ModuleInitializeException( "Error: Could not find the expression meta-data description file" );
    }
    try {
      ExpressionRegistry.getInstance().registerFromXml( expressionMetaSource );
    } catch ( Exception e ) {
      throw new ModuleInitializeException( "Error: Could not parse the element meta-data description file", e );
    }

  }
}
