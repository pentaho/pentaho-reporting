package org.pentaho.reporting.libraries.designtime.swing.colorchooser;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JComponent;

public class ColorPreviewPanel extends JComponent
{
  private Color previous;
  private Color current;

  public ColorPreviewPanel()
  {
    previous = Color.white;
    current = Color.white;
  }

  public Color getPrevious()
  {
    return previous;
  }

  public void setPrevious(final Color previous)
  {
    if (previous == null)
    {
      throw new NullPointerException();
    }
    this.previous = previous;
    repaint();
  }

  public Color getCurrent()
  {
    return current;
  }

  public void setCurrent(final Color current)
  {
    if (current == null)
    {
      throw new NullPointerException();
    }
    this.current = current;
    repaint();
  }

  public Dimension getPreferredSize()
  {
    return new Dimension(60, 60);
  }

  public Dimension getMinimumSize()
  {
    return new Dimension(60, 60);
  }

  protected void paintComponent(final Graphics g)
  {
    final Graphics graphics = g.create();
    graphics.setColor(current);
    graphics.fillRect(0, 0, getWidth(), getHeight() / 2);
    graphics.setColor(previous);
    graphics.fillRect(0, getHeight() / 2, getWidth(), getHeight() / 2);
    graphics.dispose();
  }
}
