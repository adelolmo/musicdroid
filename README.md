# MusicDroid
Desktop application to move music albums to an android device

## Build

### Install JADB dependency
    $ cd /tmp
    $ wget https://github.com/adelolmo/jadb/releases/download/jadb-1.0/jadb-1.0.jar 
    $ mvn install:install-file \
    -Dfile=/tmp/jadb-1.0.jar \
    -DgroupId=se.vidstige \
    -DartifactId=jadb \
    -Dversion=1.0 \
    -Dpackaging=jar

### Build Music Droid
    $ cd /tmp
    $ git clone https://github.com/adelolmo/musicdroid.git
    $ cd musicdroid
    $ mvn clean install

## Install Music Droid
    $ cd /tmp/musicdroid
    $ ./install.sh
    
## Start Music Droid
    $ /opt/musicdroid/startup.sh
    