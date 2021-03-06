
println "Generating Getting Started wikis..."




def version = "1.6.5"
def home = properties."build.home"
def temp = new File( home, "TEMP" )
def upgradefile = new File( temp, "test-upgrade.sql" )
def buildxmlfile = new File( temp, "build.xml" )
def pomxmlfile = new File( temp, "pom.xml" )
def propertiesfile = new File( temp, "solidbase.properties" )
def repo = new File( properties."user.home", ".m2/repository" )




def definition1 = """--* DEFINITION
--*     SETUP "" --> "1.1"
--* /DEFINITION
"""

	
def setup = """--* SETUP "" --> "1.1"

--* SECTION "Creating SolidBase control tables"

CREATE TABLE DBVERSION
(
    SPEC VARCHAR(5),
    VERSION VARCHAR(20),
    TARGET VARCHAR(20),
    STATEMENTS DECIMAL(4) NOT NULL
);

CREATE TABLE DBVERSIONLOG
(
    TYPE VARCHAR(1) NOT NULL,
    SOURCE VARCHAR(20),
    TARGET VARCHAR(20) NOT NULL,
    STATEMENT DECIMAL(4) NOT NULL,
    STAMP TIMESTAMP NOT NULL,
    COMMAND VARCHAR(4000),
    RESULT VARCHAR(4000)
);

CREATE INDEX DBVERSIONLOG_INDEX1 ON DBVERSIONLOG ( TYPE, TARGET );

--* /SETUP
"""

	
def definition2 = """--* DEFINITION
--*     SETUP "" --> "1.1"
--*     UPGRADE "" --> "1.0.1"
--* /DEFINITION
"""

	
def upgrade1 = """--* UPGRADE "" --> "1.0.1"

--* SECTION "Creating table USERS"
CREATE TABLE USERS
(
    USER_ID INT NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    USER_USERNAME VARCHAR(26) NOT NULL,
    USER_PASSWORD VARCHAR(30) NOT NULL
);

--* /UPGRADE
"""

	
def definition3 = """--* DEFINITION
--*     SETUP "" --> "1.1"
--*     UPGRADE "" --> "1.0.1"
--*     UPGRADE "1.0.1" --> "1.0.2"
--* /DEFINITION
"""

	
def upgrade2 = """--* UPGRADE "1.0.1" --> "1.0.2"

--* SECTION "Inserting admin user"
INSERT INTO USERS ( USER_USERNAME, USER_PASSWORD ) VALUES ( 'admin', '*****' );

--* SECTION "Inserting user"
INSERT INTO USERS ( USER_USERNAME, USER_PASSWORD ) VALUES ( 'rene', '*****' );

--* /UPGRADE
"""

	
def props = """properties-version = 1.0

# add jars to the classpath
classpath.ext = derby-10.5.3.0.jar

# primary connection
connection.driver = org.apache.derby.jdbc.EmbeddedDriver
"""

	
def properties = """properties-version = 1.0

# add jars to the classpath
classpath.ext = derby-10.5.3.0.jar

# upgrade file & upgrade target
upgrade.file = test-upgrade.sql
upgrade.target = 1.0.*

# primary connection
connection.driver = org.apache.derby.jdbc.EmbeddedDriver
connection.url = jdbc:derby:testant;create=true
connection.username = app
"""

	
def buildxml = """<?xml version="1.0" encoding="UTF-8"?>

<project basedir=".">

	<taskdef resource="solidbasetasks" classpath="solidbase.jar;derby-10.5.3.0.jar" />

	<target name="upgradedb">
		<solidbase-upgrade driver="org.apache.derby.jdbc.EmbeddedDriver" url="jdbc:derby:testant;create=true" 
			username="app" password="" 
			upgradefile="test-upgrade.sql" target="1.0.*" />
	</target>

</project>
"""

	
def pomxml = """<?xml version="1.0" encoding="UTF-8"?>

<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">

	<modelVersion>4.0.0</modelVersion>
	
	<groupId>solidbase</groupId>
	<artifactId>mavenplugintest</artifactId>
	<version>1.0</version>
	<packaging>pom</packaging>

	<pluginRepositories>
		<pluginRepository>
			<id>solidbase</id>
			<name>SolidBase Repository</name>
			<layout>default</layout>
			<url>http://solidbase.googlecode.com/svn/repository</url>
			<releases><enabled>true</enabled></releases>
			<snapshots><enabled>false</enabled></snapshots>
		</pluginRepository>
	</pluginRepositories>

	<build>
		<plugins>
			<plugin>
				<groupId>solidbase</groupId>
				<artifactId>solidbase</artifactId>
				<version>${version}</version>
				<dependencies>
					<dependency>
						<groupId>org.apache.derby</groupId>
						<artifactId>derby</artifactId>
						<version>10.4.2.0</version>
					</dependency>
				</dependencies>
				<configuration>
					<driver>org.apache.derby.jdbc.EmbeddedDriver</driver>
					<url>jdbc:derby:testmaven;create=true</url>
					<username>app</username>
					<password></password>
					<upgradefile>test-upgrade.sql</upgradefile>
					<target>1.0.*</target>
				</configuration>
			</plugin>
		</plugins>
	</build>
  
</project>
"""

	
def pomxml2 = """<?xml version="1.0" encoding="UTF-8"?>

<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">

	<modelVersion>4.0.0</modelVersion>
	
	<groupId>solidbase</groupId>
	<artifactId>mavenplugintest</artifactId>
	<version>1.0</version>
	<packaging>pom</packaging>

	<pluginRepositories>
		<pluginRepository>
			<id>solidbase</id>
			<name>SolidBase Repository</name>
			<layout>default</layout>
			<url>http://solidbase.googlecode.com/svn/repository</url>
			<releases><enabled>true</enabled></releases>
			<snapshots><enabled>false</enabled></snapshots>
		</pluginRepository>
	</pluginRepositories>

	<build>
		<plugins>
			<plugin>
				<groupId>solidbase</groupId>
				<artifactId>solidbase</artifactId>
				<version>${version}</version>
				<dependencies>
					<dependency>
						<groupId>org.apache.derby</groupId>
						<artifactId>derby</artifactId>
						<version>10.4.2.0</version>
    				</dependency>
				</dependencies>
				<configuration>
					<driver>org.apache.derby.jdbc.EmbeddedDriver</driver>
					<url>jdbc:derby:testmaven;create=true</url>
					<username>app</username>
					<password></password>
					<upgradefile>test-upgrade.sql</upgradefile>
					<target>1.0.*</target>
				</configuration>
				<executions>
					<execution>
						<id>solidbase-upgrade</id>
						<phase>pre-integration-test</phase>
						<goals><goal>upgrade</goal></goals>
					</execution>
				</executions>
			</plugin>
		</plugins>
	</build>
  
</project>
"""




