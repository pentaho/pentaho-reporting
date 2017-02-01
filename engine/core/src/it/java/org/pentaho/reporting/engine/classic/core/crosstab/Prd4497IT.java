package org.pentaho.reporting.engine.classic.core.crosstab;

import junit.framework.TestCase;
import org.junit.Assert;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;

public class Prd4497IT extends TestCase {
  public Prd4497IT() {
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testLayout() throws Exception {
    // this report should generate only two pages of content. With the bug still active, it generates 3.
    MasterReport report = DebugReportRunner.parseGoldenSampleReport( "Prd-4497.prpt" );
    LogicalPageBox pageBox = DebugReportRunner.layoutPageStrict( report, 2, 0 );
    Assert.assertEquals( StrictGeomUtility.toInternalValue( 784 ), pageBox.getPageEnd() );
    RenderNode[] elementsByNodeType = MatchFactory.findElementsByNodeType( pageBox, LayoutNodeTypes.TYPE_BOX_TABLE );
    Assert.assertEquals( 1, elementsByNodeType.length );
    RenderBox box = (RenderBox) elementsByNodeType[0];

    // ModelPrinter.INSTANCE.print(box);
    RenderNode lastChild = box.getLastChild();

    Assert.assertEquals( StrictGeomUtility.toInternalValue( 804 ), lastChild.getY2() );
    Assert.assertEquals( StrictGeomUtility.toInternalValue( 800 ), box.getCachedHeight() );
    Assert.assertEquals( StrictGeomUtility.toInternalValue( 804 ), box.getHeight() );
  }
}
