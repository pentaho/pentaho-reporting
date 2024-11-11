/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.core.states;

public final class EmptyGroupSizeRecorder implements GroupSizeRecorder {
  public EmptyGroupSizeRecorder() {
  }

  public void advanceItems() {

  }

  public void enterGroup() {

  }

  public void enterItems() {

  }

  public void leaveItems() {

  }

  public void leaveGroup() {

  }

  public void reset() {

  }

  public Object clone() {
    return this;
  }

  public Integer getPredictedStateCount() {
    return null;
  }
}
