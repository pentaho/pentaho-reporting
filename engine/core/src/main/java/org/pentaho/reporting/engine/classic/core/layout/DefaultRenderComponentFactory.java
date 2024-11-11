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

import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.layout.build.DefaultLayoutBuilderStrategy;
import org.pentaho.reporting.engine.classic.core.layout.build.DefaultLayoutModelBuilder;
import org.pentaho.reporting.engine.classic.core.layout.build.DefaultRenderNodeFactory;
import org.pentaho.reporting.engine.classic.core.layout.build.LayoutBuilderStrategy;
import org.pentaho.reporting.engine.classic.core.layout.build.LayoutModelBuilder;
import org.pentaho.reporting.engine.classic.core.layout.build.RenderNodeFactory;
import org.pentaho.reporting.engine.classic.core.layout.build.RichTextStyleResolver;
import org.pentaho.reporting.engine.classic.core.layout.output.DefaultProcessingContext;
import org.pentaho.reporting.libraries.base.util.ArgumentNullException;

public class DefaultRenderComponentFactory implements RenderComponentFactory {
  private RichTextStyleResolver resolver;

  public DefaultRenderComponentFactory() {
    resolver = new RichTextStyleResolver( new DefaultProcessingContext(), new MasterReport() );
  }

  public DefaultRenderComponentFactory( RichTextStyleResolver resolver ) {
    ArgumentNullException.validate( "resolver", resolver );
    this.resolver = resolver;
  }

  public LayoutModelBuilder createLayoutModelBuilder( final String name ) {
    return new DefaultLayoutModelBuilder( name );
  }

  public RenderNodeFactory createRenderNodeFactory() {
    return new DefaultRenderNodeFactory();
  }

  public LayoutBuilderStrategy createLayoutBuilderStrategy() {
    return new DefaultLayoutBuilderStrategy( resolver );
  }
}
