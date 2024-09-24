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

package org.pentaho.reporting.designer.core.editor.drilldown.basic;

import org.dom4j.io.DOMReader;
import org.pentaho.reporting.designer.core.ReportDesignerBoot;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.editor.drilldown.DrillDownUi;
import org.pentaho.reporting.designer.core.editor.drilldown.DrillDownUiException;
import org.pentaho.reporting.designer.core.editor.drilldown.model.DrillDownModel;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.ui.xul.XulComponent;
import org.pentaho.ui.xul.XulDomContainer;
import org.pentaho.ui.xul.XulException;
import org.pentaho.ui.xul.XulRunner;
import org.pentaho.ui.xul.swing.SwingXulLoader;
import org.pentaho.ui.xul.swing.SwingXulRunner;
import org.pentaho.ui.xul.swing.tags.SwingDialog;
import org.pentaho.ui.xul.swing.tags.SwingWindow;
import org.w3c.dom.Document;

import javax.swing.*;
import java.awt.*;

public class XulDrillDownUi implements DrillDownUi {
  private XulDrillDownController controller;
  private Component editor;
  private DrillDownModel model;
  private String[] validConfigs;
  private String configurationTag;

  public XulDrillDownUi( final String[] validConfigs, final String configurationTag ) {
    if ( validConfigs == null || validConfigs.length == 0 ) {
      throw new IllegalArgumentException();
    }
    if ( configurationTag == null ) {
      throw new NullPointerException();
    }

    this.configurationTag = configurationTag;
    this.validConfigs = validConfigs.clone();
    this.editor = new JLabel( "Editor has not been configured" );
  }

  public void init( final Component parent,
                    final ReportDesignerContext reportDesignerContext,
                    final DrillDownModel model,
                    final String[] extraFields ) throws DrillDownUiException {
    if ( model == null ) {
      throw new NullPointerException();
    }
    if ( reportDesignerContext == null ) {
      throw new NullPointerException();
    }
    if ( extraFields == null ) {
      throw new NullPointerException();
    }
    try {

      this.model = model;
      // Load the document
      final SwingXulLoader loader = new DrillDownSwingLoader();
      loader.setOuterContext( parent );

      final String modelProfileName = model.getDrillDownConfig();
      final String profileName = filter( modelProfileName );
      model.setDrillDownConfig( profileName );

      final Configuration configuration = ReportDesignerBoot.getInstance().getGlobalConfig();
      final String xulDocument = configuration.getConfigProperty
        ( "org.pentaho.reporting.designer.core.editor.drilldown.xul-document." + configurationTag, // NON-NLS
          "res://org/pentaho/reporting/designer/core/editor/drilldown/basic/basic-drilldown.xul" ); // NON-NLS

      final ResourceManager resourceManager = new ResourceManager();
      final Resource resource = resourceManager.createDirectly( xulDocument, Document.class );
      final Document document = (Document) resource.getResource();
      final DOMReader domReader = new DOMReader();
      final XulDomContainer container = loader.loadXulFragment( domReader.read( document ) );


      // Create our main Controller
      final String className = configuration.getConfigProperty
        ( "org.pentaho.reporting.designer.core.editor.drilldown.xul-controller." + configurationTag, // NON-NLS
          DefaultXulDrillDownController.class.getName() );
      controller =
        ObjectUtilities.loadAndInstantiate( className, XulDrillDownController.class, XulDrillDownController.class );
      if ( controller != null ) {
        controller.setName( "controller" ); // NON-NLS
        controller.setXulDomContainer( container );
        controller.init( reportDesignerContext, model, extraFields );
        container.addEventHandler( controller );
      }

      // Start it up!
      final XulRunner runner = new SwingXulRunner();
      runner.addContainer( container );

      runner.initialize();
      final org.pentaho.ui.xul.dom.Document documentRoot = runner.getXulDomContainers().get( 0 ).getDocumentRoot();
      final XulComponent rootElement = documentRoot.getElementById( "root" );
      if ( rootElement != null ) {
        editor = (Component) rootElement.getManagedObject();
      } else {
        final XulComponent rootEle = documentRoot.getRootElement();
        if ( rootEle instanceof SwingWindow ) {
          final SwingWindow window = (SwingWindow) rootEle;
          final JFrame rootFrame = (JFrame) window.getRootObject();
          editor = rootFrame.getContentPane();
        } else if ( rootEle instanceof SwingDialog ) {
          final SwingDialog dialog = (SwingDialog) rootEle;
          final JDialog rootFrame = dialog.getDialog();
          editor = rootFrame.getContentPane();
        } else {
          throw new DrillDownUiException( "Root element not a Frame: " + rootEle );
        }
      }
    } catch ( XulException xe ) {
      throw new DrillDownUiException( xe );
    } catch ( ResourceException e ) {
      throw new DrillDownUiException( e );
    }
  }

  private String extractPropertiesName( final String xulDocument ) {
    String base;
    int startIndex = 0;
    if ( xulDocument.startsWith( "res:/" ) ) {
      startIndex = 5;
    }
    if ( xulDocument.endsWith( ".xul" ) ) {
      base = xulDocument.substring( startIndex, xulDocument.length() - 4 );
    } else {
      base = xulDocument.substring( startIndex );
    }
    return base.replace( '/', '.' );
  }

  private String filter( final String modelProfileName ) {
    for ( int i = 0; i < validConfigs.length; i++ ) {
      final String config = validConfigs[ i ];
      if ( config.equals( modelProfileName ) ) {
        return modelProfileName;
      }
    }
    return validConfigs[ 0 ];
  }

  public Component getEditorPanel() {
    return editor;
  }

  public DrillDownModel getModel() {
    if ( controller != null ) {
      return controller.getModel();
    }
    return model;
  }

  public void deactivate() {
    if ( controller != null ) {
      controller.deactivate();
    }
  }
}
