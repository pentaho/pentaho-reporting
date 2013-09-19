package org.pentaho.reporting.libraries.designtime.swing.colorchooser;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.HeadlessException;

import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;

public class ColorChooserDialog extends CommonDialog
{
  private ColorChooserPane colorChooserPane;

  public ColorChooserDialog()
  {
    init();
  }

  public ColorChooserDialog(final Frame owner) throws HeadlessException
  {
    super(owner);
    init();
  }

  public ColorChooserDialog(final Dialog owner) throws HeadlessException
  {
    super(owner);
    init();
  }

  public void addColorChooserPanel(final AbstractColorChooserPanel panel)
  {
    colorChooserPane.addColorChooserPanel(panel);
  }

  public AbstractColorChooserPanel getColorChooserPanel(final int index)
  {
    return colorChooserPane.getColorChooserPanel(index);
  }

  public void clearSwatches()
  {
    colorChooserPane.clearSwatches();
  }

  public SwatchColorChooser getSwatchColorChooser()
  {
    return colorChooserPane.getSwatchColorChooser();
  }

  public int getColorChooserPanelCount()
  {
    return colorChooserPane.getColorChooserPanelCount();
  }

  public void removeAllColorChooserPanels()
  {
    colorChooserPane.removeAllColorChooserPanels();
  }

  public void removeColorChooserPanel(final AbstractColorChooserPanel panel)
  {
    colorChooserPane.removeColorChooserPanel(panel);
  }

  public ExtendedColorModel getModel()
  {
    return colorChooserPane.getModel();
  }

  protected ColorChooserPane getColorChooserPane()
  {
    return colorChooserPane;
  }

  protected void init()
  {
    setTitle(ColorChooserMessages.getInstance().getString("ColorChooserDialog.Title"));
    colorChooserPane = new ColorChooserPane();
    super.init();
  }


  protected String getDialogId()
  {
    return "LibSwing.ColorChooser";
  }

  protected Component createContentPane()
  {
    return colorChooserPane;
  }

  public Color performEdit(final Color color, final ColorSchema[] colorSchemas)
  {
    if (colorSchemas != null)
    {
      colorChooserPane.clearSwatches();
      for (final ColorSchema colorSchema : colorSchemas)
      {
        colorChooserPane.addSwatches(colorSchema);
      }
    }
    getModel().setSelectedColor(color);
    if (performEdit())
    {
      return getModel().getSelectedColor();
    }
    return null;
  }
}
