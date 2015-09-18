package org.grails.plugin.google.drive

import groovy.json.JsonSlurper

/**                               \
 * Loads the JSON credential file downloaded from the Google Developers Console
 * Created by dstieglitz on 9/18/15.
 */
class JSONConfigLoader {

    public static Map getConfigFromJSON(String type, File jsonFile) throws IOException {
        switch (type) {
            case 'service':
                return getConfigFromServiceFileJSON(jsonFile)
            case 'web':
                return getConfigFromWebApplicationJSONFile(jsonFile)
            default:
                throw new IOException("Invalid credential type specified: ${type}")
        }
    }

    public static Map getConfigFromWebApplicationJSONFile(File jsonFile) throws IOException {
        JsonSlurper slurper = new JsonSlurper()
        def object = slurper.parseText(jsonFile.text)

        return ['key'   : object.web.client_id,
                'secret': object.web.client_secret]
    }

    public static Map getConfigFromServiceFileJSON(File jsonFile) throws IOException {
        JsonSlurper slurper = new JsonSlurper()
        def object = slurper.parseText(jsonFile.text)

        return ['email'     : object.client_email,
                'privateKey': object.private_key]
    }
}
