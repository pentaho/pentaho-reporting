package org.pentaho.reporting.designer.core.settings.ui;

import javax.swing.Icon;
import javax.swing.JComponent;

/**
 * Todo: Document me!
 * <p/>
 * Date: 28.04.2010
 * Time: 13:58:11
 *
 * @author Thomas Morgner.
 */
public interface SettingsPlugin
{
  public JComponent getComponent();

  public Icon getIcon();

  public String getTitle();

  public void apply();

  public void reset();

  public ValidationResult validate(ValidationResult result);
}
