package org.pentaho.reporting.designer.core.editor.drilldown.basic;

/**
 * Todo: Document me!
 * <p/>
 * Date: 05.08.2010
 * Time: 15:12:00
 *
 * @author Thomas Morgner.
 */
public class GenericUrlDrillDownUiProfile extends XulDrillDownUiProfile
{
  public GenericUrlDrillDownUiProfile() throws IllegalStateException
  {
    super(new String[]{"generic-url","local-url"});//NON-NLS
  }

  public int getOrderKey()
  {
    return 1000;
  }
}