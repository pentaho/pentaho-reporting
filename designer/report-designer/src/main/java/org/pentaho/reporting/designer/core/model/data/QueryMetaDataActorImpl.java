/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

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
