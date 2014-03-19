#!/usr/bin/env python
# -*- coding: utf-8 -*-
import web
from config import dbr,dbw

table_name = 'subjects'

def insert(user_id,subject,body):
    #避免重复提交
    last_one = load_last_one(user_id)
    if last_one and last_one.subject.strip()==subject.strip():  
        return last_one.pk_id

    return dbw.insert(table_name,user_id=user_id,subject=subject,body=body,
        created_date=web.SQLLiteral('now()'),
        last_update=web.SQLLiteral('now()'),
        plan_start_date=web.SQLLiteral('now()'))

def load_by_id(pk_id):
    rows = list(dbr.select(table_name,where='pk_id=$pk_id' , vars=locals()))
    return rows[0]

def load_by_date(user_id,date):
    return list(dbr.select(table_name,what="pk_id,subject",
        where='user_id=$user_id and date(plan_start_date)=$date',
        order="pk_id",vars=locals()))

def load_last_one(user_id):
    rows = list(dbr.select(table_name,what="pk_id,subject",
        where='user_id=$user_id',vars=locals()))
    if rows:
        return rows[0]
    return False 