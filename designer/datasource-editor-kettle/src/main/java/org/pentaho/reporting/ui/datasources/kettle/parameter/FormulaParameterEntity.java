package org.pentaho.reporting.ui.datasources.kettle.parameter;

public class FormulaParameterEntity {
  public enum Type {
    ARGUMENT, PARAMETER
  }

  private Type type;
  private String name;
  private String value;

  public FormulaParameterEntity( final Type type,
                                 final String name,
                                 final String value ) {
    this.type = type;
    this.name = name;
    this.value = value;
  }

  public Type getType() {
    return type;
  }

  public void setName( final String name ) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setValue( final String value ) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public String toString() {
    return getName();
  }

  @Deprecated
  public static FormulaParameterEntity createArgument( String value ) {
    return new FormulaParameterEntity( Type.ARGUMENT, "Argument", value );
  }
}
