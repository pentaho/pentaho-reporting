package org.pentaho.reporting.designer.core.editor.drilldown.model;

/**
 * Todo: Document me!
 * <p/>
 * Date: 22.07.2010
 * Time: 18:01:47
 *
 * @author Thomas Morgner.
 */
public class ParameterSelection
{
  private String label;
  private String type;
  private boolean selected;
  private String value;

  public ParameterSelection(final String type, final String value, final boolean selected, final String label)
  {
    this.type = type;
    this.value = value;
    this.selected = selected;
    this.label = label;
  }

  public String getLabel()
  {
    return label;
  }

  public String getType()
  {
    return type;
  }

  public boolean isSelected()
  {
    return selected;
  }

  public String getValue()
  {
    return value;
  }
}
