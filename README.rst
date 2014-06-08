==========
cronometer
==========

.. image:: https://travis-ci.org/myint/cronometer.svg?branch=master
    :target: https://travis-ci.org/myint/cronometer
    :alt: Build status

cronometer is a nutrition tracking tool. This is a fork of the original_.

.. _original: http://sourceforge.net/projects/cronometer


Build on OS X
=============

::

    $ ./build_osx.bash


Download
========

https://github.com/myint/cronometer/releases


Importing new USDA food database
================================

Unzip the old processed database::

    $ unzip lib/usda_sr24.jar

Run the importer to update the old processed data::

    $ java -cp lib/cronometer.jar \
        ca.spaz.cron.datasource.USDAImport.USDAImporter < sr24.zip