def wrap( String text )
{
	def result = new StringBuilder()
	def reader = new StringReader( text )
	reader.eachLine
	{
		line ->
		if( line.length() <= 80 )
			result.append( line )
		else
		{
			def words = line.split( " " )
			def col = 0;
			for( word in words )
			{
				if( word.length() + col + 1 > 80 )
				{
					result.append( " \\\n    " )
					col = 3;
				}
				if( col > 0 )
				{
					result.append( " " )
					col ++
				}
				result.append( word )
				col += word.length()
			}
		}
		result.append( "\n" )
	}
	return result.toString()
}




def execute( String command, File folder, String input, out, String display = null )
{
	if( out )
		out << wrap( "${folder}>${display != null ? display : command}\n" )
	String[] args = command.split( " " )
	process = new ProcessBuilder( args ).directory( folder ).redirectErrorStream( true ).start()
	if( input )
		process.outputStream << input
	if( out )
	{
		out << wrap( process.inputStream.text )
		out << "\n${folder}>\n"
	}
	else
		process.inputStream.text
	process.waitFor()
}




println "Generating GettingStartedCommandLineWithArguments.wiki..."

ant.delete( dir: temp )
ant.mkdir( dir: temp )
ant.copy( file: "${home}/dist/solidbase.jar", todir: "${home}/TEMP", preservelastmodified: true )
ant.copy( todir: temp, preservelastmodified: true ) { fileset( dir: "${home}/dist", includes: "derby*.jar" ) }

