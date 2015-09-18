package google.drive

import grails.test.spock.IntegrationSpec
import org.grails.plugin.google.drive.GoogleDriveService

class GoogleDriveServiceIntegrationSpec extends IntegrationSpec {

    def googleDriveService

    def setup() {
    }

    def cleanup() {
    }

    void "test configuration"() {
        setup:
        def list

        when:
        list = googleDriveService.list()

        then:
        list.size() > 0
    }
}
