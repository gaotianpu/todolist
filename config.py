#!/usr/bin/env python
# -*- coding: utf-8 -*-
import web
import os

debug = "SERVER_SOFTWARE" not in os.environ
web.config.debug = debug

if not debug:     
    import sae.const
    import pylibmc  #memcached
    dbr = web.database(dbn='mysql', host=sae.const.MYSQL_HOST_S, port=int(sae.const.MYSQL_PORT), db=sae.const.MYSQL_DB, user=sae.const.MYSQL_USER, pw=sae.const.MYSQL_PASS)
    dbw = web.database(dbn='mysql', host=sae.const.MYSQL_HOST, port=int(sae.const.MYSQL_PORT), db=sae.const.MYSQL_DB, user=sae.const.MYSQL_USER, pw=sae.const.MYSQL_PASS)    
    mc = pylibmc.Client()
    redirect_uri = "http://cygs.sinaapp.com/callback"
else:
    #dev
    dbw = web.database(dbn='mysql', host='127.0.0.1', db='planc', user='root', pw='root')
    dbr = web.database(dbn='mysql', host='127.0.0.1', db='planc', user='root', pw='root')
    mc = False  
    redirect_uri = 'http://127.0.0.1:8080/callback'
