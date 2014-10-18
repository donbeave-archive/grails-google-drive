grails.project.class.dir = 'target/classes'
grails.project.test.class.dir = 'target/test-classes'
grails.project.test.reports.dir = 'target/test-reports'

grails.project.dependency.resolver = 'maven'
grails.project.dependency.resolution = {
    inherits('global') {

    }
    log 'warn'
    repositories {
        grailsCentral()
        mavenLocal()
        mavenCentral()
    }
    dependencies {
        compile 'com.google.apis:google-api-services-drive:v2-rev123-1.18.0-rc', {
            excludes  'httpclient', 'junit'
        }
        compile 'com.google.http-client:google-http-client-jackson2:1.18.0-rc', {
            excludes 'httpclient', 'httpcore', 'commons-logging'
        }
        compile 'com.google.oauth-client:google-oauth-client-jetty:1.18.0-rc', {
            excludes 'httpclient', 'junit'
        }

        compile 'org.apache.tika:tika-core:1.5', {
            excludes 'xercesImpl', 'xmlParserAPIs', 'xml-apis', 'groovy'
        }
    }

    plugins {
        build(':release:3.0.1', ':rest-client-builder:1.0.3') {
            export = false
        }
    }
}
