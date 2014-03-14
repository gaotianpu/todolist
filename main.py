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
    '/', 'Index',
)

render = web.template.render('templates/',base='layout') 

class Index:
    def GET(self):
        return render.index()

class DateList:
    def GET(self):
        i = web.input(date=datetime.now().strftime('%Y-%m-%d'))
        rows = da.subject.load_by_date(cust_id,i.date)
        date = {'shortDate':u'今天','strDate':datetime.now().strftime('%y/%m/%d'),'dayOfWeek':'Fri'}
        r = {'code':1,'list':rows,'date':date}
        return json.dumps(r)

class New:
    def POST(self):
        i = web.input(content='')
        da.subject.insert(cust_id,i.content,i.content)
        return '{"code":1}'  

app = web.application(urls, globals())
if __name__ == "__main__":
    app.run()