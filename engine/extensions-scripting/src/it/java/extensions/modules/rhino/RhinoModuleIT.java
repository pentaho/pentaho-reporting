package extensions.modules.rhino;

import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionRegistry;
import org.pentaho.reporting.engine.classic.extensions.modules.rhino.RhinoModule;
import org.pentaho.reporting.libraries.base.boot.ModuleInfo;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class RhinoModuleIT {

  private static final String EXPRESSION_ID =
      "org.pentaho.reporting.engine.classic.extensions.modules.rhino.RhinoExpression";

  @Test
  public void testInitialize() throws Exception {
    ClassicEngineBoot.getInstance().start();
    RhinoModule module = new RhinoModule();
    module.initialize( null );

    assertThat( module.getDescription(), is( equalTo( "test rhino description" ) ) );
    assertThat( module.getMajorVersion(), is( equalTo( "1" ) ) );
    assertThat( module.getMinorVersion(), is( equalTo( "1" ) ) );
    assertThat( module.getPatchLevel(), is( equalTo( "0" ) ) );
    assertThat( module.getName(), is( equalTo( "test-rhino-module-name" ) ) );
    assertThat( module.getProducer(), is( equalTo( "test rhino producer" ) ) );

    ModuleInfo[] requiredModules = module.getRequiredModules();
    assertThat( requiredModules.length, is( equalTo( 1 ) ) );
    ModuleInfo requiredModule = requiredModules[0];
    assertThat( requiredModule.getModuleClass(), is( equalTo( "test.required.module.class" ) ) );
    assertThat( requiredModule.getMinorVersion(), is( equalTo( "2" ) ) );
    assertThat( requiredModule.getMajorVersion(), is( equalTo( "2" ) ) );
    assertThat( requiredModule.getPatchLevel(), is( equalTo( "1" ) ) );

    ModuleInfo[] optionalModules = module.getOptionalModules();
    assertThat( optionalModules.length, is( equalTo( 1 ) ) );
    ModuleInfo optionalModule = optionalModules[0];
    assertThat( optionalModule.getModuleClass(), is( equalTo( "test.optional.module.class" ) ) );
    assertThat( optionalModule.getMinorVersion(), is( equalTo( "1" ) ) );
    assertThat( optionalModule.getMajorVersion(), is( equalTo( "1" ) ) );
    assertThat( optionalModule.getPatchLevel(), is( equalTo( "0" ) ) );

    assertThat( ExpressionRegistry.getInstance().isExpressionRegistered( EXPRESSION_ID ), is( equalTo( true ) ) );
    ExpressionMetaData meta = ExpressionRegistry.getInstance().getExpressionMetaData( EXPRESSION_ID );
    assertThat( meta.getBundleLocation(),
        is( equalTo( "org.pentaho.reporting.engine.classic.extensions.modules.rhino.RhinoExpressionBundle" ) ) );
    assertThat( meta.getPropertyDescription( "expression" ), is( notNullValue() ) );
  }
}
