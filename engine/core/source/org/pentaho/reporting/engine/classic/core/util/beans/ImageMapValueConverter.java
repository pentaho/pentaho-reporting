package org.pentaho.reporting.engine.classic.core.util.beans;

import org.pentaho.reporting.engine.classic.core.imagemap.ImageMap;
import org.pentaho.reporting.engine.classic.core.imagemap.parser.ImageMapParser;
import org.pentaho.reporting.engine.classic.core.imagemap.parser.ImageMapWriter;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;

public class ImageMapValueConverter implements ValueConverter
{
  public ImageMapValueConverter()
  {
  }

  /**
   * Converts an object to an attribute value.
   *
   * @param o the object.
   * @return the attribute value.
   * @throws org.pentaho.reporting.engine.classic.core.util.beans.BeanException
   *          if there was an error during the conversion.
   */
  public String toAttributeValue(final Object o) throws BeanException
  {
    if (o == null)
    {
      throw new NullPointerException();
    }
    if ((o instanceof ImageMap) == false)
    {
      throw new BeanException("Failed to convert object of type " + o.getClass() + ": Not a ImageMap.");
    }
    return ImageMapWriter.writeImageMapAsString((ImageMap) o);
  }

  /**
   * Converts a string to a property value.
   *
   * @param s the string.
   * @return a property value.
   * @throws org.pentaho.reporting.engine.classic.core.util.beans.BeanException
   *          if there was an error during the conversion.
   */
  public Object toPropertyValue(final String s) throws BeanException
  {
    if (s == null)
    {
      throw new NullPointerException();
    }
    try
    {
      final ImageMapParser parser = new ImageMapParser();
      return parser.parseFromString(s);
    }
    catch (ResourceException ioe)
    {
      throw new BeanException("Failed to parse image map.", ioe);
    }
  }
}
