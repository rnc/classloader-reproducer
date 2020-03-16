
import com.redhat.cpaas.pipeline.model.Errata

def call(kerberosKeytab, principal, product, release, errata, projectName,
    branch, gitlabUrl, groupName, credentialsId) {
}

def createAdvisories(kerberosKeytab, principal, product, release, errata) {
    Map<String, Integer> advisories = [:]

//    System.out.println ("### In script, Errata OBJECT classloader " + errata.class.getClassLoader() + " and type " + errata.class.toString())
//    System.out.println ("###*** Errata classloader " + Errata.class.getClassLoader())
//    System.out.println ("###*** Errata created new, classloader " + new Errata().class.getClassLoader())

    return new Errata()
}
