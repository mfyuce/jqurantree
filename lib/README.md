mvn install:install-file \
   -Dfile=AlKhalil-Analyzer-2.1.jar \
   -DgroupId=org.alkhalil \
   -DartifactId=AlKhalil-Analyzer \
   -Dversion=2.1 \
   -Dpackaging=jar \
   -DgeneratePom=true
   
mvn install:install-file \
   -Dfile=AlKhalil-db-Long-2.1.jar \
   -DgroupId=org.alkhalil \
   -DartifactId=AlKhalil-db-Long \
   -Dversion=2.1 \
   -Dpackaging=jar \
   -DgeneratePom=true
   
mvn install:install-file \
   -Dfile=AlKhalil-db-Short-2.1.jar \
   -DgroupId=org.alkhalil \
   -DartifactId=AlKhalil-db-Short \
   -Dversion=2.1 \
   -Dpackaging=jar \
   -DgeneratePom=true
   
mvn install:install-file \
   -Dfile=stanford-parser-3.9.2-models.jar \
   -DgroupId=edu.stanford.nlp \
   -DartifactId=stanford-parse-models \
   -Dversion=3.9.2 \
   -Dpackaging=jar \
   -DgeneratePom=true
   

mvn install:install-file \
   -Dfile=stanford-corenlp-3.9.2-models-arabic.jar \
   -DgroupId=edu.stanford.nlp \
   -DartifactId=stanford-parse-models-arabic \
   -Dversion=3.9.2 \
   -Dpackaging=jar \
   -DgeneratePom=true