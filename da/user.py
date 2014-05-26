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
