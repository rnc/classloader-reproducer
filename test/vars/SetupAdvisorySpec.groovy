import com.homeaway.devtools.jenkins.testing.InvalidlyNamedScriptWrapper
import com.homeaway.devtools.jenkins.testing.JenkinsPipelineSpecification
import com.redhat.cpaas.pipeline.model.Errata

import javax.lang.model.SourceVersion
import java.nio.charset.Charset

class SetupAdvisorySpec extends JenkinsPipelineSpecification {

    def setupAdvisory
    def product
    def release
    def errata = new Errata()
    def schemaUtils
    def logMock

    def "Validate new instance"() {
        setup:
        setupAdvisory = loadPipelineScriptForTest("../../vars/setupAdvisory.groovy")
        when:
            System.out.println("### setupAdvisory classloader " + setupAdvisory.class.getClassLoader())
            System.out.println("### this classloader " + this.class.getClassLoader())
            System.out.println("### Errata classloader " + errata.class.getClassLoader())
            def advisories = setupAdvisory.createAdvisories(null, null, product, release, errata)
        then:
            advisories.class.getClassLoader() != setupAdvisory.class.getClassLoader()
            advisories.class.getCanonicalName().equals(errata.class.getCanonicalName())
            def reflected = Class.forName("com.redhat.cpaas.pipeline.model.Errata", true, setupAdvisory.class.getClassLoader()).newInstance()
            System.out.println("### reflected " + reflected.class + " and returned " + advisories.class)
            advisories.class.getClassLoader() == errata.class.getClassLoader()
            advisories.class == reflected.class
            advisories.class == errata.class
    }


    def "Validate forName"() {
        setup:
            setupAdvisory = loadPipelineScriptForTest("../../vars/setupAdvisory.groovy")
        when:
            System.out.println("### setupAdvisory classloader " + setupAdvisory.class.getClassLoader())
            System.out.println("### this classloader " + this.class.getClassLoader())
            System.out.println("### Errata classloader " + errata.class.getClassLoader())
            def advisories = setupAdvisory.createAdvisories(null, null, product, release, errata)
        then:
            advisories.class.getClassLoader() == setupAdvisory.class.getClassLoader()
            advisories.class.getClassLoader() != errata.class.getClassLoader()
            advisories.class.getCanonicalName().equals(errata.class.getCanonicalName())
           def reflected = Class.forName("com.redhat.cpaas.pipeline.model.Errata", true, setupAdvisory.class.getClassLoader()).newInstance()
            System.out.println("### reflected " + reflected.class + " and returned " + advisories.class)
            advisories.class == reflected.class
    }
    
    def "Validate with custom classloader"(){
        setup:
            setupAdvisory = loadCustomPipelineScriptForTest("../../vars/setupAdvisory.groovy")
        when:
            System.out.println("### setupAdvisory classloader " + setupAdvisory.class.getClassLoader())
            System.out.println("### this classloader " + this.class.getClassLoader())
            System.out.println("### Errata classloader " + errata.class.getClassLoader())
            def advisories = setupAdvisory.createAdvisories(null, null, product, release, errata)
        then:
            advisories.class.getClassLoader() != setupAdvisory.class.getClassLoader()
            advisories.class.getCanonicalName().equals(errata.class.getCanonicalName())
            def reflected = Class.forName("com.redhat.cpaas.pipeline.model.Errata", true, setupAdvisory.class.getClassLoader()).newInstance()
            System.out.println("### reflected " + reflected.class + " and returned " + advisories.class)
            advisories.class.getClassLoader() == errata.class.getClassLoader()
            advisories.class == reflected.class
            advisories.class == errata.class
    }


    protected Script loadCustomPipelineScriptForTest(String _path) {
        String[] path_parts = _path.split( "/" )

        String filename = path_parts[path_parts.length-1]

        String resource_path = "/"
        if( path_parts.length >= 2 ) {
            resource_path = String.join( "/", path_parts[0..path_parts.length-2] )
            resource_path = "/${resource_path}/"
        }

        GroovyClassLoader gl = new GroovyClassLoader()
        File scriptFile = null

        for ( String c : generateScriptClasspath(resource_path) ) {
            gl.addClasspath(c)
            scriptFile = new File(c,"${filename}")
            if ( scriptFile.exists() ) {
                break
            }
        }
        if ( scriptFile == null ) {
            throw new IOException("Unable to locate " + "${filename}")
        }

        Class<Script> script_class = gl.parseClass(scriptFile.getText(Charset.defaultCharset().displayName()))
        Script script = script_class.newInstance()
        addPipelineMocksToObjects( script )

        if( SourceVersion.isName( script_class.getSimpleName() ) ) {
            return script
        } else {
            return new InvalidlyNamedScriptWrapper( script )
        }
    }
}
