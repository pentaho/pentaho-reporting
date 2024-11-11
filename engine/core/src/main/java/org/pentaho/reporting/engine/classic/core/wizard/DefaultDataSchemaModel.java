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


package org.pentaho.reporting.engine.classic.core.wizard;

import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeDataSchemaModel;
import org.pentaho.reporting.libraries.base.util.LinkedMap;

/**
 * A design-time helper component.
 *
 * @author Thomas Morgner
 * @deprecated Use the DesignTimeDataSchemaModel instead.
 */
public class DefaultDataSchemaModel extends DesignTimeDataSchemaModel {
  public DefaultDataSchemaModel( final AbstractReportDefinition report ) {
    super( report );
  }

  public DefaultDataSchemaModel( final MasterReport masterReportElement, final AbstractReportDefinition report ) {
    super( masterReportElement, report );
  }

  public static LinkedMap computeParameterValueSet( final MasterReport report ) {
    return DesignTimeDataSchemaModel.computeParameterValueSet( report );
  }

}
