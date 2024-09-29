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


package org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.model;

public class Guideline {
  private boolean active;
  private double position;

  public Guideline() {
  }

  public boolean isActive() {
    return active;
  }

  public void setActive( final boolean active ) {
    this.active = active;
  }

  public double getPosition() {
    return position;
  }

  public void setPosition( final double position ) {
    this.position = position;
  }

  public String externalize() {
    return "(" + active + ',' + position + ')';
  }

  public String toString() {
    return "org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.model.Guideline{" +
      "active=" + active +
      ", position=" + position +
      '}';
  }
}
