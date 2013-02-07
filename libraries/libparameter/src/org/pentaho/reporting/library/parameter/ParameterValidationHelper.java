package org.pentaho.reporting.library.parameter;

import java.util.ArrayList;

public class ParameterValidationHelper
{
  public static String[] toMessageList(final ParameterValidationResult result)
  {
    final ArrayList<String> l = new ArrayList<String>();
    final ValidationMessage[] messages1 = result.getGlobalErrors();
    for (int i = 0; i < messages1.length; i++)
    {
      final ValidationMessage message = messages1[i];
      l.add(message.getMessage());
    }
    final String[] names = result.getErrorParameterNames();
    for (int i = 0; i < names.length; i++)
    {
      final String name = names[i];
      final ValidationMessage[] messages2 = result.getErrors(name);
      final ValidationMessage message = messages2[i];
      l.add(name + " => " + message.getMessage());
    }
    return l.toArray(new String[l.size()]);
  }
}
