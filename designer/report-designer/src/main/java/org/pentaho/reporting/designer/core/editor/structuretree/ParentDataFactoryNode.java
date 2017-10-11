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

package org.pentaho.reporting.designer.core.editor.structuretree;

import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.SubReport;

/**
 * Todo: Document me!
 * <p/>
 * Date: 10.06.2009 Time: 15:01:49
 *
 * @author Thomas Morgner.
 */
public class ParentDataFactoryNode {
  private AbstractReportDefinition parentReport;

  public ParentDataFactoryNode( final AbstractReportDefinition parentReport ) {
    this.parentReport = parentReport;
  }

  public CompoundDataFactory getDataFactory() {
    return (CompoundDataFactory) parentReport.getDataFactory();
  }

  public boolean isSubReport() {
    return parentReport instanceof SubReport;
  }

  public Object getParentNode() {
    if ( isSubReport() == false ) {
      return null;
    }
    final Section parentSection = parentReport.getParentSection();
    if ( parentSection == null ) {
      return null;
    }
    final AbstractReportDefinition reportDefinition =
      (AbstractReportDefinition) parentSection.getReportDefinition();
    return new ParentDataFactoryNode( reportDefinition );
  }
}
