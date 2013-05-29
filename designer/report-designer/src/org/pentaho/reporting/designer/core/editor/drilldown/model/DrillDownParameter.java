package org.pentaho.reporting.designer.core.editor.drilldown.model;

import java.io.Serializable;
import java.util.Locale;

import org.pentaho.reporting.engine.classic.core.metadata.MetaData;

public class DrillDownParameter implements MetaData, Serializable
{
  public static enum Type
  {
    PREDEFINED, SYSTEM, MANUAL 
  }

  private String name;
  private String formulaFragment;
  private Type type;
  private int position;
  private boolean preferred;
  private boolean expert;
  private int compatibilityLevel;

  public DrillDownParameter(final String name)
  {
    if (name == null)
    {
      throw new NullPointerException();
    }
    this.compatibilityLevel = -1;
    this.preferred = true;
    this.name = name;
    this.type = Type.MANUAL;
  }

  public DrillDownParameter(final String name, final String formulaFragment)
  {
    this(name);
    this.formulaFragment = formulaFragment;
    this.type = Type.MANUAL;
  }
  
  public DrillDownParameter(final String name,
                            final String formulaFragment,
                            final Type type,
                            final boolean expert,
                            final boolean hidden)
  {
    this(name, formulaFragment);
    if (type == null)
    {
      throw new NullPointerException();
    }
    this.expert = expert;
    this.type = type;
  }

  public String getName()
  {
    return name;
  }

  public void setName(final String name)
  {
    if (name == null)
    {
      throw new NullPointerException();
    }
    this.name = name;
  }

  public String getFormulaFragment()
  {
    return formulaFragment;
  }

  public void setFormulaFragment(final String formulaFragment)
  {
    this.formulaFragment = formulaFragment;
  }

  public Type getType()
  {
    return type;
  }

  public void setType(final Type type)
  {
    if (type == null)
    {
      throw new NullPointerException();
    }
    this.type = type;
  }

  public int getPosition()
  {
    return position;
  }

  public void setPosition(final int position)
  {
    this.position = position;
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

    final DrillDownParameter that = (DrillDownParameter) o;

    if (position != that.position)
    {
      return false;
    }
    if (formulaFragment != null ? !formulaFragment.equals(that.formulaFragment) : that.formulaFragment != null)
    {
      return false;
    }
    if (name != null ? !name.equals(that.name) : that.name != null)
    {
      return false;
    }
    if (type != that.type)
    {
      return false;
    }

    return true;
  }

  public int hashCode()
  {
    int result = name != null ? name.hashCode() : 0;
    result = 31 * result + (formulaFragment != null ? formulaFragment.hashCode() : 0);
    result = 31 * result + type.hashCode();
    result = 31 * result + position;
    return result;
  }

  /** @noinspection HardCodedStringLiteral*/
  public String toString()
  {
    final StringBuilder sb = new StringBuilder();
    sb.append("DrillDownParameter");
    sb.append("{name='").append(name).append('\'');
    sb.append(", formulaFragment='").append(formulaFragment).append('\'');
    sb.append(", type=").append(type);
    sb.append(", position=").append(position);
    sb.append('}');
    return sb.toString();
  }

  public void setPreferred(final boolean preferred)
  {
    this.preferred = preferred;
  }

  public boolean isPreferred()
  {
    return preferred;
  }

  public String getDisplayName(final Locale locale)
  {
    return name;
  }

  public String getMetaAttribute(final String attributeName, final Locale locale)
  {
    return null;
  }

  public String getGrouping(final Locale locale)
  {
    return type.toString();
  }

  public int getGroupingOrdinal(final Locale locale)
  {
    return type.ordinal();
  }

  public int getItemOrdinal(final Locale locale)
  {
    return position;
  }

  public String getDeprecationMessage(final Locale locale)
  {
    return null;
  }

  public String getDescription(final Locale locale)
  {
    return null;
  }

  public boolean isDeprecated()
  {
    return false;
  }

  public void setExpert(final boolean expert)
  {
    this.expert = expert;
  }

  public boolean isExpert()
  {
    return expert;
  }

  public boolean isHidden()
  {
    return false;
  }

  public boolean isExperimental()
  {
    return false;
  }

  public int getCompatibilityLevel()
  {
    return compatibilityLevel;
  }

  public String getKeyPrefix()
  {
    return null;
  }

  public String getBundleLocation()
  {
    return null;
  }
}
