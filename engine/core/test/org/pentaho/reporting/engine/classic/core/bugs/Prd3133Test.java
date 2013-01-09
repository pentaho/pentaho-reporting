package org.pentaho.reporting.engine.classic.core.bugs;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ItemBand;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.PageFooter;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.NumberFieldElementFactory;
import org.pentaho.reporting.engine.classic.core.function.PageItemSumFunction;
import org.pentaho.reporting.engine.classic.core.layout.model.BlockRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableText;
import org.pentaho.reporting.engine.classic.core.layout.output.LogicalPageKey;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugRenderer;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportProcessor;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportValidator;
import org.pentaho.reporting.engine.classic.core.util.TypedTableModel;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

public class Prd3133Test extends TestCase
{
  private class Prd3133ReportValidator implements DebugReportValidator
  {
    public void processPageContent(final LogicalPageKey logicalPageKey, final LogicalPageBox logicalPage)
    {
      final BlockRenderBox footerArea = logicalPage.getFooterArea();
      final RenderNode p1 = getElementByName(footerArea, "P2");
      assertNotNull(p1);
      assertTrue(p1 instanceof ParagraphRenderBox);
      final ParagraphRenderBox p = (ParagraphRenderBox) p1;
      final RenderNode firstChild = p.getPool().getFirstChild();
      assertTrue(firstChild instanceof RenderableText);
      final RenderableText text = (RenderableText) firstChild;
      final int val = Integer.parseInt(text.getRawText());
      assertTrue("Value " + val + " is either 15 or 10", val == 15 || val == 10);
    }
  }

  public Prd3133Test()
  {
  }

  public void setUp() throws Exception
  {
    ClassicEngineBoot.getInstance().start();
  }

  public void testPageSum() throws Exception
  {
    final MasterReport report = createReport();

    final DebugRenderer renderer = new DebugRenderer();
    renderer.setValidator(new Prd3133ReportValidator());
    final DebugReportProcessor reportProcessor = new DebugReportProcessor(report, renderer);
    reportProcessor.processReport();

//    PreviewDialog d = new PreviewDialog(report);
//    d.setModal(true);
//    d.setVisible(true);
  }


  private MasterReport createReport()
  {
    final TypedTableModel model = new TypedTableModel();
    model.addColumn("key", String.class);
    model.addColumn("value", Integer.class);
    for (int i = 0; i < 1000; i += 1)
    {
      model.addRow(new Object[]{"K1", Integer.valueOf(1)});
    }

    final PageItemSumFunction itemSumFunction = new PageItemSumFunction();
    itemSumFunction.setField("value");
    itemSumFunction.setName("fn");

    final MasterReport report = new MasterReport();
    report.setQuery("test");
    report.setDataFactory(new TableDataFactory(report.getQuery(), model));
    report.addExpression(itemSumFunction);
    final ItemBand itemBand = report.getItemBand();
    itemBand.addElement(createNumberElement(0, "value", "I1"));
    itemBand.addElement(createNumberElement(100, "fn", "I2"));

    final PageFooter pageFooter = report.getPageFooter();
    pageFooter.addElement(createNumberElement(0, "value", "P1"));
    pageFooter.addElement(createNumberElement(100, "fn", "P2"));
    return report;
  }


  /**
   * Creates a new TextElement containing a numeric filter structure.
   *
   * @param field the field in the datamodel to retrieve values from.
   * @return a report element for displaying <code>Number</code> objects.
   * @throws NullPointerException     if bounds, name or function are null
   * @throws IllegalArgumentException if the given alignment is invalid
   */
  public static Element createNumberElement(final int x,
                                            final String field,
                                            final String name)
  {

    final NumberFieldElementFactory factory = new NumberFieldElementFactory();
    factory.setX(new Float(x));
    factory.setY(new Float(0));
    factory.setMinimumWidth(new Float(100));
    factory.setMinimumHeight(new Float(40));
    factory.setName(name);

    factory.setFontName("Dialog");
    factory.setFontSize(new Integer(12));
    factory.setBold(false);
    factory.setItalic(false);
    factory.setUnderline(false);
    factory.setStrikethrough(false);
    factory.setEmbedFont(false);
    factory.setFieldname(field);
    return factory.createElement();
  }


  public RenderNode getElementByName(final RenderNode node, final String name)
  {
    if (ObjectUtilities.equal(node.getName(), name))
    {
      return node;
    }

    if (node instanceof RenderBox)
    {
      final RenderBox box = (RenderBox) node;
      RenderNode child = box.getFirstChild();
      while (child != null)
      {
        final RenderNode result = getElementByName(child, name);
        if (result != null)
        {
          return result;
        }
        child = child.getNext();
      }
    }
    return null;
  }


}
