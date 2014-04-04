#!/usr/bin/env python
# -*- coding: utf-8 -*-
import web
from config import dbr,dbw

table_name = 'subjects'

def insert(user_id,subject):
    #避免重复提交
    last_one = load_last_one(user_id)
    if last_one and last_one.subject.strip()==subject.strip():  
        return last_one.pk_id

    return dbw.insert(table_name,user_id=user_id,subject="",body=subject,
        created_date=web.SQLLiteral('now()'),
        last_update=web.SQLLiteral('now()'),
        plan_start_date=web.SQLLiteral('now()'))

def update(pk_id,**kv):
    return dbw.update(table_name,last_update=web.SQLLiteral('now()'),where='pk_id=$pk_id',vars=locals(),**kv)

def load_by_id(pk_id):
    rows = list(dbr.select(table_name,where='pk_id=$pk_id' , vars=locals()))
    return rows[0] if rows else False

def load_by_ids(pk_ids):
    rows = list(dbr.select(table_name,what="pk_id,body",where='pk_id in $pk_ids' , vars=locals()))
    return rows

def load_by_date(user_id,date):
    return list(dbr.select(table_name,what="pk_id,subject,body,task_status",
        where='user_id=$user_id and date(plan_start_date)=$date',
        order="pk_id",vars=locals()))

def load_last_one(user_id):
    rows = list(dbr.select(table_name,what="pk_id,subject,body,task_status",
        where='user_id=$user_id',vars=locals()))
    if rows:
        return rows[0]
    return False 

def load_all(offset,limit=100):
    return list(dbr.select(table_name,offset=offset,limit=limit))

def load_count():
    r = dbr.select(table_name,what="count(*) as count")
    return r[0].count


