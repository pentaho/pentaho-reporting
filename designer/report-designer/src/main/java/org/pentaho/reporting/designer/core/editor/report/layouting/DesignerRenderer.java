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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.reporting.designer.core.editor.report.layouting;

import org.pentaho.reporting.engine.classic.core.layout.RenderComponentFactory;
import org.pentaho.reporting.engine.classic.core.layout.StreamingRenderer;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;

public class DesignerRenderer extends StreamingRenderer {
  private DesignerRenderComponentFactory designerRenderComponentFactory;

  public DesignerRenderer( final DesignerOutputProcessor outputProcessor,
                           final DesignerRenderComponentFactory designerRenderComponentFactory ) {
    super( outputProcessor );
    this.designerRenderComponentFactory = designerRenderComponentFactory;
  }

  protected RenderComponentFactory createComponentFactory() {
    if ( designerRenderComponentFactory == null ) {
      designerRenderComponentFactory = new DesignerRenderComponentFactory( getMetaData() );
    }
    return designerRenderComponentFactory;
  }

  public LogicalPageBox getPageBox() {
    return super.getPageBox();
  }

  /**
   * Override so that we do not perform any intermediate layouting. This speeds up the layout process for complex
   * reports. We do a single, complete layout run at the end of the report processing.
   *
   * @return the layout result.
   * @throws ContentProcessingException
   */
  public LayoutResult validatePages() throws ContentProcessingException {
    final LogicalPageBox pageBox = getPageBox();
    if ( pageBox == null ) {
      return LayoutResult.LAYOUT_UNVALIDATABLE;
    }

    if ( pageBox.isOpen() ) {
      return LayoutResult.LAYOUT_UNVALIDATABLE;
    }

    return super.validatePages();
  }

  public void createRollbackInformation() {
    // intentionally a No-Op
  }

  public void applyRollbackInformation() {
    // intentionally a No-Op
  }

  public void rollback() {
    // intentionally a No-Op
  }
}