new File( "${home}/GettingStartedCommandLineWithArguments.wiki" ).withPrintWriter
{
	out ->
	out << """#summary Getting Started with SolidBase on the command-line.

This Getting Started is generated by scripts/!GenerateGettingStarted.groovy

(Use !SolidBase ${version} or newer.)

To start using !SolidBase on the command line put solidbase.jar in a folder of your choosing. Get the driver jar for your database and put it in the same folder. Your folder now looks like:

{{{
"""
	execute( "cmd /c dir", temp, null, out, "dir" )
	upgradefile.text = "${definition1}\n\n\n${setup}"
	out << """}}}

Create an upgrade script for the database. The upgrade script starts with a definition and a setup to create the control tables that !SolidBase needs.

You can find example upgrade scripts for Oracle, MySQL, Derby/JavaDB and HSQLDB/HyperSQL [http://code.google.com/p/solidbase/source/browse/trunk#trunk/dist here]. 

test-upgrade.sql:
{{{
${upgradefile.text}}}}

Version 1.1 indicates to !SolidBase what the structure is of the DBVERSION and DBVERSIONLOG tables. So you shouldn't change that. The folder now looks like:

{{{
"""
	execute( "cmd /c dir", temp, null, out, "dir" )
	out << """}}}

Run the following command:

{{{
java -jar solidbase.jar
}}}

You get the following output:

{{{
"""
	execute( "java -jar solidbase.jar", temp, null, out )
	out << """}}}

In this help page you see that you need to specify the -driver, -url, -username and -upgradefile:

{{{
"""
	execute( "java -jar solidbase.jar -driver org.apache.derby.jdbc.EmbeddedDriver -url jdbc:derby:test;create=true -username app -upgradefile test-upgrade.sql", temp, null, out )
	out << """}}}

But this is not right. We have to add the driver jar to the classpath. When using Ant or Maven you can use Ant or Maven techniques. When using the solidbase.properties file you can do that in there (shown below). But now we have to use the java way like this (press enter when asked for the password):

{{{
"""
	execute( "java -cp solidbase.jar;derby-10.5.3.0.jar solidbase.Main -driver org.apache.derby.jdbc.EmbeddedDriver -url jdbc:derby:test;create=true -username app -upgradefile test-upgrade.sql", temp, "\n", out )
	upgradefile.text = "${definition2}\n\n\n${setup}\n\n\n${upgrade1}"
	out << """}}}

So what happened here? The database has no version yet. This is recognized as "no version". !SolidBase always checks if the current version exists in the upgrade file, because if not, this means that this version is either deprecated (removed from the upgrade file) or an upgrade file is used that does not belong to the database.

We need to add an upgrade path to the upgrade file. The result might look like this:

{{{
${upgradefile.text}}}}

When you repeat the previous command you get:

{{{
"""
	execute( "java -cp solidbase.jar;derby-10.5.3.0.jar solidbase.Main -driver org.apache.derby.jdbc.EmbeddedDriver -url jdbc:derby:test;create=true -username app -upgradefile test-upgrade.sql", temp, "\n", out )
	out << """}}}

!SolidBase works incremental. You can add a change to the upgrade file and run !SolidBase again. Add the following to the upgrade file:

{{{
${upgrade2}}}}

You get the following result:

{{{
"""
	upgradefile.text = "${definition2}\n\n\n${setup}\n\n\n${upgrade1}\n\n\n${upgrade2}"
	execute( "java -cp solidbase.jar;derby-10.5.3.0.jar solidbase.Main -driver org.apache.derby.jdbc.EmbeddedDriver -url jdbc:derby:test;create=true -username app -upgradefile test-upgrade.sql", temp, "\n", out )
	out << """}}}

Problem is, we only added the change itself, but forgot to define it. The definition in the top of the file is needed to give a better overview of the contents of the upgrade. Change the definition to this:

{{{
${definition3}}}}

If you run the upgrade again, you get:

{{{
"""
	upgradefile.text = "${definition3}\n\n\n${setup}\n\n\n${upgrade1}\n\n\n${upgrade2}"
	execute( "java -cp solidbase.jar;derby-10.5.3.0.jar solidbase.Main -driver org.apache.derby.jdbc.EmbeddedDriver -url jdbc:derby:test;create=true -username app -upgradefile test-upgrade.sql", temp, "\n", out )
	out << """}}}

Below we are going to use the solidbase.properties file to set some default properties. This makes the command-line version of !SolidBase easier to use.

When a solidbase.properties file is found in the working folder, it will be read automatically. You can add default values for certain properties in it, for example the extra jars and the driver class name. Add a solidbase.properties file:

{{{
${props}}}}

The folder now looks like:

{{{
"""
	new File( "${temp}/solidbase.properties" ).text = props
	execute( "cmd /c dir", temp, null, out, "dir" )
	out << """}}}

Now we can remove the -classpath and the -driver arguments from the commandline:

{{{
"""
	execute( "java -jar solidbase.jar -url jdbc:derby:test;create=true -username app -upgradefile test-upgrade.sql", temp, "\n", out )
	out << """}}}

This concludes the Getting Started with the command line version of !SolidBase.

Enjoy!
"""
}




