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
google.drive.enabled = true
google.drive.credentials.path = System.getProperty('catalina.base') ?
        "${System.getProperty('catalina.base')}/data/oauth-credentials" :
        "${System.getProperty('user.home')}/.oauth-credentials";

google.drive.credentials.type='service'
google.drive.credentials.filePath='/Users/dstieglitz/Grails Google Drive Plugin-7b32fc08891a.json'
google.drive.scopes = ['https://www.googleapis.com/auth/drive']