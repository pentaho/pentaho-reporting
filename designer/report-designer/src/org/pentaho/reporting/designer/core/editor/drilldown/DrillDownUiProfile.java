package org.pentaho.reporting.designer.core.editor.drilldown;

/**
 * Instances of this class must override equals and hashcode or you will be doomed!
 * <p/>
 *
 * @author Thomas Morgner.
 */
public interface DrillDownUiProfile
{
  public DrillDownUi createUI();

  public String getDisplayName();

  public boolean canHandle(String profileName);

  public int getOrderKey();
}
