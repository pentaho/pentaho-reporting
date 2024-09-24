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

package org.pentaho.reporting.designer.core.model;

import org.pentaho.reporting.designer.core.Messages;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.engine.classic.core.PageDefinition;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;

import javax.swing.*;
import java.awt.*;

public class AlignmentOptionsDialog extends CommonDialog {
  private PageDefinition pageDefinition;
  private ReportDocumentContext context;

  private JRadioButton leftButton;
  private JRadioButton centerButton;
  private JRadioButton rightButton;
  private JRadioButton proportionalButton;
  private JRadioButton noneButton;

  public AlignmentOptionsDialog( final ReportDocumentContext context,
                                 final PageDefinition pageDefinition ) {
    init( context, pageDefinition );
  }

  public AlignmentOptionsDialog( final Dialog parent,
                                 final ReportDocumentContext context,
                                 final PageDefinition pageDefinition ) {
    super( parent );
    init( context, pageDefinition );
  }

  public AlignmentOptionsDialog( final Frame parent,
                                 final ReportDocumentContext context,
                                 final PageDefinition pageDefinition ) {
    super( parent );
    init( context, pageDefinition );
  }

  private void init( final ReportDocumentContext reportRenderContext,
                     final PageDefinition pageDefinition ) {
    if ( reportRenderContext == null ) {
      throw new NullPointerException();
    }
    if ( pageDefinition == null ) {
      throw new NullPointerException();
    }

    this.context = reportRenderContext;
    this.pageDefinition = pageDefinition;

    setTitle( Messages.getString( "ResizeReportOptionPane.Title" ) );

    leftButton = new JRadioButton( Messages.getString( "ResizeReportOptionPane.OptionAlignLeft" ), false );
    centerButton = new JRadioButton( Messages.getString( "ResizeReportOptionPane.OptionAlignCenter" ), false );
    rightButton = new JRadioButton( Messages.getString( "ResizeReportOptionPane.OptionAlignRight" ), false );
    proportionalButton =
      new JRadioButton( Messages.getString( "ResizeReportOptionPane.OptionResizeProportional" ), false );
    noneButton = new JRadioButton( Messages.getString( "ResizeReportOptionPane.OptionAlignNone" ), true );

    final ButtonGroup buttonGroup = new ButtonGroup();
    buttonGroup.add( leftButton );
    buttonGroup.add( centerButton );
    buttonGroup.add( rightButton );
    buttonGroup.add( proportionalButton );
    buttonGroup.add( noneButton );

    super.init();
  }

  protected String getDialogId() {
    return "ReportDesigner.Core.AlignmentOptions";
  }

  protected Component createContentPane() {
    final JLabel resizeLabel = new JLabel( Messages.getString( "ResizeReportOptionPane.Message" ) );
    resizeLabel.setBorder( BorderFactory.createEmptyBorder( 10, 10, 5, 5 ) );

    final JPanel optionsPane = new JPanel( new GridLayout( 5, 1 ) );
    optionsPane.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
    optionsPane.add( leftButton );
    optionsPane.add( centerButton );
    optionsPane.add( rightButton );
    optionsPane.add( proportionalButton );
    optionsPane.add( noneButton );

    final JPanel contentPane = new JPanel();
    contentPane.setLayout( new BorderLayout() );
    contentPane.add( resizeLabel, BorderLayout.NORTH );
    contentPane.add( optionsPane, BorderLayout.CENTER );
    return contentPane;
  }

  public boolean performEdit() {
    if ( super.performEdit() ) {
      final AlignmentUtilities theAlignmentUtil = new AlignmentUtilities( context, pageDefinition );
      if ( leftButton.isSelected() ) {
        theAlignmentUtil.alignLeft();
      }
      if ( centerButton.isSelected() ) {
        theAlignmentUtil.alignCenter();
      }
      if ( rightButton.isSelected() ) {
        theAlignmentUtil.alignRight();
      }
      if ( proportionalButton.isSelected() ) {
        theAlignmentUtil.resizeProportional();
      }
      return true;
    }

    return false;
  }
}