println "Generating GettingStartedCommandLineWithProperties.wiki..."

ant.delete( dir: temp )
ant.mkdir( dir: temp )
ant.copy( file: "${home}/dist/solidbase.jar", todir: "${home}/TEMP", preservelastmodified: true )
ant.copy( todir: temp, preservelastmodified: true ) { fileset( dir: "${home}/dist", includes: "derby*.jar" ) }

new File( "${home}/GettingStartedCommandLineWithProperties.wiki" ).withPrintWriter
{
	out ->
	upgradefile.text = "${definition3}\n\n\n${setup}\n\n\n${upgrade1}\n\n\n${upgrade2}"
	out << """#summary Getting Started with SolidBase using a properties file.

This Getting Started is generated by scripts/!GenerateGettingStarted.groovy

(Use !SolidBase ${version} or newer.)

You should first read [GettingStartedCommandLineWithArguments Getting Started with SolidBase on the command-line]. That one contains details that are assumed knowledge in this Getting Started.

To start using !SolidBase with a properties file, put solidbase.jar in a folder of your choosing. Get the driver jar for your database and put it in the same folder.

Create the upgrade script for the database named test-upgrade.sql:

{{{
${upgradefile.text}}}}

You can find example upgrade scripts for Oracle, MySQL, Derby/JavaDB and HSQLDB/HyperSQL [http://code.google.com/p/solidbase/source/browse/trunk#trunk/dist here]. 

Create a solidbase.properties file:

{{{
${properties}}}}

The folder now looks like:

{{{
"""
	propertiesfile.text = properties
	execute( "cmd /c dir", temp, null, out, "dir" )
	out << """}}}

Run the following command (press enter when asked for the password):

{{{
java -jar solidbase.jar
}}}

You get the following output:

{{{
"""
	execute( "java -jar solidbase.jar", temp, "\n", out )
	out << """}}}

This concludes the Getting Started with a properties file.

Enjoy!
"""
}




println "Generating GettingStartedAnt.wiki..."

ant.delete( dir: temp )
ant.mkdir( dir: temp )
ant.copy( file: "${home}/dist/solidbase.jar", todir: "${home}/TEMP", preservelastmodified: true )
ant.copy( todir: temp, preservelastmodified: true ) { fileset( dir: "${home}/dist", includes: "derby*.jar" ) }

