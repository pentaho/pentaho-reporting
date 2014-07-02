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
 *  Copyright (c) 2006 - 2009 Pentaho Corporation..  All rights reserved.
 */

package org.pentaho.reporting.designer.core.model.data;

import akka.dispatch.OnFailure;
import akka.dispatch.OnSuccess;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;
import org.pentaho.reporting.engine.classic.core.wizard.ContextAwareDataSchemaModel;
import org.pentaho.reporting.libraries.base.util.ArgumentNullException;
import scala.PartialFunction;
import scala.concurrent.Future;
import scala.runtime.BoxedUnit;

public class AsynchronousDataSchemaManager implements DataSchemaManager
{
  private final MasterReport masterReport;
  private final AbstractReportDefinition report;
  private final QueryMetaDataActor actor;

  public AsynchronousDataSchemaManager(final MasterReport masterReport,
                                       final AbstractReportDefinition report)
  {
    ArgumentNullException.validate("masterReport", masterReport);
    ArgumentNullException.validate("report", report);

    this.actor = ActorSystemHost.INSTANCE.createActor(QueryMetaDataActor.class, QueryMetaDataActorImpl.class);
    this.masterReport = masterReport;
    this.report = report;
  }

  public ContextAwareDataSchemaModel getModel()
  {
    Future<ContextAwareDataSchemaModel> retrieve = this.actor.retrieve(null, null, null);
    // IntelliJ does not know how to handle this construct, thinks it is not valid.
    retrieve.onSuccess(new SuccessHandler(), ActorSystemHost.INSTANCE.getSystem().dispatcher());
    retrieve.onFailure(new FailureHandler(), ActorSystemHost.INSTANCE.getSystem().dispatcher());
    return new TemporaryDataSchemaModel(masterReport, report);
  }

  public void close()
  {

  }

  public void nodeChanged(final ReportModelEvent event)
  {

  }

  private static class SuccessHandler extends OnSuccess<ContextAwareDataSchemaModel>
      implements PartialFunction<ContextAwareDataSchemaModel, BoxedUnit>
  {
    public void onSuccess(final ContextAwareDataSchemaModel result) throws Throwable
    {

    }
  }

  private static class FailureHandler extends OnFailure
      implements PartialFunction<Throwable, BoxedUnit>
  {
    public void onFailure(final Throwable failure) throws Throwable
    {

    }
  }
}
