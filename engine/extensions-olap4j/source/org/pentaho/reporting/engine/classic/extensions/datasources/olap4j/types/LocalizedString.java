package org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.types;

import java.util.Locale;

import org.olap4j.metadata.Member;

public class LocalizedString
{
  private Member member;
  private boolean description;

  public LocalizedString(final Member member, final boolean description)
  {
    this.member = member;
    this.description = description;
  }

  public String getValue(final Locale locale)
  {
    if (description)
    {
      return member.getDescription();
    }
    return member.getCaption();
  }

  public String toString()
  {
    final StringBuilder sb = new StringBuilder();
    sb.append("LocalizedString");
    sb.append("{description=").append(description);
    sb.append(", member=").append(member.getUniqueName());
    sb.append('}');
    return sb.toString();
  }
}
