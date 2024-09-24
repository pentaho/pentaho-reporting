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

package org.pentaho.reporting.engine.classic.core.modules.output.table.base.layout.model;

import java.util.ArrayList;

/**
 * This class encapsulates a whole test run, which is an alternating sequence of sources and result validators.
 *
 * @author Thomas Morgner
 */
public class ValidationSequence {
  private ArrayList list;
  private int pageWidth;
  private boolean strict;

  public ValidationSequence() {
    list = new ArrayList();
  }

  public void addSourceChunk( final SourceChunk sourceChunk ) {
    list.add( sourceChunk );
  }

  public void addResultTable( final ResultTable resultTable ) {
    list.add( resultTable );
  }

  public int getPageWidth() {
    return pageWidth;
  }

  public void setPageWidth( final int pageWidth ) {
    this.pageWidth = pageWidth;
  }

  public ArrayList getContents() {
    return list;
  }

  public boolean isStrict() {
    return strict;
  }

  public void setStrict( final boolean strict ) {
    this.strict = strict;
  }
}
