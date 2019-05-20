# MusicDroid
Copy your music albums to your Android device.

<img src="musicdroid.png"/>

# How to Install

## Setup repository

Follow the instructions [here](https://adelolmo.github.io).

## Install package
```
# apt-get install musicdroid
```

# How to remove
```
# apt-get purge musicdroid
```

# How to Build it

## Install JADB dependency
    $ cd /tmp
    $ wget https://github.com/adelolmo/jadb/releases/download/jadb-1.1/jadb-1.1.jar 
    $ mvn install:install-file \
    -Dfile=/tmp/jadb-1.1.jar \
    -DgroupId=se.vidstige \
    -DartifactId=jadb \
    -Dversion=1.1 \
    -Dpackaging=jar

## Build Music Droid
    $ git clone https://github.com/adelolmo/musicdroid.git
    $ mvn clean package