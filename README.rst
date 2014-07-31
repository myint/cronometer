==========
cronometer
==========

.. image:: https://travis-ci.org/myint/cronometer.svg?branch=master
    :target: https://travis-ci.org/myint/cronometer
    :alt: Build status

`cronometer` is a nutrition tracking tool. This is a fork of the original_.

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

As an example, below is the procedure that was used to upgrade from ``SR24`` to
``SR26``.

Unzip the old processed database::

    $ unzip lib/usda_sr24.jar
    $ mv usda_sr24 usda_sr26

Update ``src/ca/spaz/cron/datasource/USDAImport/USDAImporter.java`` to point
to ``sr26``.

Run the importer to update the old processed data::

    $ java -classpath lib/cronometer.jar \
        ca.spaz.cron.datasource.USDAImport.USDAImporter < sr26.zip

Append deleted items from the old ``foods.index`` into the new
``deprecated.index``::

    $ ./scripts/deprecated.py usda_sr24/foods.index usda_sr26/foods.index \
         >> usda_sr26/deprecated.index

Create the new JAR::

    $ rm lib/usda_sr24.jar
    $ zip -r lib/usda_sr26.jar usda_sr26

Update ``src/ca/spaz/cron/datasource/USDAFoods.java`` to point to the new JAR.
And update the OS X app to point to the new JAR.
