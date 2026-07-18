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



package org.pentaho.reporting.engine.classic.core.layout;

import org.pentaho.reporting.engine.classic.core.layout.build.LayoutBuilderStrategy;
import org.pentaho.reporting.engine.classic.core.layout.build.LayoutModelBuilder;
import org.pentaho.reporting.engine.classic.core.layout.build.RenderNodeFactory;

public interface RenderComponentFactory {
  public LayoutModelBuilder createLayoutModelBuilder( String name );

  public RenderNodeFactory createRenderNodeFactory();

  public LayoutBuilderStrategy createLayoutBuilderStrategy();
}
