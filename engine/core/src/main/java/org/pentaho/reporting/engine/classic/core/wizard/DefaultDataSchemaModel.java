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
