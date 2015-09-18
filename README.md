grails-google-drive [![Build Status](https://travis-ci.org/donbeave/grails-google-drive.svg?branch=master)](https://travis-ci.org/donbeave/grails-google-drive)
===================

## Credentials setup

Setup credentails from Google Developer Console. This plugin supports "Web" credentials and "Service" (non-interactive) credentials. Download the JSON credentials file from the Google console and point the config attribute `google.drive.credentials.filePath` to point to the file. You should also specify the type of credentials using `google.drive.credentials.type` with values `web` or `service`.

## Scopes

Specify scopes using `google.drive.scopes` and the accepted string values provided by Google. See https://developers.google.com/drive/web/scopes




Copyright and license
---------------------

Copyright 2013-2015 Alexey Zhokhov and Dan Stieglitz under the [Apache License, Version 2.0](LICENSE). Supported by [Polusharie][polusharie].

[polusharie]: http://www.polusharie.com
