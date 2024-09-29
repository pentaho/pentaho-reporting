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


package org.pentaho.reporting.engine.classic.wizard.model;

public class DefaultRootBandDefinition extends AbstractElementFormatDefinition implements RootBandDefinition {
  private Boolean repeat;
  private boolean visible;

  public DefaultRootBandDefinition() {
    visible = true;
  }

  public Boolean getRepeat() {
    return repeat;
  }

  public void setRepeat( final Boolean repeat ) {
    this.repeat = repeat;
  }

  public boolean isVisible() {
    return visible;
  }

  public void setVisible( final boolean visible ) {
    this.visible = visible;
  }
}
