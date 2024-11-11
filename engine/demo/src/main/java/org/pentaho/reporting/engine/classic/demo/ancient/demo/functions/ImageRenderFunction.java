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


package org.pentaho.reporting.engine.classic.demo.ancient.demo.functions;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import javax.swing.JButton;
import javax.swing.JRadioButton;

import org.pentaho.reporting.engine.classic.core.DefaultImageReference;
import org.pentaho.reporting.engine.classic.core.event.PageEventListener;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.function.AbstractFunction;

/**
 * The ImageRenderFunction creates a simple Image using a BufferedImage within a function to show the use of the
 * ImageFunctionElement. The image is created whenever a new page is started.
 *
 * @author Thomas Morgner
 */
public class ImageRenderFunction extends AbstractFunction
    implements Serializable, PageEventListener
{
  /**
   * The function value.
   */
  private transient DefaultImageReference functionValue;

  /**
   * Creates an unnamed function. Make sure the name of the function is set using {@link #setName} before the function
   * is added to the report's function collection.
   */
  public ImageRenderFunction()
  {
  }

  /**
   * Create a image according to the current state, simple and silly ...
   *
   * @param event the report event.
   */
  public void pageStarted(final ReportEvent event)
  {
    final BufferedImage image = new BufferedImage(150, 50, BufferedImage.TYPE_INT_ARGB);
    final Graphics2D g2 = image.createGraphics();
    final JButton bt = new JButton("A Button");
    bt.setSize(90, 20);
    final JRadioButton radio = new JRadioButton("A radio button");
    radio.setSize(100, 20);

    g2.setColor(Color.darkGray);
    bt.paint(g2);
    g2.setColor(Color.blue);
    g2.setTransform(AffineTransform.getTranslateInstance(20, 20));
    radio.paint(g2);
    g2.setTransform(AffineTransform.getTranslateInstance(0, 0));
    g2.setPaint(Color.green);
    g2.setFont(new Font("Serif", Font.PLAIN, 10));
    g2.drawString("You are viewing a graphics of JFreeReport on index "
        + event.getState().getCurrentRow(), 10, 10);
    g2.dispose();
    try
    {
      functionValue = new DefaultImageReference(image);
    }
    catch (IOException e)
    {
      functionValue = null;
    }
  }

  /**
   * Receives notification that a page is completed.
   *
   * @param event The event.
   */
  public void pageFinished(final ReportEvent event)
  {
  }

  /**
   * Return the last generated Image.
   *
   * @return the function value.
   */
  public Object getValue()
  {
    return functionValue;
  }
}
