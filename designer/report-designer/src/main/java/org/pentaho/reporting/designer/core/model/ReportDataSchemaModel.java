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

package org.pentaho.reporting.designer.core.model;

import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeDataSchemaModel;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeDataSchemaModelChangeTracker;

public class ReportDataSchemaModel extends DesignTimeDataSchemaModel {
  public ReportDataSchemaModel( final MasterReport masterReportElement,
                                final AbstractReportDefinition report ) {
    super( masterReportElement, report );
  }

  protected DesignTimeDataSchemaModelChangeTracker createChangeTracker() {
    return new EmptyTracker();
  }

  protected void handleError( final Throwable e ) {
    UncaughtExceptionsModel.getInstance().addException( e );
  }

  private static final class EmptyTracker implements DesignTimeDataSchemaModelChangeTracker {
    public void updateChangeTrackers() {

    }

    public boolean isReportChanged() {
      return false;
    }

    public boolean isReportQueryChanged() {
      return false;
    }
  }

}
