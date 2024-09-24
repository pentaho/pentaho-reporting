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

package org.pentaho.reporting.designer.core.editor;

import org.pentaho.reporting.designer.core.Messages;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.editor.attributes.VisualAttributeEditorPanel;
import org.pentaho.reporting.designer.core.editor.expressions.ExpressionPropertiesEditorPanel;
import org.pentaho.reporting.designer.core.editor.styles.StyleEditorPanel;
import org.pentaho.reporting.designer.core.model.selection.DocumentContextSelectionModel;
import org.pentaho.reporting.designer.core.util.SidePanel;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.function.Expression;

import javax.swing.*;
import java.awt.*;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class ElementPropertiesPanel extends SidePanel {
  private static final String BLANK_CARD = "Blank";
  private static final String ATTRIBUTES_CARD = "Attributes";
  private static final String EXPRESSIONS_CARD = "Expressions";
  private static final String DATASOURCE_CARD = "Datasource";

  private VisualAttributeEditorPanel attributeEditorPanel;
  private StyleEditorPanel styleEditorPanel;
  private CardLayout cardLayout;
  private ExpressionPropertiesEditorPanel expressionEditorPanel;
  private JPanel datasourceCarrier;

  private boolean allowAttributeCard = true;
  private boolean allowDataSourceCard = false;
  private boolean allowExpressionCard = false;

  public ElementPropertiesPanel() {
    cardLayout = new CardLayout();
    setLayout( cardLayout );

    this.attributeEditorPanel = new VisualAttributeEditorPanel();
    this.styleEditorPanel = new StyleEditorPanel();
    this.expressionEditorPanel = new ExpressionPropertiesEditorPanel();
    this.datasourceCarrier = new JPanel( new BorderLayout() );
    final JPanel blankPanel = new JPanel();

    final JTabbedPane tabbedPane = new JTabbedPane();
    tabbedPane.addTab( Messages.getString( "ElementPropertiesPanel.Style" ), styleEditorPanel );
    tabbedPane.addTab( Messages.getString( "ElementPropertiesPanel.Attributes" ), attributeEditorPanel );

    add( tabbedPane, ATTRIBUTES_CARD );
    add( expressionEditorPanel, EXPRESSIONS_CARD );
    add( datasourceCarrier, DATASOURCE_CARD );
    add( blankPanel, BLANK_CARD );
  }

  public void setReportDesignerContext( final ReportDesignerContext reportDesignerContext ) {
    super.setReportDesignerContext( reportDesignerContext );
    attributeEditorPanel.setReportDesignerContext( reportDesignerContext );
    styleEditorPanel.setReportDesignerContext( reportDesignerContext );
    expressionEditorPanel.setReportDesignerContext( reportDesignerContext );
  }

  public void setAllowAttributeCard( final boolean allowAttributeCard ) {
    this.allowAttributeCard = allowAttributeCard;
  }

  public void setAllowDataSourceCard( final boolean allowDataSourceCard ) {
    this.allowDataSourceCard = allowDataSourceCard;
  }

  public void setAllowExpressionCard( final boolean allowExpressionCard ) {
    this.allowExpressionCard = allowExpressionCard;
  }

  public void reset( final DocumentContextSelectionModel model ) {
    // clear selections
    cardLayout.show( this, BLANK_CARD );
    updateSelection( model );
  }

  protected void updateSelection( final DocumentContextSelectionModel model ) {
    datasourceCarrier.removeAll();
    if ( model.getSelectionCount() < 1 ) {
      cardLayout.show( this, BLANK_CARD );
      return;
    }

    final Object o = model.getSelectedElement( 0 );
    if ( o instanceof DataFactory && allowDataSourceCard ) {
      cardLayout.show( this, DATASOURCE_CARD );
      return;
    }

    if ( o instanceof Expression && allowExpressionCard ) {
      cardLayout.show( this, EXPRESSIONS_CARD );
      return;
    }

    if ( o instanceof ReportElement && allowAttributeCard ) {
      cardLayout.show( this, ATTRIBUTES_CARD );
      return;
    }

    cardLayout.show( this, BLANK_CARD );
  }
}
