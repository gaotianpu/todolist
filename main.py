#!/usr/bin/env python
# -*- coding: utf-8 -*-
import web
import da
from datetime import *
import json 

cust_id = 1  #tmp

urls = (
    '/datelist','DateList',    
    '/new','New',  
    '/details','Details',
    '/done', 'Done',  
    '/', 'Index',
)

render = web.template.render('templates/',base='layout') 

class CJsonEncoder(json.JSONEncoder):
    def default(self, obj):
        if isinstance(obj, datetime):
            return obj.strftime('%Y-%m-%d %H:%M:%S')
        elif isinstance(obj, date):
            return obj.strftime('%Y-%m-%d')
        else:
            return json.JSONEncoder.default(self, obj)

class Index:
    def GET(self):
        return render.index()

class DateList:
    def GET(self):
        i = web.input(date=datetime.now().strftime('%Y-%m-%d'))
        rows = da.subject.load_by_date(cust_id,i.date)
        #date format 应该放在js端处理 ?
        date = {'shortDate':i.date,'queryDate':i.date, 'strDate':i.date,'dayOfWeek':''}
        r = {'code':1,'list':rows,'date':date,'count':len(rows)}
        return json.dumps(r)

class New:
    def POST(self):
        i = web.input(content='')
        content = web.websafe(i.content)
        pk_id = da.subject.insert(cust_id,content)
        task = da.subject.load_by_id(pk_id)
        r = {"code":1,"data":task}
        return json.dumps(r,cls=CJsonEncoder) 

class Details:
    def GET(self):
        i = web.input(pk_id=0)
        detail = da.subject.load_by_id(i.pk_id)
        r = {"code":1,"data":detail}
        return json.dumps(r,cls=CJsonEncoder) 
    def POST(self):
        i = web.input(pk_id=0,subject='',body='')
        da.subject.update(i.pk_id,subject="",body=i.body)
        return  

class Done:
    def POST(self):
        i = web.input(pk_id=0,checked='true')
        task_status = 1 if i.checked=='true' else 0
        da.subject.update(i.pk_id,task_status=task_status) 
        r = {"code":1,"data":True}
        return json.dumps(r) 

app = web.application(urls, globals())
if __name__ == "__main__":
    app.run()