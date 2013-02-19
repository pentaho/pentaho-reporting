package org.pentaho.reporting.library.parameter.server;

public class OutputParameter
{
  private String id;
  private String displayName;

  public OutputParameter(final String id, final String displayName)
  {
    this.id = id;
    this.displayName = displayName;
  }

  public String getId()
  {
    return id;
  }

  public String getDisplayName()
  {
    return displayName;
  }

  public boolean equals(final Object o)
  {
    if (this == o)
    {
      return true;
    }
    if (o == null || getClass() != o.getClass())
    {
      return false;
    }

    final OutputParameter that = (OutputParameter) o;

    if (displayName != null ? !displayName.equals(that.displayName) : that.displayName != null)
    {
      return false;
    }
    if (id != null ? !id.equals(that.id) : that.id != null)
    {
      return false;
    }

    return true;
  }

  public int hashCode()
  {
    int result = id != null ? id.hashCode() : 0;
    result = 31 * result + (displayName != null ? displayName.hashCode() : 0);
    return result;
  }
}
