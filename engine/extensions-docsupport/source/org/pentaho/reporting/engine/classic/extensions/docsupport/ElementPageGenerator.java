package org.pentaho.reporting.engine.classic.extensions.docsupport;

import java.util.Arrays;
import java.util.Locale;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementTypeRegistry;
import org.pentaho.reporting.engine.classic.core.metadata.GroupedMetaDataComparator;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

public class ElementPageGenerator
{
  public static void main(String[] args)
  {
    ClassicEngineBoot.getInstance().start();
    final ElementMetaData[] datas = ElementTypeRegistry.getInstance().getAllElementTypes();
    final GroupedMetaDataComparator comp = new GroupedMetaDataComparator();
    Arrays.sort(datas, comp);

    final Locale locale = Locale.getDefault();
    String group = null;
    for (int i = 0; i < datas.length; i++)
    {
      final ElementMetaData data = datas[i];
      if (data.isHidden())
      {
        continue;
      }
      if (ObjectUtilities.equal(data.getGrouping(locale), group) == false)
      {
        group = data.getGrouping(locale);
        System.out.println();
        System.out.println("h2. " + group);
      }
      printElementInfo(locale, data);
    }

    System.out.println();
    System.out.println("h2. structural-elements");
    printElementInfo(locale, ElementTypeRegistry.getInstance().getElementType("master-report"));
    printElementInfo(locale, ElementTypeRegistry.getInstance().getElementType("relational-group"));

  }

  private static void printElementInfo(final Locale locale, final ElementMetaData data)
  {
    System.out.print("* ");
    if (data.isDeprecated())
    {
      System.out.print("Deprecated - ");
    }
    if (data.isPreferred())
    {
      System.out.print("*");
    }
    System.out.print("[" + data.getDisplayName(locale) + "|" + data.getName() + "]");
    if (data.isPreferred())
    {
      System.out.print("*");
    }
    System.out.println();
  }
}
