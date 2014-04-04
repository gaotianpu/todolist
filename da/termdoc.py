#!/usr/bin/env python
# -*- coding: utf-8 -*-
# termdoc.py
import web
from config import dbr,dbw
import datetime

tname="term_doc_count"
def load_all():
	return list(dbr.select(tname,what="term,idf")) 

def insert(terms):
	if not terms:
		return 
	values = []	
	for t in terms:
		if not t[0].strip(): continue
		values.append({'term':t[0],'count':t[1],'idf':t[2],'last_update':datetime.datetime.now()} )
	dbw.supports_multiple_insert = True
	dbw.multiple_insert(tname, values=values) 

def update_a(term,**kv):
	return dbw.update(tname,where="term=$term",vars=locals(),**kv)
	#web.sqlquote

def update(term,count,idf=0):
	return dbw.update(tname,count=count,idf=idf,where="term=$term",vars=locals())
	#web.sqlquote


def load_all():
	return dbr.select(tname, what="term",where="CHAR_LENGTH(term)>1",order="count desc")

def load_best_terms():
	return dbr.select(tname, what="term",where="sogou_tf_idf is not null",order="sogou_tf_idf desc")

#####
def load_sogou_terms():
	return dbr.select(tname,where="CHAR_LENGTH(term)>1 and sogou_ix_count is null")

def load_has_sogou_terms():
	return list(dbr.select(tname,where="sogou_ix_count is not null"))

def update_sogou_idf(term,count,idf):
	return dbw.update(tname,sogou_ix_count=count,sogou_idf=idf,sogou_last_get=web.SQLLiteral('now()'),
		where="term=$term",vars=locals())