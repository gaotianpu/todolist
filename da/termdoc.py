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

def update(term,count,idf=0):
	return dbw.update(tname,count=count,idf=idf,where="term=$term",vars=locals())
	#web.sqlquote


def load_all():
	return dbr.select(tname, what="term",where="CHAR_LENGTH(term)>1",order="count desc")