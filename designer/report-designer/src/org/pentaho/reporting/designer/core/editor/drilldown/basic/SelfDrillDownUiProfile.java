package org.pentaho.reporting.designer.core.editor.drilldown.basic;

/**
 * Todo: Document me!
 * <p/>
 * Date: 05.08.2010
 * Time: 15:12:00
 *
 * @author Thomas Morgner.
 */
public class SelfDrillDownUiProfile extends XulDrillDownUiProfile
{
  public SelfDrillDownUiProfile() throws IllegalStateException
  {
    super("self");
  }

  public int getOrderKey()
  {
    return 500;
  }
}