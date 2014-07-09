**Plugin de dépot Moodle depuis nuxeo user workspace: Repository** 
==================================================================


Description 
===========

Ce projet s'incrit dans le cadre du developpement de plugin de repository moodle.

IL permet à l'utilisateur Moodle de recupérer des resources depuis son espace personnel [**Nuxeo**] [1].

Les ressources récupérées sont enregistrées dans  [**Moodle**] [2], sous forme d'activités ou ressources  [**Moodle**] [2].

C'est un plugin Moodle, donc il respecte la structure de  [**plugin de depot Moodle**] [3].

L'accés à la plateforme Nuxeo se fait par authentification SSO.


le fichier locallib.php : contient la classe metier. elle contient aussi des méthodes de traitements et de génération de client SSO

	
Installation 
============

### 	Préréquis :
Pour utiliser ce plugin nous avons besoin de :

*	[**Nuxeo**] [1] supportant l'authentification Portal SSO 

*	[**Moodle**] [2] 

*	un utilisateur dans ces deux plateforme sous le même username

###	Déploiement

 
1. recuperez le [**client automation php (avec SSO)**] [4] 

2. copiez le dossier **NuxeoAutomationClient**  dans le repertoir {moodleracine}/lib/nuxeo 

3. Copiez le dossier **nuxeouworkspace** sur dans le repertoire **[moodleRacine]/repository/** de moodle 
	



### 	Configuration: 

La clé secrete sso doit étre renseignée dans le fichier de configuration
moodle : $CFG->nuxeokey = "{secretkey}".

Un fichier de configuration xml contenu dans /db/settings.xml permet de paramettrer les types de document et de copie autorisés.


[1]: http://www.nuxeo.com/
[2]: https://moodle.org/
[3]: http://docs.moodle.org/dev/Repository_plugins
[4]: https://github.com/nuxeo/nuxeo-moodle-plugin/tree/master/moodle-plugin/moodle-plugin-automation
 
 
			 
	
