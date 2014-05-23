#!/usr/bin/env python
# -*- coding: utf-8 -*-
import web
import da
import json 
from datetime import *


urls = ("/new2", "New2",
    "/list3","List3")

#register, mobile + sms_validate_code
#login, mobile + password_encryption
#

class CJsonEncoder(json.JSONEncoder):
    def default(self, obj):
        if isinstance(obj, datetime):
            return obj.strftime('%Y-%m-%d %H:%M:%S')
        elif isinstance(obj, date):
            return obj.strftime('%Y-%m-%d')
        else:
            return json.JSONEncoder.default(self, obj)

class New2:
    def POST(self):
        i = web.input(cust_id=0,content='',device_type='',device_no='',local_id=0,creation_date=1)
        content = web.websafe(i.content)
        pk_id = da.subject.insert2(i.cust_id,i.content,i.device_type,i.device_no,i.local_id,i.creation_date) 

        task = da.subject.load_by_id(pk_id)
        task.local_id = i.local_id  #local_id必须是本次请求的id
        #cron.update_term_count(task) #remove to eda?
        r = {"code":1,"data":task}
        return json.dumps(r,cls=CJsonEncoder) 

class List3:
    def GET(self):
        i = web.input(cust_id=0,min_pk_id=1,size=50)
        rows = da.subject.load_page3(i.cust_id,i.min_pk_id,int(i.size))
        r = {'code':1,'list':rows}
        return json.dumps(r,cls=CJsonEncoder)

def init_app():
    app = web.application(urls, globals())
    #app.notfound = api_notfound
    #app.internalerror = api_internalerror
    # app.add_processor(web.loadhook(api_loadhook))
    # app.add_processor(web.unloadhook(api_unloadhook))
    return app

app = init_app()