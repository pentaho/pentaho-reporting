/*!
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2002-2018 Hitachi Vantara..  All rights reserved.
 */

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
