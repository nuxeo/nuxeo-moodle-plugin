**Nuxeo Automation Client PHP: avec Portal SSO**
==============================================



Description 
===========

Ce client automation est une librairie basée sur [**php-automation-client**] [4] de nuxeo .
Son objectif est d'enrichir le php-automation-client basic pour permettre la prise en compte de l'authentification Portal SSO.


Nous avons ajouté le type d'authentification SSO. 
Il contient deux parties :

* NuxeoAutomationClient : Dans le fichier NuxeoAutomationAPI nous avons:

        NuxeoPortalSSOSession : permet de créer une session SSO
        PortalSSORequestInterceptor : utilise la session SSO pour executer les requetes 

* Sample : contient un exemple d'utilisation de l'API. 
  


Installation 
============


Configuration 
=============



## About Nuxeo

Nuxeo provides a modular, extensible Java-based [open source software
platform for enterprise content management] [5] and packaged applications
for [document management] [6], [digital asset management] [7] and
[case management] [8]. Designed by developers for developers, the Nuxeo
platform offers a modern architecture, a powerful plug-in model and
extensive packaging capabilities for building content applications.

[4]: https://github.com/nuxeo/nuxeo-automation-php-client
[5]: http://www.nuxeo.com/en/products/ep
[6]: http://www.nuxeo.com/en/products/document-management
[7]: http://www.nuxeo.com/en/products/dam
[8]: http://www.nuxeo.com/en/products/case-management

More information on: <http://www.nuxeo.com/>
