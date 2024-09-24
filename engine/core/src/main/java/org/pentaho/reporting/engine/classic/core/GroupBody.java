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

package org.pentaho.reporting.engine.classic.core;

/**
 * This class only has two valid sub-classes and exists purely to limit the applicable composition options in the
 * group-class.
 *
 * @author Thomas Morgner
 */
public abstract class GroupBody extends Section {
  protected GroupBody() {
  }

  public abstract Group getGroup();
}
