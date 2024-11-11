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


package org.pentaho.reporting.designer.core.status;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionModelListener;

import javax.swing.*;
import java.awt.*;

/**
 * User: Martin Date: 03.02.2006 Time: 10:40:31
 */
public class StatusBar extends JPanel implements UncaughtExceptionModelListener {
  private JLabel pagesLabel;
  private JLabel statusLabel;
  private ExceptionStatusGadget exceptionStatusGadget;
  private MemoryStatusGadget memoryStatusGadget;
  private MessagesStatusGadget messagesStatusGadget;

  public StatusBar( final ReportDesignerContext designerContext ) {
    setLayout( new GridBagLayout() );
    setBorder( BorderFactory.createMatteBorder( 1, 0, 0, 0, SystemColor.controlLtHighlight ) );

    pagesLabel = new JLabel( " " );
    pagesLabel.setBorder( BorderFactory.createEmptyBorder( 4, 4, 4, 4 ) );
    statusLabel = new JLabel( " " );
    statusLabel.setBorder( BorderFactory.createEmptyBorder( 4, 4, 4, 4 ) );
    exceptionStatusGadget = new ExceptionStatusGadget();
    memoryStatusGadget = new MemoryStatusGadget();
    messagesStatusGadget = new MessagesStatusGadget( designerContext );

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.weightx = 1;
    gbc.fill = GridBagConstraints.BOTH;
    add( statusLabel, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.weightx = 0;
    gbc.fill = GridBagConstraints.VERTICAL;
    add( new JSeparator( JSeparator.VERTICAL ), gbc );

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridx = 2;
    gbc.weightx = 0;
    gbc.ipadx = 120;
    gbc.fill = GridBagConstraints.BOTH;
    add( pagesLabel, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 3;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.weightx = 0;
    gbc.fill = GridBagConstraints.VERTICAL;
    add( new JSeparator( JSeparator.VERTICAL ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 4;
    gbc.anchor = GridBagConstraints.EAST;
    gbc.fill = GridBagConstraints.VERTICAL;
    gbc.weightx = 0;
    add( messagesStatusGadget, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 5;
    gbc.anchor = GridBagConstraints.EAST;
    gbc.fill = GridBagConstraints.VERTICAL;
    gbc.weightx = 0;
    add( exceptionStatusGadget, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 6;
    gbc.anchor = GridBagConstraints.EAST;
    gbc.weightx = 0;
    gbc.fill = GridBagConstraints.VERTICAL;
    add( new JSeparator( JSeparator.VERTICAL ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 7;
    gbc.anchor = GridBagConstraints.EAST;
    gbc.fill = GridBagConstraints.VERTICAL;
    gbc.weightx = 0;
    add( memoryStatusGadget );

  }

  public void dispose() {
    memoryStatusGadget.dispose();
  }

  public void setPages( final int page, final int totalPage ) {
    if ( page == 0 || totalPage == 0 ) {
      pagesLabel.setText( " " );
    } else {
      pagesLabel.setText( page + " / " + totalPage );
    }
  }

  public void setGeneralInfoText( final String text ) {
    if ( text == null || text.length() == 0 ) {
      statusLabel.setText( " " );
    } else {
      statusLabel.setText( text );
    }
  }

  public void exceptionCaught( final Throwable throwable ) {
    exceptionStatusGadget.exceptionCaught( throwable );
  }

  public void exceptionsCleared() {
    exceptionStatusGadget.exceptionsCleared();
  }

  public void exceptionsViewed() {
    exceptionStatusGadget.exceptionsViewed();
  }

}
