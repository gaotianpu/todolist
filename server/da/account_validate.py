#!/usr/bin/env python
# -*- coding: utf-8 -*-
import web
from config import dbr,dbw

tname="user_account_validate"

val_sorts = (1,2) #1 mobile, 2 email
code_status = (0,1,2,3) #0默认，1发送成功，2已用，3过期

def insert(user_id,val_sort,val_account,val_code):
    return dbw.insert(tname,
        user_id=user_id,validate_sort=val_sort,validate_account=val_account,validate_code=val_code,
        code_status=0,creation_date=web.SQLLiteral('now()'),last_update=web.SQLLiteral('now()'))

def update_code_status(pk_id,status):
    dbw.update(tname,
        code_status=status, last_update=web.SQLLiteral('now()'),
        where="pk_id=$pk_id",
        vars=locals())

def check_code(user_id,val_sort,val_account,val_code):
    result = list(dbr.select(tname,what="pk_id,validate_account",
        where="user_id=$user_id and validate_sort=$val_sort and validate_code=$val_code and code_status=1",
        vars=locals()))
    if result:
        pk_id = result[0].pk_id
        if result[0].validate_account == val_account:
            update_code_status(pk_id,2)
            return True
    return False
