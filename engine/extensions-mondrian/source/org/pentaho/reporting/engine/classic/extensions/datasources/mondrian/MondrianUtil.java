package org.pentaho.reporting.engine.classic.extensions.datasources.mondrian;

import mondrian.olap.Dimension;
import mondrian.olap.Hierarchy;
import mondrian.olap.Member;
import mondrian.olap.Util;

public class MondrianUtil {

  public static String getUniqueMemberName(Member member) {
    String memberValue = Util.quoteMdxIdentifier(member.getName());
    while (member.getParentMember() != null) {
      memberValue = Util.quoteMdxIdentifier(member.getParentMember().getName()) + "." + memberValue;
      member = member.getParentMember();
    }
    final Hierarchy hierarchy = member.getHierarchy();
    final Dimension dimension = hierarchy.getDimension();
    if (hierarchy.getName().equals(dimension.getName())) {
      return Util.quoteMdxIdentifier(hierarchy.getName()) + "." + memberValue;
    } else {
      return Util.quoteMdxIdentifier(dimension.getName()) + "." + Util.quoteMdxIdentifier(hierarchy.getName()) + "." + 
      memberValue;
    }
  }
}
