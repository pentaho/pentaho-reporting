/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.demo.ancient.demo.layouts.internalframe;

import java.awt.Container;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;

/**
 * Creation-Date: 11.12.2005, 12:50:21
 *
 * @author Thomas Morgner
 *//* Used by InternalFrameDemo.java. */
public class DocumentInternalFrame extends JInternalFrame
{
  public DocumentInternalFrame()
  {
    super("Document", true, true, true, true);
    final Container contentPane = getContentPane();
    contentPane.setLayout(new FlowLayout());
    contentPane.add(new JLabel("Some text"));
    contentPane.add(new JButton("A button"));
    contentPane.add(new JLabel("Some more text"));
  }

}
