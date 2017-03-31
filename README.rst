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



Build and start on Linux
========================

::

    $ ant
    $ ./start_cronometer.sh


Download
========

https://github.com/myint/cronometer/releases


Importing new USDA food database
================================

As an example, below is the procedure that was used to upgrade from ``SR26`` to
``SR28``.

Unzip the old processed database::

    $ unzip lib/usda_sr26.jar
    $ mv usda_sr26 usda_sr28

Update ``src/ca/spaz/cron/datasource/USDAImport/USDAImporter.java`` to point
to ``sr28``.

Run the importer to update the old processed data::

    $ java -classpath lib/cronometer.jar \
        ca.spaz.cron.datasource.USDAImport.USDAImporter < sr28.zip

Append deleted items from the old ``foods.index`` into the new
``deprecated.index``::

    $ ./scripts/deprecated.py usda_sr26/foods.index usda_sr28/foods.index \
         >> usda_sr28/deprecated.index

Create the new JAR::

    $ rm lib/usda_sr26.jar
    $ zip -r lib/usda_sr28.jar usda_sr28

Update ``src/ca/spaz/cron/datasource/USDAFoods.java`` to point to the new JAR.
And update the OS X app to point to the new JAR.
