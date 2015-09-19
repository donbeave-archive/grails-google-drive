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
package org.grails.plugin.google.drive

import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.Permission
import org.springframework.web.multipart.MultipartFile

import javax.annotation.PostConstruct

/**
 * @author <a href='mailto:donbeave@gmail.com'>Alexey Zhokhov</a>
 */
class GoogleDriveService {

    def grailsApplication

    GoogleDrive drive

    @PostConstruct
    def init() {
        def config = grailsApplication.config.google.drive


        if (config.enabled) {
            switch (config.credentials.type) {
                case 'web':
                    drive = getWebConfiguredDrive(config.credentials.filePath)
                    break;
                case 'service':
                    drive = getServiceConfiguredDrive(config.credentials.filePath)
                    break;
            }
        }
    }

    private def getWebConfiguredDrive(configFilePath) {
        def config = grailsApplication.config.google.drive
        def key = null
        def secret = null
        def jsonConfig = null

        try {
            def jsonFile = new java.io.File(configFilePath)
            jsonConfig = JSONConfigLoader.getConfigFromJSON('web', jsonFile)
        } catch (IOException e) {
            log.error e.message
        }

        key = jsonConfig?.key ?: config.key
        if (!key) {
            throw new RuntimeException('Google Drive API key is not specified')
        }

        secret = jsonConfig?.secret ?: config.secret
        if (!secret) {
            throw new RuntimeException('Google Drive API secret is not specified')
        }

        return new GoogleDrive(key, secret, config.credentials.path, 'grails')
    }

    private def getServiceConfiguredDrive(configFilePath) {
        def config = grailsApplication.config.google.drive
        def key = null
        def secret = null
        def jsonConfig = null

        try {
            def jsonFile = new java.io.File(configFilePath)
            jsonConfig = JSONConfigLoader.getConfigFromJSON('service', jsonFile)
        } catch (IOException e) {
            log.error e.message
        }

        key = jsonConfig?.email ?: config.key
        if (!key) {
            throw new RuntimeException('Google Drive API email is not specified for service account')
        }

        secret = jsonConfig?.privateKey ?: config.secret
        if (!secret) {
            throw new RuntimeException('Google Drive API private key is not specified for service account')
        }

        return new GoogleDrive(key, secret, config.scopes)
    }

    List<File> list() {
        def files = drive.native.files().list().execute()

        files.getItems()
    }

    List<File> foldersList() {
        GoogleDrive.foldersList(drive.native).getItems()
    }

    File uploadFile(java.io.File file, String parentFolderName = null) {
        drive.uploadFile(file, parentFolderName)
    }

    File uploadFile(MultipartFile multipartFile, String parentFolderName = null) {
        drive.uploadFile(multipartFile, parentFolderName)
    }

    File makeDirectory(String name) {
        GoogleDrive.insertFolder(drive.native, name)
    }

    def insertPermission(String fileId, String role, String type) {
        def permission = new Permission()
                .setRole(role)
                .setType(type)

        drive.native.permissions().insert(fileId, permission).execute()
    }

    def remove(String id) {
        drive.native.files().delete(id).execute()
    }

}
