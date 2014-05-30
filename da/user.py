#!/usr/bin/env python
# -*- coding: utf-8 -*-
import web
from config import dbr,dbw

tname="users"

def register(name,password):
    #,device_type,device_no,os_type
    user_id = dbw.insert(tname,email=name,password=password,
        creation_date=web.SQLLiteral('now()'),
        last_update=web.SQLLiteral('now()'),
        last_login=web.SQLLiteral('now()'))  
    return  user_id 
    # dbw.insert('user_devices',user_id=user_id,device_no=device_no, 
    #     device_type=device_type,os_type=os_type,
    #     creation_date=web.SQLLiteral('now()'),
    #     last_update=web.SQLLiteral('now()'))
    # return get_access_token(user_id,device_no)

def login(name,password):
    result = list(dbr.select(tname,what="pk_id,nick_name,password",where="mobile=$name or email=$name",vars=locals()))
    print  result
    if not result: 
        return False
    if not cmp(result[0].password , password):
        return False      
    return result[0] 

import uuid 
def generate_access_token():     
    return str(uuid.uuid1()).replace('-','')

def get_access_token(user_id,device_no,device_type,os_type): 
    result = list(dbr.select('user_devices',
        what="user_id,device_no,access_token,device_type,os_type",
        where="user_id=$user_id",vars=locals()))

    access_token = generate_access_token()
    if not result:
        dbw.insert('user_devices',user_id=user_id,device_no=device_no, 
        device_type=device_type,os_type=os_type,
        access_token=access_token,
        creation_date=web.SQLLiteral('now()'),
        last_update=web.SQLLiteral('now()'))

        result = list(dbr.select('user_devices',
        what="user_id,device_no,access_token,device_type,os_type",
        where="user_id=$user_id",vars=locals()))
    else:
        dbw.update('user_devices',
            access_token=access_token,
            last_update=web.SQLLiteral('now()'),
            where="user_id=$user_id",vars=locals())
        result[0].access_token = access_token 
    
    return result[0] 


def auth(name,password,deive_no,device_type):
    result = list(dbr.select(tname,what="pk_id,nick_name,password",where="email=$name",vars=locals()))
    if result:
        if result[0].password == password:
            return result[0]
            #user_devices, user_id=$user_id and device_no=$deive_no
            #是否存在，
            #如果存在，accesss_token是否过期
    return False 