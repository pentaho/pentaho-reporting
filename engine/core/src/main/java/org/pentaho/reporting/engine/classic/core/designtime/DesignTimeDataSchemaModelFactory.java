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


package org.pentaho.reporting.engine.classic.core.designtime;

import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.wizard.ContextAwareDataSchemaModel;
import org.pentaho.reporting.engine.classic.core.wizard.ContextAwareDataSchemaModelFactory;

public class DesignTimeDataSchemaModelFactory implements ContextAwareDataSchemaModelFactory {
  public DesignTimeDataSchemaModelFactory() {
  }

  private static MasterReport findMasterReport( final AbstractReportDefinition def ) {
    AbstractReportDefinition loopDef = def;
    while ( loopDef instanceof MasterReport == false ) {
      final Section parentSection = def.getParentSection();
      if ( parentSection == null ) {
        break;
      }
      loopDef = (AbstractReportDefinition) parentSection.getReportDefinition();
    }

    if ( loopDef instanceof MasterReport ) {
      return (MasterReport) def;
    }
    return new MasterReport();
  }

  public ContextAwareDataSchemaModel create( final AbstractReportDefinition report ) {
    return new DesignTimeDataSchemaModel( findMasterReport( report ), report );
  }
}
