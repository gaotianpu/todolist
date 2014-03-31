#!/usr/bin/env python
# -*- coding: utf-8 -*-
# termdoc.py
import web
from config import dbr,dbw
import datetime

tname="term_doc_count"
def load_all():
	rows = dbr.select(tname,what="term")
	return [r.term for r in rows]

def insert(terms):
	if not terms:
		return 
	values = []	
	for t in terms:
		values.append({'term':t,'count':0,'last_update':datetime.datetime.now()} )
	dbw.supports_multiple_insert = True
	dbw.multiple_insert(tname, values=values) 

def update(term,count,idf=0):
	return dbw.update(tname,count=count,idf=idf,where="term=$term",vars=locals())
	#web.sqlquote