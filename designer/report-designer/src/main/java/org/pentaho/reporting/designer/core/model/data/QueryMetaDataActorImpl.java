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

package org.pentaho.reporting.designer.core.model.data;


import org.apache.pekko.dispatch.Futures;
import org.pentaho.reporting.designer.core.model.ReportDataSchemaModel;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.wizard.ContextAwareDataSchemaModel;
import scala.concurrent.Future;

public class QueryMetaDataActorImpl implements QueryMetaDataActor {
  public Future<ContextAwareDataSchemaModel> retrieve( final MasterReport master,
                                                       final AbstractReportDefinition report ) {
    ContextAwareDataSchemaModel model = new ReportDataSchemaModel( master, report );
    // trigger the actual query while still being on the actor thread.
    model.getDataSchema();
    return Futures.successful( model );
  }
}
