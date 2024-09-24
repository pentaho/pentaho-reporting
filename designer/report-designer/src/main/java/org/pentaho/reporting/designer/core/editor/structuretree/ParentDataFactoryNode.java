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
