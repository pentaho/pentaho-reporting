package org.pentaho.reporting.libraries.parameter;

import junit.framework.TestCase;
import org.pentaho.reporting.libraries.parameter.defaults.DefaultListParameter;
import org.pentaho.reporting.libraries.parameter.defaults.DefaultParameterContext;
import org.pentaho.reporting.libraries.parameter.defaults.DefaultParameterDataTable;
import org.pentaho.reporting.libraries.parameter.defaults.DefaultParameterDefinition;
import org.pentaho.reporting.libraries.parameter.defaults.DefaultParameterQuery;
import org.pentaho.reporting.libraries.parameter.validation.DefaultParameterValidationResult;
import org.pentaho.reporting.libraries.parameter.validation.DefaultParameterValidator;

public class Prd3174Test extends TestCase
{
  public Prd3174Test()
  {
  }

  public void setUp() throws Exception
  {
    LibParameterBoot.getInstance().start();
  }

  public void testParameterValidation() throws Exception
  {
    final DefaultParameterDataTable model = new DefaultParameterDataTable("key", "value");
    model.setValue("key", 0, "key-value");
    model.setValue("value", 0, "value-entry");
    model.addRow("K1", "V1");
    model.addRow("K2", "V2");
    model.addRow("K3", "V3");

    final DefaultParameterQuery query = new DefaultParameterQuery();
    query.setQuery("query", model);

    final DefaultListParameter listParameter = new DefaultListParameter("parameter", String.class, "query", "key");
    listParameter.setTextColumn("value");
    listParameter.setAllowMultiSelection(true);
    listParameter.setStrictValueCheck(false);

    final DefaultParameterDefinition parameterDefinition = new DefaultParameterDefinition();
    parameterDefinition.addParameter(listParameter);

    final DefaultParameterContext paramContext = new DefaultParameterContext();
    paramContext.setDataFactory(query);
    paramContext.getParameterData().put("parameter", new Object[]{"K1", new Integer(1)});

    final DefaultParameterValidator validator = new DefaultParameterValidator();
    ParameterValidationResult validate = validator.validate
        (new DefaultParameterValidationResult(), parameterDefinition, paramContext);

    assertFalse(validate.isParameterSetValid());

    paramContext.getParameterData().put("parameter", new Object[]{"K1", "K2"});
    validate = validator.validate(null, parameterDefinition, paramContext);
    assertTrue(validate.isParameterSetValid());

    paramContext.getParameterData().put("parameter", new Object[]{"K1", "K2", "K5"});
    validate = validator.validate(null, parameterDefinition, paramContext);
    assertTrue(validate.isParameterSetValid());
  }


  public void testStrictParameterValidation() throws Exception
  {
    final DefaultParameterDataTable model = new DefaultParameterDataTable("key", "value");
    model.setValue("key", 0, "key-value");
    model.setValue("value", 0, "value-entry");
    model.addRow("K1", "V1");
    model.addRow("K2", "V2");
    model.addRow("K3", "V3");

    final DefaultParameterQuery query = new DefaultParameterQuery();
    query.setQuery("query", model);

    final DefaultListParameter listParameter = new DefaultListParameter("parameter", String.class, "query", "key");
    listParameter.setTextColumn("value");
    listParameter.setAllowMultiSelection(true);
    listParameter.setStrictValueCheck(true);

    final DefaultParameterDefinition parameterDefinition = new DefaultParameterDefinition();
    parameterDefinition.addParameter(listParameter);

    final DefaultParameterContext paramContext = new DefaultParameterContext();
    paramContext.setDataFactory(query);
    paramContext.getParameterData().put("parameter", new Object[]{"K1", new Integer(1)});

    final DefaultParameterValidator validator = new DefaultParameterValidator();
    ParameterValidationResult validate = validator.validate
        (new DefaultParameterValidationResult(), parameterDefinition, paramContext);

    assertFalse(validate.isParameterSetValid());

    paramContext.getParameterData().put("parameter", new Object[]{"K1", "K2"});
    validate = validator.validate(null, parameterDefinition, paramContext);
    assertTrue(validate.isParameterSetValid());

    paramContext.getParameterData().put("parameter", new Object[]{"K1", "K2", "K5"});
    validate = validator.validate(null, parameterDefinition, paramContext);
    assertFalse(validate.isParameterSetValid());

  }
}
