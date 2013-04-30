package org.pentaho.plugin.jfreereport.reportcharts;

import junit.framework.TestCase;
import org.jfree.data.general.DefaultPieDataset;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugExpressionRuntime;

public class Prd4442Test extends TestCase
{
  public Prd4442Test()
  {
  }

  protected void setUp() throws Exception
  {
    ClassicEngineBoot.getInstance().start();
  }

  public void testPieTooltip() throws Exception
  {
    DebugExpressionRuntime runtime = new DebugExpressionRuntime();
    FormulaPieTooltipGenerator gen = new FormulaPieTooltipGenerator(runtime, "=[chart::item]");

    DefaultPieDataset dataSet = new DefaultPieDataset();
    dataSet.setValue("Key-1", 5);
    dataSet.setValue("Key-2", 7);
    dataSet.setValue("Key-3", 10);
    assertEquals ("5.0", gen.generateToolTip(dataSet, "Key-1"));
    assertEquals ("7.0", gen.generateToolTip(dataSet, "Key-2"));
    assertEquals ("10.0", gen.generateToolTip(dataSet, "Key-3"));


  }
}
