# MusicDroid
Desktop application to move music albums to an android device

## Install Application

### Install

Download the tar ball from 'releases' and extract it in your computer. e.g. in your home directory.

    $ cd /tmp
    $ wget https://github.com/adelolmo/musicdroid/releases/download/musicdroid-1.0/musicdroid-1.0-dist.tar.gz
    $ mkdir $HOME/musicdroid
    $ tar zxvf musicdroid-1.0-dist.tar.gz -C $HOME/musicdroid
    
### Launch

    $ $HOME/musicdroid/startup.sh

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
    $ cd ~/dev
    $ git clone https://github.com/adelolmo/musicdroid.git
    $ cd musicdroid
    $ mvn clean install
    