new File( "${home}/GettingStartedAnt.wiki" ).withPrintWriter
{
	out ->
	upgradefile.text = "${definition3}\n\n\n${setup}\n\n\n${upgrade1}\n\n\n${upgrade2}"
	out << """#summary Getting Started with the SolidBase Ant task.

This Getting Started is generated by scripts/!GenerateGettingStarted.groovy

(Use !SolidBase ${version} or newer.)

You should first read [GettingStartedCommandLineWithArguments Getting Started with SolidBase on the command-line]. That one contains details that are assumed knowledge in this Getting Started.

To start using !SolidBase's Ant task, put solidbase.jar in a folder of your choosing. Get the driver jar for your database and put it in the same folder.

Create the upgrade script for the database named test-upgrade.sql:

{{{
${upgradefile.text}}}}

You can find example upgrade scripts for Oracle, MySQL, Derby/JavaDB and HSQLDB/HyperSQL [http://code.google.com/p/solidbase/source/browse/trunk#trunk/dist here]. 

Create the build.xml:

{{{
${buildxml}}}}

The taskdef element also adds the driver jar to the classpath to make it available to !SolidBase.

The folder now looks like:

{{{
"""
	buildxmlfile.text = buildxml
	execute( "cmd /c dir", temp, null, out, "dir" )
	out << """}}}

Run the following command:

{{{
ant upgradedb
}}}

You get the following output:

{{{
"""
	execute( "ant.bat upgradedb", temp, null, out, "ant upgradedb" )
	out << """}}}

This concludes the Getting Started with the Ant task.

Enjoy!
"""
}




println "Generating GettingStartedMaven.wiki..."

ant.delete( dir: temp )
ant.mkdir( dir: temp )
ant.delete( dir: new File( repo, "solidbase" ) )

new File( "${home}/GettingStartedMaven.wiki" ).withPrintWriter
{
	out ->
	upgradefile.text = "${definition3}\n\n\n${setup}\n\n\n${upgrade1}\n\n\n${upgrade2}"
	out << """#summary Getting Started with the SolidBase Maven plugin.

This Getting Started is generated by scripts/!GenerateGettingStarted.groovy

(Use !SolidBase ${version} or newer.)

You should first read [GettingStartedCommandLineWithArguments Getting Started with SolidBase on the command-line]. That one contains details that are assumed knowledge in this Getting Started.

Create the upgrade script for the database named test-upgrade.sql and put it in a folder of your choosing:

{{{
${upgradefile.text}}}}

You can find example upgrade scripts for Oracle, MySQL, Derby/JavaDB and HSQLDB/HyperSQL [http://code.google.com/p/solidbase/source/browse/trunk#trunk/dist here]. 

Add a pom.xml:

{{{
${pomxml}}}}

The plugin is defined with a dependency on the database driver jar. This causes the driver jar to be included in the classpath when !SolidBase runs.

The folder now looks like:

{{{
"""
	pomxmlfile.text = pomxml
	execute( "cmd /c dir", temp, null, out, "dir" )
	out << """}}}

Run the following command:

{{{
mvn solidbase:upgrade
}}}

You get the following output:

{{{
"""
	execute( "mvn.bat -B solidbase:upgrade", temp, null, out, "mvn solidbase:upgrade" )
	out << """}}}

Running it a second time will give:

{{{
"""
	execute( "mvn.bat -B solidbase:upgrade", temp, null, out, "mvn solidbase:upgrade" )
	out << """}}}

It is also possible to let !SolidBase execute during a build phase, for example the pre-integration-test phase. For that you need to add an {{{<execution />}}} element to the plugin definition:

{{{
${pomxml2}}}}

You can now run the mvn install command. Assuming all dependencies are already downloaded, you get:

{{{
"""
	pomxmlfile.text = pomxml2
	execute( "mvn.bat -B install", temp, null, null )
	execute( "mvn.bat -B install", temp, null, out, "mvn install" ) // Twice, so that everything is downloaded into the Maven repo
	out << """}}}

This concludes the Getting Started with the Maven plugin.

Enjoy!
"""
}
