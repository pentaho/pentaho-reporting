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

package org.pentaho.reporting.designer.extensions.legacycharts;

import org.pentaho.reporting.designer.core.actions.AbstractElementSelectionAction;
import org.pentaho.reporting.designer.core.model.selection.DocumentContextSelectionModel;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.designtime.ReportModelEventFilter;
import org.pentaho.reporting.engine.classic.core.designtime.ReportModelEventFilterFactory;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;
import org.pentaho.reporting.engine.classic.extensions.legacy.charts.LegacyChartType;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class EditLegacyChartAction extends AbstractElementSelectionAction {
  private ReportModelEventFilter eventFilter;

  public EditLegacyChartAction() {
    putValue( Action.NAME, Messages.getInstance().getString( "EditLegacyChartAction.Text" ) );
    putValue( Action.SHORT_DESCRIPTION, Messages.getInstance().getString( "EditLegacyChartAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, Messages.getInstance().getOptionalMnemonic( "EditLegacyChartAction.Mnemonic" ) );
    putValue( Action.ACCELERATOR_KEY,
      Messages.getInstance().getOptionalKeyStroke( "EditLegacyChartAction.Accelerator" ) );

    eventFilter = new ReportModelEventFilterFactory().createAttributeFilter
      ( AttributeNames.Core.NAMESPACE, AttributeNames.Core.ELEMENT_TYPE );
  }

  protected void selectedElementPropertiesChanged( final ReportModelEvent event ) {
    if ( eventFilter.isFilteredEvent( event ) ) {
      updateSelection();
    }
  }

  protected void updateSelection() {
    if ( isSingleElementSelection() == false ) {
      setEnabled( false );
      return;
    }

    final Object selectedElement = getSelectionModel().getSelectedElement( 0 );
    if ( selectedElement instanceof Section ) {
      setEnabled( false );
      return;
    }
    if ( selectedElement instanceof Element ) {
      final Element element = (Element) selectedElement;
      setEnabled( element.getElementType() instanceof LegacyChartType );
    } else {
      setEnabled( false );
    }
  }

  public void actionPerformed( final ActionEvent e ) {
    final DocumentContextSelectionModel selectionModel = getSelectionModel();
    if ( selectionModel == null ) {
      return;
    }

    if ( selectionModel.getSelectionCount() != 1 ) {
      return;
    }
    final Object selectedElement = selectionModel.getSelectedElement( 0 );
    if ( selectedElement instanceof Element == false || selectedElement instanceof Section ) {
      return;
    }
    final Element chartElement = (Element) selectedElement;
    if ( LegacyChartsUtil.isLegacyChartElement( chartElement ) == false ) {
      return;
    }

    LegacyChartsUtil.performEdit( chartElement, getReportDesignerContext() );
  }

}
