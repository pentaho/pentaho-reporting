/*!
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
* Foundation.
*
* You should have received a copy of the GNU Lesser General Public License along with this
* program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
* or from the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU Lesser General Public License for more details.
*
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

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
