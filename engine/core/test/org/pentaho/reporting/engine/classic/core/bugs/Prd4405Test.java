package org.pentaho.reporting.engine.classic.core.bugs;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.libraries.base.util.MemoryByteArrayOutputStream;

public class Prd4405Test extends TestCase
{
  public Prd4405Test()
  {
  }

  public void setUp() throws Exception
  {
    ClassicEngineBoot.getInstance().start();
  }

  protected MasterReport postProcess(final MasterReport originalReport) throws Exception
  {
    final byte[] bytes = serializeReportObject(originalReport);
    return deserializeReportObject(bytes);
  }

  private byte[] serializeReportObject(final MasterReport report) throws IOException
  {
    // we don't test whether our demo models are serializable :)
    // clear all report properties, which may cause trouble ...
    final MemoryByteArrayOutputStream bo = new MemoryByteArrayOutputStream();
    final ObjectOutputStream oout = new ObjectOutputStream(bo);
    oout.writeObject(report);
    oout.close();
    return bo.toByteArray();
  }

  private MasterReport deserializeReportObject(final byte[] data) throws IOException, ClassNotFoundException
  {
    final ByteArrayInputStream bin = new ByteArrayInputStream(data);
    final ObjectInputStream oin = new ObjectInputStream(bin);
    final MasterReport report2 = (MasterReport) oin.readObject();
    assertNotNull(report2);
    return report2;
  }

  public void testConfiguration() throws Exception
  {
    final MasterReport report = postProcess(new MasterReport());
    final String key = getClass().getName() + ";" + System.identityHashCode(report);
    final String value = "" + System.identityHashCode(report);
    ClassicEngineBoot.getInstance().getEditableConfig().setConfigProperty
        (key, value);
    assertEquals(value, report.getConfiguration().getConfigProperty(key));
  }
}
