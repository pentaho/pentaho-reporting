package org.pentaho.reporting.libraries.designtime.swing.colorchooser;

import java.awt.Color;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

public class ExtendedColorModel
{
  private EventListenerList eventListenerList;

  private int hue;
  private int saturation;
  private int value;
  private int red;
  private int green;
  private int blue;
  private boolean traceEvents;

  public ExtendedColorModel()
  {
    eventListenerList = new EventListenerList();
  }

  public void setTraceEvents(final boolean traceEvents)
  {
    this.traceEvents = traceEvents;
  }

  public int getHue()
  {
    return hue;
  }

  public int getSaturation()
  {
    return saturation;
  }

  public int getValue()
  {
    return value;
  }

  public int getRed()
  {
    return red;
  }

  public int getGreen()
  {
    return green;
  }

  public int getBlue()
  {
    return blue;
  }

  public void fireChangeEvent()
  {
    final ChangeListener[] listeners = eventListenerList.getListeners(ChangeListener.class);
    if (listeners.length == 0)
    {
      return;
    }
    final ChangeEvent event = new ChangeEvent(this);
    for (int i = 0; i < listeners.length; i++)
    {
      final ChangeListener listener = listeners[i];
      listener.stateChanged(event);
    }

    if (traceEvents)
    {
      new Exception().printStackTrace();
    }
  }

  public void addChangeListener(final ChangeListener changeListener)
  {
    eventListenerList.add(ChangeListener.class, changeListener);
  }

  public void removeChangeListener(final ChangeListener changeListener)
  {
    eventListenerList.remove(ChangeListener.class, changeListener);
  }

  public Color getSelectedColor()
  {
    return new Color(red, green, blue);
  }

  public void setSelectedColor(final Color color)
  {
    if (color == null)
    {
      return;
    }

    if (this.red == color.getRed() && this.green == color.getGreen() && this.blue == color.getBlue())
    {
      return;
    }

    this.red = color.getRed();
    this.green = color.getGreen();
    this.blue = color.getBlue();

    final float[] hsb = Color.RGBtoHSB(red, green, blue, null);
    hue = (int) (hsb[0] * 360f);
    saturation = (int) (hsb[1] * 100f);
    value = (int) (hsb[2] * 100f);

    fireChangeEvent();
  }

  public void setHSB(final int hue, final int saturation, final int value)
  {
    if (this.hue == hue && this.saturation == saturation && this.value == value)
    {
      return;
    }

    this.hue = hue;
    this.saturation = saturation;
    this.value = value;

    final Color color = Color.getHSBColor(hue / 360f, saturation / 100f, value / 100f);
    this.red = color.getRed();
    this.green = color.getGreen();
    this.blue = color.getBlue();

    fireChangeEvent();
  }

  public void setRGB(final int red, final int green, final int blue)
  {
    if (this.red == red && this.green == green && this.blue == blue)
    {
      return;
    }

    this.red = red;
    this.green = green;
    this.blue = blue;

    final float[] hsb = Color.RGBtoHSB(red, green, blue, null);
    hue = (int) (hsb[0] * 360f);
    saturation = (int) (hsb[1] * 100f);
    value = (int) (hsb[2] * 100f);

    fireChangeEvent();

  }

  public void copyInto(final ExtendedColorModel colorModel)
  {
    if (this.red == colorModel.red && this.green == colorModel.green && this.blue == colorModel.blue &&
        this.hue == colorModel.hue && this.saturation == colorModel.saturation && this.value == colorModel.value)
    {
      return;
    }

    this.red = colorModel.red;
    this.green = colorModel.green;
    this.blue = colorModel.blue;
    this.hue = colorModel.hue;
    this.saturation = colorModel.saturation;
    this.value = colorModel.value;
    fireChangeEvent();
  }
}
