
# AlKhalil1.1
mvn install:install-file \
   -Dfile=AlKhalil.jar \
   -DgroupId=AlKhalil \
   -DartifactId=AlKhalil1.1 \
   -Dversion=1.1 \
   -Dpackaging=jar \
   -DgeneratePom=true

# POM.xml
		<dependency>
			<groupId>AlKhalil</groupId>
			<artifactId>AlKhalil1.1</artifactId>
			<version>1.1</version>
		</dependency>