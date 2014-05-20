/*
 * Copyright 2013 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * @author <a href='mailto:donbeave@gmail.com'>Alexey Zhokhov</a>
 */
class GoogleDriveGrailsPlugin {

    def version = '0.1-SNAPSHOT'
    def grailsVersion = '2.0 > *'

    def pluginExcludes = [
            'grails-app/views/error.gsp'
    ]

    def title = 'Google Drive Plugin'
    def author = 'Alexey Zhokhov'
    def authorEmail = 'donbeave@gmail.com'
    def description = '''\
Brief summary/description of the plugin.
'''

    def documentation = "http://grails.org/plugin/google-drive"

    def license = 'APACHE'

    def developers = [[name: 'Alexey Zhokhov', email: 'donbeave@gmail.com']]

    def issueManagement = [system: 'GITHUB',
                           url   : 'https://github.com/donbeave/grails-google-drive/issues']
    def scm = [url: 'https://github.com/donbeave/grails-google-drive']

}
