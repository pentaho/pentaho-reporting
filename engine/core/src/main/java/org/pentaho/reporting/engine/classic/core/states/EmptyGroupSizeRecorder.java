/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
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
