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

package org.pentaho.reporting.engine.classic.core.layout;

import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.process.layoutrules.InlineSequenceElement;

class TestInlineSequenceElement implements InlineSequenceElement {
  private Classification classification;

  TestInlineSequenceElement( final Classification classification ) {
    this.classification = classification;
  }

  public long getMinimumWidth( final RenderNode node ) {
    return 10;
  }

  public long getMaximumWidth( final RenderNode node ) {
    return 10;
  }

  public boolean isPreserveWhitespace( final RenderNode node ) {
    return false;
  }

  public int getClassification() {
    return classification.ordinal();
  }

  public Classification getType() {
    return classification;
  }
}
