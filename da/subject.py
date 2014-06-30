#!/usr/bin/env python
# -*- coding: utf-8 -*-
import web
from config import dbr,dbw

table_name = 'subjects'

def insert(user_id,subject):
    #避免重复提交
    last_one = load_last_one(user_id)
    if last_one and last_one.body.strip()==subject.strip():  
        return last_one.pk_id

    return dbw.insert(table_name,user_id=user_id,subject="",body=subject,
        created_date=web.SQLLiteral('now()'),
        last_update=web.SQLLiteral('now()'),
        plan_start_date=web.SQLLiteral('now()'))

def insert2(user_id,body,device_type,device_no,local_id,created_date,last_update):
    rows = list(dbr.select(table_name,what="pk_id,body", where="user_id=$user_id",vars=locals(),order="pk_id desc", limit=20))
    for r in rows:
        if cmp(r.body,body)==0:
            return r.pk_id
    return dbw.insert(table_name,user_id=user_id,subject="",body=body,
            device_no = device_no, local_id=local_id, device_type=device_type,
            app_created_date=created_date,
            last_update=last_update,
            created_date = web.SQLLiteral('now()'),
            plan_start_date=web.SQLLiteral('now()'))

    #device_type, 手机型号，比如HUAWEI_G730
    # result = list(dbr.select(table_name,what="pk_id",
    #     where="user_id=$user_id and local_id=$local_id and device_type=$device_type and device_no=$device_no",
    #     vars=locals()))
    # if result:
    #     pk_id = result[0].pk_id
    #     dbw.update(table_name,body=body,where="pk_id=$pk_id",vars=locals())
    #     return pk_id
    # else:
    #     pass

def update(pk_id,user_id,**kv):
    return dbw.update(table_name,last_update=web.SQLLiteral('now()'),where='pk_id=$pk_id and user_id=$user_id',vars=locals(),**kv) 
 

def update1(pk_id,**kv):
    return dbw.update(table_name,last_update=web.SQLLiteral('now()'),where='pk_id=$pk_id ',vars=locals(),**kv)


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
    return list(dbr.select(table_name,order="pk_id desc",offset=offset,limit=limit))


def load_page(cust_id,offset,limit): 
    rows = list(dbr.select(table_name,
        what="pk_id,body,created_date", 
        where="user_id=$cust_id and is_delete=0", 
        order="pk_id desc",
        offset=offset,limit=limit,
        vars=locals()))
    r = {}  
    for row in rows:
        day = row.created_date.strftime('%Y-%m-%d')         
        if day in r:
            r[day].append(row)
        else:            
            r[day]=[row]
    return sorted(r.iteritems(), key=lambda k:k[0], reverse=True)  
    
def load_page2(cust_id,offset,limit): 
    rows = list(dbr.select(table_name,
        what="pk_id,user_id,body,created_date,last_update,local_id,is_delete,is_todo,is_remind",
        where="user_id=$cust_id", 
        order="pk_id asc",
        offset=offset,limit=limit,
        vars=locals()))
    return rows

def load_page3(cust_id,min_pk_id,limit): 
    rows = list(dbr.select(table_name,
        what="pk_id,user_id,body,created_date,last_update,local_id,is_delete,is_todo,is_remind",
        where="user_id=$cust_id and pk_id>$min_pk_id", 
        order="pk_id",
        offset=0,limit=limit,
        vars=locals()))
    return rows

def load_count(user_id):
    r = dbr.select(table_name,what="count(*) as count",where="user_id=$user_id",vars=locals())
    return r[0].count


#update subjects a, subjects_old b set a.terms = b.terms where a.pk_id=b.pk_id and b.terms is not null;