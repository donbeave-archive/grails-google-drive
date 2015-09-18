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
package org.grails.plugin.google.drive;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.SecurityUtils;
import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.ParentReference;
import org.apache.log4j.Logger;
import org.apache.tika.Tika;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href='mailto:donbeave@gmail.com'>Alexey Zhokhov</a>
 */
public class GoogleDrive {

    /**
     * Define a global instance of the HTTP transport.
     */
    public static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    /**
     * Define a global instance of the JSON factory.
     */
    public static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    static final Logger LOG = Logger.getLogger(GoogleDrive.class);

    static final String FOLDER_TYPE = "application/vnd.google-apps.folder";
    static final String FOLDERS_QUERY = "mimeType='" + FOLDER_TYPE + "' and trashed=false";

    private static final Tika TIKA = new Tika();

    static class ProgressListener implements MediaHttpUploaderProgressListener {

        public void progressChanged(MediaHttpUploader uploader) throws IOException {
            switch (uploader.getUploadState()) {
                case INITIATION_STARTED:
                    LOG.debug("Initiation Started");
                    break;
                case INITIATION_COMPLETE:
                    LOG.debug("Initiation Completed");
                    break;
                case MEDIA_IN_PROGRESS:
                    LOG.debug("Upload in progress");
                    LOG.debug("Upload percentage: ${uploader.getProgress()}");
                    break;
                case MEDIA_COMPLETE:
                    LOG.debug("Upload Completed!");
                    break;
                case NOT_STARTED:
                    LOG.debug("Upload Not Started!");
                    break;
            }
        }

    }

    static Drive init(String clientId, String clientSecret, String credentialsPath, String credentialStore)
            throws IOException, GeneralSecurityException {
        // Set up OAuth 2.0 access of protected resources
        // using the refresh and access tokens, automatically
        // refreshing the access token when it expires
        Credential credential = authorize(clientId, clientSecret, credentialsPath, credentialStore, HTTP_TRANSPORT,
                JSON_FACTORY);

        return new Drive(HTTP_TRANSPORT, JSON_FACTORY, credential);
    }

    static Drive init(String emailAddress, String privateKey, List<String> scopes) throws IOException, GeneralSecurityException  {

        GoogleCredential credential = new GoogleCredential.Builder()
                .setTransport(HTTP_TRANSPORT)
                .setJsonFactory(JSON_FACTORY)
                .setServiceAccountId(emailAddress)
                .setServiceAccountPrivateKey(PrivateKeyUtil.readPrivateKey(privateKey))
                .setServiceAccountScopes(scopes)
                .build();

        return new Drive(HTTP_TRANSPORT, JSON_FACTORY, credential);
    }

    static Credential authorize(String clientId, String clientSecret, String credentialsPath, String credentialStore,
                                HttpTransport httpTransport, JsonFactory jsonFactory) throws IOException {
        GoogleClientSecrets.Details installedDetails = new GoogleClientSecrets.Details();
        installedDetails.setClientId(clientId);
        installedDetails.setClientSecret(clientSecret);

        GoogleClientSecrets clientSecrets = new GoogleClientSecrets();
        clientSecrets.setInstalled(installedDetails);

        FileDataStoreFactory fileDataStoreFactory = new FileDataStoreFactory(new java.io.File(credentialsPath));
        DataStore<StoredCredential> datastore = fileDataStoreFactory.getDataStore(credentialStore);

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, jsonFactory,
                clientSecrets, Collections.singleton(DriveScopes.DRIVE_FILE))
                .setCredentialDataStore(datastore)
                .build();

        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }

    static File insertFile(Drive drive, File metaData) throws IOException {
        return insertFile(drive, metaData, null);
    }

    static File insertFile(Drive drive, File metaData, String type) throws IOException {
        return insertFile(drive, metaData, type, (java.io.File) null);
    }

    static File insertFile(Drive drive, File metaData, String type, java.io.File media) throws IOException {
        FileContent mediaContent = null;

        if (media != null) {
            type = type != null ? type : TIKA.detect(media);

            mediaContent = new FileContent(type, media);
        }

        Drive.Files.Insert request = mediaContent != null ? drive.files().insert(metaData, mediaContent) :
                drive.files().insert(metaData);
        if (mediaContent != null) {
            request.getMediaHttpUploader().setProgressListener(new ProgressListener());
        }
        return request.execute();
    }

    static File insertFile(Drive drive, File metaData, String type, MultipartFile media) throws IOException {
        if (media == null) {
            throw new IllegalArgumentException("Media can't be null");
        }

        type = type != null ? type : media.getContentType();

        ByteArrayContent mediaContent = new ByteArrayContent(type, media.getBytes());

        Drive.Files.Insert request = drive.files().insert(metaData, mediaContent);
        request.getMediaHttpUploader().setProgressListener(new ProgressListener());
        return request.execute();
    }

    static File insertFolder(Drive drive, String folderName) throws IOException {
        File folder = new File();
        folder.setTitle(folderName);
        folder.setMimeType(FOLDER_TYPE);

        return insertFile(drive, folder);
    }

    static FileList foldersList(Drive drive) throws IOException {
        Drive.Files.List request = drive.files().list().setQ(FOLDERS_QUERY);
        return request.execute();
    }

    static File getFolderByName(Drive drive, String folderName) throws IOException {
        for (File folder : foldersList(drive).getItems()) {
            if (folder.getTitle().equals(folderName))
                return folder;
        }
        return null;
    }

    static String getFolderId(Drive drive, String folderName) throws IOException {
        File folder = getFolderByName(drive, folderName);
        if (folder != null)
            return folder.getId();
        return null;
    }

    public GoogleDrive(String clientId, String clientSecret, String credentialsPath, String credentialStore)
            throws IOException, GeneralSecurityException {
        drive = init(clientId, clientSecret, credentialsPath, credentialStore);
    }

    public GoogleDrive(String emailAddress, String privateKey, List<String> scopes) throws IOException, GeneralSecurityException {
        drive = init(emailAddress, privateKey, scopes);
    }

    private Drive drive;

    public File uploadFile(java.io.File file) throws IOException {
        return uploadFile(file, null);
    }

    public File uploadFile(java.io.File file, String rootFolderName) throws IOException {
        String folderId = rootFolderName != null ? getFolderId(drive, rootFolderName) : null;

        if (folderId == null)
            folderId = insertFolder(drive, rootFolderName).getId();

        File fileMetadata = new File();
        fileMetadata.setTitle(file.getName());

        // Set the parent folder.
        if (folderId != null)
            fileMetadata.setParents(Arrays.asList(new ParentReference().setId(folderId)));

        return insertFile(drive, fileMetadata, null, file);
    }

    public File uploadFile(MultipartFile multipartFile, String rootFolderName) throws IOException {
        String folderId = rootFolderName != null ? getFolderId(drive, rootFolderName) : null;

        if (folderId == null)
            folderId = insertFolder(drive, rootFolderName).getId();

        File fileMetadata = new File();
        fileMetadata.setTitle(multipartFile.getOriginalFilename());
        fileMetadata.setDescription(multipartFile.getOriginalFilename());
        fileMetadata.setMimeType(multipartFile.getContentType());

        // Set the parent folder.
        if (folderId != null)
            fileMetadata.setParents(Arrays.asList(new ParentReference().setId(folderId)));

        return insertFile(drive, fileMetadata, null, multipartFile);
    }

    public Drive getNative() {
        return drive;
    }

}
