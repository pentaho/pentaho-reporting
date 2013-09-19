package org.pentaho.reporting.engine.classic.extensions.datasources.olap4j;

import org.olap4j.metadata.Dimension;
import org.olap4j.metadata.Hierarchy;
import org.olap4j.metadata.Member;

public class Olap4jUtil {
  
  private static String quoteMdxIdentifier(String ident) {
    return "[" + ident.replaceAll("]", "]]") + "]";
  }

  public static String getUniqueMemberName(Member member) {
    String memberValue = quoteMdxIdentifier(member.getName());
    while (member.getParentMember() != null) {
      memberValue = quoteMdxIdentifier(member.getParentMember().getName()) + "." + memberValue;
      member = member.getParentMember();
    }
    final Hierarchy hierarchy = member.getHierarchy();
    final Dimension dimension = hierarchy.getDimension();
    if (hierarchy.getName().equals(dimension.getName())) {
      return quoteMdxIdentifier(hierarchy.getName()) + "." + memberValue;
    } else {
      return quoteMdxIdentifier(dimension.getName()) + "." + quoteMdxIdentifier(hierarchy.getName()) + "." + 
      memberValue;
    }
  }
}
