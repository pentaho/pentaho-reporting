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


package org.pentaho.reporting.engine.classic.core.layout.process;

import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;

/**
 * Marks the Page-Header as commited and as seen. Page-headers are generated on the fly and are not part of the common
 * report-processing system. Therefore they must be printed as soon as there is some commitable content.
 *
 * @author Thomas Morgner
 */
public final class ApplyAutoCommitPageHeaderStep extends IterateSimpleStructureProcessStep {
  private ApplyAutoCommitStep autoCommitStep;
  private boolean hasCommitableContent;

  public ApplyAutoCommitPageHeaderStep() {
    autoCommitStep = new ApplyAutoCommitStep();
  }

  public boolean compute( final LogicalPageBox pageBox ) {
    hasCommitableContent = false;
    processBoxChilds( pageBox );
    if ( hasCommitableContent ) {
      autoCommitStep.compute( pageBox.getWatermarkArea() );
      autoCommitStep.compute( pageBox.getHeaderArea() );
      return true;
    } else {
      return false;
    }
  }

  public void commitAll( final LogicalPageBox pageBox ) {
    processBoxChilds( pageBox );
    autoCommitStep.compute( pageBox.getWatermarkArea() );
    autoCommitStep.compute( pageBox.getHeaderArea() );
    autoCommitStep.compute( pageBox.getFooterArea() );
    autoCommitStep.compute( pageBox.getRepeatFooterArea() );
  }

  protected void processParagraphChilds( final ParagraphRenderBox box ) {
    processBoxChilds( box );
  }

  protected boolean startBox( final RenderBox box ) {
    if ( hasCommitableContent == true || box.isCommited() ) {
      hasCommitableContent = true;
      return false;
    }

    return true;
  }

}
