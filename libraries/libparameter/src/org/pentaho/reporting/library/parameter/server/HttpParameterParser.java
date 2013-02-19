package org.pentaho.reporting.library.parameter.server;

import java.util.Map;

import org.pentaho.reporting.library.parameter.ParameterContext;
import org.pentaho.reporting.library.parameter.ParameterDefinition;
import org.pentaho.reporting.library.parameter.ParameterException;
import org.pentaho.reporting.library.parameter.ParameterValidationException;
import org.pentaho.reporting.library.parameter.ParameterValidationResult;
import org.pentaho.reporting.library.parameter.ParameterValidator;

/**
 * Takes a map of strings and string-arrays and converts them into real objects.
 */
public interface HttpParameterParser extends ParameterValidator
{
  public void setUntrustedServletValues(Map<String, String[]> parameters);
  public void setUntrustedRawValues(Map<String, Object> parameters);

  /**
   * Validates the untrusted parameters by injecting these parameters safely into the given context.
   *
   * @param result              the validation result, null to create a new one.
   * @param parameterDefinition the parameter definitions.
   * @param parameterContext    the parameter context
   * @return
   * @throws ParameterValidationException
   * @throws ParameterException
   */
  public ParameterValidationResult validate(final ParameterValidationResult result,
                                            final ParameterDefinition parameterDefinition,
                                            final ParameterContext parameterContext)
      throws ParameterValidationException, ParameterException;

}
