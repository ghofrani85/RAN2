#!/bin/bash

sudo -u postgres dropdb databaseRAN2
sudo -u postgres createdb -O postgresPLRE2 databaseRAN2
