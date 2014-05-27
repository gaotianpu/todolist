#!/usr/bin/env python
# -*- coding: utf-8 -*-
import web
from config import dbr,dbw

tname="users"

def login(name,password):
    result = list(dbr.select(tname,what="pk_id,nick_name,password",where="mobile=$name",vars=locals()))
    if result:
        if result[0].password == password:
            return result[0]
    return False 


def auth(name,password,deive_no,device_type):
    result = list(dbr.select(tname,what="pk_id,nick_name,password",where="email=$name",vars=locals()))
    if result:
        if result[0].password == password:
            return result[0]
            #user_devices, user_id=$user_id and device_no=$deive_no
            #是否存在，
            #如果存在，accesss_token是否过期
    return False 