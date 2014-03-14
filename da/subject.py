#!/usr/bin/env python
# -*- coding: utf-8 -*-
import web
from config import dbr,dbw

table_name = 'subjects'
def insert(user_id,subject,body):
	return dbw.insert(table_name,user_id=user_id,subject=subject,body=body,
		created_date=web.SQLLiteral('now()'),
		last_update=web.SQLLiteral('now()'),
		plan_start_date=web.SQLLiteral('now()'))

def load_by_date(user_id,date):
	return list(dbr.select(table_name,what="pk_id,subject",where='user_id=$user_id and date(plan_start_date)=$date',vars=locals()))