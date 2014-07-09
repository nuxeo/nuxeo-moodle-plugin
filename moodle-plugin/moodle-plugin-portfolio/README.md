**Plugin d'export de moodle vers nuxeo: Portfolio** 
===================================================


Description 
===========

Cet projet est un plugin d'export de contenu moodle vers 
une plateforme Nuxeo.


C'est un plugin Moodle, donc il respecte la structure de  [**plugin d'export Moodle**] [3].

L'accés à la plateforme Nuxeo se fait par authentification SSO.

L'export est effectué au format zip avec un fichier de signature : '**.moodle_export**'



Installation 
============

### 	Préréquis :
Pour utiliser ce plugin nous avons besoin de :

*	[**Nuxeo**] [1] supportant l'authentification Portal SSO 

*	[**Moodle**] [2] 

*	un utilisateur dans ces deux plateforme sous le même username

*	Installez et activez le [**point d'extension pour l'import d'archive moodle**] [5] sur le site nuxeo.


###	Déploiement


1. recuperez le [**client automation php (avec SSO)**] [4]

2. copiez le dossier **NuxeoAutomationClient**  dans le repertoir {moodleracine}/lib/nuxeo 

3. Copiez le dossier **nuxeo** sur dans le repertoire [moodleRacine]/porfolio/ de moodle 
	



Configuration 
=============

La clé secrete sso doit étre renseignée dans le fichier de configuration
moodle : $CFG->nuxeokey = "{secretkey}".

L'URL Nuxeo et le dossier d'export sont configurés par l'admin du site moodle
après activation du plugin.


[1]: http://www.nuxeo.com/
[2]: https://moodle.org/
[3]: http://docs.moodle.org/dev/Portfolio_plugins
[4]: https://github.com/nuxeo/nuxeo-moodle-plugin/tree/master/moodle-plugin/moodle-plugin-automation
[5]: https://github.com/nuxeo/nuxeo-moodle-plugin/tree/master/nuxeo-plugin

