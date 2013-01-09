package org.pentaho.reporting.engine.classic.core.wizard;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.MetaAttributeNames;
import org.pentaho.reporting.engine.classic.core.designtime.DefaultDesignTimeContext;
import org.pentaho.reporting.engine.classic.core.parameters.DefaultParameterDefinition;
import org.pentaho.reporting.engine.classic.core.parameters.PlainParameter;

public class FieldMappingTest extends TestCase
{
  public FieldMappingTest()
  {
  }

  public FieldMappingTest(final String name)
  {
    super(name);
  }

  protected void setUp() throws Exception
  {
    ClassicEngineBoot.getInstance().start();
  }

  public void testParameterMapping()
  {
    final PlainParameter parameter = new PlainParameter("P", Number.class);
    final DefaultParameterDefinition defaultParameterDefinition = new DefaultParameterDefinition();
    defaultParameterDefinition.addParameterDefinition(parameter);
    
    final MasterReport report = new MasterReport();
    report.setParameterDefinition(defaultParameterDefinition);

    final DefaultDesignTimeContext context = new DefaultDesignTimeContext(report);
    final DataSchema dataSchema = context.getDataSchemaModel().getDataSchema();
    final DataAttributes attributes = dataSchema.getAttributes("P");
    final Object o = attributes.getMetaAttribute
        (MetaAttributeNames.Core.NAMESPACE, MetaAttributeNames.Core.TYPE, Class.class, new DefaultDataAttributeContext());
    assertEquals("Number.class expected", Number.class, o);
  }
}
