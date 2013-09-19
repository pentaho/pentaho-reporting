package org.pentaho.reporting.libraries.designtime.swing.colorchooser;

import java.awt.Color;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public abstract class AbstractColorChooserPanel extends JComponent
{
  private class ColorSelectionListener implements ChangeListener
  {
    private ColorSelectionListener()
    {
    }

    public void stateChanged(final ChangeEvent e)
    {
      colorUpdated();
    }
  }

  private ExtendedColorModel colorSelectionModel;
  private ColorSelectionListener colorSelectionListener;

  protected AbstractColorChooserPanel()
  {
    colorSelectionListener = new ColorSelectionListener();
  }

  public ExtendedColorModel getColorSelectionModel()
  {
    return colorSelectionModel;
  }

  public abstract String getDisplayName();

  public abstract Icon getSmallDisplayIcon();

  public int getMnemonic()
  {
    return 0;
  }

  public int getDisplayedMnemonicIndex()
  {
    return -1;
  }

  public void installChooserPanel(final ExtendedColorModel colorSelectionModel)
  {
    if (colorSelectionModel == null)
    {
      throw new NullPointerException();
    }
    if (this.colorSelectionModel != null)
    {
      this.colorSelectionModel.removeChangeListener(colorSelectionListener);
    }
    this.colorSelectionModel = colorSelectionModel;
    this.colorSelectionModel.addChangeListener(colorSelectionListener);
  }

  public void uninstallChooserPanel()
  {
    if (this.colorSelectionModel != null)
    {
      this.colorSelectionModel.removeChangeListener(colorSelectionListener);
    }
    this.colorSelectionModel = null;
  }

  protected Color getColorFromModel()
  {
    if (colorSelectionModel == null)
    {
      return null;
    }
    return colorSelectionModel.getSelectedColor();
  }

  protected void colorUpdated()
  {

  }
}
